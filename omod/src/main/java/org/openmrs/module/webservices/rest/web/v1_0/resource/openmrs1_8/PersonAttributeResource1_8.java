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

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Attributable;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * {@link Resource} for PersonAttributes, supporting standard CRUD operations
 */
@SubResource(parent = PersonResource1_8.class, path = "attribute", supportedClass = PersonAttribute.class, supportedOpenmrsVersions = {
        "1.8.* - 9.*" })
public class PersonAttributeResource1_8 extends DelegatingSubResource<PersonAttribute, Person, PersonResource1_8> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("value");
			description.addProperty("attributeType", Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("value");
			description.addProperty("attributeType", Representation.FULL);
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addProperty("hydratedObject");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("attributeType");
		description.addProperty("value");
		description.addProperty("hydratedObject");
		return description;
	}
	
	@PropertySetter("hydratedObject")
	public void setHydratedObject(PersonAttribute personAttribute, String attributableUuid) {
		try {
			Class<?> attributableClass = OpenmrsClassLoader.getInstance().loadClass(
			    personAttribute.getAttributeType().getFormat());
			Attributable value = (Attributable) ConversionUtil.convert(attributableUuid, attributableClass);
			personAttribute.setValue(value.serialize());
		}
		catch (ClassNotFoundException e) {
			throw new APIException("Could not convert value to Attributable", e);
		}
	}

	@PropertySetter("value")
	public void setValue(PersonAttribute personAttribute, String value) {
		if (RestUtil.isValidUuid(value)) {
			Location location = Context.getLocationService().getLocationByUuid(value);
			if (location != null) {
				personAttribute.setValue(location.getId().toString());
				return;
			}
		}

		PersonAttributeType attributeType = personAttribute.getAttributeType();
		if (attributeType == null) {
			personAttribute.setValue(value);
		} else {
			// Check if expected value is attributable and do the right thing.
			try {
				String format = personAttribute.getAttributeType().getFormat();
				if (format == null) {
					personAttribute.setValue(value);
				} else {
					Class<?> clazz = Context.loadClass(personAttribute.getAttributeType().getFormat());
					if (Attributable.class.isAssignableFrom(clazz)) {
						Attributable instance = (Attributable) ConversionUtil.convert(value, clazz);
						if (instance != null) {
							personAttribute.setValue(instance.serialize());
						} else {
							// Could not find a corresponding domain object, so just set the value?
							personAttribute.setValue(value);
						}
					} else {
						// Not Attributable just assign
						personAttribute.setValue(value);
					}
				}
			}
			catch (ClassNotFoundException cnfe) {
				// No Class found? just assign the string
				personAttribute.setValue(value);
			}
			catch (ConversionException ce) {
				// Couldn't convert? just assign the string
				personAttribute.setValue(value);
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation) {
			model
			        .property("display", new StringProperty())
			        .property("uuid", new StringProperty())
			        .property("value", new StringProperty())
			        .property("attributeType", new RefProperty("#/definitions/PersonattributetypeGetRef"))
			        .property("voided", new BooleanProperty());
		} else if (rep instanceof FullRepresentation) {
			model
			        .property("display", new StringProperty())
			        .property("uuid", new StringProperty())
			        .property("value", new StringProperty())
			        .property("attributeType", new RefProperty("#/definitions/PersonattributetypeGetRef"))
			        .property("voided", new BooleanProperty())
			        .property("hydratedObject", new StringProperty());
		}
		return model;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		ModelImpl model = new ModelImpl()
		        .property("attributeType", new StringProperty().example("uuid"))
		        .property("value", new StringProperty())
		        .property("hydratedObject", new StringProperty().example("uuid"))
		        
		        .required("attributeType");
		if (rep instanceof FullRepresentation) {
			model
			        .property("attributeType", new RefProperty("#/definitions/PersonattributetypeCreate"));
		}
		return model;
	}
	
	@Override
	public Model getUPDATEModel(Representation rep) {
		return getCREATEModel(rep);
	}
	
	/**
	 * @see DelegatingSubResource#getParent(Object)
	 */
	@Override
	public Person getParent(PersonAttribute instance) {
		return instance.getPerson();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public PersonAttribute newDelegate() {
		return new PersonAttribute();
	}
	
	/**
	 * @see DelegatingSubResource#setParent(Object,
	 *      Object)
	 */
	@Override
	public void setParent(PersonAttribute instance, Person person) {
		instance.setPerson(person);
	}
	
	/**
	 * Sets the attribute type for a person attribute.
	 * 
	 * @param instance
	 * @param attributeType
	 * @throws org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException
	 */
	@PropertySetter("attributeType")
	public void setAttributeType(PersonAttribute instance, PersonAttributeType attributeType) {
		instance.setAttributeType(Context.getPersonService().getPersonAttributeTypeByUuid(attributeType.getUuid()));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(String)
	 */
	@Override
	public PersonAttribute getByUniqueId(String uniqueId) {
		return Context.getPersonService().getPersonAttributeByUuid(uniqueId);
	}
	
	/**
	 * @see DelegatingSubResource#doGetAll(Object,
	 *      RequestContext)
	 */
	@Override
	public NeedsPaging<PersonAttribute> doGetAll(Person parent, RequestContext context) throws ResponseException {
		return new NeedsPaging<PersonAttribute>(parent.getActiveAttributes(), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(Object)
	 */
	@Override
	public PersonAttribute save(PersonAttribute delegate) {
		// make sure it has not already been added to the person
		boolean needToAdd = true;
		for (PersonAttribute pa : delegate.getPerson().getActiveAttributes()) {
			if (pa.equals(delegate)) {
				needToAdd = false;
				break;
			}
		}
		if (needToAdd)
			delegate.getPerson().addAttribute(delegate);
		
		Context.getPersonService().savePerson(delegate.getPerson());
		
		return delegate;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(Object,
	 *      String, RequestContext)
	 */
	@Override
	protected void delete(PersonAttribute delegate, String reason, RequestContext context) throws ResponseException {
		delegate.voidAttribute(reason);
		Context.getPersonService().savePerson(delegate.getPerson());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(Object,
	 *      RequestContext)
	 */
	@Override
	public void purge(PersonAttribute delegate, RequestContext context) throws ResponseException {
		delegate.getPerson().removeAttribute(delegate);
		Context.getPersonService().savePerson(delegate.getPerson());
	}
	
	/**
	 * Gets the display string for a person attribute.
	 * 
	 * @param pa the person attribute.
	 * @return value (for concise display purposes), or {@link ConversionUtil#MASKED_DISPLAY} when a
	 *         rule refuses the attribute type or the record the value refers to
	 */
	@PropertyGetter("display")
	public String getDisplayString(PersonAttribute pa) {
		// a PersonAttribute without a type cannot really be converted
		if (pa.getAttributeType() == null) {
			return pa.getValue() != null ? pa.getValue() : "";
		}

		Object hydrated = pa.getHydratedObject();
		if (ConversionUtil.isRefusedByARule(pa.getAttributeType()) || ConversionUtil.isRefusedByARule(hydrated)
		        || referencesARecordThatDidNotResolve(pa, hydrated)) {
			return ConversionUtil.MASKED_DISPLAY;
		}

		// PersonAttribute#toString() calls PersonAttribute#hydrateObject() and Attributable#getDisplayString()
		String value = pa.toString();
		return value == null ? "" : value;
	}

	/**
	 * Whether this attribute's value names a record that hydration did not produce, in which case the
	 * raw value must not be printed in its place.
	 * <p>
	 * A refusal reaches this getter as nothing else. The value of a concept-typed attribute is a
	 * concept id, {@code Concept.hydrate} resolves it through {@code ConceptService}, whose
	 * {@code hasPermission} guard throws when a rule refuses what it was about to return - and
	 * {@code hydrate} catches that and answers null. {@code PersonAttribute.toString()} then falls back
	 * to printing {@code value} itself, which is the refused concept's own id, so the response carried
	 * {@code "display": "6"} with nothing under {@code value} at all. There is no object left for
	 * {@link ConversionUtil#isRefusedByARule(Object)} to judge by then, only the null.
	 * <p>
	 * A reference that did not resolve for the other reason - the record is gone - is masked too, since
	 * the two are indistinguishable from here. That is the side to err on: {@code value} is null either
	 * way, so this keeps the two properties telling one story, and a dangling key was never a display
	 * string. Narrowed to a format naming a record so that it stays that choice and not a wider one: a
	 * value that is its own content rather than a reference ({@code AttributableDate}) hydrates to null
	 * when it is malformed, and printing that back is all this getter can do.
	 */
	private static boolean referencesARecordThatDidNotResolve(PersonAttribute pa, Object hydrated) {
		if (hydrated != null || StringUtils.isBlank(pa.getValue())) {
			return false;
		}
		try {
			return OpenmrsObject.class.isAssignableFrom(OpenmrsClassLoader.getInstance().loadClass(
			    pa.getAttributeType().getFormat()));
		}
		catch (Exception e) {
			// a format naming no loadable class never hydrates to anything either, and
			// PersonAttribute#getHydratedObject answers the raw value rather than null for it, so this
			// branch is not the state above
			return false;
		}
	}

	/**
	 * Gets the hydrated object of person attribute.
	 *
	 * @param pa the person attribute.
	 * @return an object containing the hydrated object.
	 */
	@PropertyGetter("value")
	public Object getValue(PersonAttribute pa) {
		Object value = pa.getHydratedObject();
		if (value == null) {
			return null;
		}

		return ConversionUtil.convertToRepresentation(value, Representation.REF);
	}
}
