package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests CRUD operations for {@link PatientIdentifier}s via web service calls
 */
public class PatientIdentifierController1_8Test extends MainResourceControllerTest {
	
	private PatientService service;
	
	@Override
	public String getURI() {
		return "patient/" + RestTestConstants1_8.PATIENT_UUID + "/identifier";
	}
	
	@Override
	public String getUuid() {
		return RestTestConstants1_8.PATIENT_IDENTIFIER_UUID;
	}
	
	@Override
	public long getAllCount() {
		return service.getPatientByUuid(RestTestConstants1_8.PATIENT_UUID).getActiveIdentifiers().size();
	}
	
	@Before
	public void before() {
		this.service = Context.getPatientService();
	}
	
	@Test
	public void shouldGetAPatientIdentifierByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		PatientIdentifier patientIdentifier = service.getPatientIdentifierByUuid(getUuid());
		assertEquals(patientIdentifier.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertEquals(patientIdentifier.getIdentifier(), PropertyUtils.getProperty(result, "identifier"));
		assertNotNull(PropertyUtils.getProperty(result, "identifierType"));
	}
	
	@Test
	public void shouldListAllPatientIdentifiersForAPatient() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		assertNotNull(result);
		assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	@Test
	public void shouldAddANewIdentifierToAPatient() throws Exception {
		long originalCount = getAllCount();
		
		SimpleObject patientIdentifier = new SimpleObject();
		patientIdentifier.add("identifier", "abc123ez");
		patientIdentifier.add("identifierType", "2f470aa8-1d73-43b7-81b5-01f0c0dfa53c");
		patientIdentifier.add("location", RestTestConstants1_8.LOCATION_UUID);
		
		String json = new ObjectMapper().writeValueAsString(patientIdentifier);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newPatientIdentifier = deserialize(handle(req));
		
		assertNotNull(PropertyUtils.getProperty(newPatientIdentifier, "uuid"));
		assertEquals(originalCount + 1, getAllCount());
	}
	
	@Test
	public void shouldEditAPatientIdentifier() throws Exception {
		final String newLocationUuid = RestTestConstants1_8.LOCATION_UUID;
		PatientIdentifier patientIdentifierType = service.getPatientIdentifierByUuid(getUuid());
		assertFalse(newLocationUuid.equals(patientIdentifierType.getLocation().getUuid()));
		SimpleObject patientIdentifier = new SimpleObject();
		patientIdentifier.add("location", newLocationUuid);
		
		String json = new ObjectMapper().writeValueAsString(patientIdentifier);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		assertEquals(newLocationUuid, patientIdentifierType.getLocation().getUuid());
	}
	
	@Test
	public void shouldVoidAPatientIdentifier() throws Exception {
		assertEquals(false, service.getPatientIdentifierByUuid(getUuid()).isVoided());
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("!purge", "");
		final String reason = "none";
		req.addParameter("reason", reason);
		handle(req);
		assertEquals(true, service.getPatientIdentifierByUuid(getUuid()).isVoided());
		assertEquals(reason, service.getPatientIdentifierByUuid(getUuid()).getVoidReason());
	}
	
	@Test
	public void shouldPurgeAPatientIdentifier() throws Exception {
		long initialIdCount = getAllCount();
		assertNotNull(service.getPatientIdentifierByUuid(getUuid()));
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("purge", "");
		handle(req);
		assertNull(service.getPatientIdentifierByUuid(getUuid()));
		assertEquals(--initialIdCount, getAllCount());
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		
		assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
}
