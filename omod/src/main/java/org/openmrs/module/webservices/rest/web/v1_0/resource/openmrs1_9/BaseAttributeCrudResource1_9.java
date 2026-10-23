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

import java.util.Arrays;
import java.util.List;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.attribute.Attribute;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.customdatatype.NotYetPersistedException;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.springframework.security.access.AccessDeniedException;

/**
 * Subclass of {@link DelegatingSubResource} with helper methods specific to {@link Attribute}
 * 
 * @param <T> The type of the attribute this sub resource is associated to
 * @param <P> The parent/owning type for the type T
 * @param <PR> The Resource for the parent/owning type P
 */
public abstract class BaseAttributeCrudResource1_9<T extends Attribute<?, ?>, P, PR> extends DelegatingSubResource<T, P, DelegatingCrudResource<P>> {
	
	/**
	 * Stands for a value the service a datatype resolves its reference through would not hand over. Not
	 * null, which this resource already uses for an attribute that has no value to resolve yet.
	 */
	private static final Object REFUSED = new Object();

	/**
	 * Sets value on the given attribute.
	 * 
	 * @param instance
	 * @param value
	 */
	@PropertySetter("value")
	public static void setValue(Attribute<?, ?> instance, String value) throws Exception {
		CustomDatatype<?> datatype = CustomDatatypeUtil.getDatatype(instance.getAttributeType().getDatatypeClassname(),
		    instance.getAttributeType().getDatatypeConfig());
		if (StringUtils.isNotEmpty(value)) // check empty instead of blank, because " " is meaningful
			instance.setValue(datatype.fromReferenceString(value));
	}
	
	/**
	 * Gets an attribute value, catching any {@link NotYetPersistedException} and returning null in
	 * that case
	 * 
	 * @param instance
	 * @return the typed value, {@link ConversionUtil#maskedRepresentation()} when reading the record it
	 *         refers to was refused, or null when the attribute has not been persisted yet
	 */
	@PropertyGetter("value")
	public static Object getValue(Attribute<?, ?> instance) {
		Object value = resolveValue(instance);
		return value == REFUSED ? ConversionUtil.maskedRepresentation() : value;
	}

	/**
	 * The attribute's typed value, or {@link #REFUSED} when the record it refers to was not handed
	 * over, or null when there is nothing to resolve yet.
	 * <p>
	 * A refusal arrives here as an exception rather than as a masked object: a datatype deserializes
	 * through a guarded service method - {@code ConceptDatatype} calls
	 * {@code ConceptService.getConceptByUuid}, whose {@code hasPermission} guard covers the privilege
	 * and every rule registered for {@code Concept} in one expression - and a guard throws where
	 * {@code Authorize} would have masked. Unhandled, it came out of both getters below and failed the
	 * whole representation, so a visit carrying one attribute the reader may not see served none of the
	 * visit at all.
	 * <p>
	 * What the callers do with it is mask, and the mask deliberately does not distinguish a rule's
	 * refusal from a missing privilege, because the guard that threw does not either. That is the side
	 * to err on: a reader is told this one value is hidden rather than handed an error for the whole
	 * record that carries it. Dropping the property the way a privilege denial does elsewhere is not
	 * available here, since that happens in {@code ConversionUtil.convertToRepresentation} and this
	 * value never reaches it.
	 */
	private static Object resolveValue(Attribute<?, ?> instance) {
		try {
			return instance.getValue();
		}
		catch (NotYetPersistedException ex) {
			return null;
		}
		catch (AccessDeniedException ex) {
			// covers the legacy exception too: as of core 3.0.0 APIAuthenticationException extends this,
			// which is the same pairing BaseRestController's handler relies on
			return REFUSED;
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("attributeType", Representation.REF);
			description.addProperty("value");
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("attributeType", Representation.REF);
			description.addProperty("value");
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("attributeType");
		description.addRequiredProperty("value");
		return description;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
		        .property("attributeType", new StringProperty().example("uuid"))
		        .property("value", new StringProperty())
		        
		        .required("attributeType").required("value");
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model
			        .property("display", new StringProperty())
			        .property("uuid", new StringProperty())
			        .property("attributeType", new StringProperty()) //FIXME type
			        .property("value", new StringProperty()) //FIXME type
			        .property("voided", new BooleanProperty());
		}
		return model;
	}
	
	/**
	 * Gets the display string for an attribute.
	 * 
	 * @param attr the attribute.
	 * @return attribute type: value (for concise display purposes), or
	 *         {@link ConversionUtil#MASKED_DISPLAY} when a rule refuses the attribute type or the
	 *         record the value resolves to
	 */
	@PropertyGetter("display")
	public String getDisplayString(T attr) {
		if (attr.getAttributeType() == null)
			return "";
		// a datatype backed by a record hands one back - a Concept for ConceptDatatype, a Location for
		// LocationDatatype - and that is masked under "value" while this printed its toString() beside
		// the mask: "Concept #6" names the refused concept by its id. The type is a record of its own
		// too, masked under "attributeType"
		Object value = resolveValue(attr);
		if (value == REFUSED || ConversionUtil.isRefusedByARule(attr.getAttributeType())
		        || ConversionUtil.isRefusedByARule(value)) {
			return ConversionUtil.MASKED_DISPLAY;
		}
		return attr.getAttributeType().getName() + ": " + value;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "1.9";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getPropertiesToExposeAsSubResources()
	 */
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("attributes");
	}
}
