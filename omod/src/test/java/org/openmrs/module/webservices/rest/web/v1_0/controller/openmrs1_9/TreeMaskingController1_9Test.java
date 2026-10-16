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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.openmrs.security.ConceptMaskingTestRule;
import org.openmrs.security.ObsMaskingTestRule;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * What {@code /concepttree} and {@code /obstree} return for a record a rule refuses.
 * <p>
 * At controller level on purpose. Both build their nodes by hand rather than through
 * {@link ConversionUtil}, so the funnel's check never sees the concepts they name, and asserting on
 * the response body is the only way to show the name is absent from all of it rather than from the
 * one field a unit test happened to look at. It also shows the request still succeeds, which is the
 * other half of the problem: a tree reaching a guarded call and throwing would turn a hidden record
 * into a failed request.
 */
public class TreeMaskingController1_9Test extends RestControllerTestUtils {

	/** Concept 230 "Bloodwork", the set both trees are rooted at. */
	private static final String BLOODWORK_UUID = "0f97e14e-cdc2-49ac-9255-b5126f8a5148";

	/** Patient 7, who has the observations in {@code obsTreeDataset.xml}. */
	private static final String PATIENT_UUID = "5946f880-b197-400b-9caa-a3c661d23041";

	private static final int HEMATOCRIT_CONCEPT_ID = 182;

	private static final String HEMATOCRIT_NAME = "Hematocrit";

	private static final int FAVOURITE_FOOD_CONCEPT_ID = 19;

	private static final String FAVOURITE_FOOD_NAME = "FAVORITE FOOD, NON-CODED";

	/** An observation of concept 182 on patient 7, the only one, with value 7. */
	private static final int HEMATOCRIT_OBS_ID = 136;

	private static final String HEMATOCRIT_VALUE = "7.0";

	/** Concept 7 "YES", a set member under Bloodwork and the answer CIVIL STATUS records. */
	private static final int YES_CONCEPT_ID = 7;

	private static final String YES_NAME = "YES";

	@BeforeEach
	public void init() throws Exception {
		executeDataSet("obsTreeDataset.xml");
	}

	@AfterEach
	public void disarmRules() {
		ConceptMaskingTestRule.deniedId = null;
		ObsMaskingTestRule.deniedId = null;
	}

	@Test
	public void conceptTree_shouldNotNameAConceptARuleRefuses() throws Exception {
		assertTrue(conceptTree().contains(HEMATOCRIT_NAME), "the tree should name concept "
		        + HEMATOCRIT_CONCEPT_ID + " when no rule refuses it, or this is testing nothing");

		ConceptMaskingTestRule.deniedId = HEMATOCRIT_CONCEPT_ID;
		String masked = conceptTree();

		assertFalse(masked.contains(HEMATOCRIT_NAME), "a refused concept should be named nowhere, got " + masked);
		assertTrue(masked.contains(ConversionUtil.MASKED_PROPERTY), "and the node should say it is hidden");
	}

	@Test
	public void obsTree_shouldNotNameAConceptARuleRefuses() throws Exception {
		assertTrue(obsTree().contains(FAVOURITE_FOOD_NAME), "the tree should name concept "
		        + FAVOURITE_FOOD_CONCEPT_ID + " when no rule refuses it, or this is testing nothing");

		ConceptMaskingTestRule.deniedId = FAVOURITE_FOOD_CONCEPT_ID;
		String masked = obsTree();

		assertFalse(masked.contains(FAVOURITE_FOOD_NAME), "a refused concept should be named nowhere, got " + masked);
		assertTrue(masked.contains(ConversionUtil.MASKED_PROPERTY));
	}

	/**
	 * A refused <em>reading</em> is dropped rather than masked here, and this records that rather than
	 * claiming otherwise. {@code ObsService.getObservations} is {@code @PostFilter}'ed, so the
	 * observation never reaches this resource to be masked: by the time the tree is built the value is
	 * simply absent, and a reader cannot tell it from a reading never taken. Closing that needs the
	 * service to hand the element over masked; no change in this module can do it.
	 */
	@Test
	public void obsTree_shouldDropRatherThanMaskARefusedObservation() throws Exception {
		assertTrue(obsTree().contains(HEMATOCRIT_VALUE),
		    "the reading should be listed when no rule refuses it, or this is testing nothing");

		ObsMaskingTestRule.deniedId = HEMATOCRIT_OBS_ID;
		String masked = obsTree();

		assertTrue(masked.contains(HEMATOCRIT_NAME), "the concept itself is not refused, only one of its readings");
		assertFalse(masked.contains(HEMATOCRIT_VALUE), "the value is gone, dropped by the service's @PostFilter");
	}

	/**
	 * A rule refusing an <em>answer</em> concept, which the {@code @PostFilter} does not drop: the
	 * observation itself is authorized, so it arrives with the refused concept's name as its value.
	 * Concept 7 is both a set member under Bloodwork and the answer recorded for CIVIL STATUS, so
	 * before the check one response masked the YES node and spelled YES out under CIVIL STATUS.
	 */
	@Test
	public void obsTree_shouldMaskAValueWhoseAnswerConceptIsRefused() throws Exception {
		assertTrue(obsTree().contains("\"value\":\"" + YES_NAME + "\""),
		    "CIVIL STATUS should record " + YES_NAME + " as its value, or this is testing nothing");

		ConceptMaskingTestRule.deniedId = YES_CONCEPT_ID;
		String masked = obsTree();

		assertFalse(masked.contains("\"value\":\"" + YES_NAME + "\""),
		    "a refused answer should be named nowhere, got " + masked);
		assertTrue(masked.contains(ConversionUtil.MASKED_DISPLAY), "the value should say it is hidden");
	}

	private String conceptTree() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, "concepttree");
		request.addParameter("concept", BLOODWORK_UUID);
		return handle(request).getContentAsString();
	}

	private String obsTree() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, "obstree");
		request.addParameter("patient", PATIENT_UUID);
		request.addParameter("concept", BLOODWORK_UUID);
		return handle(request).getContentAsString();
	}
}
