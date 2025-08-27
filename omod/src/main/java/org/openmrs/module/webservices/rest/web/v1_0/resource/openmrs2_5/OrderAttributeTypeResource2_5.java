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
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.BaseAttributeTypeCrudResource1_9;

/**
 * Allows standard CRUD for the {@link OrderAttributeType} domain object
 */
@Resource(name = RestConstants.VERSION_1 + "/orderattributetype", supportedClass = OrderAttributeType.class, supportedOpenmrsVersions = {
        "2.5.* - 9.*" })
public class OrderAttributeTypeResource2_5 extends BaseAttributeTypeCrudResource1_9<OrderAttributeType> {

    public OrderAttributeTypeResource2_5() {

    }

    private OrderService service() {
        return Context.getOrderService();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(String)
     */
    @Override
    public OrderAttributeType getByUniqueId(String uniqueId) {
        return service().getOrderAttributeTypeByUuid(uniqueId);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(RequestContext)
     */
    @Override
    protected NeedsPaging<OrderAttributeType> doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<>(service().getAllOrderAttributeTypes(), context);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(Object,
     *      RequestContext)
     */
    @Override
    public void purge(OrderAttributeType delegate, RequestContext context) throws ResponseException {
        service().purgeOrderAttributeType(delegate);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
     */
    @Override
    public OrderAttributeType newDelegate() {
        return new OrderAttributeType();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(Object)
     */
    @Override
    public OrderAttributeType save(OrderAttributeType delegate) {
        return service().saveOrderAttributeType(delegate);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
     */
    @Override
    public String getResourceVersion() {
        return "2.5";
    }
}