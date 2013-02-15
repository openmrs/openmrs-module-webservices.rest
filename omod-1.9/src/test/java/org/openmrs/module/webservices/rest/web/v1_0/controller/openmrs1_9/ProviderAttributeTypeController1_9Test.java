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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Rest1_9TestConstants;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

public class ProviderAttributeTypeController1_9Test extends BaseCrudControllerTest {
	
	@Before
	public void before() throws Exception {
		executeDataSet(Rest1_9TestConstants.TEST_DATASET);
	}
	
	/**
	 * @see ProviderAttributeTypeController#createProviderAttributeType(SimpleObject,WebRequest)
	 * @verifies create a new ProviderAttributeType
	 */
	@Test
	public void createProviderAttributeType_shouldCreateANewProviderAttributeType() throws Exception {
		int before = Context.getProviderService().getAllProviderAttributeTypes().size();
		String json = "{ \"name\":\"Some attributeType\",\"description\":\"Attribute Type for provider\",\"datatypeClassname\":\"org.openmrs.customdatatype.datatype.FreeTextDatatype\"}";
		
		handle(newPostRequest(getURI(), json));
		
		Assert.assertEquals(before + 1, Context.getProviderService().getAllProviderAttributeTypes().size());
	}
	
	/**
	 * @see ProviderAttributeTypeController#updateProviderAttributeType(ProviderAttributeType,SimpleObject,WebRequest)
	 * @verifies change a property on a provider
	 */
	@Test
	public void updateProviderAttributeType_shouldChangeAPropertyOnAProviderAttributeType() throws Exception {
		String json = "{\"description\":\"Updated description\"}";
		handle(newPostRequest(getURI() + "/" + Rest1_9TestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID, json));
		
		Assert.assertEquals("Updated description", Context.getProviderService().getProviderAttributeType(1).getDescription());
	}
	
	/**
	 * @see ProviderAttributeTypeController#retireProviderAttributeType(ProviderAttributeType,String,WebRequest)
	 * @verifies void a provider attribute type
	 */
	@Test
	public void retireProviderAttributeType_shouldRetireAProviderAttributeType() throws Exception {
		ProviderAttributeType providerAttributeType = Context.getProviderService().getProviderAttributeType(1);
		Assert.assertFalse(providerAttributeType.isRetired());
		
		MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		request.addParameter("reason", "test");
		
		handle(request);
		
		providerAttributeType = Context.getProviderService().getProviderAttributeType(1);
		Assert.assertTrue(providerAttributeType.isRetired());
		Assert.assertEquals("test", providerAttributeType.getRetireReason());
	}
	
	/**
	 * @see ProviderAttributeTypeController#findProviderAttributeTypes(String,WebRequest,HttpServletResponse)
	 * @verifies return no results if there are no matching provider(s)
	 */
	@Test
	public void findProviderAttributeTypes_shouldReturnNoResultsIfThereAreNoMatchingProviders() throws Exception {
		MockHttpServletRequest request = newGetRequest(getURI());
		request.addParameter("q", "zzzznotype");
		
		SimpleObject result = deserialize(handle(request));
		Assert.assertEquals(0, Util.getResultsSize(result));
	}
	
	/**
	 * @see ProviderAttributeTypeController#findProviderAttributeTypes(String,WebRequest,HttpServletResponse)
	 * @verifies find matching provider attribute types
	 */
	@Test
	public void findProviderAttributeTypes_shouldFindMatchingProviderAttributeTypes() throws Exception {
		MockHttpServletRequest request = newGetRequest(getURI());
		request.addParameter("q", "Joining");
		
		SimpleObject response = deserialize(handle(request));
		Assert.assertEquals(1, Util.getResultsSize(response));
		
        List<Object> results = Util.getResultsList(response);
        Object result = results.get(0);
		
		Assert.assertEquals(Rest1_9TestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "providerattributetype";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return Rest1_9TestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return 3;
	}
}
