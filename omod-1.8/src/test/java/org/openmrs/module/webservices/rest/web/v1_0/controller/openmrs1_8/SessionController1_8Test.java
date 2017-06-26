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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

public class SessionController1_8Test extends BaseModuleWebContextSensitiveTest {
	
	private String SESSION_ID = "test-session-id";
	
	private SessionController1_8 controller;
	
	private WebRequest request;
	
	@Before
	public void before() {
		controller = new SessionController1_8();
		MockHttpServletRequest hsr = new MockHttpServletRequest();
		hsr.setSession(new MockHttpSession(new MockServletContext(), SESSION_ID));
		request = new ServletWebRequest(hsr);
		
		Context.getAdministrationService().saveGlobalProperty(
		    new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, "en_GB, sp, fr"));
	}
	
	/**
	 * @see SessionController1_8#delete()
	 * @verifies log the client out
	 */
	@Test
	public void delete_shouldLogTheClientOut() throws Exception {
		Assert.assertTrue(Context.isAuthenticated());
		controller.delete();
		Assert.assertFalse(Context.isAuthenticated());
	}
	
	/**
	 * @see SessionController1_8#get(WebRequest)
	 * @verifies return the session id if the user is authenticated
	 */
	@Test
	public void get_shouldReturnTheSessionIdAndUserIfTheUserIsAuthenticated() throws Exception {
		Assert.assertTrue(Context.isAuthenticated());
		Object ret = controller.get(request);
		Object userProp = PropertyUtils.getProperty(ret, "user");
		Assert.assertEquals(SESSION_ID, PropertyUtils.getProperty(ret, "sessionId"));
		Assert.assertEquals(true, PropertyUtils.getProperty(ret, "authenticated"));
		Assert.assertEquals(Context.getAuthenticatedUser().getUuid(), PropertyUtils.getProperty(userProp, "uuid"));
		Object personProp = PropertyUtils.getProperty(userProp, "person");
		Assert.assertEquals(Context.getAuthenticatedUser().getPerson().getUuid(),
		    PropertyUtils.getProperty(personProp, "uuid"));
	}
	
	@Test
	public void get_shouldReturnLocaleInfoIfTheUserIsAuthenticated() throws Exception {
		Assert.assertTrue(Context.isAuthenticated());
		Object ret = controller.get(request);
		Assert.assertEquals(Context.getLocale(), PropertyUtils.getProperty(ret, "locale"));
		Assert.assertArrayEquals(Context.getAdministrationService().getAllowedLocales().toArray(),
		    ((List<Locale>) PropertyUtils.getProperty(ret, "allowedLocales")).toArray());
	}
	
	/**
	 * @see SessionController1_8#get(WebRequest)
	 * @verifies return the session id if the user is not authenticated
	 */
	@Test
	public void get_shouldReturnTheSessionIdIfTheUserIsNotAuthenticated() throws Exception {
		Context.logout();
		Assert.assertFalse(Context.isAuthenticated());
		Object ret = controller.get(request);
		Assert.assertEquals(SESSION_ID, PropertyUtils.getProperty(ret, "sessionId"));
		Assert.assertEquals(false, PropertyUtils.getProperty(ret, "authenticated"));
	}
	
	@Test
	public void post_shouldSetTheUserLocale() throws Exception {
		Locale newLocale = new Locale("sp");
		String content = "{\"locale\":\"" + newLocale.toString() + "\"}";
		Assert.assertNotEquals(newLocale, Context.getLocale());
		controller.post(new ObjectMapper().readValue(content, HashMap.class));
		Assert.assertEquals(newLocale, Context.getLocale());
	}
	
	@Test(expected = APIException.class)
	public void post_shouldFailWhenSettingIllegalLocale() throws Exception {
		String newLocale = "fOOb@r:";
		String content = "{\"locale\":\"" + newLocale + "\"}";
		controller.post(new ObjectMapper().readValue(content, HashMap.class));
	}
	
	@Test(expected = APIException.class)
	public void post_shouldFailWhenSettingDisallowedLocale() throws Exception {
		String newLocale = "km_KH";
		String content = "{\"locale\":\"" + newLocale + "\"}";
		controller.post(new ObjectMapper().readValue(content, HashMap.class));
	}
}
