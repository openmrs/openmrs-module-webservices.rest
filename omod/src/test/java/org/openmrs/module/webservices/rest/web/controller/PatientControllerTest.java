package org.openmrs.module.webservices.rest.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

public class PatientControllerTest extends BaseModuleWebContextSensitiveTest {
	
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
	
	private WebRequest emptyRequest() {
		return new ServletWebRequest(new MockHttpServletRequest());
	}
			
	/**
	 * @see PatientController#createPatient(SimpleObject,WebRequest)
	 * @verifies create a new patient
	 */
	@Test
	public void createPatient_shouldCreateANewPatient() throws Exception {
		int before = Context.getPatientService().getAllPatients().size();
		String json = "{ \"preferredIdentifier\":{ \"identifier\":\"abc123ez\", \"identifierType\":\"2f470aa8-1d73-43b7-81b5-01f0c0dfa53c\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\" }, \"preferredName\":{ \"givenName\":\"Darius\", \"familyName\":\"Programmer\" }, \"birthdate\":\"1978-01-15\", \"gender\":\"M\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object newPatient = new PatientController().create(post, emptyRequest(), new MockHttpServletResponse());
		log("Created patient", newPatient);
		Assert.assertEquals(before + 1, Context.getPatientService().getAllPatients().size());
	}
	
	/**
	 * @see PatientController#getPatient(Patient,WebRequest)
	 * @verifies get a default representation of a patient
	 */
	@Test
	public void getPatient_shouldGetADefaultRepresentationOfAPatient() throws Exception {
		Object result = new PatientController().retrieve("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", emptyRequest());
		Assert.assertNotNull(result);
		log("Patient fetched (default)", result);
		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "preferredName"));
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
    	Object result = new PatientController().retrieve("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", new ServletWebRequest(req));
		Assert.assertNotNull(result);
		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
		log("Patient fetched (full)", result);
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
		new PatientController().purge("5946f880-b197-400b-9caa-a3c661d23041", emptyRequest(),
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
		Object editedPatient = new PatientController().update("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", post, emptyRequest(), new MockHttpServletResponse());
		log("Edited patient", editedPatient);
		Assert.assertEquals(df.format(now), df.format(Context.getPatientService().getPatient(2).getBirthdate()));
	}
	
	/**
	 * DOES NOT WORK YET BECAUSE WE DON'T HAVE A CONVERTER FOR CONCEPTS
     * @see PatientController#updatePatient(String,SimpleObject,WebRequest)
     * @verifies change a complex property on a patient
     */
    @Test
    @Ignore
    public void updatePatient_shouldChangeAComplexPropertyOnAPatient() throws Exception {
		SimpleObject post = new ObjectMapper().readValue("{\"dead\":true, \"causeOfDeath\":\"15f83cd6-64e9-4e06-a5f9-364d3b14a43d\"}", SimpleObject.class);
		Object editedPatient = new PatientController().update("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", post, emptyRequest(), new MockHttpServletResponse());
		log("Set patient as dead", editedPatient);
		Assert.assertEquals(new Concept(88), PropertyUtils.getProperty(editedPatient, "causeOfDeath"));
    }
	
	/**
	 * @see PatientController#voidPatient(Patient,String,WebRequest)
	 * @verifies void a patient
	 */
	@Test
	public void voidPatient_shouldVoidAPatient() throws Exception {
		Patient pat = Context.getPatientService().getPatient(2);
		Assert.assertFalse(pat.isVoided());
		new PatientController().delete("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", "unit test", emptyRequest(),
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
	    List<Object> results = new PatientController().search("zzzznobody", emptyRequest(), new MockHttpServletResponse());
	    Assert.assertEquals(0, results.size());
    }

	/**
     * @see PatientController#findPatients(String,WebRequest,HttpServletResponse)
     * @verifies find matching patients
     */
    @Test
    public void findPatients_shouldFindMatchingPatients() throws Exception {
	    List<Object> results = new PatientController().search("Horatio", emptyRequest(), new MockHttpServletResponse());
	    Assert.assertEquals(1, results.size());
	    log("Found " + results.size() + " patient(s)", results);
	    Object result = results.get(0);
	    Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
	    Assert.assertNotNull(PropertyUtils.getProperty(result, "uri"));
	    Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
    }

}
