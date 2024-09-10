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

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.RefProperty;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link ConceptMap}, supporting standard CRUD operations
 */
@SubResource(path = "mapping", parent = ConceptResource1_9.class, supportedClass = ConceptMap.class, supportedOpenmrsVersions = {
        "1.9.* - 9.*" })
public class ConceptMapResource1_9 extends DelegatingSubResource<ConceptMap, Concept, ConceptResource1_9> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("conceptReferenceTerm", Representation.REF);
			description.addProperty("conceptMapType", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("conceptReferenceTerm", Representation.DEFAULT);
			description.addProperty("conceptMapType", Representation.DEFAULT);
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation) {
			model
			        .property("conceptReferenceTerm", new RefProperty("#/definitions/ConceptreferencetermGetRef"))
			        .property("conceptMapType", new RefProperty("#/definitions/ConceptmaptypeGetRef"));
		} else if (rep instanceof FullRepresentation) {
			model
			        .property("conceptReferenceTerm", new RefProperty("#/definitions/ConceptreferencetermGet"))
			        .property("conceptMapType", new RefProperty("#/definitions/ConceptmaptypeGet"));
		}
		model.getProperties().remove("source"); //FIXME check
		model.getProperties().remove("sourceCode");
		model.getProperties().remove("comment");
		return model;
	}
	
	@Override
	public Model getCREATEModel(Representation representation) {
		return new ModelImpl()
		        .property("conceptReferenceTerm", new RefProperty("#/definitions/ConceptreferencetermCreate"))
		        .property("conceptMapType", new RefProperty("#/definitions/ConceptmaptypeCreate"))
		        
		        .required("conceptReferenceTerm").required("conceptMapType");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("conceptReferenceTerm");
		description.addRequiredProperty("conceptMapType");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "1.9";
	}
	
	/**
	 * Gets the display string for a concept map.
	 * 
	 * @param conceptMap the concept map object.
	 * @return the display string.
	 */
	@PropertyGetter("display")
	public String getDisplayString(ConceptMap conceptMap) {
		if (conceptMap.getConceptReferenceTerm() == null || conceptMap.getConceptReferenceTerm().getConceptSource() == null) {
			return "";
		}
		
		String display = conceptMap.getConceptReferenceTerm().getConceptSource().getName() + ": "
		        + conceptMap.getConceptReferenceTerm().getCode();
		if (!StringUtils.isBlank(conceptMap.getConceptReferenceTerm().getName())) {
			display += " (" + conceptMap.getConceptReferenceTerm().getName() + ")";
		}
		return display;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#newDelegate()
	 */
	@Override
	public ConceptMap newDelegate() {
		return new ConceptMap();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public ConceptMap save(ConceptMap newMap) {
		newMap.getConcept().addConceptMapping(newMap);
		Context.getConceptService().saveConcept(newMap.getConcept());
		return newMap;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Concept getParent(ConceptMap instance) {
		return instance.getConcept();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(ConceptMap instance, Concept parent) {
		instance.setConcept(parent);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public PageableResult doGetAll(Concept parent, RequestContext context) throws ResponseException {
		List<ConceptMap> maps = new ArrayList<ConceptMap>(parent.getConceptMappings());
		return new NeedsPaging<ConceptMap>(maps, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public ConceptMap getByUniqueId(String uniqueId) {
		return Context.getService(RestHelperService.class).getObjectByUuid(ConceptMap.class, uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(ConceptMap delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(ConceptMap delegate, RequestContext context) throws ResponseException {
		delegate.getConcept().removeConceptMapping(delegate);
	}
}
