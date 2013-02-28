/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;

/**
 * Contains tests for the {@link VisitTypeController}
 */
public class VisitTypeController1_9Test extends BaseCrudControllerTest {
	
	private VisitService service;

	/**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
     */
    @Override
    public String getURI() {
	    return "visittype";
    }

	/**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
	    return RestTestConstants1_9.VISIT_TYPE_UUID;
    }

	/**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
    	int count = 0;
    	for (VisitType type : service.getAllVisitTypes()) {
	        if (!type.isRetired()) {
	        	count++;
	        }
        }
    	
	    return count;
    }
	
	@Before
	public void before() {
		this.service = Context.getVisitService();
	}
	
	@Test
	public void shouldGetAVisitTypeByName() throws Exception {		
		Object result = deserialize(handle(newGetRequest(getURI() + "/Return TB Clinic Visit")));
		Assert.assertNotNull(result);
		Assert.assertEquals(RestTestConstants1_9.VISIT_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("Return TB Clinic Visit", PropertyUtils.getProperty(result, "name"));
	}
	
	
	@Test
	public void shouldCreateAVisitType() throws Exception {
		int originalCount = service.getAllVisitTypes().size();
		String json = "{ \"name\":\"test visitType\", \"description\":\"description\" }";
		Object newVisitType = deserialize(handle(newPostRequest(getURI(), json)));
		Assert.assertNotNull(PropertyUtils.getProperty(newVisitType, "uuid"));
		Assert.assertEquals(originalCount + 1, service.getAllVisitTypes().size());
	}
	
	@Test
	public void shouldEditAVisitType() throws Exception {
		String json = "{ \"name\":\"new visit type\", \"description\":\"new description\" }";
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		VisitType updated = service.getVisitTypeByUuid(RestTestConstants1_9.VISIT_TYPE_UUID);
		Assert.assertNotNull(updated);
		Assert.assertEquals("new visit type", updated.getName());
		Assert.assertEquals("new description", updated.getDescription());
	}
	
	@Test
	public void shouldRetireAVisitType() throws Exception {
		VisitType visitType = service.getVisitTypeByUuid(RestTestConstants1_9.VISIT_TYPE_UUID);
		Assert.assertFalse(visitType.isRetired());
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", "test reason")));
		visitType = service.getVisitTypeByUuid(RestTestConstants1_9.VISIT_TYPE_UUID);
		Assert.assertTrue(visitType.isRetired());
		Assert.assertEquals("test reason", visitType.getRetireReason());
	}
	
	@Test
	public void shouldPurgeAVisitType() throws Exception {
		String uuid = "759799ab-c9a5-435e-b671-77773ada74e6";
		Assert.assertNotNull(service.getVisitTypeByUuid(uuid));
		int originalCount = service.getAllVisitTypes().size();
		handle(newDeleteRequest(getURI() + "/" + uuid, new Parameter("purge", "")));
		Assert.assertNull(service.getVisitTypeByUuid(uuid));
		Assert.assertEquals(originalCount - 1, service.getAllVisitTypes().size());
	}
	
	@Test
	public void shouldSearchAndReturnAListOfVisitTypesMatchingTheQueryString() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "Ret"))));
		List<Object> hits = Util.getResultsList(result);
		Assert.assertEquals(1, hits.size());
		Assert.assertEquals(RestTestConstants1_9.VISIT_TYPE_UUID, PropertyUtils.getProperty(hits.get(0), "uuid"));
		
	}
	
	@Test
	public void shouldSearchAndReturnAListOfVisitTypesMatchingTheQueryStringExcludingRetiredOnes() throws Exception {
		final String searchString = "Hos";
		//sanity check
		Assert.assertEquals(1, Context.getVisitService().getVisitTypes(searchString).size());
		
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("q", searchString))));
		List<Object> hits = Util.getResultsList(result);
		Assert.assertEquals(0, hits.size());
		
	}

}
