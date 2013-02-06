package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

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
import org.openmrs.PatientIdentifier;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

public class PatientControllerTest extends BaseCrudControllerTest {
	
	//	/**
	//	 * @see PatientController#createPatient(SimpleObject,WebRequest)
	//	 * @verifies create a new patient
	//	 */
	//	@Test
	//	public void createPatient_shouldCreateANewPatient() throws Exception {
	//		int before = Context.getPatientService().getAllPatients().size();
	//		String json = "{ \"person\": \"ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562\", \"identifiers\": [{ \"identifier\":\"abc123ez\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"preferred\": true }] }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newPatient = new PatientController()
	//		        .create(post, new MockHttpServletRequest(), new MockHttpServletResponse());
	//		Util.log("Created patient", newPatient);
	//		Assert.assertEquals(before + 1, Context.getPatientService().getAllPatients().size());
	//		Assert.assertEquals("Super User", Util.getByPath(newPatient, "person/preferredName/display"));
	//	}
	//	
	//	/**
	//	 * @see PatientController#getPatient(Patient,WebRequest)
	//	 * @verifies get a default representation of a patient
	//	 */
	//	@Test
	//	public void getPatient_shouldGetADefaultRepresentationOfAPatient() throws Exception {
	//		Object result = new PatientController().retrieve("da7f524f-27ce-4bb2-86d6-6d1d05312bd5",
	//		    new MockHttpServletRequest());
	//		Assert.assertNotNull(result);
	//		Util.log("Patient fetched (default)", result);
	//		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "identifiers"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see PatientController#getPatient(String,WebRequest)
	//	 * @verifies get a full representation of a patient
	//	 */
	//	@Test
	//	public void getPatient_shouldGetAFullRepresentationOfAPatient() throws Exception {
	//		MockHttpServletRequest req = new MockHttpServletRequest();
	//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = new PatientController().retrieve("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", req);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "identifiers"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//		Util.log("Patient fetched (full)", result);
	//	}
	//	
	//	/**
	//	 * FAILING. I CAN'T FIGURE OUT WHY THIS METHOD IS NOT THROWING AN EXCEPTION WHEN THE PURGE
	//	 * FAILS.
	//	 * 
	//	 * @see PatientController#purgePatient(Patient,WebRequest)
	//	 * @verifies fail to purge a patient with dependent data
	//	 */
	//	@Ignore
	//	@Test(expected = APIException.class)
	//	public void purgePatient_shouldFailToPurgeAPatientWithDependentData() throws Exception {
	//		Assert.assertNotSame(0, Context.getEncounterService().getEncountersByPatient(new Patient(7)).size());
	//		new PatientController().purge("5946f880-b197-400b-9caa-a3c661d23041", new MockHttpServletRequest(),
	//		    new MockHttpServletResponse());
	//		Assert.assertEquals(0, Context.getEncounterService().getEncountersByPatient(new Patient(7)).size());
	//	}
	//	
	//	/**
	//	 * @see PatientController#updatePatient(Patient,SimpleObject,WebRequest)
	//	 * @verifies should fail when changing a person property on a patient
	//	 */
	//	@Test(expected = ConversionException.class)
	//	public void updatePatient_shouldFailWhenChangingAPersonPropertyOnAPatient() throws Exception {
	//		Date now = new Date();
	//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//		SimpleObject post = new ObjectMapper().readValue("{\"birthdate\":\"" + df.format(now) + "\"}", SimpleObject.class);
	//		new PatientController().update("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", post, new MockHttpServletRequest(),
	//		    new MockHttpServletResponse());
	//	}
	//	
	//	/**
	//	 * @see PatientController#voidPatient(Patient,String,WebRequest)
	//	 * @verifies void a patient
	//	 */
	//	@Test
	//	public void voidPatient_shouldVoidAPatient() throws Exception {
	//		Patient pat = Context.getPatientService().getPatient(2);
	//		Assert.assertFalse(pat.isVoided());
	//		new PatientController().delete("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", "unit test", new MockHttpServletRequest(),
	//		    new MockHttpServletResponse());
	//		pat = Context.getPatientService().getPatient(2);
	//		Assert.assertTrue(pat.isVoided());
	//		Assert.assertEquals("unit test", pat.getVoidReason());
	//	}
	//	
	//	/**
	//	 * @see PatientController#findPatients(String,WebRequest,HttpServletResponse)
	//	 * @verifies return no results if there are no matching patients
	//	 */
	//	@Test
	//	public void findPatients_shouldReturnNoResultsIfThereAreNoMatchingPatients() throws Exception {
	//		List<?> results = (List<?>) new PatientController().search("zzzznobody", new MockHttpServletRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(0, results.size());
	//	}
	//	
	//	/**
	//	 * @see PatientController#findPatients(String,WebRequest,HttpServletResponse)
	//	 * @verifies find matching patients
	//	 */
	//	@Test
	//	public void findPatients_shouldFindMatchingPatients() throws Exception {
	//		List<?> results = (List<?>) new PatientController().search("Horatio", new MockHttpServletRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(1, results.size());
	//		Util.log("Found " + results.size() + " patient(s)", results);
	//		Object result = results.get(0);
	//		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	//	}
	//	
	//	@Test(expected = ConversionException.class)
	//	public void shouldFailWhenSetingThePreferredAddressOnAPatient() throws Exception {
	//		executeDataSet("personAddress-Test.xml");
	//		String patientUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
	//		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
	//		Assert.assertFalse(patient.getPersonAddress().isPreferred());
	//		String json = "{ \"preferredAddress\":\"3350d0b5-821c-4e5e-ad1d-a9bce331e118\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		new PatientController().update(patientUuid, post, new MockHttpServletRequest(), new MockHttpServletResponse());
	//	}
	//	
	//	@Test(expected = ConversionException.class)
	//	public void shouldFailWhenUpdatingAPersonOnAPatient() throws Exception {
	//		String json = "{ \"person\": \"ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		new PatientController().update("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", post, new MockHttpServletRequest(),
	//		    new MockHttpServletResponse());
	//	}
	//	
	//	@Test(expected = Exception.class)
	//	public void shouldFailToOverwriteIdentifiers() throws Exception {
	//		String json = "{ \"identifiers\": [{ \"identifier\":\"abc123ez\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"preferred\": true }] }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		new PatientController().update("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", post, new MockHttpServletRequest(),
	//		    new MockHttpServletResponse());
	//	}
	//	
	//	@Test
	//	public void shouldRespectStartIndexAndLimit() throws Exception {
	//		MockHttpServletRequest hsr = new MockHttpServletRequest("GET", "http://localhost:8080/openmrs/ws/rest/"
	//		        + RestConstants.VERSION_1 + "/patient?q=Test");
	//		SimpleObject wrapper = new PatientController().search("Test", hsr, new MockHttpServletResponse());
	//		Util.log("Everything", wrapper);
	//		List<Object> results = (List<Object>) wrapper.get("results");
	//		int fullCount = results.size();
	//		Assert.assertTrue("This test assumes >2 matching patients", fullCount > 2);
	//		
	//		hsr.removeAllParameters();
	//		hsr.setParameter(RestConstants.REQUEST_PROPERTY_FOR_LIMIT, "2");
	//		wrapper = new PatientController().search("Test", hsr, new MockHttpServletResponse());
	//		Util.log("First 2", wrapper);
	//		results = (List<Object>) wrapper.get("results");
	//		int firstCount = results.size();
	//		Assert.assertEquals(2, firstCount);
	//		
	//		hsr.removeAllParameters();
	//		hsr.setParameter(RestConstants.REQUEST_PROPERTY_FOR_START_INDEX, "2");
	//		wrapper = new PatientController().search("Test", hsr, new MockHttpServletResponse());
	//		Util.log("The rest", wrapper);
	//		results = (List<Object>) wrapper.get("results");
	//		int restCount = results.size();
	//		Assert.assertEquals(fullCount, firstCount + restCount);
	//	}
	//	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "patient";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return "5946f880-b197-400b-9caa-a3c661d23041";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	//	
	//	@Test
	//	public void shouldMarkTheFirstIdentifierAsPreferredIfNoneMarked() throws Exception {
	//		MockHttpServletRequest request = request(RequestMethod.POST, getURI());
	//		String personUUID = "ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562";
	//		String json = "{ \"person\": \""
	//		        + personUUID
	//		        + "\", \"identifiers\": ["
	//		        + "{ \"identifier\":\"1234\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\" }, "
	//		        + "{\"identifier\":\"12345678\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\"} ] }";
	//		request.setContent(json.getBytes());
	//		handle(request);
	//		
	//		Patient patient = Context.getPatientService().getPatientByUuid(personUUID);
	//		for (PatientIdentifier id : patient.getIdentifiers()) {
	//			if (id.getIdentifier().equals("1234")) {
	//				Assert.assertTrue(id.isPreferred());
	//			} else {
	//				Assert.assertFalse(id.isPreferred());
	//			}
	//		}
	//	}
	//	
	//	@Test
	//	public void shouldRespectPreferredIdentifier() throws Exception {
	//		MockHttpServletRequest request = request(RequestMethod.POST, getURI());
	//		String personUUID = "ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562";
	//		String json = "{ \"person\": \""
	//		        + personUUID
	//		        + "\", \"identifiers\": ["
	//		        + "{ \"identifier\":\"1234\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\" }, "
	//		        + "{\"identifier\":\"12345678\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"preferred\": true}, "
	//		        + "{\"identifier\":\"1234567890\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\"} ] }";
	//		request.setContent(json.getBytes());
	//		handle(request);
	//		
	//		Patient patient = Context.getPatientService().getPatientByUuid(personUUID);
	//		for (PatientIdentifier id : patient.getIdentifiers()) {
	//			if (id.getIdentifier().equals("12345678")) {
	//				Assert.assertTrue(id.isPreferred());
	//			} else {
	//				Assert.assertFalse(id.isPreferred());
	//			}
	//		}
	//	}
	//	
	//	@Test(expected = Exception.class)
	//	public void shouldFailIfMorePreferredIdentifiers() throws Exception {
	//		MockHttpServletRequest request = request(RequestMethod.POST, getURI());
	//		String personUUID = "ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562";
	//		String json = "{ \"person\": \""
	//		        + personUUID
	//		        + "\", \"identifiers\": ["
	//		        + "{ \"identifier\":\"1234\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\" }, "
	//		        + "{\"identifier\":\"12345678\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"preferred\": true}, "
	//		        + "{\"identifier\":\"1234567890\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"preferred\": true} ] }";
	//		request.setContent(json.getBytes());
	//		handle(request);
	//	}
}
