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

import java.util.ArrayList;
import java.util.List;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.StringProperty;
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
 * {@link Resource} for ConceptMaps, supporting standard CRUD operations
 */
@SubResource(path = "mapping", parent = ConceptResource1_8.class, supportedClass = ConceptMap.class, supportedOpenmrsVersions = "1.8.*")
public class ConceptMapResource1_8 extends DelegatingSubResource<ConceptMap, Concept, ConceptResource1_8> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("source", Representation.REF);
			description.addProperty("sourceCode");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("source", Representation.DEFAULT);
			description.addProperty("sourceCode");
			description.addProperty("comment");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	public Model getGETModel(Representation rep) {
		ModelImpl modelImpl = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation) {
			modelImpl
			        .property("display", new StringProperty())
			        .property("uuid", new StringProperty())
			        .property("source", new StringProperty()) //FIXME
			        .property("sourceCode", new StringProperty());
		} else if (rep instanceof FullRepresentation) {
			modelImpl
			        .property("display", new StringProperty())
			        .property("uuid", new StringProperty())
			        .property("source", new StringProperty()) //FIXME
			        .property("sourceCode", new StringProperty())
			        .property("comment", new StringProperty());
		}
		return modelImpl;
	}
	
	@Override
	public Model getCREATEModel(Representation representation) {
		return new ModelImpl()
		        .property("source", new StringProperty())
		        .property("sourceCode", new StringProperty())
		        .required("source").required("sourceCode");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("source");
		description.addRequiredProperty("sourceCode");
		return description;
	}
	
	/**
	 * Gets the display string for a concept map.
	 * 
	 * @param conceptMap the concept map object.
	 * @return the display string.
	 */
	@PropertyGetter("display")
	public String getDisplayString(ConceptMap conceptMap) {
		//openmrs-api-2.4.x upgrade
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#newDelegate()
	 */
	@Override
	public ConceptMap newDelegate() {
		return new ConceptMap();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(Object)
	 */
	@Override
	public ConceptMap save(ConceptMap newMap) {
		newMap.getConcept().addConceptMapping(newMap);
		Context.getConceptService().saveConcept(newMap.getConcept());
		return newMap;
	}
	
	/**
	 * @see DelegatingSubResource#getParent(Object)
	 */
	@Override
	public Concept getParent(ConceptMap instance) {
		return instance.getConcept();
	}
	
	/**
	 * @see DelegatingSubResource#setParent(Object,
	 *      Object)
	 */
	@Override
	public void setParent(ConceptMap instance, Concept parent) {
		instance.setConcept(parent);
	}
	
	/**
	 * @see DelegatingSubResource#doGetAll(Object,
	 *      RequestContext)
	 */
	@Override
	public PageableResult doGetAll(Concept parent, RequestContext context) throws ResponseException {
		List<ConceptMap> maps = new ArrayList<ConceptMap>(parent.getConceptMappings());
		return new NeedsPaging<ConceptMap>(maps, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(String)
	 */
	@Override
	public ConceptMap getByUniqueId(String uniqueId) {
		return Context.getService(RestHelperService.class).getObjectByUuid(ConceptMap.class, uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(Object,
	 *      String, RequestContext)
	 */
	@Override
	protected void delete(ConceptMap delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(Object,
	 *      RequestContext)
	 */
	@Override
	public void purge(ConceptMap delegate, RequestContext context) throws ResponseException {
		delegate.getConcept().removeConceptMapping(delegate);
	}
	
}
