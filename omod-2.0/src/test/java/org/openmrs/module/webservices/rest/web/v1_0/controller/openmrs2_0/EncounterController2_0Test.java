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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

public class EncounterController2_0Test extends MainResourceControllerTest {
	
	public static final String currentTimezone = Calendar.getInstance().getTimeZone().getDisplayName(true, TimeZone.SHORT);
	
	@Test
	public void shouldGetEncountersByEncounterTypeAndPatient() throws Exception {
		executeDataSet("EncountersForDifferentTypesWithObservations.xml");
		
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
