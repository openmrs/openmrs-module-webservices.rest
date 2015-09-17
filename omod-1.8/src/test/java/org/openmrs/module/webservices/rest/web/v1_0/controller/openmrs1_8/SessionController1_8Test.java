package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
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
                Assert.assertEquals(Context.getAuthenticatedUser().getPerson().getUuid(), PropertyUtils.getProperty(personProp, "uuid"));
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
}
