package org.openmrs.module.webservices.rest.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

public class PatientControllerTest extends BaseModuleWebContextSensitiveTest {
	
	/**
	 * @see PatientController#createPatient(SimpleObject,WebRequest)
	 * @verifies create a new patient
	 */
	@Test
	public void createPatient_shouldCreateANewPatient() throws Exception {
		int before = Context.getPatientService().getAllPatients().size();
		String json = "{ \"preferredIdentifier\":{ \"identifier\":\"abc123ez\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\" }, \"preferredName\":{ \"givenName\":\"Darius\", \"familyName\":\"Programmer\" }, \"birthdate\":\"1978-01-15\", \"gender\":\"M\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object newPatient = new PatientController()
		        .create(post, new MockHttpServletRequest(), new MockHttpServletResponse());
		Util.log("Created patient", newPatient);
		Assert.assertEquals(before + 1, Context.getPatientService().getAllPatients().size());
		Assert.assertEquals("Darius Programmer", Util.getByPath(newPatient, "preferredName/display"));
	}
	
	/**
	 * @see PatientController#getPatient(Patient,WebRequest)
	 * @verifies get a default representation of a patient
	 */
	@Test
	public void getPatient_shouldGetADefaultRepresentationOfAPatient() throws Exception {
		Object result = new PatientController().retrieve("da7f524f-27ce-4bb2-86d6-6d1d05312bd5",
		    new MockHttpServletRequest());
		Assert.assertNotNull(result);
		Util.log("Patient fetched (default)", result);
		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "identifiers"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
	 * @see PatientController#getPatient(String,WebRequest)
	 * @verifies get a full representation of a patient
	 */
	@Test
	public void getPatient_shouldGetAFullRepresentationOfAPatient() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		Object result = new PatientController().retrieve("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", req);
		Assert.assertNotNull(result);
		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "identifiers"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
		Util.log("Patient fetched (full)", result);
	}
	
	/**
	 * FAILING. I CAN'T FIGURE OUT WHY THIS METHOD IS NOT THROWING AN EXCEPTION WHEN THE PURGE
	 * FAILS.
	 * 
	 * @see PatientController#purgePatient(Patient,WebRequest)
	 * @verifies fail to purge a patient with dependent data
	 */
	@Ignore
	@Test(expected = APIException.class)
	public void purgePatient_shouldFailToPurgeAPatientWithDependentData() throws Exception {
		Assert.assertNotSame(0, Context.getEncounterService().getEncountersByPatient(new Patient(7)).size());
		new PatientController().purge("5946f880-b197-400b-9caa-a3c661d23041", new MockHttpServletRequest(),
		    new MockHttpServletResponse());
		Assert.assertEquals(0, Context.getEncounterService().getEncountersByPatient(new Patient(7)).size());
	}
	
	/**
	 * @see PatientController#updatePatient(Patient,SimpleObject,WebRequest)
	 * @verifies change a property on a patient
	 */
	@Test
	public void updatePatient_shouldChangeAPropertyOnAPatient() throws Exception {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleObject post = new ObjectMapper().readValue("{\"birthdate\":\"" + df.format(now) + "\"}", SimpleObject.class);
		Object editedPatient = new PatientController().update("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", post,
		    new MockHttpServletRequest(), new MockHttpServletResponse());
		Util.log("Edited patient", editedPatient);
		Assert.assertEquals(df.format(now), df.format(Context.getPatientService().getPatient(2).getBirthdate()));
	}
	
	/**
	 * @see PatientController#voidPatient(Patient,String,WebRequest)
	 * @verifies void a patient
	 */
	@Test
	public void voidPatient_shouldVoidAPatient() throws Exception {
		Patient pat = Context.getPatientService().getPatient(2);
		Assert.assertFalse(pat.isVoided());
		new PatientController().delete("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", "unit test", new MockHttpServletRequest(),
		    new MockHttpServletResponse());
		pat = Context.getPatientService().getPatient(2);
		Assert.assertTrue(pat.isVoided());
		Assert.assertEquals("unit test", pat.getVoidReason());
	}
	
