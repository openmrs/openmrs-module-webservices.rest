/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openmrs.attribute.AttributeType;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;

import java.util.Collections;

/**
 * Subclass of {@link MetadataDelegatingCrudResource} with helper methods specific to
 * {@link AttributeType}
 * 
 * @param <T>
 */
public abstract class BaseAttributeTypeCrudResource1_9<T extends AttributeType<?>> extends MetadataDelegatingCrudResource<T> {
	
	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> model = (Schema<?>) super.getGETSchema(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model
			        .addProperty("minOccurs", new IntegerSchema())
			        .addProperty("maxOccurs", new IntegerSchema())
			        .addProperty("datatypeClassname", new StringSchema())
			        .addProperty("preferredHandlerClassname", new StringSchema());
		}
		if (rep instanceof FullRepresentation) {
			model
			        .addProperty("datatypeConfig", new StringSchema())
			        .addProperty("handlerConfig", new StringSchema());
		}
		return model;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Schema<?> getCREATESchema(Representation rep) {
		return super.getCREATESchema(rep)
				.addProperty("datatypeClassname", new StringSchema())
				.addProperty("minOccurs", new IntegerSchema())
				.addProperty("maxOccurs", new IntegerSchema())
				.addProperty("datatypeConfig", new StringSchema())
				.addProperty("preferredHandlerClassname", new StringSchema())
				.addProperty("handlerConfig", new StringSchema()).required(Collections.singletonList("datatypeClassname"));
		
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
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("minOccurs");
			description.addProperty("maxOccurs");
			description.addProperty("datatypeClassname");
			description.addProperty("preferredHandlerClassname");
			description.addProperty("retired");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("minOccurs");
			description.addProperty("maxOccurs");
			description.addProperty("datatypeClassname");
			description.addProperty("datatypeConfig");
			description.addProperty("preferredHandlerClassname");
			description.addProperty("handlerConfig");
			description.addProperty("retired");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		description.addRequiredProperty("datatypeClassname");
		description.addProperty("description");
		description.addProperty("minOccurs");
		description.addProperty("maxOccurs");
		description.addProperty("datatypeConfig");
		description.addProperty("preferredHandlerClassname");
		description.addProperty("handlerConfig");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "1.9";
	}
}
