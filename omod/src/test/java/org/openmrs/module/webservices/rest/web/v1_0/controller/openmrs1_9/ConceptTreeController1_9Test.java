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
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ConceptTreeController1_9Test extends RestControllerTestUtils {

	private String rootSetUuid;
	private String childSetUuid;
	private String leafConceptUuid;

	@Before
	public void init() throws Exception {
		ConceptService conceptService = Context.getConceptService();

		Concept leaf = new Concept();
		leaf.addName(new ConceptName("Leaf Concept", Locale.ENGLISH));
		leaf.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
		leaf.setConceptClass(conceptService.getConceptClassByName("Misc"));
		conceptService.saveConcept(leaf);
		leafConceptUuid = leaf.getUuid();

		Concept childSet = new Concept();
		childSet.setSet(true);
		childSet.addName(new ConceptName("Child Set", Locale.ENGLISH));
		childSet.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
		childSet.setConceptClass(conceptService.getConceptClassByName("ConvSet"));
		childSet.addSetMember(leaf);
		conceptService.saveConcept(childSet);
		childSetUuid = childSet.getUuid();

		Concept rootSet = new Concept();
		rootSet.setSet(true);
		rootSet.addName(new ConceptName("Root Set", Locale.ENGLISH));
		rootSet.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
		rootSet.setConceptClass(conceptService.getConceptClassByName("ConvSet"));
		rootSet.addSetMember(childSet);
		conceptService.saveConcept(rootSet);
		rootSetUuid = rootSet.getUuid();
	}

	public String getURI() {
		return "concepttree";
	}

	@Test
	public void search_shouldReturnConceptTree() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("concept", rootSetUuid);

		SimpleObject result = deserialize(handle(request));

		assertEquals(rootSetUuid, PropertyUtils.getProperty(result, "uuid"));

		List<Map<String, Object>> rootMembers = result.get("setMembers");
		assertEquals(1, rootMembers.size());

		Map<String, Object> childNode = rootMembers.get(0);
		assertEquals(childSetUuid, childNode.get("uuid"));

		List<Map<String, Object>> childMembers = (List<Map<String, Object>>) childNode.get("setMembers");
		assertEquals(1, childMembers.size());

		Map<String, Object> leafNode = childMembers.get(0);
		assertEquals(leafConceptUuid, leafNode.get("uuid"));

		List<Object> leafMembers = (List<Object>) leafNode.get("setMembers");
		assertEquals(0, leafMembers.size());
	}

	@Test(expected = ObjectNotFoundException.class)
	public void search_shouldThrowExceptionForNonExistentConcept() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("concept", "invalid-concept");
		handle(req);
	}
}
