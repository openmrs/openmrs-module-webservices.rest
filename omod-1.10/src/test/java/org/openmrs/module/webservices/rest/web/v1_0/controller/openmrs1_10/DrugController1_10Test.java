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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_10;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

/**
 * Integration tests for the Order resource
 */
public class DrugController1_10Test extends MainResourceControllerTest {

	@Override
	public String getURI() {
		return "drug";
	}

	@Override
	public String getUuid() {
		return "3cfcf118-931c-46f7-8ff6-7b876f0d4202";
	}

	@Override
	public long getAllCount() {
		return conceptService.getAllDrugs(false).size();
	}

	protected static final String DRUG_MAPPINGS = "org/openmrs/api/include/ConceptServiceTest-getDrugMappings.xml";

	protected static final String DRUG_SEARCH_TEST_DATA = "org/openmrs/api/include/ConceptServiceTest-drugSearch.xml";

	private ConceptService conceptService;

	@Before
	public void init() throws Exception {
		conceptService = Context.getConceptService();
	}

	@Test
	@Ignore
	// test fails
	public void getDrugByMapping_shouldReturnADrugThatMatchesTheCodeAndSourceAndTheBestMapType()
			throws Exception {
		executeDataSet(DRUG_MAPPINGS);
		final String sourceUuid = conceptService.getConceptSource(2).getUuid();
		String mapTypeUuids = conceptService.getConceptMapType(1).getUuid()
				+ "," + conceptService.getConceptMapType(2).getUuid();
		SimpleObject results = deserialize(handle(newGetRequest(getURI(),
				new Parameter("s", "getDrugByMapping"), new Parameter("code",
						"WGT234"), new Parameter("source", sourceUuid),
				new Parameter("preferredMapTypes", mapTypeUuids))));
		assertEquals(getUuid(), PropertyUtils.getProperty(
				Util.getResultsList(results).get(0), "uuid"));

		// Lets switch the order of the map types in the list to make sure that
		// if there is no match on the first map type, the logic matches on the
		// second
		// sanity check that actually there will be no match on the first map
		// type in the list
		mapTypeUuids = conceptService.getConceptMapType(2).getUuid();
		results = deserialize(handle(newGetRequest(getURI(), new Parameter("s",
				"getDrugByMapping"), new Parameter("code", "WGT234"),
				new Parameter("source", sourceUuid), new Parameter(
						"preferredMapTypes", mapTypeUuids))));
		assertEquals(0, Util.getResultsSize(results));

		mapTypeUuids = conceptService.getConceptMapType(1).getUuid();
		results = deserialize(handle(newGetRequest(getURI(), new Parameter("s",
				"getDrugByMapping"), new Parameter("code", "WGT234"),
				new Parameter("source", sourceUuid), new Parameter(
						"preferredMapTypes", mapTypeUuids))));
		assertEquals(1, Util.getResultsSize(results));
		assertEquals(getUuid(), PropertyUtils.getProperty(
				Util.getResultsList(results).get(0), "uuid"));
	}

	@Test
	@Ignore
	// test fails
	public void getDrugsByMapping_shouldReturnADrugThatMatchesTheCodeAndSourceAndTheBestMapType()
			throws Exception {
		executeDataSet(DRUG_MAPPINGS);
		final String sourceUuid = conceptService.getConceptSource(1).getUuid();
		String expectedDrugUuid = conceptService.getDrug(3).getUuid();
		String mapTypeUuids = conceptService.getConceptMapType(3).getUuid();
		SimpleObject results = deserialize(handle(newGetRequest(getURI(),
				new Parameter("s", "getDrugsByMapping"), new Parameter("code",
						"CD41003"), new Parameter("source", sourceUuid),
				new Parameter("preferredMapTypes", mapTypeUuids))));

		assertEquals(0, Util.getResultsSize(results));

		mapTypeUuids = conceptService.getConceptMapType(2).getUuid();
		results = deserialize(handle(newGetRequest(getURI(), new Parameter("s",
				"getDrugsByMapping"), new Parameter("code", "CD41003"),
				new Parameter("source", sourceUuid), new Parameter(
						"preferredMapTypes", mapTypeUuids))));

		assertEquals(expectedDrugUuid, PropertyUtils.getProperty(Util
				.getResultsList(results).get(0), "uuid"));
	}

