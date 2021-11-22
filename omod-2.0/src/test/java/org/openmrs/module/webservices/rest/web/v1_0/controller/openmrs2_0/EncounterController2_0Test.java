/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

public class EncounterController2_0Test extends MainResourceControllerTest {
	
	public static final String currentTimezone = Calendar.getInstance().getTimeZone().getDisplayName(true, TimeZone.SHORT);
	
	@Before
	public void setup() throws Exception {
		executeDataSet("EncountersForDifferentTypesWithObservations.xml");
	}
	
	@Test
	public void shouldGetEncountersByEncounterTypeAndPatient() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("s", "default"), new Parameter(
		        "patient", "41c6b35e-c093-11e3-be87-005056821db0"), new Parameter("encounterType",
		        "ff7397ea-c090-11e3-be87-005056821db0"))));
		
		List<?> encounters = result.get("results");
		Assert.assertEquals(1, encounters.size());
		String encounterUuid = (String) PropertyUtils.getProperty(encounters.get(0), "uuid");
		Assert.assertEquals("62967e68-96bb-11e0-8d6b-9b9415a91465", encounterUuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	@Test
	public void shouldReturnEncounterAsComplexCustomRepresentation() throws Exception {
		final String CUSTOM_REP = "custom:(uuid,display,patient:(uuid,display),location:(name,tags:(display)))";
		
		final String ENCOUNTER_UUID = "62967e68-96bb-11e0-8d6b-9b9415a91465";
		final String ENCOUNTER_DISPLAY = "sample encounter a 01/08/2008";
		final String PATIENT_UUID = "41c6b35e-c093-11e3-be87-005056821db0";
		final String PATIENT_DISPLAY = "";
		final String LOCATION_NAME = "Unknown Location";
		
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("patient",
		        "41c6b35e-c093-11e3-be87-005056821db0"), new Parameter("v", CUSTOM_REP))));
		
		assertEquals(2, Util.getResultsSize(result));
		List<?> encounters = result.get("results");
		assertEquals(ENCOUNTER_UUID, PropertyUtils.getProperty(encounters.get(0), "uuid"));
		assertEquals(ENCOUNTER_DISPLAY, PropertyUtils.getProperty(Util.getResultsList(result).get(0), "display"));
		assertEquals(PATIENT_UUID, PropertyUtils.getProperty(Util.getResultsList(result).get(0), "patient.uuid"));
		assertEquals(PATIENT_DISPLAY, PropertyUtils.getProperty(Util.getResultsList(result).get(0), "patient.display"));
		assertEquals(LOCATION_NAME, PropertyUtils.getProperty(Util.getResultsList(result).get(0), "location.name"));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "encounter";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.ENCOUNTER_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		Map<Integer, List<Encounter>> allPatientEncounters = Context.getEncounterService().getAllEncounters(null);
		int totalEncounters = 0;
		for (Integer integer : allPatientEncounters.keySet()) {
			List<Encounter> encounters = allPatientEncounters.get(integer);
			if (encounters != null) {
				totalEncounters = totalEncounters + encounters.size();
			}
		}
		return totalEncounters;
	}
}
