/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_12;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openmrs.Concept;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.OrderType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for OrderSetMembers, supporting standard CRUD operations
 */
@SubResource(parent = OrderSetResource1_12.class, path = "ordersetmember", supportedClass = OrderSetMember.class, supportedOpenmrsVersions = {
        "1.12.* - 9.*" })
public class OrderSetMemberResource1_12 extends DelegatingSubResource<OrderSetMember, OrderSet, OrderSetResource1_12> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		if (rep instanceof DefaultRepresentation) {
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("retired");
			description.addProperty("orderType", Representation.REF);
			description.addProperty("orderTemplate");
			description.addProperty("orderTemplateType");
			description.addProperty("concept", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("retired");
			description.addProperty("orderType", Representation.DEFAULT);
			description.addProperty("orderTemplate");
			description.addProperty("orderTemplateType");
			description.addProperty("concept", Representation.DEFAULT);
			description.addSelfLink();
			description.addProperty("auditInfo");
			return description;
		} else if (rep instanceof RefRepresentation) {
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("concept", Representation.REF);
			description.addSelfLink();
		}
		return null;
	}
	
	@PropertyGetter("display")
	public String getDisplayString(OrderSetMember orderSetMember) {
		return orderSetMember.getDescription();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("orderType");
		description.addProperty("orderTemplate");
		description.addProperty("concept");
		description.addProperty("retired");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}

	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> schema = super.getGETSchema(rep);
		if (schema != null && (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation)) {
            schema
					.addProperty("uuid", new StringSchema())
					.addProperty("display", new StringSchema())
					.addProperty("retired", new BooleanSchema())
					.addProperty("orderTemplate", new StringSchema())
					.addProperty("orderTemplateType", new StringSchema());

			if (rep instanceof DefaultRepresentation) {
				schema
						.addProperty("orderType", new Schema<OrderType>().$ref("#/components/schemas/OrdertypeGetRef"))
						.addProperty("concept", new Schema<Concept>().$ref("#/components/schemas/ConceptGetRef"));
			} else if (rep instanceof FullRepresentation) {
				schema
						.addProperty("orderType", new Schema<OrderType>().$ref("#/components/schemas/OrdertypeGet"))
						.addProperty("concept", new Schema<Concept>().$ref("#/components/schemas/ConceptGet"));
			}
		}
		return schema;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		return new ObjectSchema()
				.addProperty("orderType", new ObjectSchema()
						.addProperty("uuid", new StringSchema()))
				.addProperty("orderTemplate", new StringSchema())
				.addProperty("concept", new StringSchema().example("uuid"))
				.addProperty("retired", new BooleanSchema());
	}

	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return getCREATESchema(rep);
	}
	
	@Override
	public OrderSetMember getByUniqueId(String uniqueId) {
		return Context.getOrderSetService().getOrderSetMemberByUuid(uniqueId);
	}
	
	@Override
	protected void delete(OrderSetMember orderSetMember, String reason, RequestContext context) throws ResponseException {
		OrderSet orderSet = orderSetMember.getOrderSet();
		orderSet.retireOrderSetMember(orderSetMember);
		Context.getOrderSetService().saveOrderSet(orderSet);
	}
	
	@Override
	public OrderSetMember newDelegate() {
		return new OrderSetMember();
	}
	
	@Override
	public OrderSetMember save(OrderSetMember delegate) {
		OrderSet parent = delegate.getOrderSet();
		parent.addOrderSetMember(delegate);
		Context.getOrderSetService().saveOrderSet(parent);
		return delegate;
	}
	
	@Override
	public void purge(OrderSetMember orderSetMember, RequestContext context) throws ResponseException {
		OrderSet orderSet = orderSetMember.getOrderSet();
		orderSet.removeOrderSetMember(orderSetMember);
		Context.getOrderSetService().saveOrderSet(orderSet);
	}
	
	@Override
	public OrderSet getParent(OrderSetMember instance) {
		return instance.getOrderSet();
	}
	
	@Override
	public void setParent(OrderSetMember instance, OrderSet orderSet) {
		instance.setOrderSet(orderSet);
	}
	
	@Override
	public PageableResult doGetAll(OrderSet parent, RequestContext context) throws ResponseException {
		List<OrderSetMember> orderSetMembers = new ArrayList<OrderSetMember>();
		if (parent != null) {
			for (OrderSetMember orderSetMember : parent.getOrderSetMembers()) {
				orderSetMembers.add(orderSetMember);
			}
		}
		return new NeedsPaging<OrderSetMember>(orderSetMembers, context);
	}
}
