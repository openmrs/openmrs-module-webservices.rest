/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Contains tests for {@link VisitAttributeTypeController} CRUD operations
 */
public class VisitAttributeTypeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void fakeTest() {
		
	}
	
//	private VisitService service;
//	
//	private VisitAttributeTypeController controller;
//	
//	private MockHttpServletRequest emptyRequest() {
//		return new MockHttpServletRequest();
//	}
//	
//	private MockHttpServletResponse emptyResponse() {
//		return new MockHttpServletResponse();
//	}
//	
//	@Before
//	public void before() throws Exception {
//		service = Context.getVisitService();
//		controller = new VisitAttributeTypeController();
//		executeDataSet(Rest19ExtTestConstants.TEST_DATASET);
//	}
//	
//	/**
//	 * @see VisitAttributeTypeController#create(SimpleObject, javax.servlet.http.HttpServletRequest,
//	 *      HttpServletResponse)
//	 */
//	@Test
//	public void create_shouldCreateANewVisitAttributeType() throws Exception {
//		int before = service.getAllVisitAttributeTypes().size();
//		String json = "{ \"name\":\"Some attributeType\",\"description\":\"Attribute Type for visit\",\"datatypeClassname\":\"org.openmrs.customdatatype.datatype.FreeTextDatatype\"}";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		Object visitAttributeType = controller.create(post, emptyRequest(), emptyResponse());
//		Assert.assertNotNull(visitAttributeType);
//		Assert.assertEquals(before + 1, service.getAllVisitAttributeTypes().size());
//	}
//	
//	/**
//	 * @see VisitAttributeTypeController#retrieve(String, javax.servlet.http.HttpServletRequest)
//	 */
//	@Test
//	public void retrieve_shouldGetADefaultRepresentationOfAVisitAttributeType() throws Exception {
//		Object result = controller.retrieve(Rest19ExtTestConstants.VISIT_ATTRIBUTE_TYPE_UUID, emptyRequest());
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.VISIT_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "name"));
//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	/**
//	 * @see VisitAttributeTypeController#retrieve(String, javax.servlet.http.HttpServletRequest)
//	 */
//	@Test
//	public void retrieve_shouldGetAFullRepresentationOfAVisitAttributeType() throws Exception {
//		MockHttpServletRequest req = new MockHttpServletRequest();
//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
//		Object result = controller.retrieve(Rest19ExtTestConstants.VISIT_ATTRIBUTE_TYPE_UUID, req);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.VISIT_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	/**
//	 * @see VisitAttributeTypeController#update(String, SimpleObject,
//	 *      javax.servlet.http.HttpServletRequest, HttpServletResponse)
//	 */
//	@Test
//	public void update_shouldChangeAPropertyOnAVisitAttributeType() throws Exception {
//		SimpleObject post = new ObjectMapper().readValue("{\"description\":\"Updated description\"}", SimpleObject.class);
//		controller.update(Rest19ExtTestConstants.VISIT_ATTRIBUTE_TYPE_UUID, post, emptyRequest(), emptyResponse());
//		Assert.assertEquals("Updated description", service.getVisitAttributeType(1).getDescription());
//	}
//	
//	/**
//	 * @see VisitAttributeTypeController#delete(String, String,
//	 *      javax.servlet.http.HttpServletRequest, HttpServletResponse)
//	 */
//	@Test
//	public void delete_shouldRetireAVisitAttributeType() throws Exception {
//		VisitAttributeType visitAttributeType = service.getVisitAttributeType(1);
//		Assert.assertFalse(visitAttributeType.isRetired());
//		controller.delete(Rest19ExtTestConstants.VISIT_ATTRIBUTE_TYPE_UUID, "test", emptyRequest(), emptyResponse());
//		visitAttributeType = service.getVisitAttributeType(1);
//		Assert.assertTrue(visitAttributeType.isRetired());
//		Assert.assertEquals("test", visitAttributeType.getRetireReason());
//	}
//	
//	/**
//	 * @see VisitAttributeTypeController#getAll(javax.servlet.http.HttpServletRequest,
//	 *      HttpServletResponse)
//	 */
//	@Test
//	public void getAll_shouldGellVisitAttributeTypesExcludingRetiredOnes() throws Exception {
//		Assert.assertEquals(3, Util.getResultsSize(controller.getAll(emptyRequest(), emptyResponse())));
//	}
//	
//	/**
//	 * @see VisitAttributeTypeController#getAll(javax.servlet.http.HttpServletRequest,
//	 *      HttpServletResponse)
//	 */
//	@Test
//	public void getAll_shouldGellVisitAttributeTypesIfIncludeAllIsSetToTrue() throws Exception {
//		MockHttpServletRequest request = emptyRequest();
//		request.addParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, "true");
//		Assert.assertEquals(4, Util.getResultsSize(controller.getAll(request, emptyResponse())));
//	}
//	
//	/**
//	 * @see VisitAttributeTypeController#search(String, javax.servlet.http.HttpServletRequest,
//	 *      HttpServletResponse)
//	 */
//	@Test
//	public void search_shouldFindMatchingVisitAttributeTypesExcludingRetiredOnes() throws Exception {
//		Assert.assertEquals(2, Util.getResultsSize(controller.search("date", emptyRequest(), emptyResponse())));
//	}
//	
//	/**
//	 * @see VisitAttributeTypeController#search(String, javax.servlet.http.HttpServletRequest,
//	 *      HttpServletResponse)
//	 */
//	@Test
//	public void search_shouldFindAllMatchingVisitAttributeTypesIfIncludeAllIsSetToTrue() throws Exception {
//		MockHttpServletRequest request = emptyRequest();
//		request.addParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, "true");
//		Assert.assertEquals(3, Util.getResultsSize(controller.search("date", request, emptyResponse())));
//	}
//	
//	/**
//	 * @See {@link VisitAttributeTypeController#purge(String, javax.servlet.http.HttpServletRequest, HttpServletResponse)}
//	 */
//	@Test
//	public void purge_shouldPurgeAVisitAttributeType() throws Exception {
//		final String visitAttributeTypeUuid = "6770f6d6-7673-11e0-8f03-001e378eb67g";
//		Assert.assertNotNull(service.getVisitAttributeTypeByUuid(visitAttributeTypeUuid));
//		int originalCount = service.getAllVisitAttributeTypes().size();
//		controller.purge(visitAttributeTypeUuid, emptyRequest(), emptyResponse());
//		Assert.assertNull(service.getVisitAttributeTypeByUuid(visitAttributeTypeUuid));
//		Assert.assertEquals(originalCount - 1, service.getAllVisitAttributeTypes().size());
//	}
}
