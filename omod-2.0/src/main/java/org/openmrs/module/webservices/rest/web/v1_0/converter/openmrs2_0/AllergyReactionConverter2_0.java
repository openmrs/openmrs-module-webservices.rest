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
package org.openmrs.module.webservices.rest.web.v1_0.converter.openmrs2_0;

import org.openmrs.Concept;
import org.openmrs.annotation.Handler;
import org.openmrs.AllergyReaction;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Outputs a REST-compatible representation of an AllergyReaction
 */
@Handler(supports = AllergyReaction.class)
public class AllergyReactionConverter2_0 extends OutputOnlyConverter2_0<AllergyReaction> {

    //@Override
    public SimpleObject asRepresentation(AllergyReaction allergyReaction, Representation representation) throws ConversionException {
        SimpleObject ret = new SimpleObject();
        ret.add("reaction", ConversionUtil.convertToRepresentation(allergyReaction.getReaction(), representation));
        ret.add("reactionNonCoded", allergyReaction.getReactionNonCoded());
        return ret;
    }
}
