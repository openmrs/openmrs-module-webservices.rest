package org.openmrs.module.webservices.rest.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.CohortService;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7Service;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link HL7MessageController}.
 */
public class HL7MessageControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String hl7SourceId = "0";
	
	private static final String hl7SourceKey = "test";
	
	private static final String hl7Data = "OBR|1|||1238^MEDICAL RECORD OBSERVATIONS^DCT|";
	
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
	public void enqueHl7Message_shouldEnqueueHl7InQueueMessage() throws Exception {
		int before = service.getAllHL7InQueues().size();
		String json = "{ \"hl7SourceId\": " + hl7SourceId + ", \"hl7SourceKey\": \"" + hl7SourceKey + "\", \"hl7Data\": \""
		        + hl7Data + "\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object newHl7Message = controller.create(post, request, response);
		Util.log("Enqued hl7 message", newHl7Message);
		Assert.assertEquals(before + 1, service.getAllHL7InQueues().size());
	}
}
