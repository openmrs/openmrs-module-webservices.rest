/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.CareSetting;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.DrugOrderSubclassHandler1_8;

/**
 * Exposes the {@link org.openmrs.DrugOrder} subclass as a type in
 * {@link org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.DrugOrderSubclassHandler1_10}
 */
@SubClassHandler(supportedClass = DrugOrder.class, supportedOpenmrsVersions = { "1.10.*", "1.11.*" })
public class DrugOrderSubclassHandler1_10 extends DrugOrderSubclassHandler1_8 {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#newDelegate()
	 */
	@Override
	public DrugOrder newDelegate() {
		return new DrugOrder();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler#getAllByType(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public PageableResult getAllByType(RequestContext context) throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			OrderResource1_10 orderResource = (OrderResource1_10) Context.getService(RestService.class).getResourceBySupportedClass(Order.class);
			DelegatingResourceDescription description = orderResource.getRepresentationDescription(rep);
			description.addProperty("display");
			description.addProperty("drug", Representation.REF);
			description.addProperty("dosingType");
			description.addProperty("dose");
			description.addProperty("doseUnits", Representation.REF);
			description.addProperty("frequency", Representation.REF);
			description.addProperty("asNeeded");
			description.addProperty("asNeededCondition");
			description.addProperty("quantity");
			description.addProperty("quantityUnits", Representation.REF);
			description.addProperty("numRefills");
			description.addProperty("dosingInstructions");
			description.addProperty("duration");
			description.addProperty("durationUnits", Representation.REF);
			description.addProperty("route", Representation.REF);
			description.addProperty("brandName");
			description.addProperty("dispenseAsWritten");
			return description;
		} else if (rep instanceof FullRepresentation) {
			OrderResource1_10 orderResource = (OrderResource1_10) Context.getService(RestService.class).getResourceBySupportedClass(Order.class);
			DelegatingResourceDescription description = orderResource.getRepresentationDescription(rep);
			description.addProperty("display");
			description.addProperty("drug", Representation.REF);
			description.addProperty("dosingType");
			description.addProperty("dose");
			description.addProperty("doseUnits", Representation.DEFAULT);
			description.addProperty("frequency", Representation.REF);
			description.addProperty("asNeeded");
			description.addProperty("asNeededCondition");
			description.addProperty("quantity");
			description.addProperty("quantityUnits", Representation.DEFAULT);
			description.addProperty("numRefills");
			description.addProperty("dosingInstructions");
			description.addProperty("duration");
			description.addProperty("durationUnits", Representation.DEFAULT);
			description.addProperty("route", Representation.REF);
			description.addProperty("brandName");
			description.addProperty("dispenseAsWritten");
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		OrderResource1_10 orderResource = (OrderResource1_10) Context.getService(RestService.class).getResourceBySupportedClass(Order.class);
		DelegatingResourceDescription description = orderResource.getCreatableProperties();
		description.addProperty("drug");
		description.addProperty("dosingType");
		description.addProperty("dose");
		description.addProperty("doseUnits");
		description.addProperty("frequency");
		description.addProperty("asNeeded");
		description.addProperty("asNeededCondition");
		description.addProperty("quantity");
		description.addProperty("quantityUnits");
		description.addProperty("numRefills");
		description.addProperty("administrationInstructions");
		description.addProperty("dosingInstructions");
		description.addProperty("duration");
		description.addProperty("durationUnits");
		description.addProperty("route");
		description.addProperty("brandName");
		description.addProperty("dispenseAsWritten");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		OrderResource1_10 orderResource = (OrderResource1_10) Context.getService(RestService.class).getResourceBySupportedClass(Order.class);
		//this actually throws a ResourceDoesNotSupportOperationException
		return orderResource.getUpdatableProperties();
	}
	
	public PageableResult getActiveOrders(Patient patient, RequestContext context) {
		String careSettingUuid = context.getRequest().getParameter("careSetting");
		String asOfDateString = context.getRequest().getParameter("asOfDate");
		String sortParam = context.getRequest().getParameter("sort");
		CareSetting careSetting = null;
		java.util.Date asOfDate = null;
		if (StringUtils.isNotBlank(asOfDateString)) {
			asOfDate = (java.util.Date) ConversionUtil.convert(asOfDateString, java.util.Date.class);
		}
		if (StringUtils.isNotBlank(careSettingUuid)) {
			careSetting = ((CareSettingResource1_10) Context.getService(RestService.class).getResourceBySupportedClass(
			    CareSetting.class)).getByUniqueId(careSettingUuid);
		}
		
		String status = context.getRequest().getParameter("status");
		OrderService os = Context.getOrderService();
		OrderType orderType = os.getOrderTypeByName("Drug order");
		List<Order> drugOrders = OrderUtil.getOrders(patient, careSetting, orderType, status, asOfDate, context.getIncludeAll());
		OrderResource1_10 orderResource = (OrderResource1_10) Context.getService(RestService.class).getResourceBySupportedClass(Order.class);
		if (StringUtils.isNotBlank(sortParam)) {
			List<Order> sortedOrder = orderResource.sortOrdersBasedOnDateActivatedOrDateStopped(drugOrders, sortParam, status);
			return new NeedsPaging<Order>(sortedOrder, context);
		}
		else {
			return new NeedsPaging<Order>(drugOrders, context);
		}
	}
	
	/**
	 * @see OrderResource1_10#getDisplayString(org.openmrs.Order)
	 */
	@PropertyGetter("display")
	public static String getDisplay(DrugOrder delegate) {
		StringBuilder ret = new StringBuilder();
		ret.append("(" + delegate.getAction() + ") ");
		if (delegate.getDrug() != null) {
			ret.append(delegate.getDrug().getName());
		} else {
			ret.append(delegate.getConcept().getDisplayString());
		}
		if (Order.Action.DISCONTINUE != delegate.getAction() && delegate.getDosingType() != null
		        && delegate.getDosingInstructionsInstance() != null) {
			String dosingInstructionsAsString = delegate.getDosingInstructionsInstance().getDosingInstructionsAsString(Context.getLocale());
			ret.append(": ");
			ret.append(dosingInstructionsAsString);
		}		
		return ret.toString();
	}
}
