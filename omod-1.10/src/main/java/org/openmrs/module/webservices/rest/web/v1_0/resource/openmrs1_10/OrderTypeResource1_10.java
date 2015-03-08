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

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.openmrs.OrderType;
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
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link org.openmrs.OrderType}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/ordertype", supportedClass = OrderType.class, supportedOpenmrsVersions = {"1.10.*", "1.11.*", "1.12.*"})
public class OrderTypeResource1_10 extends MetadataDelegatingCrudResource<OrderType> {
	
	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("javaClassName");
			description.addProperty("retired");
			description.addProperty("description");
			description.addSelfLink();
			if (rep instanceof DefaultRepresentation) {
				description.addProperty("conceptClasses", Representation.REF);
				description.addProperty("parent", Representation.REF);
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			} else {
				description.addProperty("conceptClasses");
				description.addProperty("parent");
				description.addProperty("auditInfo", findMethod("getAuditInfo"));
			}
			return description;
		}
		return null;
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public OrderType newDelegate() {
		return new OrderType();
	}
	
	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public OrderType save(OrderType orderType) {
		return Context.getOrderService().saveOrderType(orderType);
	}
	
	/**
	 * Fetches an orderType by uuid
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public OrderType getByUniqueId(String uniqueId) {
		OrderType ot = Context.getOrderService().getOrderTypeByUuid(uniqueId);
		if (ot == null) {
			ot = Context.getOrderService().getOrderTypeByName(uniqueId);
		}
		return ot;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(OrderType orderType, RequestContext context) throws ResponseException {
		if (orderType == null)
			return;
		Context.getOrderService().purgeOrderType(orderType);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<OrderType> doGetAll(RequestContext context) {
		return new NeedsPaging<OrderType>(Context.getOrderService().getOrderTypes(context.getIncludeAll()), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<OrderType> doSearch(RequestContext context) {
		List<OrderType> orderTypes = Context.getOrderService().getOrderTypes(context.getIncludeAll());
		for (Iterator<OrderType> iterator = orderTypes.iterator(); iterator.hasNext();) {
			OrderType ot = iterator.next();
			if (!Pattern.compile(Pattern.quote(context.getParameter("q")), Pattern.CASE_INSENSITIVE).matcher(ot.getName())
			        .find()) {
				iterator.remove();
			}
		}
		return new NeedsPaging<OrderType>(orderTypes, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants1_10.RESOURCE_VERSION;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription d = super.getCreatableProperties();
		d.addRequiredProperty("javaClassName");
		d.addProperty("parent");
		d.addProperty("conceptClasses");
		return d;
	}
}
