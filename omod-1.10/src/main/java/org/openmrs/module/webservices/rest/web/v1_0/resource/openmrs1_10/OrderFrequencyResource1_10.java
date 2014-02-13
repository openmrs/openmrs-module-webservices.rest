/**
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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import org.openmrs.OrderFrequency;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link OrderFrequency}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/orderfrequency", supportedClass = OrderFrequency.class, supportedOpenmrsVersions = "1.10.*")
public class OrderFrequencyResource1_10 extends MetadataDelegatingCrudResource<OrderFrequency> {

    /**
     * @see DelegatingCrudResource#getRepresentationDescription(Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("frequencyPerDay");
            description.addProperty("retired");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("frequencyPerDay");
            description.addProperty("retired");
            description.addProperty("concept", Representation.DEFAULT);
            description.addSelfLink();
            return description;
        }
        return null;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();

        description.addRequiredProperty("name");
        description.addProperty("description");
        description.addProperty("isHidden");

        return description;
    }

    /**
     * @see DelegatingCrudResource#newDelegate()
     */
    @Override
    public OrderFrequency newDelegate() {
        return new OrderFrequency();
    }

    /**
     * @see DelegatingCrudResource#save(java.lang.Object)
     */
    @Override
    public OrderFrequency save(OrderFrequency orderFrequency) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Fetches a orderFrequency by uuid
     *
     * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
     */
    @Override
    public OrderFrequency getByUniqueId(String uuid) {
        return Context.getOrderService().getOrderFrequencyByUuid(uuid);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
     *      org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public void purge(OrderFrequency orderFrequency, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected NeedsPaging<OrderFrequency> doGetAll(RequestContext context) {
        return new NeedsPaging<OrderFrequency>(Context.getOrderService().getOrderFrequencies(), context);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected NeedsPaging<OrderFrequency> doSearch(RequestContext context) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
     */
    @Override
    public String getResourceVersion() {
        return "1.10";
    }
}
