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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7InQueue;
import org.openmrs.hl7.HL7Service;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Tests functionality of {@link HL7MessageController}.
 */
public class HL7MessageControllerTest extends BaseCrudControllerTest {
	
	private static final String hl7Data = "MSH|^~\\&|NES|AMRS.ELD|TESTSYSTEM|TESTFACILITY|20010101000000||ADT^A04|REl7wt78q9Pzlqe9ecJB|P|2.3";
	
	private static final String hl7InvalidSourceData = "MSH|^~\\&|NES|nonexistingsource|TESTSYSTEM|TESTFACILITY|20010101000000||ADT^A04|REl7wt78q9Pzlqe9ecJB|P|2.3";
	
	private HL7Service service;
	
	private static final String datasetFilename = "customTestDataset.xml";
	
	@Before
	public void before() throws Exception {
		this.service = Context.getHL7Service();
		executeDataSet(datasetFilename);
	}
	
	@Override
	public String getURI() {
		return "hl7";
	}

	@Override
	public String getUuid() {
		return null;
	}

	@Override
	public long getAllCount() {
		return service.getAllHL7InQueues().size();
	}
	
	@Test
	public void enqueHl7Message_shouldEnqueueHl7InQueueMessageInPlainFormat() throws Exception {
		int before = service.getAllHL7InQueues().size();
					
		SimpleObject hl7Message = new SimpleObject();
		hl7Message.add("data", hl7Data);
		
		MockHttpServletRequest req = newPostRequest(getURI(), hl7Message);
		SimpleObject newHl7Message = deserialize(handle(req));
		
		Util.log("Enqued hl7 message", newHl7Message);
		
		Assert.assertEquals(before + 1, service.getAllHL7InQueues().size());
		for (HL7InQueue hl7InQueue : service.getAllHL7InQueues()) {
			if (hl7InQueue.getUuid().equals(newHl7Message.get("uuid"))) {
				Assert.assertEquals(hl7InQueue.getHL7Data(), hl7Data);
			}
		}
	}
			
	@Test
	public void enqueHl7Message_shouldEnqueueHl7InQueueMessageInJSONFormat() throws Exception {
		int before = service.getAllHL7InQueues().size();
		
		SimpleObject hl7Message = new SimpleObject();
		hl7Message.add("data", hl7Data);
		String jsonHl7Data = new ObjectMapper().writeValueAsString(hl7Message);
		
		MockHttpServletRequest req = newPostRequest(getURI(), jsonHl7Data);
		SimpleObject newHl7Message = deserialize(handle(req));
		Util.log("Enqued hl7 message", newHl7Message);
		
		Assert.assertEquals(before + 1, service.getAllHL7InQueues().size());
		for (HL7InQueue hl7InQueue : service.getAllHL7InQueues()) {
			if (hl7InQueue.getUuid().equals(newHl7Message.get("uuid"))) {
				Assert.assertEquals(hl7InQueue.getHL7Data(), hl7Data);
			}
		}
	}
	
	@Test(expected = ConversionException.class)
	@Ignore
	public void enqueHl7Message_shouldFailIfSourceDoesNotExist() throws Exception {
		
		SimpleObject hl7Message = new SimpleObject();
		hl7Message.add("data", hl7InvalidSourceData);
		
		MockHttpServletRequest req = newPostRequest(getURI(), hl7Message);
		deserialize(handle(req));

	}
	
	@Test
	@Ignore
	@Override
	public void shouldGetDefaultByUuid() throws Exception {
	}
	
	@Test
	@Ignore
	@Override
	public void shouldGetRefByUuid() throws Exception {
	}
	
	@Test
	@Ignore
	@Override
	public void shouldGetFullByUuid() throws Exception {
	}
	
	@Test
	@Ignore
	@Override
	public void shouldGetAll() throws Exception {
	}
	
}
