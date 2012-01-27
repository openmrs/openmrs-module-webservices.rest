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
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7InQueue;
import org.openmrs.hl7.HL7Service;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link HL7MessageController}.
 */
public class HL7MessageControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String hl7Data = "MSH|^~|FORMENTRY|AMRS.ELD|HL7LISTENER|AMRS.ELD|20110805104142||ORU^R01|REl7wt78q9Pzlqe9ecJB|P|2.5|1||||||||3^AMRS.ELD.FORMID";
	
	private static final String hl7InvalidSourceData = "MSH|^~|FORMENTRY|NONEXISTINGSOURCE|HL7LISTENER|AMRS.ELD|20110805104142||ORU^R01|REl7wt78q9Pzlqe9ecJB|P|2.5|1||||||||3^AMRS.ELD.FORMID";
	
	private HL7Service service;
	
	private HL7MessageController controller;
	
	private MockHttpServletRequest request;
	
	private HttpServletResponse response;
	
	private static final String datasetFilename = "customTestDataset.xml";
	
	@Before
	public void before() throws Exception {
		this.service = Context.getHL7Service();
		this.controller = new HL7MessageController();
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		executeDataSet(datasetFilename);
	}
	
	@Test
	public void enqueHl7Message_shouldEnqueueHl7InQueueMessageInPlainFormat() throws Exception {
		int before = service.getAllHL7InQueues().size();
		SimpleObject newHl7Message = (SimpleObject) controller.create(hl7Data, request, response);
		Util.log("Enqued hl7 message", newHl7Message);
		Assert.assertEquals(before + 1, service.getAllHL7InQueues().size());
		for (HL7InQueue hl7InQueue : service.getAllHL7InQueues()) {
			if (hl7InQueue.getUuid().equals(newHl7Message.get("uuid"))) {
				Assert.assertEquals("AMRS.ELD", hl7InQueue.getHL7Source().getName());
				Assert.assertEquals("REl7wt78q9Pzlqe9ecJB", hl7InQueue.getHL7SourceKey());
			}
		}
	}
	
	@Test
	public void enqueHl7Message_shouldEnqueueHl7InQueueMessageInJSONFormat() throws Exception {
		int before = service.getAllHL7InQueues().size();
		SimpleObject newHl7Message = (SimpleObject) controller
		        .create("{ \"hl7\" : \"" + hl7Data + "\" }", request, response);
		Util.log("Enqued hl7 message", newHl7Message);
		Assert.assertEquals(before + 1, service.getAllHL7InQueues().size());
		for (HL7InQueue hl7InQueue : service.getAllHL7InQueues()) {
			if (hl7InQueue.getUuid().equals(newHl7Message.get("uuid"))) {
				Assert.assertEquals("AMRS.ELD", hl7InQueue.getHL7Source().getName());
				Assert.assertEquals("REl7wt78q9Pzlqe9ecJB", hl7InQueue.getHL7SourceKey());
			}
		}
	}
	
	@Test(expected = ConversionException.class)
	public void enqueHl7Message_shouldFailIfSourceDoesNotExist() throws Exception {
		controller.create(hl7InvalidSourceData, request, response);
	}
}
