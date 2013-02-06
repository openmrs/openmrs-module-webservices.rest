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
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.ProviderAttribute;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Rest19ExtTestConstants;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link ProviderAttributeController}.
 */
public class ProviderAttributeControllerTest extends BaseModuleWebContextSensitiveTest {
	
//	private ProviderService service;
//	
//	private ProviderAttributeController controller;
//	
//	private MockHttpServletRequest request;
//	
//	private HttpServletResponse response;
//	
//	@Before
//	public void before() throws Exception {
//		executeDataSet(Rest19ExtTestConstants.TEST_DATASET);
//		this.service = Context.getProviderService();
//		this.controller = new ProviderAttributeController();
//		this.request = new MockHttpServletRequest();
//		this.response = new MockHttpServletResponse();
//	}
//	
//	@Test
//	public void shouldGetAProviderAttribute() throws Exception {
//		Object result = controller.retrieve(Rest19ExtTestConstants.PROVIDER_UUID,
//		    Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_UUID, request);
//		String rfc822Timezone = new SimpleDateFormat("Z").format(new Date());
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertEquals("2011-04-25T00:00:00.000" + rfc822Timezone, PropertyUtils.getProperty(result, "value"));
//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	@Test
//	public void shouldListAttributesForProvider() throws Exception {
//		SimpleObject result = controller.getAll(Rest19ExtTestConstants.PROVIDER_UUID, request, response);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(2, Util.getResultsSize(result));
//	}
//	
//	@Test
//	public void shouldAddAttributeToProvider() throws Exception {
//		int before = service.getProviderByUuid(Rest19ExtTestConstants.PROVIDER_UUID).getAttributes().size();
//		String json = "{\"attributeType\":\"" + Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID
//		        + "\", \"value\":\"2012-05-05\"}";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		controller.create(Rest19ExtTestConstants.PROVIDER_UUID, post, request, response);
//		int after = service.getProviderByUuid(Rest19ExtTestConstants.PROVIDER_UUID).getAttributes().size();
//		Assert.assertEquals(before + 1, after);
//	}
//	
//	@Test
//	public void shouldEditProviderAttribute() throws Exception {
//		String json = "{ \"attributeType\":\"9516cc50-n8ik-bc4f-8dw4-001e378eb67e\" }";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		
//		ProviderAttribute providerAttribute = service
//		        .getProviderAttributeByUuid(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_UUID);
//		Assert.assertEquals("Joining Date", providerAttribute.getAttributeType().getName());
//		
//		controller.update(Rest19ExtTestConstants.PROVIDER_UUID, Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_UUID, post,
//		    request, response);
//		
//		providerAttribute = service.getProviderAttributeByUuid(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_UUID);
//		Assert.assertEquals("Leave Date", providerAttribute.getAttributeType().getName());
//	}
//	
//	@Test
//	public void shouldVoidAttribute() throws Exception {
//		ProviderAttribute providerAttribute = service
//		        .getProviderAttributeByUuid(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_UUID);
//		Assert.assertFalse(providerAttribute.isVoided());
//		controller.delete(Rest19ExtTestConstants.PROVIDER_UUID, Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_UUID, "unit test",
//		    request, response);
//		providerAttribute = service.getProviderAttributeByUuid(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_UUID);
//		Assert.assertTrue(providerAttribute.isVoided());
//		Assert.assertEquals("unit test", providerAttribute.getVoidReason());
//	}
//	
//	@Test
//	@Ignore
//	public void shouldPurgeAttribute() throws Exception {
//		// TODO: TEST IGNORED AS PURGING LOCATIONATTRIBUTE IS NOT POSSIBLE		
//	}
	
	@Test
	public void fakeTest() {
		
	}
}
