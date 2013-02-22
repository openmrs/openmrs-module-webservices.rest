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

import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.VisitAttribute;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Rest1_9TestConstants;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;

/**
 * Tests functionality of {@link VisitAttributeController}.
 */
public class VisitAttributeController1_9Test extends BaseCrudControllerTest {
	
	private VisitService service;
	
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "visit/" + Rest1_9TestConstants.VISIT_UUID + "/attribute";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return Rest1_9TestConstants.VISIT_ATTRIBUTE_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID).getActiveAttributes().size();
	}
	
	@Before
	public void before() throws Exception {
		executeDataSet(Rest1_9TestConstants.TEST_DATASET);
		this.service = Context.getVisitService();
	}
	
	@Test
	public void shouldListAllAttributesForAVisit() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, "true"))));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(2, Util.getResultsSize(result));
	}
	
	@Test
	public void shouldAddAnAttributeToAVisit() throws Exception {
		int before = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID).getAttributes().size();
		String json = "{\"attributeType\":\"6770f6d6-7673-11e0-8f03-001e378eb67g\", \"value\":\"2012-08-25\"}";
		
		handle(newPostRequest(getURI(), json));
		
		int after = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID).getAttributes().size();
		Assert.assertEquals(before + 1, after);
	}
	
	@Test
	public void shouldEditAVisitAttribute() throws Exception {
		final String newValue = "2012-05-05";
		VisitAttribute va = service.getVisitAttributeByUuid(Rest1_9TestConstants.VISIT_ATTRIBUTE_UUID);
		Assert.assertFalse(new SimpleDateFormat(DATE_PATTERN).parse(newValue).equals(va.getValue()));
		String json = "{ \"value\":\"2012-05-05\" }";
		
		VisitAttribute visitAttribute = service.getVisitAttributeByUuid(Rest1_9TestConstants.VISIT_ATTRIBUTE_UUID);
		Assert.assertEquals("Audit Date", visitAttribute.getAttributeType().getName());
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		VisitAttribute updated = service.getVisitAttributeByUuid(Rest1_9TestConstants.VISIT_ATTRIBUTE_UUID);
		Assert.assertTrue(new SimpleDateFormat(DATE_PATTERN).parse(newValue).equals(updated.getValue()));
	}
	
	@Test
	public void shouldVoidAVisitAttribute() throws Exception {
		VisitAttribute visitAttribute = service.getVisitAttributeByUuid(Rest1_9TestConstants.VISIT_ATTRIBUTE_UUID);
		Assert.assertFalse(visitAttribute.isVoided());
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", "unit test")));
		
		visitAttribute = service.getVisitAttributeByUuid(Rest1_9TestConstants.VISIT_ATTRIBUTE_UUID);
		Assert.assertTrue(visitAttribute.isVoided());
		Assert.assertEquals("unit test", visitAttribute.getVoidReason());
	}
}
