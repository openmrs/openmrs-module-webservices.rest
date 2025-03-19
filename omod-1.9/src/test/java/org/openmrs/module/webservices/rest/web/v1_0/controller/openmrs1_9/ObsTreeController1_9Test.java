/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;

public class ObsTreeController1_9Test extends MainResourceControllerTest {

	@Before
	public void init() throws Exception {
		executeDataSet("obsTreeDataset.xml");
	}
	
	@Override
	public String getURI() {
		return "obstree";
	}

	@Override
	public String getUuid() {
		return null;
	}

	@Override
	public long getAllCount() {
		return 0;
	}

	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	@Override
	public void shouldGetRefByUuid() throws Exception {
		
	}
	
	@Override
	public void shouldGetDefaultByUuid() throws Exception {
		
	}
	
	@Override
	public void shouldGetFullByUuid() throws Exception {
		
	}
	
	@Test
	public void shouldGetObsTree() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI(), 
				new Parameter("patient", "5946f880-b197-400b-9caa-a3c661d23041"),
				new Parameter("concept", "0f97e14e-cdc2-49ac-9255-b5126f8a5148"));
		
		SimpleObject actualResult = deserialize(handle(req));
		
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("obsTreeDataset.json");
		String json = IOUtils.toString(inputStream, "UTF-8");
		SimpleObject expectedResult = SimpleObject.parseJson(json);

		// this entire hack is because timezone will different between environments
		replaceTimeZone(actualResult);
		replaceTimeZone(expectedResult);
		Assert.assertEquals(expectedResult, actualResult);
	}

	// pull timezone off the obsDatetime (while confirming there is one based on size)
	public void replaceTimeZone(HashMap<String, Object> object) {
		for (String key : object.keySet()) {
			if (key.equals("obsDatetime")) {
				String value = object.get(key).toString();
				assert(value.length() > 23);
				object.put("obsDatetime", value.substring(0, 23));
			} else if (object.get(key) instanceof HashMap) {
				replaceTimeZone((HashMap) object.get(key));
			} else if (object.get(key) instanceof List) {
				Iterator it = ((List) object.get(key)).iterator();
				while (it.hasNext()) {
					replaceTimeZone((HashMap) it.next());
				}
			}
		}
	}
}
