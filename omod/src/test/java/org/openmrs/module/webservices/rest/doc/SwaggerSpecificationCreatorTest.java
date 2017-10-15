/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.doc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.converter.ModelConverterContextImpl;
import io.swagger.converter.ModelConverters;
import io.swagger.jackson.ModelResolver;
import io.swagger.models.Info;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.auth.BasicAuthDefinition;
import io.swagger.models.parameters.Parameter;
import io.swagger.util.Json;
import org.dbunit.database.DatabaseConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.swagger.SwaggerSpecificationCreator;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class SwaggerSpecificationCreatorTest extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void mainTest() {
		String str = new SwaggerSpecificationCreator().getJSON();
		assertNotNull(str);
	}
	
	@Test
	public void hasSearchHandler() {
		SwaggerSpecificationCreator creator = new SwaggerSpecificationCreator();
		
		assertTrue(creator.hasSearchHandler("attribute", "location"));
		
		assertFalse(creator.hasSearchHandler("workflow", null));
		assertFalse(creator.hasSearchHandler("description", "concept"));
	}
	
	@Test
	public void cacheTest() throws Exception {
		if (SwaggerSpecificationCreator.isCached()) {
			SwaggerSpecificationCreator.clearCache();
		}
		assertFalse(SwaggerSpecificationCreator.isCached());
		new SwaggerSpecificationCreator().getJSON();
		assertTrue(SwaggerSpecificationCreator.isCached());
	}
	
	@Test
	public void modelResolveTest() {
		final ModelResolver modelResolver = new ModelResolver(new ObjectMapper());
		final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
		final Model model = context.resolve(Patient.class);
		assertNotNull(model);
	}
	
	@Test
	public void swaggerSerializeTest() throws JsonProcessingException {
		final Info info = new Info().version("1.0.0").title("Swagger WebServices REST");
		
		Swagger swagger = new Swagger().info(info).securityDefinition("basicAuth", new BasicAuthDefinition())
		        .scheme(Scheme.HTTP).consumes("application/json").produces("application/json");
		
		final Model patientModel = ModelConverters.getInstance().read(Patient.class).get("Patient");
		swagger.addDefinition("Patient", patientModel);
		
		final String swaggerJson = Json.pretty(swagger);
		assertNotNull(swaggerJson);
	}
	
	Map<String, Integer> beforeCounts;
	
	public Map<String, Integer> getRowCounts() throws Exception {
		Map<String, Integer> ret = new HashMap<String, Integer>();
		
		Connection con = this.getConnection();
		DatabaseMetaData metaData = con.getMetaData();
		DatabaseConnection dbcon = new DatabaseConnection(con);
		
		ResultSet rs = metaData.getTables(null, "PUBLIC", "%", null);
		while (rs.next()) {
			String tableName = rs.getString(3);
			
			ret.put(tableName, dbcon.getRowCount(tableName));
		}
		
		return ret;
	}
	
	@Before
	public void init() throws Exception {
		// init REST
		Context.getService(RestService.class).initialize();
		
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(RestConstants.SWAGGER_QUIET_DOCS_GLOBAL_PROPERTY_NAME, "true"));
		
		// ensure GP is written to database before we count the rows
		Context.flushSession();
		
		beforeCounts = getRowCounts();
	}
	
	@Test
	public void checkNoDatabaseChanges() throws Exception {
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator();
		ssc.getJSON();
		
		Map<String, Integer> afterCounts = getRowCounts();
		
		Assert.assertEquals("Ensure no tables are created or destroyed", beforeCounts.size(), afterCounts.size());
		Assert.assertTrue("Ensure that no data was added or removed from any tables",
		    ensureCountsEqual(beforeCounts, afterCounts));
	}
	
	private boolean ensureCountsEqual(Map<String, Integer> beforeCounts, Map<String, Integer> afterCounts) throws Exception {
		for (String key : beforeCounts.keySet()) {
			if (beforeCounts.get(key) != afterCounts.get(key)) {
				System.err.println("The " + key + " table has a different number of rows (" + beforeCounts.get(key)
				        + " before, " + afterCounts.get(key) + " after).");
				
				return false;
			}
		}
		
		return true;
	}
	
	// makes sure that every operation has a unique operationId
	@Test
	public void checkOperationIdsSet() throws Exception {
		List<String> operationIds = new ArrayList<String>();
		
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator();
		ssc.getJSON();
		Swagger spec = ssc.getSwagger();
		
		for (Path p : spec.getPaths().values()) {
			for (Operation o : p.getOperations()) {
				Assert.assertFalse("Ensure each operation has a unique ID", operationIds.contains(o.getOperationId()));
				operationIds.add(o.getOperationId());
			}
		}
	}
	
	// makes sure that every GET operation has the "v" parameter
	@Test
	public void checkRepresentationParamExists() throws Exception {
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator();
		ssc.getJSON();
		Swagger spec = ssc.getSwagger();
		
		for (Path p : spec.getPaths().values()) {
			if (p.getGet() != null) {
				Assert.assertTrue("Ensure each GET operation has the 'v' query parameter",
				    operationHasRepresentationParam(p.getGet()));
			}
		}
	}
	
	private boolean operationHasRepresentationParam(Operation o) {
		boolean ret = false;
		
		for (Parameter p : o.getParameters()) {
			if (p.getName().equals("v")) {
				ret = !ret;
			}
		}
		
		return ret;
	}
	
	// make sure each operation that supports paging has the limit and startIndex parameters
	@Test
	public void checkPagingParamsExist() throws Exception {
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator();
		ssc.getJSON();
		Swagger spec = ssc.getSwagger();
		
		for (Path p : spec.getPaths().values()) {
			for (Operation o : p.getOperations()) {
				if (o.getOperationId().matches("^getAll[A-Z].*")) {
					Assert.assertTrue("Ensure each operation that supports paging has both paging parameters",
					    operationHasPagingParams(o));
				}
			}
		}
	}
	
	private boolean operationHasPagingParams(Operation o) {
		boolean limit = false, startIndex = false;
		
		for (Parameter p : o.getParameters()) {
			if (p.getName().equals("limit")) {
				limit = !limit;
			} else if (p.getName().equals("startIndex")) {
				startIndex = !startIndex;
			}
		}
		
		return limit && startIndex;
	}
}
