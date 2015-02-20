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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_10;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.OrderFrequency;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_10;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of
 * {@link org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.OrderFrequencyResource1_10}
 * . This does not use @should annotations because the controller inherits those methods from a
 * subclass
 */
public class OrderFrequencyController1_10Test extends MainResourceControllerTest {
	
	private OrderService service;
	
	@Before
	public void before() {
		this.service = Context.getOrderService();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "orderfrequency";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_10.ORDER_FREQUENCY_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getOrderFrequencies(false).size();
	}
	
	@Test
	public void shouldGetAnOrderFrequencyByUuid() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		
		OrderFrequency expectedOrderFrequency = service.getOrderFrequencyByUuid(getUuid());
		assertEquals(expectedOrderFrequency.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertEquals(expectedOrderFrequency.getName(), PropertyUtils.getProperty(result, "name"));
		assertEquals(expectedOrderFrequency.getFrequencyPerDay(), PropertyUtils.getProperty(result, "frequencyPerDay"));
		assertNotNull(PropertyUtils.getProperty(result, "concept"));
		assertEquals(expectedOrderFrequency.getDescription(), PropertyUtils.getProperty(result, "description"));
		assertEquals(expectedOrderFrequency.isRetired(), PropertyUtils.getProperty(result, "retired"));
		assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldListAllOrderFrequencys() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI())));
		
		assertNotNull(result);
		assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	@Test
	public void shouldListAllOrderFrequencysIncludingRetiredOnesIfIncludeAllIsSetToTrue() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("includeAll", "true"))));
		
		assertNotNull(result);
		assertEquals(service.getOrderFrequencies(true).size(), Util.getResultsSize(result));
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		
		assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldSearchAndReturnAListOfOrderFrequencysMatchingTheQueryString() throws Exception {
		executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrderFrequencies.xml");
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "Twice A Day");
		SimpleObject result = deserialize(handle(req));
		assertEquals(1, Util.getResultsSize(result));
		assertEquals("78090760-7c39-11e3-baa7-0800200c9a66",
		    PropertyUtils.getProperty(Util.getResultsList(result).get(0), "uuid"));
		
		req.removeAllParameters();
		req.addParameter("q", "ce");
		result = deserialize(handle(req));
		assertEquals(3, Util.getResultsSize(result));
		List<String> uuids = Arrays.asList(new String[] {
		        PropertyUtils.getProperty(Util.getResultsList(result).get(0), "uuid").toString(),
		        PropertyUtils.getProperty(Util.getResultsList(result).get(1), "uuid").toString(),
		        PropertyUtils.getProperty(Util.getResultsList(result).get(2), "uuid").toString() });
		
		String[] expectedUuids = new String[] { "68090760-7c39-11e3-baa7-0800200c9a66",
		        "78090760-7c39-11e3-baa7-0800200c9a66", "88090760-7c39-11e3-baa7-0800200c9a66" };
		assertThat(uuids, hasItems(expectedUuids));
		
		//should include retired ones if includeAll is set to true
		req.removeAllParameters();
		req.addParameter("q", "ce");
		req.addParameter("includeAll", "true");
		result = deserialize(handle(req));
		assertEquals(4, Util.getResultsSize(result));
		uuids = Arrays.asList(new String[] {
		        PropertyUtils.getProperty(Util.getResultsList(result).get(0), "uuid").toString(),
		        PropertyUtils.getProperty(Util.getResultsList(result).get(1), "uuid").toString(),
		        PropertyUtils.getProperty(Util.getResultsList(result).get(2), "uuid").toString(),
		        PropertyUtils.getProperty(Util.getResultsList(result).get(3), "uuid").toString() });
		
		expectedUuids = new String[] { "68090760-7c39-11e3-baa7-0800200c9a66", "78090760-7c39-11e3-baa7-0800200c9a66",
		        "88090760-7c39-11e3-baa7-0800200c9a66", "99090760-7c39-11e3-baa7-0800200c9a66" };
		assertThat(uuids, hasItems(expectedUuids));
	}
	
}
