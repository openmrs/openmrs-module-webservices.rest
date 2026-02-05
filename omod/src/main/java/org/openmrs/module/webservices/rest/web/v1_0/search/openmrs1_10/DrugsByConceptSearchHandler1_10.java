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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DrugsByConceptSearchHandler1_10 implements SearchHandler {

  public static final String REQUEST_PARAM_CONCEPTS = "concepts";

  @Autowired
  @Qualifier("conceptService")
  ConceptService conceptService;

  private final SearchQuery searchQuery = new SearchQuery.Builder(
      "Allows you to search for drugs associated with a list of concepts")
      .withRequiredParameters(REQUEST_PARAM_CONCEPTS)
      .build();

  private final SearchConfig searchConfig = new SearchConfig("getDrugsByConcepts", RestConstants.VERSION_1 + "/drug",
      Collections.singletonList("1.10.* - 9.*"), searchQuery);

  @Override
  public SearchConfig getSearchConfig() {
    return searchConfig;
  }

  @Override
  public PageableResult search(RequestContext context) throws ResponseException {
    String conceptReferenceParam = context.getParameter(REQUEST_PARAM_CONCEPTS);
    List<Drug> drugs = new ArrayList<Drug>();
    String[] conceptReferences = conceptReferenceParam.split(",");
    for (String ref : conceptReferences) {
      drugs.addAll(getDrugsByConceptRef(ref));
    }

    return new NeedsPaging<Drug>(drugs, context);
  }

  private List<Drug> getDrugsByConceptRef(String conceptRef) {
    List<Drug> drugs = new ArrayList<>();
    String trimmedRef = conceptRef != null ? conceptRef.trim() : "";
    if (StringUtils.isNotBlank(trimmedRef)) {
      Concept concept = conceptService.getConceptByReference(trimmedRef);
      if (concept != null) {
        drugs.addAll(conceptService.getDrugsByConcept(concept));
      }
    }

    return drugs;
  }
}