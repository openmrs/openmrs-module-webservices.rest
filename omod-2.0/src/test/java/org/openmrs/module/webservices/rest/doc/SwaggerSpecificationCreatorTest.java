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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.converter.ModelConverterContextImpl;
import io.swagger.converter.ModelConverters;
import io.swagger.jackson.ModelResolver;
import io.swagger.models.Info;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.auth.BasicAuthDefinition;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.util.Json;
import org.dbunit.database.DatabaseConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.unrelatedtest.rest.resource.UnrelatedGenericChildResource;
import org.openmrs.module.webservices.docs.swagger.SwaggerGenerationUtil;
import org.openmrs.module.webservices.docs.swagger.SwaggerSpecificationCreator;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ObsResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonResource1_8;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import static org.junit.Assert.assertEquals;

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
	public void cacheTest() {
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
	public void swaggerSerializeTest() {
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
	
	private boolean ensureCountsEqual(Map<String, Integer> beforeCounts, Map<String, Integer> afterCounts) {
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
	public void checkOperationIdsSet() {
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
	public void checkRepresentationParamExists() {
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
	public void checkPagingParamsExist() {
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
	
	@Test
	public void addPathsWorksForCoreModels() throws NoSuchMethodException, InvocationTargetException,
	        IllegalAccessException, NoSuchFieldException {
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator();
		
		// reflect the swagger propperty and initSwagger method so we can setup for the main test
		Field swagger = ssc.getClass().getDeclaredField("swagger");
		swagger.setAccessible(true);
		swagger.set(ssc, new Swagger());
		
		Method initSwagger = ssc.getClass().getDeclaredMethod("initSwagger");
		initSwagger.setAccessible(true);
		initSwagger.invoke(ssc);
		
		// make the paths method accessible
		Method addPaths = ssc.getClass().getDeclaredMethod("addPaths");
		addPaths.setAccessible(true);
		
		addPaths.invoke(ssc);
	}
	
	/**
	 * Some subresource appear to only support creation, not fetching or updating. References to the
	 * Get/Update definitions were still being included in the response options, despite not
	 * existing. Ensure that these references are not included in the resulting JSON to prevent
	 * swagger reference errors. See ticket: RESTWS-720
	 */
	@Test
	public void createOnlySubresourceDefinitions() {
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator();
		String json = ssc.getJSON();
		
		// A simple search will tell us if the problem definitions exist
		assertFalse(json.contains("SystemsettingSubdetailsGet"));
		assertFalse(json.contains("SystemsettingSubdetailsUpdate"));
		assertTrue(json.contains("SystemsettingSubdetailsCreate"));
	}

	@Test
	public void generateGETModel_shouldCheckForOpenMRSResource() {
		Model model = SwaggerGenerationUtil.generateGETModel(new ObsResource1_8(), Representation.DEFAULT);
		Assert.assertTrue(model instanceof ModelImpl);

		Map<String, Property> propertyMap = model.getProperties();
		Assert.assertTrue(propertyMap.containsKey("location"));
		Assert.assertTrue(propertyMap.containsKey("person"));
		Assert.assertTrue(propertyMap.containsKey("obsDatetime"));
		Assert.assertTrue(propertyMap.containsKey("accessionNumber"));

		Assert.assertTrue(propertyMap.get("location") instanceof RefProperty);
		Assert.assertTrue(propertyMap.get("person") instanceof RefProperty);
		Assert.assertTrue(propertyMap.get("obsDatetime") instanceof DateProperty);
		Assert.assertTrue(propertyMap.get("accessionNumber") instanceof StringProperty);

		Property property = propertyMap.get("encounter");
		Assert.assertTrue(property instanceof RefProperty);
		RefProperty stringProperty = (RefProperty) property;
		assertEquals("#/definitions/EncounterGet", stringProperty.get$ref());
	}

	@Test
	public void generateGETModel_shouldReturnAnArrayPropertyWithRefPropertyWhenFieldIsASet() {
		Model model = SwaggerGenerationUtil.generateGETModel(new PersonResource1_8(), Representation.DEFAULT);
		Assert.assertTrue(model instanceof ModelImpl);

		Map<String, Property> propertyMap = model.getProperties();
		System.out.println(propertyMap);
		Assert.assertTrue(propertyMap.containsKey("attributes"));

		Property property = propertyMap.get("attributes");
		Assert.assertTrue(property instanceof ArrayProperty);
		ArrayProperty arrayProperty = (ArrayProperty) property;
		Assert.assertTrue(arrayProperty.getItems() instanceof RefProperty);

		RefProperty refProperty = (RefProperty) arrayProperty.getItems();
		assertEquals("#/definitions/PersonAttributeGet", refProperty.get$ref());
	}

	@Test
	public void generateGETModelPatient_shouldReturnAnArrayPropertyWithRefPropertyWhenFieldIsASet() {
		Model model = SwaggerGenerationUtil.generateGETModel(new PatientResource1_8(), Representation.DEFAULT);
		Assert.assertTrue(model instanceof ModelImpl);

		Map<String, Property> propertyMap = model.getProperties();
		System.out.println(propertyMap);
		Assert.assertTrue(propertyMap.containsKey("identifiers"));

		Property property = propertyMap.get("identifiers");
		Assert.assertTrue(property instanceof ArrayProperty);
		ArrayProperty arrayProperty = (ArrayProperty) property;
		Assert.assertTrue(arrayProperty.getItems() instanceof RefProperty);

		RefProperty refProperty = (RefProperty) arrayProperty.getItems();
		assertEquals("#/definitions/PatientIdentifierGet", refProperty.get$ref());
	}
	
	/**
	 * Ensure that resources not directly related to the webservices.rest package are successfully
	 * defined in the swagger documentation.
	 */
	@Test
	public void testUnrelatedResourceDefinitions() {
		// ensure the statics are false first
		UnrelatedGenericChildResource.getCreatableProperties = false;
		UnrelatedGenericChildResource.getUpdatableProperties = false;
		UnrelatedGenericChildResource.getRepresentationDescription = false;
		
		// make sure to reset the cache for multiple tests in the same run
		if (SwaggerSpecificationCreator.isCached()) {
			SwaggerSpecificationCreator.clearCache();
		}
		
		SwaggerSpecificationCreator ssc = new SwaggerSpecificationCreator();
		ssc.getJSON();
		
		// check our custom methods were called
		assertTrue(UnrelatedGenericChildResource.getCreatableProperties);
		assertTrue(UnrelatedGenericChildResource.getUpdatableProperties);
		assertTrue(UnrelatedGenericChildResource.getRepresentationDescription);
		
		// assert the definition is now in the swagger object
		Swagger swagger = ssc.getSwagger();
		assertTrue(swagger.getDefinitions().containsKey("UnrelatedGet"));
		assertTrue(swagger.getDefinitions().containsKey("UnrelatedUpdate"));
		assertTrue(swagger.getDefinitions().containsKey("UnrelatedCreate"));
	}
}
