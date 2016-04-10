/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.doc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.database.DatabaseConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.swagger.Operation;
import org.openmrs.module.webservices.docs.swagger.Parameter;
import org.openmrs.module.webservices.docs.swagger.Path;
import org.openmrs.module.webservices.docs.swagger.SwaggerSpecification;
import org.openmrs.module.webservices.docs.swagger.SwaggerSpecificationCreator;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class SwaggerSpecificationCreatorTest extends BaseModuleWebContextSensitiveTest {
	
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
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator("/v1/");
		ssc.BuildJSON();
		
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
		
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator("/v1/");
		ssc.BuildJSON();
		SwaggerSpecification spec = ssc.getSwaggerSpecification();
		
		for (Path p : spec.getPaths().getPaths().values()) {
			for (Operation o : p.getOperations().values()) {
				Assert.assertFalse("Ensure each operation has a unique ID", operationIds.contains(o.getOperationId()));
				operationIds.add(o.getOperationId());
			}
		}
	}
	
	// makes sure that every GET operation has the "v" parameter
	@Test
	public void checkRepresentationParamExists() throws Exception {
		List<String> operationIds = new ArrayList<String>();
		
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator("/v1/");
		ssc.BuildJSON();
		SwaggerSpecification spec = ssc.getSwaggerSpecification();
		
		for (Path p : spec.getPaths().getPaths().values()) {
			for (Operation o : p.getOperations().values()) {
				if (o.getName().equals("get")) {
					Assert.assertTrue("Ensure each GET operation has the 'v' query parameter",
					    operationHasRepresentationParam(o));
				}
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
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator("/v1/");
		ssc.BuildJSON();
		SwaggerSpecification spec = ssc.getSwaggerSpecification();
		
		for (Path p : spec.getPaths().getPaths().values()) {
			for (Operation o : p.getOperations().values()) {
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
