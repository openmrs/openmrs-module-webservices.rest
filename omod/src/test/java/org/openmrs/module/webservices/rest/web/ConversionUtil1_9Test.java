/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.EncounterResource1_8;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.webservices.rest.web.representation.Representation.DEFAULT;
import static org.openmrs.module.webservices.rest.web.representation.Representation.REF;

public class ConversionUtil1_9Test extends BaseModuleWebContextSensitiveTest {

    @Autowired
    ConceptService conceptService;

    @Test
    public void convertToRepresentation_shouldConvertObsDrugValueAsNull() {

        String codedObsUuid = "9f868194-c88a-458e-9b4d-3b9a716cb0d4";
        String drugObsUuid = "1479068f-4a96-4c2f-8aba-aa32192e946f";

        // create an obs with value coded
        Concept concept = new Concept();
        ConceptName conceptName = new ConceptName();
        conceptName.setName("Test Concept");
        concept.addName(conceptName);
        Obs codedObs = new Obs();
        codedObs.setUuid(codedObsUuid);
        codedObs.setValueCoded(concept);

        // create an obs with value drug
        Drug drug = new Drug();
        Obs drugObs = new Obs();
        drugObs.setUuid(drugObsUuid);
        drugObs.setValueDrug(drug);

        Encounter encounter = new Encounter();
        encounter.addObs(codedObs);
        encounter.addObs(drugObs);

        // the custom rep is expecting a representation where all the values are concepts
        // our hack fix (see https://issues.openmrs.org/browse/RESTWS-816) is to simply to return null as the value for value drug
        SimpleObject result = (SimpleObject) ConversionUtil.convertToRepresentation(encounter, new CustomRepresentation("uuid,obs:(uuid,value:(names))"), new EncounterResource1_8());
        List<SimpleObject> resultObsList = (List<SimpleObject>) result.get("obs");
        assertThat(resultObsList.size(), is(2));
        SimpleObject resultObs1 = resultObsList.get(0);
        SimpleObject resultObs2 = resultObsList.get(1);
        assertTrue(resultObs1.containsKey("value"));
        assertTrue(resultObs2.containsKey("value"));

        // whichever obs is the coded one should have a value, while the drug obs should have a null value
        if (resultObs1.get("uuid").toString().equals(codedObsUuid)) {
            assertNotNull(resultObs1.get("value"));
        }
        else {
            assertNull(resultObs1.get("value"));
        }

        if (resultObs2.get("uuid").toString().equals(codedObsUuid)) {
            assertNotNull(resultObs2.get("value"));
        }
        else {
            assertNull(resultObs2.get("value"));
        }

    }

    @Test
    public void convert_shouldConvertObjectWithNestedCustomRepresentations() {
        String customRep = "(concepts:(creator:(person:(uuid),username),datatype:ref,uuid))";
        DelegatingResourceDescription fullDesc = ConversionUtil.getCustomRepresentationDescription(new CustomRepresentation(customRep));
        assertThat(fullDesc.getProperties().size(), is(1));
        DelegatingResourceDescription.Property conceptsProperty = fullDesc.getProperties().get("concepts");
        assertNotNull(conceptsProperty);
        assertCustomRepresentation(conceptsProperty.getRep(), "(creator:(person:(uuid),username),datatype:ref,uuid)");

        DelegatingResourceDescription conceptDesc = ConversionUtil.getCustomRepresentationDescription((CustomRepresentation) conceptsProperty.getRep());
        assertThat(conceptDesc.getProperties().size(), is(3));
        DelegatingResourceDescription.Property creatorProperty = conceptDesc.getProperties().get("creator");
        assertNotNull(creatorProperty);
        assertCustomRepresentation(creatorProperty.getRep(), "(person:(uuid),username)");

        DelegatingResourceDescription creatorDesc = ConversionUtil.getCustomRepresentationDescription((CustomRepresentation) creatorProperty.getRep());
        assertThat(creatorDesc.getProperties().size(), is(2));
        DelegatingResourceDescription.Property personProperty = creatorDesc.getProperties().get("person");
        assertNotNull(personProperty);

        DelegatingResourceDescription.Property usernameProperty = creatorDesc.getProperties().get("username");
        assertNotNull(usernameProperty);
        assertEquals(DEFAULT, usernameProperty.getRep());

        DelegatingResourceDescription personDesc = ConversionUtil.getCustomRepresentationDescription((CustomRepresentation) personProperty.getRep());
        assertThat(personDesc.getProperties().size(), is(1));
        DelegatingResourceDescription.Property personUuidProperty = personDesc.getProperties().get("uuid");
        assertNotNull(personUuidProperty);
        assertEquals(DEFAULT, personUuidProperty.getRep());

        DelegatingResourceDescription.Property datatypeProperty = conceptDesc.getProperties().get("datatype");
        assertNotNull(datatypeProperty);
        assertEquals(REF, datatypeProperty.getRep());

        DelegatingResourceDescription.Property uuidProperty = conceptDesc.getProperties().get("uuid");
        assertNotNull(uuidProperty);
        assertEquals(DEFAULT, uuidProperty.getRep());
    }

    public void assertCustomRepresentation(Representation representation, String rep) {
        assertNotNull(representation);
        assertTrue(representation instanceof CustomRepresentation);
        CustomRepresentation customRepresentation = (CustomRepresentation) representation;
        assertEquals(rep, customRepresentation.getRepresentation());
    }
}
