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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link Resource} for {@link ConceptSource}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptsource", supportedClass = ConceptSource.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class ConceptSourceResource1_8 extends MetadataDelegatingCrudResource<ConceptSource> {
	
	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("hl7Code");
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
			description.addProperty("hl7Code");
			description.addProperty("retired");
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
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addRequiredProperty("name");
		description.addRequiredProperty("description");
		description.addProperty("hl7Code");
		
		return description;
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public ConceptSource newDelegate() {
		return new ConceptSource();
	}
	
	/**
	 * Fetches a conceptSource by uuid, if no match is found, it tries to look up one with a
	 * matching name with the assumption that the passed parameter is a conceptSource name
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public ConceptSource getByUniqueId(String uuid) {
		ConceptSource conceptSource = Context.getConceptService().getConceptSourceByUuid(uuid);
		//We assume the caller was fetching by name
		if (conceptSource == null)
			conceptSource = Context.getConceptService().getConceptSourceByName(uuid);
		
		return conceptSource;
	}
	
	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public ConceptSource save(ConceptSource conceptSource) {
		return Context.getConceptService().saveConceptSource(conceptSource);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(ConceptSource conceptSource, RequestContext context) throws ResponseException {
		if (conceptSource == null)
			return;
		Context.getConceptService().purgeConceptSource(conceptSource);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<ConceptSource> doGetAll(RequestContext context) {
		List<ConceptSource> sources = Context.getConceptService().getAllConceptSources();
		if (context.getIncludeAll()) {
			return new NeedsPaging<ConceptSource>(sources, context);
		}
		List<ConceptSource> unretiredSources = new ArrayList<ConceptSource>();
		for (ConceptSource conceptSource : sources) {
			if (!conceptSource.isRetired())
				unretiredSources.add(conceptSource);
		}
		return new NeedsPaging<ConceptSource>(unretiredSources, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<ConceptSource> doSearch(RequestContext context) {
		List<ConceptSource> sources = Context.getConceptService().getAllConceptSources();
		for (Iterator<ConceptSource> iterator = sources.iterator(); iterator.hasNext();) {
			ConceptSource conceptSource = iterator.next();
			//find matches excluding retired ones if necessary
			if (!Pattern.compile(Pattern.quote(context.getParameter("q")), Pattern.CASE_INSENSITIVE).matcher(
			    conceptSource.getName()).find()
			        || (!context.getIncludeAll() && conceptSource.isRetired())) {
				iterator.remove();
			}
		}
		return new NeedsPaging<ConceptSource>(sources, context);
	}
}