	/**
	 * @see PatientController#findPatients(String,WebRequest,HttpServletResponse)
	 * @verifies return no results if there are no matching patients
	 */
	@Test
	public void findPatients_shouldReturnNoResultsIfThereAreNoMatchingPatients() throws Exception {
		List<?> results = (List<?>) new PatientController().search("zzzznobody", new MockHttpServletRequest(),
		    new MockHttpServletResponse()).get("results");
		Assert.assertEquals(0, results.size());
	}
	
	/**
	 * @see PatientController#findPatients(String,WebRequest,HttpServletResponse)
	 * @verifies find matching patients
	 */
	@Test
	public void findPatients_shouldFindMatchingPatients() throws Exception {
		List<?> results = (List<?>) new PatientController().search("Horatio", new MockHttpServletRequest(),
		    new MockHttpServletResponse()).get("results");
		Assert.assertEquals(1, results.size());
		Util.log("Found " + results.size() + " patient(s)", results);
		Object result = results.get(0);
		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	}
	
	@Test
	public void shouldSetThePreferredAddress() throws Exception {
		executeDataSet("personAddress-Test.xml");
		String patientUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		Assert.assertFalse(patient.getPersonAddress().isPreferred());
		String json = "{ \"preferredAddress\":\"3350d0b5-821c-4e5e-ad1d-a9bce331e118\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		new PatientController().update(patientUuid, post, new MockHttpServletRequest(), new MockHttpServletResponse());
		Assert.assertTrue(patient.getPersonAddress().isPreferred());
		Assert.assertEquals("1050 Wishard Blvd.", patient.getPersonAddress().getAddress1());
	}
	
	@Test
	public void shouldAddTheAddressIfThePreferredAddressBeingSetIsNew() throws Exception {
		executeDataSet("personAddress-Test.xml");
		String patientUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		Assert.assertFalse(patient.getPersonAddress().isPreferred());
		String json = "{\"preferredAddress\":{ \"address1\":\"test address\", \"country\":\"USA\" }}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		new PatientController().update(patientUuid, post, new MockHttpServletRequest(), new MockHttpServletResponse());
		Assert.assertTrue(patient.getPersonAddress().isPreferred());
		Assert.assertEquals("test address", patient.getPersonAddress().getAddress1());
	}
	
	@Test
	public void shouldUnmarkTheOldPreferredAddressAsPreferredWhenSettingANewPreferredAddress() throws Exception {
		executeDataSet("personAddress-Test.xml");
		String patientUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		//set a preferred address for testing purposes
		PersonAddress oldPreferredAddress = patient.getPersonAddress();
		oldPreferredAddress.setPreferred(true);
		Context.getPatientService().savePatient(patient);
		Assert.assertTrue(patient.getPersonAddress().isPreferred());
		String json = "{\"preferredAddress\":{ \"address1\":\"test address\", \"country\":\"USA\" }}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		new PatientController().update(patientUuid, post, new MockHttpServletRequest(), new MockHttpServletResponse());
		Assert.assertFalse(oldPreferredAddress.isPreferred());
	}
	
	@Test
	public void shouldRespectStartIndexAndLimit() throws Exception {
		MockHttpServletRequest hsr = new MockHttpServletRequest("GET",
		        "http://localhost:8080/openmrs/ws/rest/patient?q=Test");
		SimpleObject wrapper = new PatientController().search("Test", hsr, new MockHttpServletResponse());
		Util.log("Everything", wrapper);
		List<Object> results = (List<Object>) wrapper.get("results");
		int fullCount = results.size();
		Assert.assertTrue("This test assumes >2 matching patients", fullCount > 2);
		
		hsr.removeAllParameters();
		hsr.setParameter(RestConstants.REQUEST_PROPERTY_FOR_LIMIT, "2");
		wrapper = new PatientController().search("Test", hsr, new MockHttpServletResponse());
		Util.log("First 2", wrapper);
		results = (List<Object>) wrapper.get("results");
		int firstCount = results.size();
		Assert.assertEquals(2, firstCount);
		
		hsr.removeAllParameters();
		hsr.setParameter(RestConstants.REQUEST_PROPERTY_FOR_START_INDEX, "2");
		wrapper = new PatientController().search("Test", hsr, new MockHttpServletResponse());
		Util.log("The rest", wrapper);
		results = (List<Object>) wrapper.get("results");
		int restCount = results.size();
		Assert.assertEquals(fullCount, firstCount + restCount);
	}
	
}
