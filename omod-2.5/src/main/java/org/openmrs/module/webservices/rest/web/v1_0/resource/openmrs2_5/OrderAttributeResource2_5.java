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

import org.openmrs.Order;
import org.openmrs.OrderAttribute;
import org.openmrs.OrderAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.api.OrderContext;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.BaseAttributeCrudResource1_9;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.OrderResource2_2;

import java.util.List;

/**
 * {@link Resource} for OrderAttributes, supporting standard CRUD operations
 */
@SubResource(parent = OrderResource2_5.class, path = "attribute", supportedClass = OrderAttribute.class, supportedOpenmrsVersions = {
        "2.5.* - 9.*"})
public class OrderAttributeResource2_5 extends BaseAttributeCrudResource1_9<OrderAttribute, Order, OrderResource2_5> {

    /**
     * Sets attributeType on the given OrderAttribute.
     *
     * @param instance
     * @param attr
     */
    @PropertySetter("attributeType")
    public static void setAttributeType(OrderAttribute instance, OrderAttributeType attr) {
        instance.setAttributeType(attr);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
     */
    @Override
    public Order getParent(OrderAttribute instance) {
        return instance.getOrder();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
     */
    @Override
    public OrderAttribute newDelegate() {
        return new OrderAttribute();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public void setParent(OrderAttribute instance, Order order) {
        instance.setOrder(order);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
     */
    @Override
    public OrderAttribute getByUniqueId(String uniqueId) {
        return Context.getOrderService().getOrderAttributeByUuid(uniqueId);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object,
     *      org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public NeedsPaging<OrderAttribute> doGetAll(Order parent, RequestContext context) throws ResponseException {
        return new NeedsPaging<OrderAttribute>((List<OrderAttribute>) parent.getActiveAttributes(), context);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
     */
    @Override
    public OrderAttribute save(OrderAttribute delegate) {
        // make sure it has not already been added to the order
        boolean needToAdd = true;
        if (delegate.getOrder().getActiveAttributes().contains(delegate)) {
            delegate.getOrder().addAttribute(delegate);
        }
        if (needToAdd) {
            delegate.getOrder().addAttribute(delegate);
        }
        OrderContext orderContext = new OrderContext();
        orderContext.setCareSetting(delegate.getOrder().getCareSetting());
        orderContext.setOrderType(delegate.getOrder().getOrderType());
        delegate.getOrder().setAction(Order.Action.REVISE);

        Context.getOrderService().saveOrder(delegate.getOrder(), orderContext);
        return delegate;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
     *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected void delete(OrderAttribute delegate, String reason, RequestContext context) throws ResponseException {
        throw new UnsupportedOperationException("Cannot purge OrderAttribute");
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
     *      org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public void purge(OrderAttribute delegate, RequestContext context) throws ResponseException {
        throw new UnsupportedOperationException("Cannot purge OrderAttribute");
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
     */
    @Override
    public String getResourceVersion() {
        return RestConstants2_5.RESOURCE_VERSION;
    }
}
