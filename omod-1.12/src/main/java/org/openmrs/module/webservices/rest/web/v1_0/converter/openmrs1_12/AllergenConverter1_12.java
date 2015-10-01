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
<<<<<<< HEAD
import org.openmrs.Allergen;
=======
import org.openmrs.allergyapi.Allergen;
>>>>>>> origin/TRUNK-4747-A
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Outputs a REST-compatible representation of an Allergen
 */
@Handler(supports = Allergen.class)
public class AllergenConverter1_12 extends OutputOnlyConverter1_12<Allergen> {

    //@Override
    public SimpleObject asRepresentation(Allergen allergen, Representation representation) throws ConversionException {
        SimpleObject ret = new SimpleObject();
        ret.add("type", allergen.getAllergenType());
        ret.add("codedAllergen", ConversionUtil.convertToRepresentation(allergen.getCodedAllergen(), representation));
        ret.add("nonCodedAllergen", allergen.getNonCodedAllergen());
        return ret;
    }

}
