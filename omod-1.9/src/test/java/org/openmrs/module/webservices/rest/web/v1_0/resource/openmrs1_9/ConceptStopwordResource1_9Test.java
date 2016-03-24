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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.ConceptStopWord;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

import java.util.List;

/**
 * Contains tests for the {@link ConceptStopWordResource1_9}
 */
public class ConceptStopwordResource1_9Test extends BaseDelegatingResourceTest<ConceptStopwordResource1_9, ConceptStopWord> {

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#newObject()
     */
    @Override
    public ConceptStopWord newObject() {
        List<ConceptStopWord> datatypes = Context.getConceptService().getAllConceptStopWords();
        for (ConceptStopWord datatype : datatypes) {
            if (datatype.getUuid().equals(getUuidProperty())) {
                return datatype;
            }
        }
        return null;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#validateDefaultRepresentation()
     */
    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropPresent("uuid");
        assertPropPresent("display");
        assertPropPresent("value");
        assertPropPresent("locale");
        assertPropEquals("resourceVersion", "1.9");
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#validateFullRepresentation()
     */
    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("uuid");
        assertPropPresent("display");
        assertPropPresent("value");
        assertPropPresent("locale");
        assertPropEquals("resourceVersion", "1.9");
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#getDisplayProperty()
     */
    @Override
    public String getDisplayProperty() {
        return "AN";
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#getUuidProperty()
     */
    @Override
    public String getUuidProperty() {
        return "75af8c00-3ab2-4c1d-8a8d-3c0e5c2972ec";
    }
}
