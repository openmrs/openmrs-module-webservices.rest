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
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.notification.Alert;
import org.openmrs.notification.AlertRecipient;
import org.openmrs.notification.AlertService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests CRUD operations for {@link AlertRecipient}s via web service calls
 */
public class AlertRecipientController2_0Test extends MainResourceControllerTest {

	private AlertService service;

	private static final String NEW_ALERT_UUID = "78c97b6b-ef39-47a1-ad77-73494e078ecb";

	private static final String EMPTY_ALERT_UUID = "4876faba-7b14-41e6-b2ad-3050a1030cae";

	private static final String OTHERS_ALERT_UUID = "0e8a1dc0-0d3b-4f3a-9d8c-1d2e3f4a5b6c";

	private static final String OTHERS_RECIPIENT_UUID = "0c1fd7f0-2e4a-4d3a-9d8c-1d2e3f4a5b6c";

	@BeforeEach
	public void setUp() {
		this.service = Context.getAlertService();

		User alertRecipient = Context.getUserService().getUserByUuid(RestTestConstants1_8.USER_UUID);
		AlertRecipient recipient = new AlertRecipient(alertRecipient, false);
		recipient.setUuid(getUuid());

		Alert activeAlert = new Alert();
		activeAlert.setText("New Alert");
		activeAlert.setUuid(NEW_ALERT_UUID);
		activeAlert.setSatisfiedByAny(true);
		activeAlert.setAlertRead(true);
		activeAlert.addRecipient(recipient);
		this.service.saveAlert(activeAlert);

		Alert emptyAlert = new Alert();
		emptyAlert.setText("Empty Alert");
		emptyAlert.setUuid(EMPTY_ALERT_UUID);
		this.service.saveAlert(emptyAlert);
	}

	@Override
	public String getURI() {
		return "alert/" + NEW_ALERT_UUID + "/recipient";
	}

	@Override
	public String getUuid() {
		return "735c2b72-1cc6-422f-b137-af8413427ed5";
	}

	@Override
	public long getAllCount() {
		return 1;
	}

	@Test
	public void shouldAddRecipientToAlert() throws Exception {
		Alert existingAlert = getAlertByUuid(EMPTY_ALERT_UUID);
		assertNotNull(existingAlert);
		assertNull(existingAlert.getRecipients());

		String json = "{\"recipient\": \"" + RestTestConstants1_8.USER_UUID + "\"}";
		handle(newPostRequest("alert/" + EMPTY_ALERT_UUID + "/recipient", json));

		existingAlert = getAlertByUuid(EMPTY_ALERT_UUID);
		assertNotNull(existingAlert);

		assertEquals(1, existingAlert.getRecipients().size());
	}

	@Test
	public void shouldPurgeRecipientFromAlert() throws Exception {
        Alert existingAlert = getAlertByUuid(NEW_ALERT_UUID);
        assertNotNull(existingAlert);

        assertEquals(1, existingAlert.getRecipients().size());

		handle(newDeleteRequest(getURI() + "/" + getUuid()));

		existingAlert = getAlertByUuid(NEW_ALERT_UUID);
		assertNotNull(existingAlert);

		assertEquals(0, existingAlert.getRecipients().size());
	}

	@Test
	public void shouldNotRetrieveARecipientOfAnotherUsersAlertForANonPrivilegedUser() throws Exception {
		saveAlertAddressedToSuperUser();

		// "butch" is a Provider without the Get Alerts privilege and is not a recipient of the
		// alert above, so he must not be able to read its recipient even with the correct uuids
		Context.becomeUser("3-4");

		MockHttpServletRequest req = request(RequestMethod.GET,
				"alert/" + OTHERS_ALERT_UUID + "/recipient/" + OTHERS_RECIPIENT_UUID);
		assertThrows(ObjectNotFoundException.class, () -> handle(req));
	}

	@Test
	public void shouldNotListRecipientsOfAnotherUsersAlertForANonPrivilegedUser() throws Exception {
		saveAlertAddressedToSuperUser();

		// listing recipients resolves the parent alert through the (now scoped) alert resource, so
		// a non-recipient must get a 404 rather than the alert's recipients
		Context.becomeUser("3-4");

		MockHttpServletRequest req = request(RequestMethod.GET, "alert/" + OTHERS_ALERT_UUID + "/recipient");
		assertThrows(ObjectNotFoundException.class, () -> handle(req));
	}

	@Test
	public void shouldRetrieveARecipientOfTheCurrentUsersOwnAlert() throws Exception {
		// "butch" is the recipient of the alert created in setUp, so he may still read it
		Context.becomeUser("3-4");

		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));

		assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertNotNull(PropertyUtils.getProperty(result, "recipient"));
	}

	@Test
	public void shouldReturnNoRecipientsForAnAlertWithoutRecipients() throws Exception {
		// the empty alert from setUp has no recipients; listing them must not fail
		MockHttpServletRequest req = request(RequestMethod.GET, "alert/" + EMPTY_ALERT_UUID + "/recipient");
		SimpleObject result = deserialize(handle(req));

		assertEquals(0, Util.getResultsSize(result));
	}

	private Alert saveAlertAddressedToSuperUser() {
		AlertRecipient recipient = new AlertRecipient(Context.getAuthenticatedUser(), false);
		recipient.setUuid(OTHERS_RECIPIENT_UUID);
		Alert alert = new Alert();
		alert.setText("Super user only alert");
		alert.setUuid(OTHERS_ALERT_UUID);
		alert.addRecipient(recipient);
		return service.saveAlert(alert);
	}

	private Alert getAlertByUuid(String uuid) {
		for (Alert alert : service.getAllAlerts(true)) {
			if (alert.getUuid().equals(uuid))
				return alert;
		}
		return null;
	}
}
