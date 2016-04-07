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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_12;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/orderset", supportedClass = OrderSet.class, supportedOpenmrsVersions = { "1.12.*", "2.0.*" })
public class OrderSetResource1_12 extends MetadataDelegatingCrudResource<OrderSet> {

    @Override
    public OrderSet getByUniqueId(String uniqueId) {
        return Context.getOrderSetService().getOrderSetByUuid(uniqueId);
    }

    @Override
    public OrderSet newDelegate() {
        return new OrderSet();
    }

    @Override
    public OrderSet save(OrderSet orderSet) {
        if(CollectionUtils.isNotEmpty(orderSet.getOrderSetMembers())){
            for(OrderSetMember orderSetMember : orderSet.getOrderSetMembers()) {
                if (null != orderSetMember.getConcept() && StringUtils.isNotEmpty(orderSetMember.getConcept().getUuid())) {
                    orderSetMember.setConcept(Context.getConceptService().getConceptByUuid(orderSetMember.getConcept().getUuid()));
                }
                if(null != orderSetMember.getOrderType() && StringUtils.isNotEmpty(orderSetMember.getOrderType().getUuid())) {
                    orderSetMember.setOrderType(Context.getOrderService().getOrderTypeByUuid(orderSetMember.getOrderType().getUuid()));
                }
            }
        }
        return Context.getOrderSetService().saveOrderSet(orderSet);
    }

    @PropertySetter("orderSetMembers")
    public static void setOrderSetMembers(OrderSet instance, List<OrderSetMember> orderSetMembers){
        instance.setOrderSetMembers(orderSetMembers);
    }

    @Override
    public void purge(OrderSet delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected NeedsPaging<OrderSet> doGetAll(RequestContext context) {
        return new NeedsPaging<OrderSet>(Context.getOrderSetService().getOrderSets(context.getIncludeAll()), context);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = super.getRepresentationDescription(rep);
            description.addProperty("operator");
            description.addProperty("orderSetMembers", Representation.REF);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = super.getRepresentationDescription(rep);
            description.addProperty("operator");
            description.addProperty("orderSetMembers", Representation.DEFAULT);
            return description;
        } else {
            return null;
        }
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription d = super.getCreatableProperties();
        d.addProperty("operator");
        d.addProperty("orderSetMembers");
        return d;
    }
}
