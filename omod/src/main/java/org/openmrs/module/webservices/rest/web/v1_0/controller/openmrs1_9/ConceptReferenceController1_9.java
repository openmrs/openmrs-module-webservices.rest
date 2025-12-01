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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * This controller allows the fetching of concepts via reference strings that can be either a UUID
 * or concept mapping. It then returns a map of those reference strings to the underlying concept.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/conceptreferences")
public class ConceptReferenceController1_9 extends BaseRestController {
	
	@RequestMapping(method = { RequestMethod.GET })
	@ResponseBody
	public Object getConceptReferences(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false, name = "references") String references) {
        return handleRequest(request, response, references);
	}

    @RequestMapping(method = { RequestMethod.POST }, consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseBody
    public Object getConceptReferencesViaForm(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false, name = "references") String references) {
        return handleRequest(request, response, references);
    }

    @RequestMapping(method = { RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getConceptReferencesViaJson(HttpServletRequest request, HttpServletResponse response, @RequestBody SimpleObject body) {
        return handleRequest(request, response, (List<String>) body.get("references"));
    }
	
	private void addResult(SimpleObject results, String conceptReference, Concept concept, Representation rep) {
		results.put(conceptReference,
				ConversionUtil.convertToRepresentation(concept, rep == null ? new DefaultRepresentation() : rep));
	}

    private SimpleObject handleRequest(HttpServletRequest request, HttpServletResponse response, String references) {
        String[] conceptReferences = new String[0];

        if (StringUtils.isNotBlank(references)) {
            conceptReferences = references.split(",");
        }

        if (conceptReferences.length > 0) {
            return handleRequest(request, response, Arrays.asList(conceptReferences));
        }

        return new SimpleObject(0);
    }

    private SimpleObject handleRequest(HttpServletRequest request, HttpServletResponse response, List<String> conceptReferences) {
        if (!conceptReferences.isEmpty()) {
            Representation representation = RestUtil.getRequestContext(request, response).getRepresentation();
            ConceptService conceptService = Context.getConceptService();
            SimpleObject results = new SimpleObject(conceptReferences.size());

            for (String conceptReference : conceptReferences) {
                if (StringUtils.isBlank(conceptReference)) {
                    continue;
                }
                // handle UUIDs
                if (RestUtil.isValidUuid(conceptReference)) {
                    Concept concept = conceptService.getConceptByUuid(conceptReference);
                    if (concept != null) {
                        addResult(results, conceptReference, concept, representation);
                        continue;
                    }
                }
                // handle mappings
                int idx = conceptReference.indexOf(':');
                if (idx >= 0 && idx < conceptReference.length() - 1) {
                    String conceptSource = conceptReference.substring(0, idx);
                    String conceptCode = conceptReference.substring(idx + 1);
                    Concept concept = conceptService.getConceptByMapping(conceptCode, conceptSource, false);
                    if (concept != null) {
                        addResult(results, conceptReference, concept, representation);
                    }
                }
            }

            return results;
        }

        return new SimpleObject(0);
    }

}
