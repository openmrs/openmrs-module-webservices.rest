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
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.BaseAttributeCrudResource1_9;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.OrderResource2_2;

import java.util.List;

/**
 * {@link Resource} for OrderAttributes, supporting standard CRUD operations
 */
@SubResource(parent = OrderResource2_2.class, path = "attribute", supportedClass = OrderAttribute.class, supportedOpenmrsVersions = {
        "2.5.* - 9.*"})
public class OrderAttributeResource2_5 extends BaseAttributeCrudResource1_9<OrderAttribute, Order, OrderResource2_2> {

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
     * Sets value on the given OrderAttribute.
     *
     * @param instance
     * @param attr
     */
    @PropertySetter("value")
    public static void setValue(OrderAttribute instance, Object attr) {
        instance.setValue(attr);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(String)
     */
    @Override
    public OrderAttribute getByUniqueId(String uniqueId) {
        return Context.getOrderService().getOrderAttributeByUuid(uniqueId);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(Object, String, RequestContext)
     */
    @Override
    protected void delete(OrderAttribute delegate, String reason, RequestContext context) throws ResponseException {
        throw new UnsupportedOperationException("Cannot delete OrderAttribute");
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#newDelegate()
     */
    @Override
    public OrderAttribute newDelegate() {
        return new OrderAttribute();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(Object)
     */
    @Override
    public OrderAttribute save(OrderAttribute delegate) {
        boolean needToAdd = true;
        for (OrderAttribute pa : delegate.getOrder().getActiveAttributes()) {
            if (pa.equals(delegate)) {
                needToAdd = false;
                break;
            }
        }
        if (needToAdd) {
            delegate.getOrder().addAttribute(delegate);
        }
        return delegate;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#purge(Object, RequestContext)
     */
    @Override
    public void purge(OrderAttribute delegate, RequestContext context) throws ResponseException {
        throw new UnsupportedOperationException("Cannot purge OrderAttribute");
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(Object)
     */
    @Override
    public Order getParent(OrderAttribute instance) {
        return instance.getOrder();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(Object, Object)
     */
    @Override
    public void setParent(OrderAttribute instance, Order parent) {
        instance.setOrder(parent);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(Object, RequestContext)
     */
    @Override
    public PageableResult doGetAll(Order parent, RequestContext context) throws ResponseException {
        return new NeedsPaging<>((List<OrderAttribute>) parent.getActiveAttributes(), context);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getResourceVersion()
     */
    @Override
    public String getResourceVersion() {
        return "2.5";
    }
}