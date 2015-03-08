/**
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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openmrs.ConceptClass;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSearchResult;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for
 * {@link org.openmrs.ConceptSearchResult}, supporting only search operations for concepts and
 * returns ConceptSearchResults instead of Concepts, this is useful when searching for concepts and
 * the client needs to know extra details about the matches e.g the matched words, concept names and
 * their weights
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptsearch", supportedClass = ConceptSearchResult.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class ConceptSearchResource1_9 extends BaseDelegatingResource<ConceptSearchResult> implements Searchable {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		if (rep instanceof RefRepresentation || rep instanceof DefaultRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("concept", Representation.REF);
			description.addProperty("conceptName", Representation.REF);
		} else if (rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("concept", Representation.DEFAULT);
			description.addProperty("conceptName", Representation.DEFAULT);
			description.addProperty("word");
			description.addProperty("transientWeight");
		}
		
		return description;
	}
	
	/**
	 * @see
	 */
	@PropertyGetter("display")
	public String getDisplayString(ConceptSearchResult csr) {
		ConceptName cn = csr.getConcept().getName();
		return cn == null ? null : cn.getName();
	}
	
	@Override
	public SimpleObject search(RequestContext context) throws ResponseException {
		String query = context.getParameter("q");
		List<ConceptClass> conceptClasses = null;
		String[] classUuids = context.getRequest().getParameterValues("conceptClasses");
		if (classUuids != null) {
			for (String uuid : classUuids) {
				if (conceptClasses == null) {
					conceptClasses = new ArrayList<ConceptClass>();
				}
				ConceptClass cc = (ConceptClass) ConversionUtil.convert(uuid, ConceptClass.class);
				if (cc != null) {
					conceptClasses.add(cc);
				}
			}
		}
		
		List<Locale> locales = new ArrayList<Locale>();
		locales.add(Context.getLocale());
		
		return new NeedsPaging<ConceptSearchResult>(Context.getConceptService().getConcepts(query, locales,
		    context.getIncludeAll(), conceptClasses, null, null, null, null, context.getStartIndex(), context.getLimit()),
		        context).toSimpleObject(this);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public ConceptSearchResult newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(String)
	 */
	@Override
	public ConceptSearchResult getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#save(Object)
	 */
	@Override
	public ConceptSearchResult save(ConceptSearchResult delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(Object,
	 *      String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(ConceptSearchResult delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(ConceptSearchResult delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
