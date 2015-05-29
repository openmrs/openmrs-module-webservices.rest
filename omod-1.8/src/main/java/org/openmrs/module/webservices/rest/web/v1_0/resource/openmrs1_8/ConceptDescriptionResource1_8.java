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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link ConceptDescription}, supporting standard CRUD operations
 */
@SubResource(parent = ConceptResource1_8.class, path = "description", supportedClass = ConceptDescription.class, supportedOpenmrsVersions = "1.8.*")
public class ConceptDescriptionResource1_8 extends DelegatingSubResource<ConceptDescription, Concept, ConceptResource1_8> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addSelfLink();
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("description");
			description.addProperty("locale");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("description");
			description.addProperty("locale");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
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
		description.addRequiredProperty("description");
		description.addRequiredProperty("locale");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Concept getParent(ConceptDescription instance) {
		return instance.getConcept();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(ConceptDescription instance, Concept concept) {
		instance.setConcept(concept);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<ConceptDescription> doGetAll(Concept parent, RequestContext context) throws ResponseException {
		//convert from a collection to a list
		List<ConceptDescription> descriptions = new ArrayList<ConceptDescription>();
		if (parent != null) {
			for (ConceptDescription description : parent.getDescriptions()) {
				descriptions.add(description);
			}
		}
		return new NeedsPaging<ConceptDescription>(descriptions, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public ConceptDescription getByUniqueId(String uuid) {
		return Context.getConceptService().getConceptDescriptionByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(ConceptDescription cd, String reason, RequestContext context) throws ResponseException {
		//concept descriptions are neither voidable nor retireable, so delegate to purge
		purge(cd, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(ConceptDescription cd, RequestContext context) throws ResponseException {
		cd.getConcept().removeDescription(cd);
		Context.getConceptService().saveConcept(cd.getConcept());
	}
	
	/**
	 * @param newDescription
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public ConceptDescription save(ConceptDescription newDescription) {
		newDescription.getConcept().addDescription(newDescription);
		Context.getConceptService().saveConcept(newDescription.getConcept());
		return newDescription;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public ConceptDescription newDelegate() {
		return new ConceptDescription();
	}
	
	/**
	 * Gets the display string for a concept description including its locale.
	 * 
	 * @param conceptDescription the concept description object.
	 * @return the display string.
	 */
	@PropertyGetter("display")
	public String getDisplayString(ConceptDescription conceptDescription) {
		return conceptDescription.getDescription();
	}
	
	/**
	 * Gets extra book-keeping info, for the full representation
	 * 
	 * @param description
	 * @return
	 * @throws Exception
	 */
	public SimpleObject getAuditInfo(ConceptDescription description) throws Exception {
		SimpleObject ret = new SimpleObject();
		ret.put("creator", ConversionUtil.getPropertyWithRepresentation(description, "creator", Representation.REF));
		ret.put("dateCreated", ConversionUtil.convertToRepresentation(description.getDateCreated(), Representation.DEFAULT));
		ret.put("changedBy", ConversionUtil.getPropertyWithRepresentation(description, "changedBy", Representation.REF));
		ret.put("dateChanged", ConversionUtil.convertToRepresentation(description.getDateChanged(), Representation.DEFAULT));
		
		return ret;
	}
	
	/**
	 * Returns the locale as a string
	 * 
	 * @param instance
	 * @return
	 */
	@PropertyGetter("locale")
	public String getLocaleAsString(ConceptDescription instance) {
		if (instance.getLocale() == null)
			return "";
		
		return instance.getLocale().toString();
	}
}
