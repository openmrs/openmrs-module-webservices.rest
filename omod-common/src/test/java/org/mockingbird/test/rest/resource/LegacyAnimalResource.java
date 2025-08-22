/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mockingbird.test.rest.resource;

import org.mockingbird.test.Animal;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/animal", order = 3, supportedClass = Animal.class, supportedOpenmrsVersions = { "4.0.x" })
public class LegacyAnimalResource extends DelegatingCrudResource<Animal> {
    @Override
    public Animal getByUniqueId(String uniqueId) {
        return null;
    }

    @Override
    protected void delete(Animal delegate, String reason, RequestContext context) throws ResponseException {

    }

    @Override
    public Animal newDelegate() {
        return null;
    }

    @Override
    public Animal save(Animal delegate) {
        return null;
    }

    @Override
    public void purge(Animal delegate, RequestContext context) throws ResponseException {

    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        return null;
    }
}
