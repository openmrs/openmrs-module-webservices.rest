/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_7;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.jupiter.MainResourceControllerTest;

/**
 * Tests functionality of {@link org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_7.ObsResource2_7}.
 */
public class ObsController2_7Test extends MainResourceControllerTest {

	@Override
	public String getURI() {
		return "obs";
	}

	@Override
	public long getAllCount() {
		return Context.getObsService().getObservationCount(null, true);
	}

	@Override
	public String getUuid() {
		return RestTestConstants1_8.OBS_UUID;
	}

	@Override
	@Test
	public void shouldGetAll() throws Exception {
		Assert.assertThrows(ResourceDoesNotSupportOperationException.class, super::shouldGetAll);
	}

	@Test
	public void shouldReturnDirectReferenceRangeAsEffectiveReferenceRangeWhenObsHasOne() throws Exception {
		executeDataSet("ObsReferenceRangeTestDataset.xml");

		// obs 2f616900-5e7c-4667-9a7f-dcb260abf1de (obs_id=10) has a direct obs_reference_range association
		SimpleObject response = deserialize(handle(newGetRequest("obs/2f616900-5e7c-4667-9a7f-dcb260abf1de",
		    new org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest.Parameter("v", "custom:(effectiveReferenceRange)"))));

		Object effectiveReferenceRange = PropertyUtils.getProperty(response, "effectiveReferenceRange");
		Assert.assertEquals(300.0, PropertyUtils.getProperty(effectiveReferenceRange, "hiAbsolute"));
		Assert.assertEquals(350.0, PropertyUtils.getProperty(effectiveReferenceRange, "hiCritical"));
	}

	@Test
	public void shouldFallBackToConceptLevelReferenceRangeWhenObsHasNoDirectReferenceRange() throws Exception {
		// obs 39fb7f47-e80a-4056-9285-bd798be13c63 (obs_id=7, concept WEIGHT (KG)) has no obs_reference_range,
		// so this falls back to the concept_numeric defaults (hi_normal=250.0, low_critical=0.0) - its one
		// concept_reference_range record requires gender = 'M', but this obs's patient is female
		SimpleObject response = deserialize(handle(newGetRequest("obs/" + getUuid(),
		    new org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest.Parameter("v", "custom:(effectiveReferenceRange)"))));

		Object effectiveReferenceRange = PropertyUtils.getProperty(response, "effectiveReferenceRange");
		Assert.assertEquals(250.0, PropertyUtils.getProperty(effectiveReferenceRange, "hiNormal"));
		Assert.assertEquals(0.0, PropertyUtils.getProperty(effectiveReferenceRange, "lowCritical"));
	}

	@Test
	public void shouldNotReturnEffectiveReferenceRangeWhenNotRequested() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest("obs/" + getUuid())));

		Assert.assertFalse(response.containsKey("effectiveReferenceRange"));
	}
}
