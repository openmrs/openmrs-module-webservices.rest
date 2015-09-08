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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7InQueue;
import org.openmrs.hl7.HL7Service;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Tests functionality of {@link HL7MessageController1_8}.
 */
public class HL7MessageController1_8Test extends MainResourceControllerTest {
	
	private static final String hl7Data = "MSH|^~\\&|NES|AMRS.ELD|TESTSYSTEM|TESTFACILITY|20010101000000||ADT^A04|REl7wt78q9Pzlqe9ecJB|P|2.5";
	
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
		return "notsupported";
	}
	
	@Override
	public long getAllCount() {
		return service.getAllHL7InQueues().size();
	}
	
	@Test
	public void enqueHl7Message_shouldEnqueueHl7InQueueMessageInPlainFormat() throws Exception {
		int before = service.getAllHL7InQueues().size();
		
		MockHttpServletRequest req = newPostRequest(getURI(), hl7Data);
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
		hl7Message.add("hl7", hl7Data);
		
		MockHttpServletRequest req = newPostRequest(getURI(), hl7Message);
		SimpleObject newHl7Message = deserialize(handle(req));
		
		Assert.assertEquals(before + 1, service.getAllHL7InQueues().size());
		for (HL7InQueue hl7InQueue : service.getAllHL7InQueues()) {
			if (hl7InQueue.getUuid().equals(newHl7Message.get("uuid"))) {
				Assert.assertEquals(hl7InQueue.getHL7Data(), hl7Data);
			}
		}
	}
	
	@Test
	public void adt_a28_shouldCreatePatient() throws Exception {
		
		//get the initial number of patients
		int count = Context.getPatientService().getAllPatients().size();
		
		//all hl7 queues should be empty
		Assert.assertEquals(0, service.getAllHL7InQueues().size());
		Assert.assertEquals(0, service.getAllHL7InErrors().size());
		Assert.assertEquals(0, service.getAllHL7InArchives().size());
		
		//create an ADT_A28 hl7 message
		SimpleObject hl7Message = new SimpleObject();
		String hl7Data = "MSH|^~\\&|REST|LOCAL|HL7HANDLER|OPENMRS|20140331101300^0|HUP|ADT^A28^ADT_A05|ADD PERSON INFO|P|2.5|1|||AL||ASCII\r"
			+"EVN|A28|20140331101300|||1\r"
			+"PID|||1991^^^Old Identification Number||Rest^Created^Patient||20011114000000|M|||20371^02^2400^724||||||724^Y||||||02|||11|20371|724^DEUT^N||N";
		hl7Message.add("hl7", hl7Data);
		
		//post the hl7 message
		MockHttpServletRequest req = newPostRequest(getURI(), hl7Message);
		handle(req);
		
		//only the hl7 in queue should have data
		Assert.assertEquals(1, service.getAllHL7InQueues().size());
		Assert.assertEquals(0, service.getAllHL7InErrors().size());
		Assert.assertEquals(0, service.getAllHL7InArchives().size());
		
		service.processHL7InQueue(service.getAllHL7InQueues().get(0));
		
		//only the hl7 archive queue should have data
		Assert.assertEquals(0, service.getAllHL7InQueues().size());
		Assert.assertEquals(0, service.getAllHL7InErrors().size());
		Assert.assertEquals(1, service.getAllHL7InArchives().size());
		
		//a new patient should be created
		Assert.assertEquals((count + 1), Context.getPatientService().getAllPatients().size());
	}
	
	@Test(expected = ConversionException.class)
	public void enqueHl7Message_shouldFailIfSourceDoesNotExist() throws Exception {
		SimpleObject hl7Message = new SimpleObject();
		hl7Message.add("hl7", hl7InvalidSourceData);
		
		MockHttpServletRequest req = newPostRequest(getURI(), hl7Message);
		deserialize(handle(req));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetDefaultByUuid()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetDefaultByUuid() throws Exception {
		super.shouldGetDefaultByUuid();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetFullByUuid()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetFullByUuid() throws Exception {
		super.shouldGetFullByUuid();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetRefByUuid()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetRefByUuid() throws Exception {
		super.shouldGetRefByUuid();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
}
