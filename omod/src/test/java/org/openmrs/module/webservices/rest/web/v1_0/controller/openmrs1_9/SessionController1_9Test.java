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

import org.apache.commons.beanutils.PropertyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SessionController1_9Test extends BaseModuleWebContextSensitiveTest {
	
	private static final String SESSION_ID = "test-session-id";
	
	private static final String UNKNOWN_LOCATION_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"; // Unknown Location
	
	private static final String XANADU_UUID = "9356400c-a5a2-4532-8f2b-2361b3446eb8"; // Xanadu
	
	private SessionController1_9 controller;
	
	private HttpServletRequest hsr;
	
	@BeforeEach
	public void before() {
		controller = Context.getRegisteredComponents(SessionController1_9.class).iterator().next(); // should only be 1
		MockHttpServletRequest mockHsr = new MockHttpServletRequest();
		mockHsr.setSession(new MockHttpSession(new MockServletContext(), SESSION_ID));
		hsr = mockHsr;
		
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, "en_GB, sp, fr"));
		Context.getUserContext().setLocation(Context.getLocationService().getLocationByUuid(UNKNOWN_LOCATION_UUID));
	}
	
	/**
	 * @see SessionController1_9#delete(HttpServletRequest) 
	 * @verifies log the client out
	 */
	@Test
	public void delete_shouldLogTheClientOut() throws Exception {
		Assertions.assertTrue(Context.isAuthenticated());
		controller.delete(hsr);
		Assertions.assertFalse(Context.isAuthenticated());
		Assertions.assertNull(hsr.getSession(false));
	}
	
	/**
	 * @see SessionController1_9#get()
	 * @verifies return the session id if the user is authenticated
	 */
	@Test
	public void get_shouldReturnTheUserIfTheUserIsAuthenticated() throws Exception {
		Assertions.assertTrue(Context.isAuthenticated());
		Object ret = controller.get();
		Object userProp = PropertyUtils.getProperty(ret, "user");
		List<HashMap<String, String>> userRoles = (List<HashMap<String, String>>) PropertyUtils.getProperty(userProp,
		    "roles");
		Assertions.assertEquals("System Developer", userRoles.get(0).get("name"));
		Assertions.assertEquals(true, PropertyUtils.getProperty(ret, "authenticated"));
		Assertions.assertEquals(Context.getAuthenticatedUser().getUuid(), PropertyUtils.getProperty(userProp, "uuid"));
		Object personProp = PropertyUtils.getProperty(userProp, "person");
		Assertions.assertEquals(Context.getAuthenticatedUser().getPerson().getUuid(),
		    PropertyUtils.getProperty(personProp, "uuid"));
	}

	@Test
	public void get_shouldReturnLocaleInfoIfTheUserIsNotAuthenticated() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assertions.assertTrue(Context.isAuthenticated());

		// log out the current authenticated user
		controller.delete(hsr);
		Assertions.assertFalse(Context.isAuthenticated());
		Assertions.assertNull(hsr.getSession(false));

		// check if the unauthenticated user response has locale and allowedLocales
		Object ret = controller.get();
		Assertions.assertEquals(Context.getLocale(), PropertyUtils.getProperty(ret, "locale"));
		Assert.assertArrayEquals(Context.getAdministrationService().getAllowedLocales().toArray(),
				((List<Locale>) PropertyUtils.getProperty(ret, "allowedLocales")).toArray());
	}
	
	@Test
	public void get_shouldReturnLocaleInfoIfTheUserIsAuthenticated() throws Exception {
		Assertions.assertTrue(Context.isAuthenticated());
		Object ret = controller.get();
		Assertions.assertEquals(Context.getLocale(), PropertyUtils.getProperty(ret, "locale"));
		Assert.assertArrayEquals(Context.getAdministrationService().getAllowedLocales().toArray(),
		    ((List<Locale>) PropertyUtils.getProperty(ret, "allowedLocales")).toArray());
	}
	
	@Test
	public void get_shouldReturnLocationIfTheUserIsAuthenticated() throws Exception {
		Assertions.assertTrue(Context.isAuthenticated());
		Object ret = controller.get();
		Object loc = PropertyUtils.getProperty(ret, "sessionLocation");
		Assertions.assertTrue(loc.toString() + " should contain 'display=Unknown Location'",
		    loc.toString().contains("display=Unknown Location"));
	}

	/**
	 * @see SessionController1_9#get()
	 * @verifies return the session with current provider if the user is authenticated
	 */
	@Test
	public void get_shouldReturnCurrentProviderIfTheUserIsAuthenticated() throws Exception {
		Assertions.assertTrue(Context.isAuthenticated());
		Object ret = controller.get();
		Object currentProvider = PropertyUtils.getProperty(ret, "currentProvider");
		Assertions.assertNotNull(currentProvider);
		Assertions.assertTrue(currentProvider.toString().contains("Super User"));
	}

	@Test
	public void post_shouldReturnTheCurrentSession() throws Exception{
		String content = "{}";
		Object ret = controller.post(hsr,new ObjectMapper().readValue(content, HashMap.class));
		Object currentProvider = PropertyUtils.getProperty(ret, "currentProvider");
		Assertions.assertNotNull(currentProvider);
		Assertions.assertTrue(currentProvider.toString().contains("Super User"));
	}
	
	@Test
	public void post_shouldSetTheUserLocale() throws Exception {
		Locale newLocale = new Locale("sp");
		String content = "{\"locale\":\"" + newLocale.toString() + "\"}";
		Assertions.assertNotEquals(newLocale, Context.getLocale());
		Object ret = controller.post(hsr, new ObjectMapper().readValue(content, HashMap.class));
		Assertions.assertEquals(newLocale, Context.getLocale());
		Assertions.assertEquals(Context.getLocale(), PropertyUtils.getProperty(ret, "locale"));
		Assert.assertArrayEquals(Context.getAdministrationService().getAllowedLocales().toArray(),
				((List<Locale>) PropertyUtils.getProperty(ret, "allowedLocales")).toArray());
	}
	
	@Test
	public void post_shouldFailWhenSettingIllegalLocale() throws Exception {
		assertThrows(APIException.class, () -> {
			String newLocale = "fOOb@r:";
			String content = "{\"locale\":\"" + newLocale + "\"}";
			controller.post(hsr, new ObjectMapper().readValue(content, HashMap.class));
		});
	}
	
	@Test
	public void post_shouldFailWhenSettingDisallowedLocale() throws Exception {
		assertThrows(APIException.class, () -> {
			String newLocale = "km_KH";
			String content = "{\"locale\":\"" + newLocale + "\"}";
			controller.post(hsr, new ObjectMapper().readValue(content, HashMap.class));
		});
	}
	
	@Test
	public void post_shouldSetTheSessionLocation() throws Exception {
		String content = "{\"sessionLocation\":\"" + XANADU_UUID + "\"}";
		Location loc = Context.getLocationService().getLocationByUuid(XANADU_UUID);
		Assertions.assertNotEquals(loc, Context.getUserContext().getLocation());
		Object ret = controller.post(hsr, new ObjectMapper().readValue(content, HashMap.class));
		Assertions.assertEquals(loc, Context.getUserContext().getLocation());
		Object responseLoc = PropertyUtils.getProperty(ret, "sessionLocation");
		Assertions.assertTrue(responseLoc.toString() + " should contain 'display=Xanadu'",
				responseLoc.toString().contains("display=Xanadu"));
	}
	
	@Test
	public void post_shouldFailWhenSettingNonexistantLocation() throws Exception {
		assertThrows(APIException.class, () -> {
			String content = "{\"sessionLocation\":\"fake-nonexistant-uuid\"}";
			controller.post(hsr, new ObjectMapper().readValue(content, HashMap.class));
		});
	}
}
