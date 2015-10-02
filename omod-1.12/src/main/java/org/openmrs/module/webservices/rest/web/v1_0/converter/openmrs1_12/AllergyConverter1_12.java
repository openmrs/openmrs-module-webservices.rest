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
package org.openmrs.module.webservices.rest.web.v1_0.converter.openmrs1_12;

import org.openmrs.annotation.Handler;
import org.openmrs.Allergy;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Outputs a REST-compatible representation of a single Allergy (within an Allergies object)
 */
@Handler(supports = Allergy.class)
public class AllergyConverter1_12 extends OutputOnlyConverter1_12<Allergy> {

    //@Override
    public SimpleObject asRepresentation(Allergy allergy, Representation representation) throws ConversionException {
        SimpleObject ret = new SimpleObject()
                .add("uuid", allergy.getUuid())
                .add("display", allergy.getAllergen().toString());
        if (representation.equals(Representation.DEFAULT)) {
            ret.add("allergen", ConversionUtil.convertToRepresentation(allergy.getAllergen(), Representation.REF));
            ret.add("severity", ConversionUtil.convertToRepresentation(allergy.getSeverity(), Representation.REF));
            ret.add("reactions", ConversionUtil.convertToRepresentation(allergy.getReactions(), Representation.REF));
            ret.add("comment", allergy.getComment());
        }
        else if (representation.equals(Representation.FULL)) {
            ret.add("patient", ConversionUtil.convertToRepresentation(allergy.getPatient(), Representation.REF));
            ret.add("allergen", ConversionUtil.convertToRepresentation(allergy.getAllergen(), Representation.DEFAULT));
            ret.add("severity", ConversionUtil.convertToRepresentation(allergy.getSeverity(), Representation.DEFAULT));
            ret.add("reactions", ConversionUtil.convertToRepresentation(allergy.getReactions(), Representation.DEFAULT));
            ret.add("comment", allergy.getComment());
        }
        return ret;
    }

}
