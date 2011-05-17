package org.openmrs.module.webservices.rest.web.controller;


import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Tests functionality of {@link PatientIdentifierController}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class PatientIdentifierControllerTest extends BaseModuleWebContextSensitiveTest {

	String patientUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
	
	private PatientService service;
	private PatientIdentifierController controller;
	private WebRequest request;
	private HttpServletResponse response;
	
	@Before
	public void before() {
		this.service = Context.getPatientService();
		this.controller = new PatientIdentifierController();
		this.request = new ServletWebRequest(new MockHttpServletRequest());
		this.response = new MockHttpServletResponse();
	}
	
	private void log(String label, Object object) {
		String toPrint;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().set(SerializationConfig.Feature.INDENT_OUTPUT, true);
			toPrint = mapper.writeValueAsString(object);
		} catch (Exception ex) {
			toPrint = "" + object;
		}
		if (label != null)
			toPrint = label + ": " + toPrint;
		System.out.println(toPrint);
	}	
	
	@Test
	public void shouldGetAnIdentifier() throws Exception {
		Object result = controller.retrieve(patientUuid, "8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614", request);
		Assert.assertNotNull(result);
		log("Patient Identifier fetched (default)", result);
		Assert.assertEquals("8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldListIdentifiersForPatient() throws Exception {
		List<Object> result = controller.getAll(patientUuid, request, response);
		log("All identifiers for a patient", result);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
	}
	
	@Test
	public void shouldAddIdentifierToPatient() throws Exception {
		int before = service.getPatientByUuid(patientUuid).getActiveIdentifiers().size();
		String json = "{ \"identifier\":\"abc123ez\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object created = controller.create(patientUuid, post, request, response);
		log("Created", created);
		int after = service.getPatientByUuid(patientUuid).getActiveIdentifiers().size();
		Assert.assertEquals(before + 1, after);
	}
}