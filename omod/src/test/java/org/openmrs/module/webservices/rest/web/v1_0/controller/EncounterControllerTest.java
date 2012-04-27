package org.openmrs.module.webservices.rest.web.v1_0.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.util.Format;
import org.openmrs.util.Format.FORMAT_TYPE;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

public class EncounterControllerTest extends BaseModuleWebContextSensitiveTest {
	
	public static final String currentTimezone = Calendar.getInstance().getTimeZone().getDisplayName(true, TimeZone.SHORT);
	
	/**
	 * @see EncounterController#create(SimpleObject,WebRequest,HttpServletResponse)
	 * @verifies create a new encounter
	 */
	@Test
	public void createEncounter_shouldCreateANewEncounter() throws Exception {
		int before = Context.getEncounterService().getAllEncounters(null).size();
		String json = "{\"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"encounterType\": \"61ae96f4-6afe-4351-b6f8-cd4fc383cce1\", \"encounterDatetime\": \"2011-01-15\", \"patient\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\", \"provider\":\"ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object newEncounter = new EncounterController().create(post, emptyRequest(), new MockHttpServletResponse());
		Assert.assertNotNull(newEncounter);
		Assert.assertEquals(before + 1, Context.getEncounterService().getAllEncounters(null).size());
	}
	
	/**
	 * @see EncounterController#create(SimpleObject,WebRequest,HttpServletResponse)
	 * @verifies create a new encounter with obs
	 */
	@Test
	public void createEncounter_shouldCreateANewEncounterWithObs() throws Exception {
		int before = Context.getEncounterService().getAllEncounters(null).size();
		String json = "{\"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"encounterType\": \"61ae96f4-6afe-4351-b6f8-cd4fc383cce1\", \"encounterDatetime\": \"2011-01-15\", \"patient\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\", \"provider\":\"ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562\", \"obs\": [ ";
		// weight in kg = 70
		json += "{ \"concept\": \"c607c80f-1ea9-4da3-bb88-6276ce8868dd\", \"value\": 70 }";
		// civil status = married
		json += ", { \"concept\": \"89ca642a-dab6-4f20-b712-e12ca4fc6d36\", \"value\": \"92afda7c-78c9-47bd-a841-0de0817027d4\" }";
		// favorite food, non-coded = fried chicken
		json += ", { \"concept\": \"96408258-000b-424e-af1a-403919332938\", \"value\": \"fried chicken\" }";
		// date of food assistance = 2011-06-21
		json += ", { \"concept\": \"11716f9c-1434-4f8d-b9fc-9aa14c4d6126\", \"value\": \"2011-06-21\" }";
		json += "] }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		SimpleObject newEncounter = (SimpleObject) new EncounterController().create(post, emptyRequest(),
		    new MockHttpServletResponse());
		Assert.assertNotNull(newEncounter);
		Assert.assertEquals(before + 1, Context.getEncounterService().getAllEncounters(null).size());
		
		Util.log("encounter created", newEncounter);
		List<SimpleObject> obs = (List<SimpleObject>) newEncounter.get("obs");
		Assert.assertEquals(4, obs.size());
		Set<String> obsDisplayValues = new HashSet<String>();
		for (SimpleObject o : obs) {
			obsDisplayValues.add((String) o.get("display"));
		}
		Assert.assertTrue(obsDisplayValues.contains("CIVIL STATUS = MARRIED"));
		Assert.assertTrue(obsDisplayValues.contains("FAVORITE FOOD, NON-CODED = fried chicken"));
		Assert.assertTrue(obsDisplayValues.contains("WEIGHT (KG) = 70.0"));
		
