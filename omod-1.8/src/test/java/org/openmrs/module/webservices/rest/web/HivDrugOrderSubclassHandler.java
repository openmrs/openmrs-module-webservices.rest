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
package org.openmrs.module.webservices.rest.web;

import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.SubClassHandler;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.util.OpenmrsConstants;

/**
 * This is a contrived example for testing purposes
 */
@SubClassHandler(supportedClass = HivDrugOrder.class, supportedOpenmrsVersions = {"1.8.*"})
public class HivDrugOrderSubclassHandler extends BaseDelegatingSubclassHandler<Order, HivDrugOrder> implements DelegatingSubclassHandler<Order, HivDrugOrder> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#newDelegate()
	 */
	@Override
	public HivDrugOrder newDelegate() {
		HivDrugOrder o = new HivDrugOrder();
		o.setOrderType(Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG));
		return o;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription d = new DelegatingResourceDescription();
			d.addProperty("patient", Representation.REF);
			d.addProperty("concept", Representation.REF);
			d.addProperty("startDate");
			d.addProperty("autoExpireDate");
			d.addProperty("standardRegimenCode");
			return d;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription d = new DelegatingResourceDescription();
			d.addProperty("patient");
			d.addProperty("concept");
			d.addProperty("startDate");
			d.addProperty("autoExpireDate");
			d.addProperty("standardRegimenCode");
			d.addProperty("instructions");
			return d;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription d = new DelegatingResourceDescription();
		d.addRequiredProperty("patient");
		d.addRequiredProperty("concept");
		d.addProperty("startDate");
		d.addProperty("autoExpireDate");
		d.addProperty("standardRegimenCode");
		d.addProperty("instructions");
		return d;
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getAllByType(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public PageableResult getAllByType(RequestContext context) throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "hivdrugorder";
	}
	
	@PropertyGetter("standardRegimenCode")
	public static String getStandardRegimenCode(HivDrugOrder delegate) {
		return delegate.getInstructions().substring("Standard Regimen: ".length());
	}
	
	@PropertySetter("standardRegimenCode")
	public static void setStandardRegimenCode(HivDrugOrder delegate, String code) {
		delegate.setInstructions("Standard Regimen: " + code);
	}
	
}
