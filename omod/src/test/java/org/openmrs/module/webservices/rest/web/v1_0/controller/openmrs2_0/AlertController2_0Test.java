/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.User;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.notification.Alert;
import org.openmrs.notification.AlertRecipient;
import org.openmrs.notification.AlertService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests CRUD operations for {@link Alert}s via web service calls
 */
public class AlertController2_0Test extends MainResourceControllerTest {

	private AlertService service;

	private static final String EXPIRED_ALERT_UUID = "b24d1550-90ee-4e9c-be7a-5a0bf7e84ec1";

	private static final String OTHER_USER_ALERT_UUID = "0e8a1dc0-0d3b-4f3a-9d8c-1d2e3f4a5b6c";

	@BeforeEach
	public void setUp() {
		this.service = Context.getAlertService();

		User alertRecipient = Context.getUserService().getUserByUuid(RestTestConstants1_8.USER_UUID);

		Alert activeAlert = new Alert();
		activeAlert.setText("New Alert");
		activeAlert.setUuid(getUuid());
		activeAlert.setRecipients(new HashSet<>());
		activeAlert.setSatisfiedByAny(true);
		activeAlert.setAlertRead(true);
		activeAlert.addRecipient(alertRecipient);
		this.service.saveAlert(activeAlert);

		Alert expiredAlert = new Alert();
		expiredAlert.setText("Expired Alert");
		expiredAlert.setUuid(EXPIRED_ALERT_UUID);
		expiredAlert.setRecipients(new HashSet<>());
		expiredAlert.setDateToExpire(new Date(946681200000L)); // 01/01/2000
		expiredAlert.addRecipient(alertRecipient);
		this.service.saveAlert(expiredAlert);
	}

	@Override
	public String getURI() {
		return "alert";
	}

	@Override
	public String getUuid() {
		return "78c97b6b-ef39-47a1-ad77-73494e078ecb";
	}

	@Override
	public long getAllCount() {
		return 1;
	}

