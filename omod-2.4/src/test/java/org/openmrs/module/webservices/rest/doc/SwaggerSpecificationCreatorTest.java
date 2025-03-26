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

import io.swagger.converter.ModelConverters;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class SwaggerSpecificationCreatorTest extends BaseModuleWebContextSensitiveTest {

	private SwaggerSpecificationCreator swaggerCreator;

	Map<String, Integer> beforeCounts;

	@Before
	public void setUp() throws Exception {
		Context.getService(RestService.class).initialize();
		Context.getAdministrationService().saveGlobalProperty(
				new GlobalProperty(RestConstants.SWAGGER_QUIET_DOCS_GLOBAL_PROPERTY_NAME, "true"));
		Context.flushSession();
		swaggerCreator = new SwaggerSpecificationCreator();
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

	@Test
	public void getJSON_shouldGenerateSwaggerJSON() {
		assertNotNull(swaggerCreator.getJSON());
	}

	@Test
	public void hasSearchHandler_shouldCheckSearchHandlerAvailability() {
		assertTrue(swaggerCreator.hasSearchHandler("attribute", "location"));
		assertFalse(swaggerCreator.hasSearchHandler("workflow", null));
	}

	@Test
	public void isCached_shouldCacheSwaggerSpecification() {
		SwaggerSpecificationCreator.clearCache();
		assertFalse(SwaggerSpecificationCreator.isCached());
		swaggerCreator.getJSON();
		assertTrue(SwaggerSpecificationCreator.isCached());
	}

	@Test
	public void serializeSwagger_shouldSerializeSwagger() {
		Swagger swagger = new Swagger()
				.info(new Info().version("1.0.0").title("Swagger API"))
				.securityDefinition("basicAuth", new BasicAuthDefinition())
				.scheme(Scheme.HTTP)
				.consumes("application/json")
				.produces("application/json");

		swagger.addDefinition("Patient", ModelConverters.getInstance().read(Patient.class).get("Patient"));
		assertNotNull(Json.pretty(swagger));
	}

	@Test
	public void getOperationIds_shouldBeUnique() {
		swaggerCreator.getJSON();
		Set<String> operationIds = new HashSet<>();
		for (Path p : swaggerCreator.getSwagger().getPaths().values()) {
			for (Operation o : p.getOperations()) {
				assertTrue("Duplicate operationId found: " + o.getOperationId(), operationIds.add(o.getOperationId()));
			}
		}
	}

	@Test
	public void getOperations_shouldHaveRepresentationParam() {
		swaggerCreator.getJSON();
		for (Path path : swaggerCreator.getSwagger().getPaths().values()) {
			if (path.getGet() != null) {
				assertTrue(path.getGet().getParameters().stream().anyMatch(p -> "v".equals(p.getName())));
			}
		}
	}

	@Test
	public void getAllOperations_shouldHavePagingParameters() {
		swaggerCreator.getJSON();
		for (Path path : swaggerCreator.getSwagger().getPaths().values()) {
			for (Operation op : path.getOperations()) {
				if (op.getOperationId().startsWith("getAll")) {
					List<String> paramNames = new ArrayList<>();
					for (Parameter param : op.getParameters()) {
						paramNames.add(param.getName());
					}
					assertTrue(paramNames.containsAll(Arrays.asList("limit", "startIndex")));
				}
			}
		}
	}

	@Test
	public void addPaths_shouldWorkForCoreModels() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
		Field swagger = swaggerCreator.getClass().getDeclaredField("swagger");
		swagger.setAccessible(true);
		swagger.set(swaggerCreator, new Swagger());

		Method initSwagger = swaggerCreator.getClass().getDeclaredMethod("initSwagger");
		initSwagger.setAccessible(true);
		initSwagger.invoke(swaggerCreator);

		Method addPaths = swaggerCreator.getClass().getDeclaredMethod("addPaths");
		addPaths.setAccessible(true);
		addPaths.invoke(swaggerCreator);
	}

	/**
	 * Ensure that resources not directly related to the webservices.rest package are successfully
	 * defined in the swagger documentation.
	 */
	@Test
	public void testUnrelatedResourceDefinitions_shouldBeDefined() {
		UnrelatedGenericChildResource.getGETCalled = false;
		UnrelatedGenericChildResource.getCREATECalled = false;
		UnrelatedGenericChildResource.getUPDATECalled = false;

		if (SwaggerSpecificationCreator.isCached()) {
			SwaggerSpecificationCreator.clearCache();
		}

		swaggerCreator.getJSON();

		assertTrue(UnrelatedGenericChildResource.getGETCalled);
		assertTrue(UnrelatedGenericChildResource.getCREATECalled);
		assertTrue(UnrelatedGenericChildResource.getUPDATECalled);

		Swagger swagger = swaggerCreator.getSwagger();
		assertTrue(swagger.getDefinitions().containsKey("UnrelatedGet"));
		assertTrue(swagger.getDefinitions().containsKey("UnrelatedUpdate"));
		assertTrue(swagger.getDefinitions().containsKey("UnrelatedCreate"));
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

	public Map<String, Integer> getRowCounts() throws Exception {
		Map<String, Integer> ret = new HashMap<>();

		Connection con = this.getConnection();
		DatabaseMetaData metaData = con.getMetaData();
		DatabaseConnection databaseConnection = new DatabaseConnection(con);

		ResultSet rs = metaData.getTables(null, "PUBLIC", "%", null);
		while (rs.next()) {
			String tableName = rs.getString(3);

			ret.put(tableName, databaseConnection.getRowCount(tableName));
		}
		return ret;
	}
}
