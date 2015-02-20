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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

public class GenericPropertyResourceTest extends MainResourceControllerTest {
	private String uuid = "123456_whatevs";
	private Integer value = 42;

	@Override
	public String getURI() {
		return "genericChild";
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public long getAllCount() {
		return 0;
	}

	@Test
	public void shouldCreateAGenericChild() throws Exception {
		String json = "{ \"value\":\"42\"}";

		Object newVisit = deserialize(handle(newPostRequest(getURI(), json)));

		Assert.assertNotNull(PropertyUtils.getProperty(newVisit, "uuid"));
		Assert.assertEquals(PropertyUtils.getProperty(newVisit, "value"), value);
	}

	// Ignore these tests, we only care about testing that we can create the object

	@Override
	public void shouldGetDefaultByUuid() throws Exception {
	}

	@Override
	public void shouldGetRefByUuid() throws Exception {
	}

	@Override
	public void shouldGetFullByUuid() throws Exception {
	}

	@Override
	public void shouldGetAll() throws Exception {
	}
}

