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

import org.openmrs.Obs;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ObsResource1_8;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for Obs, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/obs", order = 2, supportedClass = Obs.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class ObsResource1_9 extends ObsResource1_8 {

    /**
     * Annotated setter for Concept
     *
     * @param obs
     * @param value
     */
    @PropertySetter("concept")
    public static void setConcept(Obs obs, Object value) {
        obs.setConcept(new ConceptResource1_9().getByUniqueId((String) value));
    }


}