		// obs.getValueAsString() uses application Locale and hence have to do this
		Calendar cal = Calendar.getInstance();
		cal.set(2011, Calendar.JUNE, 21, 0, 0, 0);
		String format = Format.format(cal.getTime(), Context.getLocale(), FORMAT_TYPE.TIMESTAMP);
		Assert.assertTrue(obsDisplayValues.contains("DATE OF FOOD ASSISTANCE = " + format));
	}
	
	/**
	 * @see EncounterController#find(String,WebRequest,HttpServletResponse)
	 * @verifies return no results if there are no matching encounters
	 */
	@Test
	public void findEncounters_shouldReturnNoResultsIfThereAreNoMatchingEncounters() throws Exception {
		List<Object> results = (List<Object>) new EncounterController().search("noencounter", emptyRequest(),
		    new MockHttpServletResponse()).get("results");
		Assert.assertEquals(0, results.size());
	}
	
	/**
	 * @see EncounterController#getEncounter(String,WebRequest)
	 * @verifies get a default representation of a encounter
	 */
	@Test
	public void getEncounter_shouldGetADefaultRepresentationOfAEncounter() throws Exception {
		Object result = new EncounterController().retrieve("6519d653-393b-4118-9c83-a3715b82d4ac", emptyRequest());
		Assert.assertNotNull(result);
		Assert.assertEquals("6519d653-393b-4118-9c83-a3715b82d4ac", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "encounterType"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "patient"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditinfo"));
	}
	
	/**
	 * @see EncounterController#getEncounter(String,WebRequest)
	 * @verifies get a full representation of a encounter
	 */
	@Test
	public void getEncounter_shouldGetAFullRepresentationOfAEncounter() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		
		Object result = new EncounterController().retrieve("6519d653-393b-4118-9c83-a3715b82d4ac", req);
		Assert.assertNotNull(result);
		Assert.assertEquals("6519d653-393b-4118-9c83-a3715b82d4ac", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "encounterType"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "patient"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
	 * @see EncounterController#getEncounter(String,WebRequest)
	 * @verifies get a full representation of a encounter including obs groups
	 */
	@Test
	public void getEncounter_shouldGetAFullRepresentationOfAEncounterIncludingObsGroups() throws Exception {
		executeDataSet("EncounterWithObsGroup.xml");
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		
		SimpleObject result = (SimpleObject) new EncounterController().retrieve("62967e68-96bb-11e0-8d6b-9b9415a91465", req);
		Util.log("full", result);
		Assert.assertNotNull(result);
		Assert.assertEquals("62967e68-96bb-11e0-8d6b-9b9415a91465", result.get("uuid"));
		Assert.assertNotNull(result.get("obs"));
		Assert.assertEquals("0f97e14e-cdc2-49ac-9255-b5126f8a5147", Util.getByPath(result, "obs[0]/concept/uuid"));
		Assert.assertEquals("96408258-000b-424e-af1a-403919332938", Util.getByPath(result,
		    "obs[0]/groupMembers[0]/concept/uuid"));
		Assert.assertEquals("Some text", Util.getByPath(result, "obs[0]/groupMembers[0]/value"));
		Assert.assertEquals("11716f9c-1434-4f8d-b9fc-9aa14c4d6126", Util.getByPath(result,
		    "obs[0]/groupMembers[1]/concept/uuid"));
		Assert.assertEquals(ConversionUtil.convertToRepresentation(ymd.parse("2011-06-12"), Representation.DEFAULT), Util
		        .getByPath(result, "obs[0]/groupMembers[1]/value"));
		// make sure there's a group in the group
		Assert.assertEquals("0f97e14e-cdc2-49ac-9255-b5126f8a5147", Util.getByPath(result,
		    "obs[0]/groupMembers[2]/concept/uuid"));
		Assert.assertEquals("96408258-000b-424e-af1a-403919332938", Util.getByPath(result,
		    "obs[0]/groupMembers[2]/groupMembers[0]/concept/uuid"));
		Assert.assertEquals("Some text", Util.getByPath(result, "obs[0]/groupMembers[2]/groupMembers[0]/value"));
		Assert.assertEquals("11716f9c-1434-4f8d-b9fc-9aa14c4d6126", Util.getByPath(result,
		    "obs[0]/groupMembers[2]/groupMembers[1]/concept/uuid"));
		Assert.assertEquals(ConversionUtil.convertToRepresentation(ymd.parse("2011-06-12"), Representation.DEFAULT), Util
		        .getByPath(result, "obs[0]/groupMembers[2]/groupMembers[1]/value"));
	}
	
	/**
	 * @see EncounterController#purge(String,WebRequest,HttpServletResponse)
	 * @verifies fail to purge a encounter with dependent data
	 */
	@Test(expected = ConstraintViolationException.class)
	public void purgeEncounter_shouldNotPurgeAEncounterWithDependentData() throws Exception {
		int size = Context.getEncounterService().getEncountersByPatient(new Patient(7)).size();
		new EncounterController().purge("6519d653-393b-4118-9c83-a3715b82d4ac", emptyRequest(),
		    new MockHttpServletResponse());
		Assert.assertEquals(size - 1, Context.getEncounterService().getEncountersByPatient(new Patient(7)).size());
	}
	
	/**
	 * @see EncounterController#update(String,SimpleObject,WebRequest,HttpServletResponse)
	 * @verifies change a property on a encounter
	 */
	@Test
	public void updateEncounter_shouldChangeAPropertyOnAEncounter() throws Exception {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleObject post = new ObjectMapper().readValue("{\"encounterDatetime\":\"" + df.format(now) + "\"}",
		    SimpleObject.class);
		Object editedPatient = new EncounterController().update("6519d653-393b-4118-9c83-a3715b82d4ac", post,
		    emptyRequest(), new MockHttpServletResponse());
		Assert.assertEquals(df.format(now), df.format(Context.getEncounterService().getEncounter(3).getEncounterDatetime()));
	}
	
	/**
	 * @see EncounterController#delete(String,String,WebRequest,HttpServletResponse)
	 * @verifies void a encounter
	 */
	@Test
	public void voidEncounter_shouldVoidAEncounter() throws Exception {
		Encounter enc = Context.getEncounterService().getEncounter(3);
		Assert.assertFalse(enc.isVoided());
		new EncounterController().delete("6519d653-393b-4118-9c83-a3715b82d4ac", "unit test", emptyRequest(),
		    new MockHttpServletResponse());
		enc = Context.getEncounterService().getEncounter(3);
		Assert.assertTrue(enc.isVoided());
		Assert.assertEquals("unit test", enc.getVoidReason());
	}
	
	/**
	 * @see EncounterController#searchByPatient(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * @verifies return a list of encounters
	 */
	@Test
	public void searchByPatient_shouldReturnAListOfEncounters() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		
		Object result = new EncounterController().searchByPatient("5946f880-b197-400b-9caa-a3c661d23041", req, null);
		Assert.assertNotNull(result);
		List encList = (List) PropertyUtils.getProperty(result, "results");
		Assert.assertNotNull(encList);
		Assert.assertTrue(encList.size() > 0);
		List<String> uuids = new ArrayList<String>();
		for (Object encounter : encList) {
			uuids.add((String) PropertyUtils.getProperty(encounter, "uuid"));
		}
		Assert.assertTrue(uuids.contains("6519d653-393b-4118-9c83-a3715b82d4ac"));
	}
	
	private MockHttpServletRequest emptyRequest() {
		return new MockHttpServletRequest();
	}
}
