/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_6;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.MedicationDispense;
import org.openmrs.api.MedicationDispenseService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_6;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Tests functionality of {@link MedicationDispenseController2_6}.
 */
public class MedicationDispenseController2_6Test extends MainResourceControllerTest {

	private MedicationDispenseService medicationDispenseService;

	@Before
	public void before() throws Exception {
		executeDataSet(RestTestConstants2_6.MEDICATION_DISPENSE_TEST_DATA_XML);
		this.medicationDispenseService = Context.getService(MedicationDispenseService.class);
	}

	@Override
	public String getURI() {
		return "medicationdispense";
	}

	@Override
	public String getUuid() {
		return RestTestConstants2_6.MEDICATION_DISPENSE_UUID;
	}

	@Override
	public long getAllCount() {
		return 0;
	}

	/**
	 * @see MainResourceControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}

	@Test
	public void shouldGetMedicationDispenseByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), result.get("uuid"));
	}

	@Test
	public void shouldCreateMedicationDispense() throws Exception {
		long before = medicationDispenseService
		        .getMedicationDispenseByCriteria(
		            new org.openmrs.parameter.MedicationDispenseCriteria()).size();

		SimpleObject dispenseSource = new SimpleObject();
		dispenseSource.add("patient", "da7f524f-27ce-4bb2-86d6-6d1d05312bd5");
		dispenseSource.add("concept", "3ee9057f-87a2-4039-a2ee-821e778aded4");
		dispenseSource.add("status", "d93d0035-d666-4f48-92e8-aa290d9c5759");

		String json = new ObjectMapper().writeValueAsString(dispenseSource);

		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());

		SimpleObject newDispense = deserialize(handle(req));
		Assert.assertNotNull(newDispense.get("uuid"));
		Assert.assertNotNull(
		    medicationDispenseService.getMedicationDispenseByUuid((String) newDispense.get("uuid")));
	}

	@Test
	public void shouldUpdateMedicationDispense() throws Exception {
		MedicationDispense dispense = medicationDispenseService
		        .getMedicationDispenseByUuid(getUuid());
		Assert.assertNull(dispense.getDosingInstructions());

		String json = "{ \"dosingInstructions\": \"Take with food\" }";
		SimpleObject response = deserialize(
		    handle(newPostRequest(getURI() + "/" + getUuid(), json)));

		MedicationDispense updated = medicationDispenseService
		        .getMedicationDispenseByUuid((String) response.get("uuid"));
		Assert.assertEquals("Take with food", updated.getDosingInstructions());
	}

	@Test
	public void shouldVoidMedicationDispense() throws Exception {
		MedicationDispense dispense = medicationDispenseService.getMedicationDispenseByUuid(getUuid());
		Assert.assertFalse(dispense.isVoided());

		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("reason", "unit test");
		handle(req);

		dispense = medicationDispenseService.getMedicationDispenseByUuid(getUuid());
		Assert.assertTrue(dispense.isVoided());
		Assert.assertEquals("unit test", dispense.getVoidReason());
		Assert.assertNotNull(dispense.getDateVoided());
	}

	@Test
	public void shouldPurgeMedicationDispense() throws Exception {
		Assert.assertNotNull(medicationDispenseService.getMedicationDispenseByUuid(getUuid()));

		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("purge", "true");
		handle(req);

		Assert.assertNull(medicationDispenseService.getMedicationDispenseByUuid(getUuid()));
	}

	@Test
	public void shouldSearchByPatient() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("patient", "da7f524f-27ce-4bb2-86d6-6d1d05312bd5");
		SimpleObject result = deserialize(handle(request));
		List<Object> results = result.get("results");
		Assert.assertTrue(results.size() > 0);
	}

	@Test
	public void shouldSetFormNamespaceAndPathOnCreate() throws Exception {
		SimpleObject dispenseSource = new SimpleObject();
		dispenseSource.add("patient", "da7f524f-27ce-4bb2-86d6-6d1d05312bd5");
		dispenseSource.add("concept", "3ee9057f-87a2-4039-a2ee-821e778aded4");
		dispenseSource.add("status", "d93d0035-d666-4f48-92e8-aa290d9c5759");
		dispenseSource.add("formFieldNamespace", "my.form.app");
		dispenseSource.add("formFieldPath", "dispenseForm/quantity");

		String json = new ObjectMapper().writeValueAsString(dispenseSource);

		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());

		SimpleObject newDispense = deserialize(handle(req));
		String uuid = newDispense.get("uuid");

		MedicationDispense saved = medicationDispenseService.getMedicationDispenseByUuid(uuid);
		Assert.assertEquals("my.form.app", saved.getFormFieldNamespace());
		Assert.assertEquals("dispenseForm/quantity", saved.getFormFieldPath());
	}

	@Test
	public void shouldUpdateFormNamespaceAndPath() throws Exception {
		MedicationDispense dispense = medicationDispenseService.getMedicationDispenseByUuid(getUuid());
		Assert.assertNull(dispense.getFormFieldNamespace());
		Assert.assertNull(dispense.getFormFieldPath());

		String json = "{ \"formFieldNamespace\": \"my.form.app\", \"formFieldPath\": \"dispenseForm/quantity\" }";
		SimpleObject response = deserialize(handle(newPostRequest(getURI() + "/" + getUuid(), json)));

		MedicationDispense updated = medicationDispenseService
		        .getMedicationDispenseByUuid((String) response.get("uuid"));
		Assert.assertEquals("my.form.app", updated.getFormFieldNamespace());
		Assert.assertEquals("dispenseForm/quantity", updated.getFormFieldPath());
	}

	@Test
	public void shouldReturnFormNamespaceAndPathInFullRepresentation() throws Exception {
		String json = "{ \"formFieldNamespace\": \"my.form.app\", \"formFieldPath\": \"dispenseForm/quantity\" }";
		handle(newPostRequest(getURI() + "/" + getUuid(), json));

		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter("v", "full");
		SimpleObject result = deserialize(handle(req));

		Assert.assertEquals("my.form.app", result.get("formFieldNamespace"));
		Assert.assertEquals("dispenseForm/quantity", result.get("formFieldPath"));
	}
}
