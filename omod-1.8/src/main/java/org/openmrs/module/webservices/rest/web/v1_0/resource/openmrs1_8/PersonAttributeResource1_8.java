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

import org.openmrs.Attributable;
import org.openmrs.Concept;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
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
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * {@link Resource} for PersonAttributes, supporting standard CRUD operations
 */
@SubResource(parent = PersonResource1_8.class, path = "attribute", supportedClass = PersonAttribute.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class PersonAttributeResource1_8 extends DelegatingSubResource<PersonAttribute, Person, PersonResource1_8> {

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
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
			description.addProperty("attributeType", Representation.REF);
			description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addProperty("hydratedObject");
			description.addSelfLink();
			return description;
		}
		return null;
	}

	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("value");
		description.addRequiredProperty("attributeType");
		description.addProperty("hydratedObject");
		return description;
	}

    /*
        We may need to hydrate Attributables in the request. To do that we need to know
        its type. So we can't set hydratedObject until attributeType has been set.
        Since SimpleObject is a LinkedHashMap, it is ordered, and the following method is
        overridden to ensure hydratedObject will appear last in the properties.
     */
    @Override
    public Object create(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException {
        uglyMethodToEnsureHydratedObjectWillBeSetLast(post);
        return super.create(parentUniqueId, post, context);
    }

    private void uglyMethodToEnsureHydratedObjectWillBeSetLast(SimpleObject post) {
        Object hydratedObject = post.get("hydratedObject");
        if (hydratedObject != null) {
            post.remove("hydratedObject");
            post.put("hydratedObject", hydratedObject);
        }
    }

    @PropertySetter("hydratedObject")
    public void setHydratedObject(PersonAttribute personAttribute, String attributableUuid) {
        try {
            Class<?> attributableClass = OpenmrsClassLoader.getInstance().loadClass(personAttribute.getAttributeType().getFormat());
            Attributable value = (Attributable) ConversionUtil.convert(attributableUuid, attributableClass);
            personAttribute.setValue(value.serialize());
        } catch (ClassNotFoundException e) {
            throw new APIException("Could not convert value to Attributable", e);
        }
    }

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public PersonAttribute getByUniqueId(String uniqueId) {
		return Context.getPersonService().getPersonAttributeByUuid(uniqueId);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<PersonAttribute> doGetAll(Person parent, RequestContext context) throws ResponseException {
		return new NeedsPaging<PersonAttribute>(parent.getActiveAttributes(), context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(PersonAttribute delegate, String reason, RequestContext context) throws ResponseException {
		delegate.voidAttribute(reason);
		Context.getPersonService().savePerson(delegate.getPerson());
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
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
	 * @return attribute type + value (for concise display purposes)
	 */
	@PropertyGetter("display")
	public String getDisplayString(PersonAttribute pa) {
		if (pa.getAttributeType() == null)
			return "";
        if (Concept.class.getName().equals(pa.getAttributeType().getFormat()) && pa.getValue() != null) {
            Concept concept = Context.getConceptService().getConcept(pa.getValue());
            return concept == null ? null : concept.getDisplayString();
        }
        return pa.getAttributeType().getName() + " = " + pa.getValue();
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
