/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class ConceptSearchHandler1_8Test extends MainResourceControllerTest {

	private ConceptService service;

	private static final String CONCEPT_DATASET_XML = "conceptTestDataSet.xml";

	@Before
	public void init() throws Exception {
		service = Context.getConceptService();
	}

	@Override
	public String getURI() {
		return "concept";
	}

	@Override
	public long getAllCount() {
		return service.getAllConcepts().size();
	}

	/**
	 * @see ConceptSearchHandler1_8#search(GetConceptByReference)
	 */
	@Test
	public void getConceptByReference_shouldReturnAListOfConceptsMatchingTheGivenReferences() throws Exception {
		executeDataSet(CONCEPT_DATASET_XML);
		final String searchString = "947eba27-2b38-43e8-91a9-4dfe3956a32d";
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("references", searchString))));
		List<Object> lists = Util.getResultsList(result);
		Assert.assertEquals(1, lists.size());
		Assert.assertEquals(RestTestConstants1_8.CONCEPT_UUID, PropertyUtils.getProperty(lists.get(0), "uuid"));
	}

	@Test
	public void shouldListAllConcepts() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));

		Assert.assertNotNull(result);
		Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
	}

	@Override
	public String getUuid() {
		return RestTestConstants1_8.CONCEPT_UUID;
	}

}
