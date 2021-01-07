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
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.EncounterResource1_8;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ConversionUtil1_9Test extends BaseModuleWebContextSensitiveTest {


    @Test
    public void convertToRepresentation_shouldNotFailIfUnableToParseObsValueUsingCustomRep() {

        // create an obs with value coded
        Concept concept = new Concept();
        ConceptName conceptName = new ConceptName();
        conceptName.setName("Test Concept");
        concept.addName(conceptName);
        Obs codedObs = new Obs();
        codedObs.setValueCoded(concept);

        // create an obs with value drug
        Drug drug = new Drug();
        Obs drugObs = new Obs();
        drugObs.setValueDrug(drug);

        Encounter encounter = new Encounter();
        encounter.addObs(codedObs);
        encounter.addObs(drugObs);

        // the custom rep is expecting a representation where all the values are concepts
        // our hack fix (see https://issues.openmrs.org/browse/RESTWS-816) is to simply to return null as the value for value drug
        SimpleObject result = (SimpleObject) ConversionUtil.convertToRepresentation(encounter, new CustomRepresentation("uuid,obs:(value:(names))"), new EncounterResource1_8());
        List<SimpleObject> resultObsList = (List<SimpleObject>) result.get("obs");
        assertThat(resultObsList.size(), is(2));
        SimpleObject resultObs1 = resultObsList.get(0);
        SimpleObject resultObs2 = resultObsList.get(1);
        assertTrue(resultObs1.containsKey("value"));
        assertTrue(resultObs2.containsKey("value"));
        assertTrue(resultObs1.get("value") == null && resultObs2.get("value") != null
                || resultObs1.get("value") != null && resultObs2.get("value") == null);

    }
}
