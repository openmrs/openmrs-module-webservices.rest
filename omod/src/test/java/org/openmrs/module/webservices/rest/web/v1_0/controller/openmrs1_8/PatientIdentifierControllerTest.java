package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link PatientIdentifierController}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class PatientIdentifierControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	String patientUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
	//	
	//	private PatientService service;
	//	
	//	private PatientIdentifierController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() {
	//		this.service = Context.getPatientService();
	//		this.controller = new PatientIdentifierController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldGetAnIdentifier() throws Exception {
	//		Object result = controller.retrieve(patientUuid, "8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614", request);
	//		Assert.assertNotNull(result);
	//		Util.log("Patient Identifier fetched (default)", result);
	//		Assert.assertEquals("8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	@Test
	//	public void shouldListIdentifiersForPatient() throws Exception {
	//		SimpleObject result = controller.getAll(patientUuid, request, response);
	//		Util.log("All identifiers for a patient", result);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(2, Util.getResultsSize(result));
	//	}
	//	
	//	@Test
	//	public void shouldAddIdentifierToPatient() throws Exception {
	//		int before = service.getPatientByUuid(patientUuid).getActiveIdentifiers().size();
	//		String json = "{ \"identifier\":\"abc123ez\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object created = controller.create(patientUuid, post, request, response);
	//		Util.log("Created", created);
	//		int after = service.getPatientByUuid(patientUuid).getActiveIdentifiers().size();
	//		Assert.assertEquals(before + 1, after);
	//	}
	//	
	//	@Test
	//	public void shouldEditIdentifier() throws Exception {
	//		String json = "{ \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update(patientUuid, "8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614", post, request, response);
	//		PatientIdentifier updated = service.getPatientIdentifierByUuid("8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614");
	//		Util.log("Updated", updated);
	//		Assert.assertNotNull(updated);
	//		Assert.assertEquals("101-6", updated.getIdentifier());
	//		Assert.assertEquals("Xanadu", updated.getLocation().getName());
	//	}
	//	
	//	@Test
	//	public void shouldVoidIdentifier() throws Exception {
	//		String piUuid = "8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614";
	//		PatientIdentifier pid = service.getPatientIdentifierByUuid(piUuid);
	//		Assert.assertFalse(pid.isVoided());
	//		controller.delete(patientUuid, piUuid, "unit test", request, response);
	//		pid = service.getPatientIdentifierByUuid(piUuid);
	//		Assert.assertTrue(pid.isVoided());
	//		Assert.assertEquals("unit test", pid.getVoidReason());
	//	}
	//	
	//	@Test
	//	public void shouldPurgeIdentifier() throws Exception {
	//		// I'm using sql queries and a flush-session because if I try to test this the natural way, hibernate
	//		// complains that the identifier will be re-created since the patient is in the session.
	//		String piUuid = "8a9aac6e-3f9f-4ed2-8fb5-25215f8bb614";
	//		Number before = (Number) Context.getAdministrationService().executeSQL(
	//		    "select count(*) from patient_identifier where patient_id = 2", true).get(0).get(0);
	//		
	//		controller.purge(patientUuid, piUuid, request, response);
	//		Context.flushSession();
	//		Number after = (Number) Context.getAdministrationService().executeSQL(
	//		    "select count(*) from patient_identifier where patient_id = 2", true).get(0).get(0);
	//		Assert.assertEquals(before.intValue() - 1, after.intValue());
	//		Assert.assertNull(service.getPatientIdentifierByUuid(piUuid));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
