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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.CareSetting;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.OrderResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for {@link org.openmrs.Order}
 * , supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/order", supportedClass = Order.class, supportedOpenmrsVersions = {"1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class OrderResource1_10 extends OrderResource1_8 {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("orderNumber");
			description.addProperty("patient", Representation.REF);
			description.addProperty("concept", Representation.REF);
			description.addProperty("action");
			description.addProperty("careSetting", Representation.REF);
			description.addProperty("previousOrder", Representation.REF);
			description.addProperty("dateActivated");
			description.addProperty("dateStopped");
			description.addProperty("autoExpireDate");
			description.addProperty("encounter", Representation.REF);
			description.addProperty("orderer", Representation.REF);
			description.addProperty("orderReason", Representation.REF);
			description.addProperty("orderReasonNonCoded");
			description.addProperty("urgency");
			description.addProperty("instructions");
			description.addProperty("commentToFulfiller");
			description.addProperty("display");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("orderNumber");
			description.addProperty("patient", Representation.REF);
			description.addProperty("concept", Representation.REF);
			description.addProperty("action");
			description.addProperty("careSetting", Representation.DEFAULT);
			description.addProperty("previousOrder", Representation.REF);
			description.addProperty("dateActivated");
			description.addProperty("dateStopped");
			description.addProperty("autoExpireDate");
			description.addProperty("encounter", Representation.REF);
			description.addProperty("orderer", Representation.REF);
			description.addProperty("orderReason", Representation.REF);
			description.addProperty("orderReasonNonCoded");
			description.addProperty("urgency");
			description.addProperty("instructions");
			description.addProperty("commentToFulfiller");
			description.addProperty("display");
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
		d.addRequiredProperty("encounter");
		d.addProperty("action");
		d.addProperty("dateActivated");
		d.addProperty("patient");
		d.addProperty("concept");
		d.addProperty("careSetting");
		d.addProperty("dateStopped");
		d.addProperty("autoExpireDate");
		d.addProperty("orderer");
		d.addProperty("previousOrder");
		d.addProperty("urgency");
		d.addProperty("orderReason");
		d.addProperty("orderReasonNonCoded");
		d.addProperty("instructions");
		d.addProperty("commentToFulfiller");
		return d;
	}
	
	/**
	 * Fetches an order by uuid or order number
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(String)
	 */
	@Override
	public Order getByUniqueId(String uniqueId) {
		Order order = super.getByUniqueId(uniqueId);
		if (order == null) {
			order = Context.getOrderService().getOrderByOrderNumber(uniqueId);
		}
		return order;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * Gets orders by given patient (paged according to context if necessary) only if a patient
	 * parameter exists in the request set on the {@link RequestContext}, optional careSetting,
	 * asOfDate request parameters can be specified to filter on
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
			if (patient == null) {
				return new EmptySearchResult();
			}
			
			// if the user indicated a specific type, try to delegate to the appropriate subclass handler
			if (context.getType() != null) {
				PageableResult ret = (PageableResult) findAndInvokeSubclassHandlerMethod(context.getType(),
				    "getActiveOrders", patient, context);
				if (ret != null) {
					return ret;
				}
			}
			
			String careSettingUuid = context.getRequest().getParameter("careSetting");
			String asOfDateString = context.getRequest().getParameter("asOfDate");
			CareSetting careSetting = null;
			Date asOfDate = null;
			if (StringUtils.isNotBlank(asOfDateString)) {
				asOfDate = (Date) ConversionUtil.convert(asOfDateString, Date.class);
			}
			if (StringUtils.isNotBlank(careSettingUuid)) {
				careSetting = ((CareSettingResource1_10) Context.getService(RestService.class).getResourceBySupportedClass(
				    CareSetting.class)).getByUniqueId(careSettingUuid);
			}
			
			String status = context.getRequest().getParameter("status");
			List<Order> orders = OrderUtil.getOrders(patient, careSetting, null, status, asOfDate, context.getIncludeAll());
			// if the user indicated a specific type, and we couldn't delegate to a subclass handler above, filter here
			if (context.getType() != null) {
				filterByType(orders, context.getType());
			}
			
			return new NeedsPaging<Order>(orders, context);
		}
		
		return new EmptySearchResult();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants1_10.RESOURCE_VERSION;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public Order save(Order delegate) {
		return Context.getOrderService().saveOrder(delegate, null);
	}
}
