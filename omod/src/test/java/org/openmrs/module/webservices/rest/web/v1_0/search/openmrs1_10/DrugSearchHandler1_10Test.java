/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_10;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class DrugSearchHandler1_10Test extends MainResourceControllerTest {

  private ConceptService conceptService;

  private static final String ASPIRIN_CONCEPT_UUID = "15f83cd6-64e9-4e06-a5f9-364d3b14a43d";
  private static final String ASPIRIN_DRUG_UUID = "05ec820a-d297-44e3-be6e-698531d9dd3f";

  private static final String TRIOMUNE_CONCEPT_UUID = "d144d24f-6913-4b63-9660-a9108c2bebef";
  private static final String TRIOMUNE_DRUG_UUID = "3cfcf118-931c-46f7-8ff6-7b876f0d4202";

  @Before
  public void init() throws Exception {
    conceptService = Context.getConceptService();
  }

  @Override
  public String getURI() {
    return "drug";
  }

  @Override
  public String getUuid() {
    return ASPIRIN_DRUG_UUID;
  }

  @Override
  public long getAllCount() {
    return conceptService.getAllDrugs(false).size();
  }

  @Test
  public void search_shouldReturnDrugsBySingleConcept() throws Exception {
    MockHttpServletRequest request = request(RequestMethod.GET, getURI());
    request.addParameter("concepts", ASPIRIN_CONCEPT_UUID);

    SimpleObject result = deserialize(handle(request));
    List<Object> extractedResults = result.get("results");

    assertEquals(1, extractedResults.size());
    assertEquals(ASPIRIN_DRUG_UUID, PropertyUtils.getProperty(extractedResults.get(0), "uuid"));
  }

  @Test
  public void search_shouldReturnDrugsByMultipleConcepts() throws Exception {
    MockHttpServletRequest request = request(RequestMethod.GET, getURI());
    String multipleConcepts = ASPIRIN_CONCEPT_UUID + "," + TRIOMUNE_CONCEPT_UUID;
    request.addParameter("concepts", multipleConcepts);

    SimpleObject result = deserialize(handle(request));
    List<Object> extractedResults = result.get("results");

    List<String> drugUuids = extractedResults.stream()
        .map(hit -> (String) ((Map) hit).get("uuid"))
        .collect(Collectors.toList());

    assertEquals(2, extractedResults.size());
    assertTrue(drugUuids.contains(ASPIRIN_DRUG_UUID));
    assertTrue(drugUuids.contains(TRIOMUNE_DRUG_UUID));
  }

  @Test
  public void search_shouldReturnEmptyResultsWhenInvalidConcepts() throws Exception {
    MockHttpServletRequest request = request(RequestMethod.GET, getURI());
    request.addParameter("concepts", "invalid-concept-ref");

    SimpleObject result = deserialize(handle(request));
    List<Object> extractedResults = result.get("results");

    assertEquals(0, extractedResults.size());
  }

  @Test
  public void search_shouldReturnResultsForValidAndInvalidConceptsCombined() throws Exception {
    MockHttpServletRequest request = request(RequestMethod.GET, getURI());
    String validAndInvalidConcepts = ASPIRIN_CONCEPT_UUID + "," + "invalid-concept-ref";
    request.addParameter("concepts", validAndInvalidConcepts);

    SimpleObject result = deserialize(handle(request));
    List<Object> extractedResults = result.get("results");

    assertEquals(1, extractedResults.size());
    assertEquals(ASPIRIN_DRUG_UUID, PropertyUtils.getProperty(extractedResults.get(0), "uuid"));
  }
}