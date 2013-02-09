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
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Rest19ExtTestConstants;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

public class ProviderAttributeTypeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private MockHttpServletRequest emptyRequest() {
		return new MockHttpServletRequest();
	}
	
	@Before
	public void before() throws Exception {
		executeDataSet(Rest19ExtTestConstants.TEST_DATASET);
	}
	
//	/**
//	 * @see
//	 * ProviderAttributeTypeController#createProviderAttributeType(SimpleObject,WebRequest)
//	 * @verifies create a new ProviderAttributeType
//	 */
//	@Test
//	public void createProviderAttributeType_shouldCreateANewProviderAttributeType() throws Exception {
//		int before = Context.getProviderService().getAllProviderAttributeTypes().size();
//		String json = "{ \"name\":\"Some attributeType\",\"description\":\"Attribute Type for provider\",\"datatypeClassname\":\"org.openmrs.customdatatype.datatype.FreeTextDatatype\"}";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		Object providerAttributeType = new ProviderAttributeTypeController().create(post, emptyRequest(),
//		    new MockHttpServletResponse());
//		Util.log("Created provider attribute type", providerAttributeType);
//		Assert.assertEquals(before + 1, Context.getProviderService().getAllProviderAttributeTypes().size());
//	}
//	
//	/**
//	 * @see
//	 * ProviderAttributeTypeController#getProviderAttributeType(ProviderAttributeType,WebRequest)
//	 * @verifies get a default representation of a provider attribute type
//	 */
//	@Test
//	public void getProviderAttributeType_shouldGetADefaultRepresentationOfAProviderAttributeType() throws Exception {
//		Object result = new ProviderAttributeTypeController().retrieve(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID,
//		    emptyRequest());
//		Assert.assertNotNull(result);
//		Util.log("ProviderAttributeType fetched (default)", result);
//		Assert.assertEquals(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "name"));
//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	/**
//	 * @see
//	 * ProviderAttributeTypeController#getProviderAttributeType(String,WebRequest)
//	 * @verifies get a full representation of a provider attribute type
//	 */
//	@Test
//	public void getProviderAttributeType_shouldGetAFullRepresentationOfAProviderAttributeType() throws Exception {
//		MockHttpServletRequest req = new MockHttpServletRequest();
//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
//		Object result = new ProviderAttributeTypeController().retrieve(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID,
//		    req);
//		Util.log("ProviderAttributeType fetched (full)", result);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	/**
//	 * @see
//	 * ProviderAttributeTypeController#updateProviderAttributeType(ProviderAttributeType,SimpleObject,WebRequest)
//	 * @verifies change a property on a provider
//	 */
//	@Test
//	public void updateProviderAttributeType_shouldChangeAPropertyOnAProviderAttributeType() throws Exception {
//		SimpleObject post = new ObjectMapper().readValue("{\"description\":\"Updated description\"}", SimpleObject.class);
//		Object editedProviderAttributeType = new ProviderAttributeTypeController().update(
//		    Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID, post, emptyRequest(), new MockHttpServletResponse());
//		Util.log("Edited provider", editedProviderAttributeType);
//		Assert
//		        .assertEquals("Updated description", Context.getProviderService().getProviderAttributeType(1)
//		                .getDescription());
//	}
//	
//	/**
//	 * @see
//	 * ProviderAttributeTypeController#retireProviderAttributeType(ProviderAttributeType,String,WebRequest)
//	 * @verifies void a provider attribute type
//	 */
//	@Test
//	public void retireProviderAttributeType_shouldRetireAProviderAttributeType() throws Exception {
//		ProviderAttributeType providerAttributeType = Context.getProviderService().getProviderAttributeType(1);
//		Assert.assertFalse(providerAttributeType.isRetired());
//		new ProviderAttributeTypeController().delete(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID, "test",
//		    emptyRequest(), new MockHttpServletResponse());
//		providerAttributeType = Context.getProviderService().getProviderAttributeType(1);
//		Assert.assertTrue(providerAttributeType.isRetired());
//		Assert.assertEquals("test", providerAttributeType.getRetireReason());
//	}
//	
//	/**
//	 * @see
//	 * ProviderAttributeTypeController#findProviderAttributeTypes(String,WebRequest,HttpServletResponse)
//	 * @verifies return no results if there are no matching provider(s)
//	 */
//	@Test
//	public void findProviderAttributeTypes_shouldReturnNoResultsIfThereAreNoMatchingProviders() throws Exception {
//		List<Object> results = (List<Object>) new ProviderAttributeTypeController().search("zzzznotype", emptyRequest(),
//		    new MockHttpServletResponse()).get("results");
//		Assert.assertEquals(0, results.size());
//	}
//	
//	/**
//	 * @see
//	 * ProviderAttributeTypeController#findProviderAttributeTypes(String,WebRequest,HttpServletResponse)
//	 * @verifies find matching provider attribute types
//	 */
//	@Test
//	public void findProviderAttributeTypes_shouldFindMatchingProviderAttributeTypes() throws Exception {
//		List<Object> results = (List<Object>) new ProviderAttributeTypeController().search("Joining", emptyRequest(),
//		    new MockHttpServletResponse()).get("results");
//		Assert.assertEquals(1, results.size());
//		Util.log("Found " + results.size() + " ProviderAttributeType(s)", results);
//		Object result = results.get(0);
//		Assert.assertEquals(Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
//	}
	
	@Test
	public void fakeTest() {
		
	}
}
