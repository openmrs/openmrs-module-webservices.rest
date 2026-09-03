/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.PrivilegeConstants;

/**
 * Resource for {@link Order} and all of its subclasses
 */
@Resource(name = RestConstants.VERSION_1 + "/order", supportedClass = Order.class, supportedOpenmrsVersions = { "1.8.* - 1.9.*" }, order = 1)
public class OrderResource1_8 extends DataDelegatingCrudResource<Order> {

	@Override
	public String getRequiredGetPrivilege() {
		return PrivilegeConstants.GET_ORDERS;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#hasTypesDefined()
	 */
	@Override
	public boolean hasTypesDefined() {
		return true;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(String)
	 */
	@Override
	public Order getByUniqueId(String uniqueId) {
		return Context.getOrderService().getOrderByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public Order newDelegate() {
		return new Order();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(Object)
	 */
	@Override
	public Order save(Order delegate) {
		return Context.getOrderService().saveOrder(delegate, null);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(Object,
	 *      String, RequestContext)
	 */
	@Override
	protected void delete(Order delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getOrderService().voidOrder(delegate, reason);
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#undelete(Object,
	 *      RequestContext)
	 */
	@Override
	protected Order undelete(Order delegate, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			delegate = Context.getOrderService().unvoidOrder(delegate);
		}
		return delegate;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(Object,
	 *      RequestContext)
	 */
	@Override
	public void purge(Order delegate, RequestContext context) throws ResponseException {
		Context.getOrderService().purgeOrder(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(RequestContext)
	 * <strong>Should</strong> return all Orders (including retired) if context.includeAll is set
	 */
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		//openmrs-api-2.4.x upgrade
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * Display string for {@link Order}
	 * 
	 * @param order
	 * @return ConceptName
	 */
	@PropertyGetter("display")
	public String getDisplayString(Order order) {
		if (order.getConcept() == null)
			return "[No Concept]";
		return order.getConcept().getName().getName();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("orderType", Representation.REF);
			description.addProperty("patient", Representation.REF);
			description.addProperty("concept", Representation.REF);
			description.addProperty("instructions");
			description.addProperty("startDate");
			description.addProperty("autoExpireDate");
			description.addProperty("encounter", Representation.REF);
			description.addProperty("orderer", Representation.REF);
			description.addProperty("accessionNumber");
			description.addProperty("discontinuedBy", Representation.REF);
			description.addProperty("discontinuedDate");
			description.addProperty("discontinuedReason");
			description.addProperty("discontinuedReasonNonCoded");
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("orderType");
			description.addProperty("patient");
			description.addProperty("concept");
			description.addProperty("instructions");
			description.addProperty("startDate");
			description.addProperty("autoExpireDate");
			description.addProperty("encounter");
			description.addProperty("orderer");
			description.addProperty("accessionNumber");
			description.addProperty("discontinuedBy");
			description.addProperty("discontinuedDate");
			description.addProperty("discontinuedReason");
			description.addProperty("discontinuedReasonNonCoded");
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		} else {
			return null;
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription d = new DelegatingResourceDescription();
		d.addRequiredProperty("orderType");
		d.addRequiredProperty("patient");
		d.addRequiredProperty("concept");
		d.addProperty("instructions");
		d.addProperty("startDate");
		d.addProperty("autoExpireDate");
		d.addProperty("encounter");
		d.addProperty("orderer");
		d.addProperty("discontinuedBy");
		d.addProperty("discontinuedDate");
		d.addProperty("discontinuedReason");
		d.addProperty("discontinuedReasonNonCoded");
		d.addProperty("accessionNumber");
		return d;
	}
	
	/**
	 * Gets orders by given patient (paged according to context if necessary) only if a patient
	 * parameter exists in the request set on the {@link RequestContext} otherwise
	 * 
	 * @param context
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(RequestContext)
	 * @return all orders for a given patient (possibly filtered by context.type)
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		//openmrs-api-2.4.x upgrade
		throw new ResourceDoesNotSupportOperationException();
	}
	
}