	@Test
	public void shouldGetAllActiveAlerts() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));

		assertEquals(1, Util.getResultsSize(result));

		List<Object> results = Util.getResultsList(result);
		assertCorrectAlertRepresentation(results.get(0));
	}

	@Test
	public void shouldGetAllAlertsIncludingExpired() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("includeExpired", "true");
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));

		assertEquals(2, Util.getResultsSize(result));
	}

	@Test
	public void shouldGetAlertByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));

		assertCorrectAlertRepresentation(result);
	}

	@Test
	public void shouldOnlyReturnTheCurrentUsersAlertsToANonPrivilegedUser() throws Exception {
		// An alert addressed to the super user, who is not "butch"
		Alert othersAlert = saveAlertFor(Context.getAuthenticatedUser(), "Super user only alert",
				OTHER_USER_ALERT_UUID);

		// "butch" is a Provider without the Get Alerts privilege; he is a recipient of the two
		// alerts created in setUp, but not of the alert above
		Context.becomeUser("3-4");

		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("includeExpired", "true");
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));

		Set<String> returnedUuids = new HashSet<>();
		for (Object alert : Util.getResultsList(result)) {
			returnedUuids.add((String) PropertyUtils.getProperty(alert, "uuid"));
		}

		assertEquals(2, returnedUuids.size());
		assertTrue(returnedUuids.contains(getUuid()));
		assertTrue(returnedUuids.contains(EXPIRED_ALERT_UUID));
		assertFalse(returnedUuids.contains(othersAlert.getUuid()));
	}

	@Test
	public void shouldNotReturnAnotherUsersAlertByUuidToANonPrivilegedUser() throws Exception {
		Alert othersAlert = saveAlertFor(Context.getAuthenticatedUser(), "Super user only alert",
				OTHER_USER_ALERT_UUID);

		// "butch" is not a recipient of the alert above and cannot manage alerts
		Context.becomeUser("3-4");

		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + othersAlert.getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);

		assertThrows(ObjectNotFoundException.class, () -> handle(req));
	}

	@Test
	public void shouldOnlyReturnTheCurrentUsersActiveAlertsToANonPrivilegedUserOnGetAll() throws Exception {
		// An alert addressed to the super user, who is not "butch"
		Alert othersAlert = saveAlertFor(Context.getAuthenticatedUser(), "Super user only alert",
				OTHER_USER_ALERT_UUID);

		Context.becomeUser("3-4");

		// no includeExpired parameter, so this exercises doGetAll rather than doSearch
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));

		Set<String> returnedUuids = new HashSet<>();
		for (Object alert : Util.getResultsList(result)) {
			returnedUuids.add((String) PropertyUtils.getProperty(alert, "uuid"));
		}

		// only butch's active alert: not the expired one, and not the super user's alert
		assertEquals(1, returnedUuids.size());
		assertTrue(returnedUuids.contains(getUuid()));
		assertFalse(returnedUuids.contains(EXPIRED_ALERT_UUID));
		assertFalse(returnedUuids.contains(othersAlert.getUuid()));
	}

	@Test
	public void shouldReturnTheCurrentUsersOwnAlertByUuidToANonPrivilegedUser() throws Exception {
		// "butch" is a recipient of the alert created in setUp, so he may still read it by uuid
		Context.becomeUser("3-4");

		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));

		assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
	}

	@Test
	public void shouldReturnEveryUsersAlertsToANonSuperUserHoldingTheGetAlertsPrivilege() throws Exception {
		// An alert addressed to the super user, who is not "butch"
		Alert othersAlert = saveAlertFor(Context.getAuthenticatedUser(), "Super user only alert",
				OTHER_USER_ALERT_UUID);

		// "butch" is not a super user; granting him Get Alerts (here via a proxy privilege) must let
		// him list every user's alerts, exercising the privileged branch without the super-user bypass
		Context.becomeUser("3-4");
		Context.addProxyPrivilege(PrivilegeConstants.GET_ALERTS);
		try {
			MockHttpServletRequest req = request(RequestMethod.GET, getURI());
			req.addParameter("includeExpired", "true");
			req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
			SimpleObject result = deserialize(handle(req));

			Set<String> returnedUuids = new HashSet<>();
			for (Object alert : Util.getResultsList(result)) {
				returnedUuids.add((String) PropertyUtils.getProperty(alert, "uuid"));
			}

			assertTrue(returnedUuids.contains(othersAlert.getUuid()));
		} finally {
			Context.removeProxyPrivilege(PrivilegeConstants.GET_ALERTS);
		}
	}

	private Alert saveAlertFor(User recipient, String text, String uuid) {
		Alert alert = new Alert();
		alert.setText(text);
		alert.setUuid(uuid);
		alert.setRecipients(new HashSet<>());
		alert.addRecipient(recipient);
		return service.saveAlert(alert);
	}

	@Test
	public void shouldCreateAlert() throws Exception {
		long originalCount = service.getAllAlerts(true).size();
		String json = "{\"text\": \"New alert\",\"satisfiedByAny\": true,\"recipients\": [{\"recipient\":\""
				+ RestTestConstants1_8.USER_UUID + "\"}]}";

		SimpleObject newAlert = deserialize(handle(newPostRequest(getURI(), json)));

		assertNotNull(PropertyUtils.getProperty(newAlert, "uuid"));

		long newCount = service.getAllAlerts(true).size();
		assertEquals(originalCount + 1, newCount);
	}

	@Test
	public void shouldUpdateAlert() throws Exception {
		Alert alert = getAlertByUuid(getUuid());
		assertNotNull(alert);
		assertNull(alert.getDateToExpire());

		String json = "{\"dateToExpire\": \"2021-06-01T17:51:43.000+0200\"}";

		SimpleObject expiredAlert = deserialize(handle(newPostRequest(getURI() + "/" + getUuid(), json)));

		assertNotNull(PropertyUtils.getProperty(expiredAlert, "dateToExpire"));
	}

	@Test
	public void shouldPurgeAlert() throws Exception {
		assertNotNull(getAlertByUuid(getUuid()));

		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "true")));

		assertNull(getAlertByUuid(getUuid()));
	}

	private void assertCorrectAlertRepresentation(Object result) throws Exception {
		Alert alert = getAlertByUuid(getUuid());
		assertNotNull(alert);

		assertEquals(alert.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertEquals(alert.getText(), PropertyUtils.getProperty(result, "text"));
		assertEquals(alert.getSatisfiedByAny(), PropertyUtils.getProperty(result, "satisfiedByAny"));
		assertEquals(alert.getAlertRead(), PropertyUtils.getProperty(result, "alertRead"));
		assertEquals(alert.getDateToExpire(), PropertyUtils.getProperty(result, "dateToExpire"));
		assertNotNull(PropertyUtils.getProperty(result, "creator"));
		assertNotNull(PropertyUtils.getProperty(result, "dateCreated"));
		assertNotNull(PropertyUtils.getProperty(result, "recipients"));

		List<Object> recipients = (List<Object>) PropertyUtils.getProperty(result, "recipients");
		List<AlertRecipient> expectedRecipients = new ArrayList<>(alert.getRecipients());
		assertEquals(1, recipients.size());
		assertEquals(expectedRecipients.get(0).getUuid(), PropertyUtils.getProperty(recipients.get(0), "uuid"));
		assertNotNull(PropertyUtils.getProperty(recipients.get(0), "recipient"));
	}

	private Alert getAlertByUuid(String uuid) {
		for (Alert alert : service.getAllAlerts(true)) {
			if (alert.getUuid().equals(uuid))
				return alert;
		}
		return null;
	}
}
