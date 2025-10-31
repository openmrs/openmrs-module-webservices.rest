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

import org.openmrs.ProviderRole;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/providerrole", supportedClass = ProviderRole.class, supportedOpenmrsVersions = { "2.8.* - 9.*" })
public class ProviderRoleResource2_8 extends MetadataDelegatingCrudResource<ProviderRole> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("name");
            description.addProperty("description");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("name");
        description.addRequiredProperty("description");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        return getCreatableProperties();
    }

    @Override
    public ProviderRole getByUniqueId(String uuid) {
        return Context.getProviderService().getProviderRoleByUuid(uuid);
    }

    @Override
    public ProviderRole newDelegate() {
        return new ProviderRole();
    }

    @Override
    public ProviderRole save(ProviderRole providerRole) {
        if (providerRole != null) {
            return Context.getProviderService().saveProviderRole(providerRole);
        }
        return null;
    }

    @Override
    public void purge(ProviderRole providerRole, RequestContext requestContext) throws ResponseException {
        if (providerRole != null) {
            Context.getProviderService().purgeProviderRole(providerRole);
        }
    }

    @Override
    protected NeedsPaging<ProviderRole> doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<ProviderRole>(Context.getProviderService().getAllProviderRoles(context.getIncludeAll()), context);
    }


    @Override
    public String getResourceVersion() {
        return "1.9";
    }
}
