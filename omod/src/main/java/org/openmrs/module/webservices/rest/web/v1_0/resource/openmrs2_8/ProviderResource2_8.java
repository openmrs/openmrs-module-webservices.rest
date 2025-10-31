/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_8;

import org.openmrs.Provider;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ProviderResource1_9;

@Resource(name = RestConstants.VERSION_1 + "/provider", supportedClass = Provider.class, supportedOpenmrsVersions = { "2.8.* - 9.*" })
public class ProviderResource2_8 extends ProviderResource1_9 {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription delegatingResourceDescription = super.getRepresentationDescription(rep);

        if (delegatingResourceDescription != null) {

            if (rep instanceof DefaultRepresentation) {
                delegatingResourceDescription.addProperty("providerRole", Representation.REF);
            } else if (rep instanceof FullRepresentation) {
                delegatingResourceDescription.addProperty("providerRole", Representation.DEFAULT);
            }
        }
        return delegatingResourceDescription;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription d = super.getCreatableProperties();
        d.addProperty("providerRole");
        return d;
    }

}
