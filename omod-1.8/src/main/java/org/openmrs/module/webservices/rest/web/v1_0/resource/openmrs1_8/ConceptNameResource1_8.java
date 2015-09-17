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
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for ConceptNames, supporting standard CRUD operations
 */

@SubResource(parent = ConceptResource1_8.class, path = "name", supportedClass = ConceptName.class, supportedOpenmrsVersions = "1.8.*")
public class ConceptNameResource1_8 extends DelegatingSubResource<ConceptName, Concept, ConceptResource1_8> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("locale");
			description.addProperty("localePreferred");
			description.addProperty("conceptNameType");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("locale");
			description.addProperty("localePreferred");
			description.addProperty("conceptNameType");
			//description.addProperty("tags");
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
		description.addRequiredProperty("name");
		description.addRequiredProperty("locale");
		description.addProperty("localePreferred");
		description.addProperty("conceptNameType");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Concept getParent(ConceptName instance) {
		return instance.getConcept();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(ConceptName instance, Concept concept) {
		instance.setConcept(concept);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<ConceptName> doGetAll(Concept parent, RequestContext context) throws ResponseException {
		List<ConceptName> names = new ArrayList<ConceptName>();
		if (parent != null) {
			for (ConceptName name : parent.getNames()) {
				if (!name.isVoided())
					names.add(name);
			}
		}
		return new NeedsPaging<ConceptName>(names, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public ConceptName getByUniqueId(String uuid) {
		return Context.getConceptService().getConceptNameByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(ConceptName cn, String reason, RequestContext context) throws ResponseException {
		cn.setVoided(true);
		cn.setVoidedBy(Context.getAuthenticatedUser());
		cn.setVoidReason(reason);
		cn.setDateVoided(new Date());
		Context.getConceptService().saveConcept(cn.getConcept());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(ConceptName cn, RequestContext context) throws ResponseException {
		cn.getConcept().removeName(cn);
		Context.getConceptService().saveConcept(cn.getConcept());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public ConceptName save(ConceptName newName) {
		// make sure that the name has actually been added to the concept
		boolean needToAdd = true;
		for (ConceptName cn : newName.getConcept().getNames()) {
			if (cn.equals(newName)) {
				needToAdd = false;
				break;
			}
		}
		if (needToAdd)
			newName.getConcept().addName(newName);
		Context.getConceptService().saveConcept(newName.getConcept());
		return newName;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public ConceptName newDelegate() {
		return new ConceptName();
	}
	
	/**
	 * Gets the display string for a concept name.
	 * 
	 * @param conceptName the concept name object.
	 * @return the display string.
	 */
	@PropertyGetter("display")
	public String getDisplayString(ConceptName conceptName) {
		return conceptName.getName();
	}
	
	/**
	 * Gets the locale as a string
	 * 
	 * @param instance
	 * @return
	 */
	@PropertyGetter("locale")
	public String getLocaleAsString(ConceptName instance) {
		
		if (instance.getLocale() == null)
			return "";
		
		return instance.getLocale().toString();
	}
}
