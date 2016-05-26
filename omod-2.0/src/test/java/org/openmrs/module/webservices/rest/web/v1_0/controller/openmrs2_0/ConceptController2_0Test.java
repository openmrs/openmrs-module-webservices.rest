/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAttribute;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_0;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

import java.text.SimpleDateFormat;

/**
 * Tests functionality of {@link ConceptController}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class ConceptController2_0Test extends MainResourceControllerTest {

    private ConceptService service;

    @Before
    public void init() {
        service = Context.getConceptService();
    }

    /**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "concept";
    }

    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return RestTestConstants2_0.CONCEPT_UUID;
    }

    /**
     * @see MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return service.getAllConcepts(null, true, false).size();
    }

    /**
     * @verifies add a new attribute to existing concept
     */
    @Test
    public void shouldAddANewConceptAttributeToExistingConcept() throws Exception {
        executeDataSet(RestTestConstants2_0.CONCEPT_ATTRIBUTE_DATA_SET);
        String json = "{ \"uuid\":\"" + RestTestConstants2_0.CONCEPT_UUID + "\","
                + "\"attributes\":[{\"attributeType\":\"" + RestTestConstants2_0.CONCEPT_ATTRIBUTE_TYPE_UUID
                + "\",\"value\":\"2005-01-01\"}]}";
        Concept concept = Context.getConceptService().getConceptByUuid(RestTestConstants2_0.CONCEPT_UUID);
        int before = concept.getAttributes().size();
        handle(newPostRequest(getURI() + "/" + getUuid(), json));
        Assert.assertEquals(before + 1, concept.getAttributes().size());
    }

    /**
     * @verifies edit concept attribute of concept
     */
    @Test
    public void shouldEditConceptAttributeOfConcept() throws Exception {
        executeDataSet(RestTestConstants2_0.CONCEPT_ATTRIBUTE_DATA_SET);
        String json = "{ \"uuid\":\"" + RestTestConstants2_0.CONCEPT_UUID + "\","
                + "\"attributes\":[{\"uuid\":\"" + RestTestConstants2_0.CONCEPT_ATTRIBUTE_UUID + "\"," +
                "\"attributeType\":\"" + RestTestConstants2_0.CONCEPT_ATTRIBUTE_TYPE_UUID
                + "\",\"value\":\"2001-01-01\"}]}";
        Concept concept = Context.getConceptService().getConceptByUuid(RestTestConstants2_0.CONCEPT_UUID);
        int before = concept.getAttributes().size();
        ConceptAttribute conceptAttributeBeforeEdit = Context.getConceptService().getConceptAttributeByUuid(RestTestConstants2_0.CONCEPT_ATTRIBUTE_UUID);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Assert.assertEquals("2011-04-25", simpleDateFormat.format(conceptAttributeBeforeEdit.getValue()));

        handle(newPostRequest(getURI() + "/" + getUuid(), json));

        Assert.assertEquals(before, concept.getAttributes().size());
        ConceptAttribute actualConceptAttribute = Context.getConceptService().getConceptAttributeByUuid(RestTestConstants2_0.CONCEPT_ATTRIBUTE_UUID);
        Assert.assertEquals("2001-01-01", simpleDateFormat.format(actualConceptAttribute.getValue()));
    }


}
