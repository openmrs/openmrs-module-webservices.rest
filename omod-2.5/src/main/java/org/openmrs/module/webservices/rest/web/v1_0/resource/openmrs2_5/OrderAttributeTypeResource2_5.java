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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Allows standard CRUD for the {@link OrderAttributeType} domain object
 */
@Resource(name = RestConstants.VERSION_1 + "/orderattributetype", supportedClass = OrderAttributeType.class, supportedOpenmrsVersions = {
        "2.5.* - 9.*" })
public class OrderAttributeTypeResource2_5 extends BaseAttributeTypeCrudResource2_5<OrderAttributeType> {
	
	public OrderAttributeTypeResource2_5() {
	}
	
	private OrderService service() {
		return Context.getOrderService();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public OrderAttributeType getByUniqueId(String uniqueId) {
		return service().getOrderAttributeTypeByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<OrderAttributeType> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<OrderAttributeType>(service().getAllOrderAttributeTypes(), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public OrderAttributeType newDelegate() {
		return new OrderAttributeType();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public OrderAttributeType save(OrderAttributeType delegate) {
		return service().saveOrderAttributeType(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(OrderAttributeType delegate, RequestContext context) throws ResponseException {
		service().purgeOrderAttributeType(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<OrderAttributeType> doSearch(RequestContext context) {
		List<OrderAttributeType> allAttrs = service().getAllOrderAttributeTypes();
		List<OrderAttributeType> queryResult = new ArrayList<OrderAttributeType>();
		for (OrderAttributeType locAttr : allAttrs) {
			if (Pattern.compile(Pattern.quote(context.getParameter("q")), Pattern.CASE_INSENSITIVE)
			        .matcher(locAttr.getName()).find()) {
				queryResult.add(locAttr);
			}
		}
		return new NeedsPaging<OrderAttributeType>(queryResult, context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "2.5";
	}
}
