/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.UserResource2_0;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;

/**
 * {@link Resource} for User, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/user", supportedClass = UserAndPassword1_8.class, supportedOpenmrsVersions = {
        "2.2.* - 9.*" })
public class UserResource2_2 extends UserResource2_0 {

    /**
     * @see DelegatingCrudResource#getRepresentationDescription(Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = super.getRepresentationDescription(rep);

        if (description != null && rep instanceof FullRepresentation) {
            description.addProperty("email");
        }
        return description;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = super.getCreatableProperties();
        description.addProperty("email");
        return description;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
     */
    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        DelegatingResourceDescription description = super.getUpdatableProperties();
        description.addProperty("email");
        return description;
    }
}
