/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_5;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.jupiter.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_5.RestConstants2_5;
import org.springframework.mock.web.MockHttpServletRequest;

public class ConceptReferenceRangeController2_5Test extends MainResourceControllerTest {

	@Override
	public String getURI() {
		return "conceptreferencerange";
	}

	@Override
	public String getUuid() {
		return RestConstants2_5.CONCEPT_NUMERIC_UUID;
	}

	@Override
	public long getAllCount() {
		return 0;
	}

	@Test
	public void shouldGetConceptReferenceRangeEvenWithSpacesAroundConceptUuid() throws Exception {
		MockHttpServletRequest request = newGetRequest(getURI());
		request.setParameter("concept", RestConstants2_5.CONCEPT_NUMERIC_UUID + " ");
		request.setParameter("v", "full");
		SimpleObject response = deserialize(handle(request));
		List<Object> resultsList = Util.getResultsList(response);
		Assert.assertEquals(1, resultsList.size());
		Assert.assertEquals(250.0, PropertyUtils.getProperty(resultsList.get(0), "hiNormal"));
		Assert.assertEquals(0.0, PropertyUtils.getProperty(resultsList.get(0), "lowCritical"));
		Assert.assertEquals("kg", PropertyUtils.getProperty(resultsList.get(0), "units"));
		Assert.assertEquals(true, PropertyUtils.getProperty(resultsList.get(0), "allowDecimal"));
	}

	@Override
	public void shouldGetDefaultByUuid() throws Exception {
		super.shouldGetDefaultByUuid();
	}

	@Override
	public void shouldGetRefByUuid() throws Exception {
		super.shouldGetRefByUuid();
	}

	@Override
	public void shouldGetFullByUuid() throws Exception {
		super.shouldGetFullByUuid();
	}

	@Override
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
}