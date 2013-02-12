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
 * Tests functionality of {@link VisitAttributeController}.
 */
public class VisitAttributeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void fakeTest() {
		
	}
	
//	private VisitService service;
//	
//	private VisitAttributeController controller;
//	
//	private MockHttpServletRequest request;
//	
//	private HttpServletResponse response;
//	
//	private static final String DATE_PATTERN = "yyyy-MM-dd";
//	
//	@Before
//	public void before() throws Exception {
//		executeDataSet(Rest19ExtTestConstants.TEST_DATASET);
//		this.service = Context.getVisitService();
//		this.controller = new VisitAttributeController();
//		this.request = new MockHttpServletRequest();
//		this.response = new MockHttpServletResponse();
//	}
//	
//	@Test
//	public void shouldGetAVisitAttribute() throws Exception {
//		Object result = controller.retrieve(Rest19ExtTestConstants.VISIT_UUID, Rest19ExtTestConstants.VISIT_ATTRIBUTE_UUID,
//		    request);
//		String rfc822Timezone = new SimpleDateFormat("Z").format(new Date());
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.VISIT_ATTRIBUTE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertEquals("2011-04-25T00:00:00.000" + rfc822Timezone, PropertyUtils.getProperty(result, "value"));
//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	@Test
//	public void shouldListAttributesForAVisitExcludingVoidedOnes() throws Exception {
//		SimpleObject result = controller.getAll(Rest19ExtTestConstants.VISIT_UUID, request, response);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(1, Util.getResultsSize(result));
//	}
//	
//	@Test
//	public void shouldListAllAttributesForAVisit() throws Exception {
//		request.addParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, "true");
//		SimpleObject result = controller.getAll(Rest19ExtTestConstants.VISIT_UUID, request, response);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(2, Util.getResultsSize(result));
//	}
//	
//	@Test
//	public void shouldAddAnAttributeToAVisit() throws Exception {
//		int before = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID).getAttributes().size();
//		String json = "{\"attributeType\":\"6770f6d6-7673-11e0-8f03-001e378eb67g\", \"value\":\"2012-08-25\"}";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		Object visitAttribute = controller.create(Rest19ExtTestConstants.VISIT_UUID, post, request, response);
//		Assert.assertNotNull(visitAttribute);
//		int after = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID).getAttributes().size();
//		Assert.assertEquals(before + 1, after);
//	}
//	
//	@Test
//	public void shouldEditAVisitAttribute() throws Exception {
//		final String newValue = "2012-05-05";
//		VisitAttribute va = service.getVisitAttributeByUuid(Rest19ExtTestConstants.VISIT_ATTRIBUTE_UUID);
//		Assert.assertFalse(new SimpleDateFormat(DATE_PATTERN).parse(newValue).equals(va.getValue()));
//		String json = "{ \"value\":\"2012-05-05\" }";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		
//		VisitAttribute visitAttribute = service.getVisitAttributeByUuid(Rest19ExtTestConstants.VISIT_ATTRIBUTE_UUID);
//		Assert.assertEquals("Audit Date", visitAttribute.getAttributeType().getName());
//		
//		controller.update(Rest19ExtTestConstants.VISIT_UUID, Rest19ExtTestConstants.VISIT_ATTRIBUTE_UUID, post, request,
//		    response);
//		
//		VisitAttribute updated = service.getVisitAttributeByUuid(Rest19ExtTestConstants.VISIT_ATTRIBUTE_UUID);
//		Assert.assertTrue(new SimpleDateFormat(DATE_PATTERN).parse(newValue).equals(updated.getValue()));
//	}
//	
//	@Test
//	public void shouldVoidAVisitAttribute() throws Exception {
//		VisitAttribute visitAttribute = service.getVisitAttributeByUuid(Rest19ExtTestConstants.VISIT_ATTRIBUTE_UUID);
//		Assert.assertFalse(visitAttribute.isVoided());
//		controller.delete(Rest19ExtTestConstants.VISIT_UUID, Rest19ExtTestConstants.VISIT_ATTRIBUTE_UUID, "unit test",
//		    request, response);
//		visitAttribute = service.getVisitAttributeByUuid(Rest19ExtTestConstants.VISIT_ATTRIBUTE_UUID);
//		Assert.assertTrue(visitAttribute.isVoided());
//		Assert.assertEquals("unit test", visitAttribute.getVoidReason());
//	}
}
