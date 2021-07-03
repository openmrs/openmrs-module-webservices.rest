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
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class AddressTemplateController2_0Test extends RestControllerTestUtils {

	@Test
	public void shouldGetCurrentAddressTemplate() throws Exception {
		// get initial address template
		String initialAddressTemplate = Context.getLocationService().getAddressTemplate();

		// make GET call
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));

		// assert response
		assertNotNull(result);
		assertEquals(initialAddressTemplate, PropertyUtils.getProperty(result, "addressTemplateXml"));
	}

	@Test
	public void shouldUpdateCurrentAddressTemplate() throws Exception {
		// set initial address template
		String initialAddressTemplate = Context.getLocationService().getAddressTemplate();
		String newAddressTemplate = "<org.openmrs.layout.address.AddressTemplate>\n     <nameMappings class=\"properties\">\n       <property name=\"postalCode\" value=\"Location.postalCode\"/>\n       <property name=\"address2\" value=\"Location.address2\"/>\n       <property name=\"address1\" value=\"Location.address1\"/>\n       <property name=\"country\" value=\"Location.country\"/>\n       <property name=\"stateProvince\" value=\"Location.stateProvince\"/>\n       <property name=\"cityVillage\" value=\"Location.cityVillage\"/>\n     </nameMappings>\n     <sizeMappings class=\"properties\">\n       <property name=\"postalCode\" value=\"10\"/>\n       <property name=\"address2\" value=\"40\"/>\n       <property name=\"address1\" value=\"40\"/>\n       <property name=\"country\" value=\"10\"/>\n       <property name=\"stateProvince\" value=\"10\"/>\n       <property name=\"cityVillage\" value=\"10\"/>\n     </sizeMappings>\n     <lineByLineFormat>\n       <string>address1</string>\n       <string>address2</string>\n       <string>cityVillage stateProvince country postalCode</string>\n     </lineByLineFormat>\n   </org.openmrs.layout.address.AddressTemplate>";
		assertNotEquals(initialAddressTemplate, newAddressTemplate); // setting different template
		Context.getLocationService().saveAddressTemplate(newAddressTemplate);

		// make POST call
		String json = "{\n    \"addressTemplateXml\": \"<org.openmrs.layout.address.AddressTemplate>\\n     <nameMappings class=\\\"properties\\\">\\n       <property name=\\\"postalCode\\\" value=\\\"Location.postalCode\\\"/>\\n       <property name=\\\"address2\\\" value=\\\"Location.address2\\\"/>\\n       <property name=\\\"address1\\\" value=\\\"Location.address1\\\"/>\\n       <property name=\\\"country\\\" value=\\\"Location.country\\\"/>\\n       <property name=\\\"stateProvince\\\" value=\\\"Location.stateProvince\\\"/>\\n       <property name=\\\"cityVillage\\\" value=\\\"Location.cityVillage\\\"/>\\n     </nameMappings>\\n     <sizeMappings class=\\\"properties\\\">\\n       <property name=\\\"postalCode\\\" value=\\\"10\\\"/>\\n       <property name=\\\"address2\\\" value=\\\"40\\\"/>\\n       <property name=\\\"address1\\\" value=\\\"40\\\"/>\\n       <property name=\\\"country\\\" value=\\\"10\\\"/>\\n       <property name=\\\"stateProvince\\\" value=\\\"10\\\"/>\\n       <property name=\\\"cityVillage\\\" value=\\\"10\\\"/>\\n     </sizeMappings>\\n     <lineByLineFormat>\\n       <string>address1</string>\\n       <string>address2</string>\\n       <string>cityVillage stateProvince country postalCode</string>\\n     </lineByLineFormat>\\n   </org.openmrs.layout.address.AddressTemplate>\"\n}";
		handle(newPostRequest(getURI(), json));

		// assert updated address template
		String xml = Context.getLocationService().getAddressTemplate();
		assertEquals(newAddressTemplate, xml);
	}

	private String getURI() {
		return "addresstemplate";
	}
}
