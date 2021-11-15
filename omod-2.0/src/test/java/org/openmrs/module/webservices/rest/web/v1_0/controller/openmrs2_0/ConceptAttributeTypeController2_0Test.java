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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_0;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link ConceptAttributeTypeController}.
 */
public class ConceptAttributeTypeController2_0Test extends MainResourceControllerTest {

    private ConceptService service;

    /**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "conceptattributetype";
    }

    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return RestTestConstants2_0.CONCEPT_ATTRIBUTE_TYPE_UUID;
    }

    /**
     * @see MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return service.getAllConceptAttributeTypes().size();
    }

    @Before
    public void before() throws Exception {
        executeDataSet(RestTestConstants2_0.CONCEPT_ATTRIBUTE_DATA_SET);
        this.service = Context.getConceptService();
    }

    @Test
    public void shouldCreateConceptAttributeType() throws Exception {
        long originalCount = getAllCount();
        String json = "{\"name\": \"Time Span\",\"description\": \"This attribute type will record the time span for the concept\"," +
                "\"datatypeClassname\": \"org.openmrs.customdatatype.datatype.LongFreeTextDatatype\",\"minOccurs\": 0,\"maxOccurs\": 1," +
                "\"datatypeConfig\": \"default\",\"preferredHandlerClassname\": \"org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler\",\"handlerConfig\": null}";

        MockHttpServletRequest req = request(RequestMethod.POST, getURI());
        req.setContent(json.getBytes());

        Object newConceptAttributeType = deserialize(handle(req));
        Assert.assertNotNull(PropertyUtils.getProperty(newConceptAttributeType, "uuid"));
        Assert.assertEquals(originalCount + 1 , getAllCount());

    }

    @Test
    public void shouldUpdateConceptAttributeType() throws Exception {
        ConceptAttributeType existingConceptAttributeType = service.getConceptAttributeTypeByUuid("858472ac-c220-4c7f-9990-980c176c6099");
        Assert.assertNotNull(existingConceptAttributeType);

        String json = "{\"name\": \"new updated name\",\"description\": \"Dummy description update\",\"datatypeClassname\": \"org.openmrs.customdatatype.datatype.LongFreeTextDatatype\"}";

        handle(newPostRequest(getURI() + "/" + existingConceptAttributeType.getUuid(), json));
        ConceptAttributeType updatedConceptAttributeType = service.getConceptAttributeTypeByUuid("858472ac-c220-4c7f-9990-980c176c6099");

        Assert.assertNotNull(updatedConceptAttributeType);
        Assert.assertEquals("new updated name",updatedConceptAttributeType.getName());
        Assert.assertEquals("Dummy description update",updatedConceptAttributeType.getDescription());
        Assert.assertEquals("org.openmrs.customdatatype.datatype.LongFreeTextDatatype",updatedConceptAttributeType.getDatatypeClassname());
    }
}
