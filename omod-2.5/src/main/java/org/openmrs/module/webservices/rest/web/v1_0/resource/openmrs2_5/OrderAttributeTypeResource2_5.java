/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_5;

import org.openmrs.OrderAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Allows standard CRUD for the {@link OrderAttributeType} domain object
 */
@Resource(name = RestConstants.VERSION_1 + "/orderattributetype", supportedClass = OrderAttributeType.class, supportedOpenmrsVersions = { "2.5.*", "2.6.*", "2.7.*" })
public class OrderAttributeTypeResource2_5 extends MetadataDelegatingCrudResource<OrderAttributeType> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("display");
        description.addProperty("name");
        description.addProperty("description");
        description.addProperty("datatypeClassname");
        description.addProperty("minOccurs");
        description.addProperty("maxOccurs");
        description.addProperty("retired");
        if (rep instanceof FullRepresentation) {
            description.addProperty("auditInfo");
        }
        description.addSelfLink();
        description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
        return description;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("name");
        description.addRequiredProperty("description");
        description.addRequiredProperty("datatypeClassname");
        description.addProperty("minOccurs");
        description.addProperty("maxOccurs");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        return getCreatableProperties();
    }

    @Override
    public OrderAttributeType getByUniqueId(String uniqueId) {
        return Context.getOrderService().getOrderAttributeTypeByUuid(uniqueId);
    }

    @Override
    protected NeedsPaging<OrderAttributeType> doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<>(Context.getOrderService().getAllOrderAttributeTypes(), context);
    }

    @Override
    public void delete(OrderAttributeType delegate, String reason, RequestContext context) throws ResponseException {
        if (!delegate.isRetired()) {
            Context.getOrderService().retireOrderAttributeType(delegate, reason);
        }
    }

    @Override
    public void purge(OrderAttributeType delegate, RequestContext context) throws ResponseException {
        Context.getOrderService().purgeOrderAttributeType(delegate);
    }

    @Override
    public OrderAttributeType newDelegate() {
        return new OrderAttributeType();
    }

    @Override
    public OrderAttributeType save(OrderAttributeType delegate) {
        return Context.getOrderService().saveOrderAttributeType(delegate);
    }

    @PropertyGetter("display")
    public String getDisplayString(OrderAttributeType delegate) {
        return delegate.getName();
    }
}