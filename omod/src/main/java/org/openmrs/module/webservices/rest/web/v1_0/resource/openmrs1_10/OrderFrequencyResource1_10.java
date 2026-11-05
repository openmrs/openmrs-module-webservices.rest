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

import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ConceptResource1_9;

/**
 * {@link Resource} for {@link OrderFrequency}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/orderfrequency", supportedClass = OrderFrequency.class, supportedOpenmrsVersions = {
        "1.10.* - 9.*" })
public class OrderFrequencyResource1_10 extends MetadataDelegatingCrudResource<OrderFrequency> {

	@Override
	public String getRequiredGetPrivilege() {
		return PrivilegeConstants.GET_ORDER_FREQUENCIES;
	}

	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("frequencyPerDay");
			description.addProperty("retired");
			description.addProperty("concept", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("frequencyPerDay");
			description.addProperty("concept", Representation.DEFAULT);
			description.addProperty("retired");
			description.addSelfLink();
			description.addProperty("auditInfo");
			return description;
		} else if (rep.getRepresentation().equals("fullconcept")) {
			DelegatingResourceDescription description = getRepresentationDescription(Representation.FULL);
			description.addProperty("concept", Representation.FULL);
			return description;
		}
		return null;
	}
	
	/**
	 * Whether a rule refuses the concept this frequency is named after, or the name it is named by.
	 * <p>
	 * Unusual among metadata, and the reason both getters below need a guard: an order frequency has no
	 * name column of its own. {@code OrderFrequency.getName()} answers
	 * {@code getConcept().getName()}, so the {@code name} property and the {@code display}
	 * {@code MetadataDelegatingCrudResource} derives from it are both read off a record this one only
	 * points at - one that is masked under {@code concept} in the same response, with its name spelled
	 * out beside the mask under both.
	 */
	private boolean readsARefusedRecord(OrderFrequency instance) {
		Concept concept = instance.getConcept();
		return ConversionUtil.isRefusedByARule(concept)
		        || (concept != null && ConversionUtil.isRefusedByARule(concept.getName()));
	}
	
	/**
	 * @param instance the order frequency
	 * @return its name, which is its concept's, or {@link ConversionUtil#MASKED_DISPLAY} when a rule
	 *         refuses that concept or the name itself
	 */
	@PropertyGetter("name")
	public String getName(OrderFrequency instance) {
		if (readsARefusedRecord(instance)) {
			return ConversionUtil.MASKED_DISPLAY;
		}
		return instance.getName();
	}
	
	/**
	 * @return the localization or the name, as for any metadata, or
	 *         {@link ConversionUtil#MASKED_DISPLAY} when a rule refuses the concept that name is read
	 *         from. The localization is masked too: it is this frequency's own name in the reader's
	 *         locale, so it is the same disclosure by another route
	 */
	@Override
	@PropertyGetter("display")
	public String getDisplayString(OrderFrequency instance) {
		if (readsARefusedRecord(instance)) {
			return ConversionUtil.MASKED_DISPLAY;
		}
		return super.getDisplayString(instance);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addRequiredProperty("frequencyPerDay");
		description.addRequiredProperty("concept");
		
		return description;
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public OrderFrequency newDelegate() {
		return new OrderFrequency();
	}
	
	/**
	 * @see DelegatingCrudResource#save(Object)
	 */
	@Override
	public OrderFrequency save(OrderFrequency orderFrequency) {
		return Context.getOrderService().saveOrderFrequency(orderFrequency);
	}
	
	/**
	 * Fetches a orderFrequency by uuid, or by the uuid or reference term of its concept. (E.g.
	 * supports specifying as "SNOMED CT:307486002")
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(String)
	 */
	@Override
	public OrderFrequency getByUniqueId(String uuid) {
		OrderFrequency frequency = Context.getOrderService().getOrderFrequencyByUuid(uuid);
		if (frequency == null) {
			// concept resource handles things like "SNOMED CT:307486002" in addition to UUIDs
			Concept concept = new ConceptResource1_9().getByUniqueId(uuid);
			if (concept != null) {
				frequency = Context.getOrderService().getOrderFrequencyByConcept(concept);
			}
		}
		return frequency;
	}
	
	/**
	 * @see DelegatingCrudResource#purge(Object,
	 *      RequestContext)
	 */
	@Override
	public void purge(OrderFrequency orderFrequency, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see DelegatingCrudResource#doGetAll(RequestContext)
	 */
	@Override
	protected NeedsPaging<OrderFrequency> doGetAll(RequestContext context) {
		return new NeedsPaging<OrderFrequency>(Context.getOrderService().getOrderFrequencies(context.getIncludeAll()),
		        context);
	}
	
	/**
	 * @see DelegatingCrudResource#doSearch(RequestContext)
	 */
	@Override
	protected NeedsPaging<OrderFrequency> doSearch(RequestContext context) {
		return new NeedsPaging<OrderFrequency>(Context.getOrderService().getOrderFrequencies(context.getParameter("q"),
		    null, false, context.getIncludeAll()), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants1_10.RESOURCE_VERSION;
	}
}