	/**
	 * @verifies get drugs linked to concepts with names that match the phrase
	 * @see ConceptService#getDrugs(String, java.util.Locale, boolean, boolean)
	 */
	@Test
	public void getDrugs_shouldGetDrugsLinkedToConceptsWithNamesThatMatchThePhrase()
			throws Exception {
		SimpleObject results = deserialize(handle(newGetRequest(getURI(),
				new Parameter("s", "default"), new Parameter("q", "amiv"))));
		Assert.assertEquals(1, Util.getResultsSize(results));
		Assert.assertEquals(conceptService.getDrug(2).getUuid(), PropertyUtils
				.getProperty(Util.getResultsList(results).get(0), "uuid"));
	}

	/**
	 * @verifies get drugs linked to concepts with names that match the phrase
	 *           and related locales
	 * @see ConceptService#getDrugs(String, java.util.Locale, boolean, boolean)
	 */
	@Test
	public void getDrugs_shouldGetDrugsLinkedToConceptsWithNamesThatMatchThePhraseAndRelatedLocales()
			throws Exception {
		executeDataSet(DRUG_SEARCH_TEST_DATA);
		final String searchPhrase = "another";
		// Should look only in the exact locale if exactLocale is set to true
		SimpleObject results = deserialize(handle(newGetRequest(getURI(),
				new Parameter("s", "default"),
				new Parameter("q", searchPhrase), new Parameter("locale",
						"fr_CA"), new Parameter("exactLocale", "true"))));
		Assert.assertEquals(0, Util.getResultsSize(results));

		// Should look in broader locale if exactLocale is set to false
		results = deserialize(handle(newGetRequest(getURI(), new Parameter("s",
				"default"), new Parameter("q", searchPhrase), new Parameter(
				"locale", "fr_CA"))));
		Assert.assertEquals(1, Util.getResultsSize(results));
		Assert.assertEquals(conceptService.getDrug(3).getUuid(), PropertyUtils
				.getProperty(Util.getResultsList(results).get(0), "uuid"));
	}

	/**
	 * @verifies get drugs that have mappings with reference term codes that
	 *           match the phrase
	 * @see ConceptService#getDrugs(String, java.util.Locale, boolean, boolean)
	 */
	@Test
	public void getDrugs_shouldGetDrugsThatHaveMappingsWithReferenceTermCodesThatMatchThePhrase()
			throws Exception {
		executeDataSet(DRUG_SEARCH_TEST_DATA);
		SimpleObject results = deserialize(handle(newGetRequest(getURI(),
				new Parameter("s", "default"), new Parameter("q", "XXXZZ"),
				new Parameter("exactLocale", "true"), new Parameter(
						"includeAll", "true"))));
		Assert.assertEquals(1, Util.getResultsSize(results));
		Assert.assertEquals(conceptService.getDrug(11).getUuid(), PropertyUtils
				.getProperty(Util.getResultsList(results).get(0), "uuid"));
	}

	/**
	 * @verifies return all drugs with a matching term code or drug name or
	 *           concept name
	 * @see ConceptService#getDrugs(String, java.util.Locale, boolean, boolean)
	 */
	@Test
	public void getDrugs_shouldReturnAllDrugsWithAMatchingTermCodeOrDrugNameOrConceptName()
			throws Exception {
		executeDataSet(DRUG_SEARCH_TEST_DATA);
		String[] expectedDrugUuids = { conceptService.getDrug(3).getUuid(),
				conceptService.getDrug(11).getUuid(),
				conceptService.getDrug(444).getUuid() };
		SimpleObject results = deserialize(handle(newGetRequest(getURI(),
				new Parameter("s", "default"), new Parameter("q", "ZZZ"),
				new Parameter("exactLocale", "false"), new Parameter(
						"includeAll", "true"))));

		Assert.assertEquals(3, Util.getResultsSize(results));
		List<Object> resultList = Util.getResultsList(results);
		List<String> uuids = Arrays
				.asList(new String[] {
						PropertyUtils.getProperty(resultList.get(0), "uuid")
								.toString(),
						PropertyUtils.getProperty(resultList.get(1), "uuid")
								.toString(),
						PropertyUtils.getProperty(resultList.get(2), "uuid")
								.toString() });
		assertThat(uuids, hasItems(expectedDrugUuids));
	}
}
