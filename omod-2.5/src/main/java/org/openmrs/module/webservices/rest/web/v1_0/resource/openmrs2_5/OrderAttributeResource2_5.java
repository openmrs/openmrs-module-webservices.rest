/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
//package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_5;
//
//import org.openmrs.ConceptAttribute;
//import org.openmrs.ConceptAttributeType;
//import org.openmrs.Order;
//import org.openmrs.OrderAttribute;
//import org.openmrs.api.context.Context;
//import org.openmrs.module.webservices.rest.web.RequestContext;
//import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
//import org.openmrs.module.webservices.rest.web.annotation.SubResource;
//import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
//import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
//import org.openmrs.module.webservices.rest.web.response.ResponseException;
//import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.BaseAttributeCrudResource1_9;
//
//import java.util.List;
//
//@SubResource(parent = OrderResource2_5.class, path = "attribute", supportedClass = OrderAttribute.class, supportedOpenmrsVersions = {"2.5.* - 9.*"})
//public class OrderAttributeResource2_5 extends BaseAttributeCrudResource1_9<OrderAttribute, Order, OrderResource2_5> {
//
//    @PropertySetter("attributeType")
//    public static void setAttributeType(ConceptAttribute instance, ConceptAttributeType attr) {
//        instance.setAttributeType(attr);
//    }
//
//    @Override
//    public OrderAttribute getByUniqueId(String uniqueId) {
//        return Context.getOrderService().getOrderAttributeByUuid(uniqueId);
//    }
//
//    @Override
//    protected void delete(OrderAttribute delegate, String reason, RequestContext context) throws ResponseException {
//
//    }
//
//    @Override
//    public OrderAttribute newDelegate() {
//        return new OrderAttribute();
//    }
//
//    @Override
//    public OrderAttribute save(OrderAttribute delegate) {
//        return null;
//    }
//
//    @Override
//    public void purge(OrderAttribute delegate, RequestContext context) throws ResponseException {
//
//    }
//
//    @Override
//    public Order getParent(OrderAttribute instance) {
//        return instance.getOrder();
//    }
//
//    @Override
//    public void setParent(OrderAttribute instance, Order parent) {
//        instance.setOrder(parent);
//    }
//
//    @Override
//    public PageableResult doGetAll(Order parent, RequestContext context) throws ResponseException {
//        return new NeedsPaging<OrderAttribute>((List<OrderAttribute>) parent.getActiveAttributes(), context);
//    }
//}
