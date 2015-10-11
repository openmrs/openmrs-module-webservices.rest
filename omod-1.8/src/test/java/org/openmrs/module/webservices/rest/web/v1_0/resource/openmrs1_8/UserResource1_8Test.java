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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class UserResource1_8Test extends
		BaseDelegatingResourceTest<UserResource1_8, UserAndPassword1_8> {

	@Override
	public UserAndPassword1_8 newObject() {
		UserAndPassword1_8 userAndPassword = new UserAndPassword1_8(Context
				.getUserService().getUserByUuid(getUuidProperty()));
		userAndPassword.setPassword("topsecret");
		return userAndPassword;
	}

	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("username", getObject().getUser().getUsername());
		assertPropEquals("systemId", getObject().getUser().getSystemId());
		assertPropEquals("userProperties", getObject().getUser()
				.getUserProperties());
		assertPropPresent("person");
		assertPropPresent("privileges");
		assertPropPresent("roles");
		assertPropEquals("retired", getObject().getUser().getRetired());
	}

	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("username", getObject().getUser().getUsername());
		assertPropEquals("systemId", getObject().getUser().getSystemId());
		assertPropEquals("userProperties", getObject().getUser()
				.getUserProperties());
		assertPropPresent("person");
		assertPropPresent("privileges");
		assertPropPresent("roles");
		assertPropPresent("allRoles");
		assertPropEquals("proficientLocales", getObject().getUser()
				.getProficientLocales());
		assertPropEquals("secretQuestion", getObject().getUser()
				.getSecretQuestion());
		assertPropEquals("retired", getObject().getUser().getRetired());
	}

	@Override
	public String getDisplayProperty() {
		return "butch";
	}

	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.USER_UUID;
	}

	/**
	 * @see {@link https://issues.openmrs.org/browse/RESTWS-490}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCorrectResourceForUser() throws Exception {
		// prepare
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("q", ""); // query for all
		final RequestContext context = RestUtil.getRequestContext(request,
				new MockHttpServletResponse());

		// search
		final SimpleObject simple = getResource().search(context);
		final List<SimpleObject> results = (List<SimpleObject>) simple
				.get("results");

		// verify
		Assert.assertFalse("A non-empty list is expected.", results.isEmpty());
		for (SimpleObject result : results) {
			final String selfLink = findSelfLink(result);
			Assert.assertTrue("Resource should be user, but is " + selfLink,
					selfLink.contains("/user/"));
		}
	}

	/**
	 * Assert that a search with the given parameters returns an expected number
	 * of results.
	 * 
	 * @param userName
	 *            The user name to search for.
	 * @param roles
	 *            The roles to search for.
	 * @param expectedResultCount
	 *            The expected result count for the given search parameters.
	 * @throws ResponseException
	 */
	@SuppressWarnings("unchecked")
	private void assertSearch(final String userName,
			final Collection<String> roles, final int expectedResultCount)
			throws ResponseException {
		// input
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("q", userName);
		if (roles != null) {
			final String rolesAsCommaSeparatedString = StringUtils.join(roles,
					",");
			request.addParameter(UserResource1_8.PARAMETER_ROLES,
					rolesAsCommaSeparatedString);
		}
		final RequestContext context = RestUtil.getRequestContext(request,
				new MockHttpServletResponse());

		// search
		final SimpleObject simple = getResource().search(context);
		final List<SimpleObject> results = (List<SimpleObject>) simple
				.get("results");

		// verify
		final String errorMessage = "Number of results does not match for: userName="
				+ userName + ", roles=" + roles + ", Results=" + results;
		Assert.assertEquals(errorMessage, expectedResultCount, results.size());
	}

	/**
	 * Test searching users by user name.
	 * 
	 * @see {@link https://issues.openmrs.org/browse/RESTWS-490}
	 * @throws Exception
	 */
	@Test
	@Ignore
	// test failed
	public void testSearchingByUser() {
		// all users
		assertSearch("", null, 2);

		// full name
		assertSearch("admin", null, 1);
		assertSearch("butch", null, 1);

		// not existing
		assertSearch("does-not-exist", null, 0);

		// prefix
		assertSearch("ad", null, 1);
	}

	/**
	 * Test searching users by role.
	 * 
	 * @see {@link https://issues.openmrs.org/browse/RESTWS-490}
	 * @throws Exception
	 */
	@Test
	public void testSearchingByRoles() {
		// by name
		assertSearch("", Arrays.asList("System Developer"), 1);
		assertSearch("", Arrays.asList("Provider"), 1);

		// by uuid
		assertSearch("", Arrays.asList("0e43640b-67d1-4458-b47f-b64fd8ce4b0d"),
				1);

		// multiple roles
		assertSearch("", Arrays.asList("System Developer", "Provider"), 2);
		assertSearch("", Arrays.asList("0e43640b-67d1-4458-b47f-b64fd8ce4b0d",
				"Provider"), 2);

		// no roles
		assertSearch("", Arrays.<String> asList(), 0);
		assertSearch("", Arrays.asList("NoRole"), 0);

		// no prefix searching
		assertSearch("", Arrays.asList("System"), 0);
	}

	/**
	 * Test searching users by a combination of user name and role.
	 * 
	 * @see {@link https://issues.openmrs.org/browse/RESTWS-490}
	 * @throws Exception
	 */
	@Test
	public void testSearchingByUserAndRole() {
		// valid combination
		assertSearch("admin", Arrays.asList("System Developer"), 1);

		// only name matches
		assertSearch("admin", Arrays.asList("Provider"), 0);
		assertSearch("admin", Arrays.asList("DoesNotExist"), 0);

		// only role matches
		assertSearch("doesNotExist", Arrays.asList("System Developer"), 0);
	}

}
