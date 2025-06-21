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

import java.util.List;

import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.util.OpenmrsConstants;

/**
 * Exposes the {@link DrugOrder} subclass as a type in {@link OrderResource1_8}
 */
@SubClassHandler(supportedClass = DrugOrder.class, supportedOpenmrsVersions = { "1.8.* - 1.9.*" })
public class DrugOrderSubclassHandler1_8 extends BaseDelegatingSubclassHandler<Order, DrugOrder> implements DelegatingSubclassHandler<Order, DrugOrder> {
	
	public DrugOrderSubclassHandler1_8() {
		//RESTWS-439
		//Order subclass fields
		allowedMissingProperties.add("dose");
		allowedMissingProperties.add("units");
		allowedMissingProperties.add("frequency");
		allowedMissingProperties.add("prn");
		allowedMissingProperties.add("complex");
		allowedMissingProperties.add("quantity");
		allowedMissingProperties.add("drug");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "drugorder";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#newDelegate()
	 */
	@Override
	public DrugOrder newDelegate() {
		DrugOrder o = new DrugOrder();
		o.setOrderType(Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG));
		return o;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getAllByType(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public PageableResult getAllByType(RequestContext context) throws ResourceDoesNotSupportOperationException {
		return new NeedsPaging<DrugOrder>(Context.getOrderService().getDrugOrders(), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			OrderResource1_8 orderResource = (OrderResource1_8) Context.getService(RestService.class)
			        .getResourceBySupportedClass(Order.class);
			DelegatingResourceDescription d = orderResource.getRepresentationDescription(rep);
			d.addProperty("dose");
			d.addProperty("units");
			d.addProperty("frequency");
			d.addProperty("prn");
			d.addProperty("complex");
			d.addProperty("quantity");
			d.addProperty("drug", Representation.REF);
			return d;
		} else if (rep instanceof FullRepresentation) {
			OrderResource1_8 orderResource = (OrderResource1_8) Context.getService(RestService.class)
			        .getResourceBySupportedClass(Order.class);
			DelegatingResourceDescription d = orderResource.getRepresentationDescription(rep);
			d.addProperty("dose");
			d.addProperty("units");
			d.addProperty("frequency");
			d.addProperty("prn");
			d.addProperty("complex");
			d.addProperty("quantity");
			d.addProperty("drug");
			return d;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		OrderResource1_8 orderResource = (OrderResource1_8) Context.getService(RestService.class)
		        .getResourceBySupportedClass(Order.class);
		DelegatingResourceDescription d = orderResource.getCreatableProperties();
		d.addProperty("dose");
		d.addProperty("units");
		d.addProperty("frequency");
		d.addProperty("prn");
		d.addProperty("complex");
		d.addProperty("quantity");
		d.addRequiredProperty("drug");
		
		// DrugOrders have a specific hardcoded value for this property
		d.removeProperty("orderType");
		return d;
	}

	/**
	 * Handles getOrdersByPatient for {@link OrderResource1_8} when type=drugorder
	 * 
	 * @param patient
	 * @param context
	 * @return
	 */
	public PageableResult getOrdersByPatient(Patient patient, RequestContext context) {
		List<DrugOrder> orders = Context.getOrderService().getDrugOrdersByPatient(patient);
		return new NeedsPaging<DrugOrder>(orders, context);
	}
	
	/**
	 * Gets a user-friendly display representation of the delegate
	 * 
	 * @param o
	 * @return
	 */
	@PropertyGetter("display")
	public static String getDisplay(DrugOrder delegate) {
		StringBuilder ret = new StringBuilder();
		ret.append(delegate.getDrug() != null ? delegate.getDrug().getName() : "[no drug]");
		ret.append(": ");
		ret.append(delegate.getDose()).append(" ").append(delegate.getUnits());
		// TODO dates, etc
		return ret.toString();
	}
}
