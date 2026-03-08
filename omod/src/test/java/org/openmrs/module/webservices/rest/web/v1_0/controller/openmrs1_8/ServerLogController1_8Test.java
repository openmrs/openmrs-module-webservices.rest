/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.openmrs.module.webservices.helper.ServerLogActionWrapper;
import org.openmrs.module.webservices.helper.openmrs2_4.ServerLogActionWrapper2_4;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.MockServerLogActionWrapper;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_4.ServerLogResource2_4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerLogController1_8Test extends MainResourceControllerTest {

	@Autowired
	private RestService restService;

	private static final String LOG_1 = "INFO - Simple.appender(115) |2018-03-03 15:44:54,834| Info Message";

	private static final String LOG_2 = "ERROR - Simple.appender(115) |2018-03-03 15:44:54,834| Info Message";

	private final MockServerLogActionWrapper<ServerLogActionWrapper> mockServerLogActionWrapper = new MockServerLogActionWrapper<ServerLogActionWrapper>(
			new ServerLogActionWrapper2_4());
	@BeforeEach
	public void setUp() {
		ServerLogResource2_4 serverLogResource = (ServerLogResource2_4) restService
				.getResourceBySupportedClass(ServerLogActionWrapper.class);
		serverLogResource.setServerLogActionWrapper(mockServerLogActionWrapper);
	}

	@Test
	public void save_shouldFailOnSave() throws Exception {
		assertThrows(Exception.class, () -> {
			MockHttpServletRequest req = request(RequestMethod.POST, getURI());
			SimpleObject resultLogs = deserialize(handle(req));
		});
	}

	@Test
	public void delete_shouldFailOnDelete() throws Exception {
		assertThrows(Exception.class, () -> {
			MockHttpServletRequest req = request(RequestMethod.DELETE, getURI());
			SimpleObject resultLogs = deserialize(handle(req));
		});
	}

	@Test
	@Override
	public void shouldGetAll() throws Exception {
		//sanity check
		List<String[]> mockServerLogs = mockServerLogActionWrapper.getServerLogs();
		Assertions.assertEquals(0, mockServerLogs.size());

		mockServerLogActionWrapper.mockMemoryAppenderBuffer.addAll(Arrays.asList(LOG_1, LOG_2));

		SimpleObject response = deserialize(handle(newGetRequest(getURI())));
		ArrayList<String[]> results = response.get("serverLog");
		Assertions.assertNotNull(results);
		Assertions.assertEquals(results.size(), getAllCount());

		Assertions.assertEquals(mockServerLogActionWrapper.getServerLogs().size(), getAllCount());
	}

	@Test
	@Override
	public void shouldGetFullByUuid() throws Exception {
		assertThrows(Exception.class, () -> {
			MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
			req.addParameter("v", "full");
			handle(req);
		});
	}

	@Test
	@Override
	public void shouldGetDefaultByUuid() throws Exception {
		assertThrows(Exception.class, () -> {
			MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
			req.addParameter("v", "full");
			handle(req);
		});
	}

	@Test
	@Override
	public void shouldGetRefByUuid() throws Exception {
		assertThrows(Exception.class, () -> {
			MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
			req.addParameter("v", "ref");
			handle(req);
		});
	}

	@Override
	public String getURI() {
		return "serverlog";
	}

	@Override
	public String getUuid() {
		return "log1";
	}

	@Override
	public long getAllCount() {
		return mockServerLogActionWrapper.mockMemoryAppenderBuffer.size();
	}
}
