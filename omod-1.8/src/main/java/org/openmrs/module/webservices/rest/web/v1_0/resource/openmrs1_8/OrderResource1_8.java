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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import java.util.List;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService.ORDER_STATUS;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Resource for {@link Order} and all of its subclasses
 */
@Resource(name = RestConstants.VERSION_1 + "/order", supportedClass = Order.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*"}, order = 1)
public class OrderResource1_8 extends DataDelegatingCrudResource<Order> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#hasTypesDefined()
	 */
	@Override
	public boolean hasTypesDefined() {
		return true;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public Order save(Order delegate) {
		return Context.getOrderService().saveOrder(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Order delegate, RequestContext context) throws ResponseException {
		Context.getOrderService().purgeOrder(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 * @should return all Orders (including retired) if context.includeAll is set
	 */
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		//ORDER_STATUS.ANY is used to specify that all orders, including voided ones should be retrieved
		ORDER_STATUS orderStatus = context.getIncludeAll() ? ORDER_STATUS.ANY : null;
		
		return new NeedsPaging<Order>(Context.getOrderService().getOrders(Order.class, null, null, orderStatus, null, null,
		    null), context);
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
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
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 * @return all orders for a given patient (possibly filtered by context.type)
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("patient");
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			
			// if the user indicated a specific type, try to delegate to the appropriate subclass handler
			if (context.getType() != null) {
				PageableResult ret = (PageableResult) findAndInvokeSubclassHandlerMethod(context.getType(),
				    "getOrdersByPatient", patient, context);
				if (ret != null)
					return ret;
			}
			
			List<Order> orders = Context.getOrderService().getOrdersByPatient(patient);
			// if the user indicated a specific type, and we couldn't delegate to a subclass handler above, filter here
			if (context.getType() != null) {
				filterByType(orders, context.getType());
			}
			return new NeedsPaging<Order>(orders, context);
		}
		
		//currently this is not supported since the superclass throws an exception
		return super.doSearch(context);
	}
	
}
