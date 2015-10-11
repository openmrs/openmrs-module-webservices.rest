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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

/**
 * {@link Resource} for {@link ConceptReferenceTerm}, supporting standard CRUD
 * operations
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptreferenceterm", order = 100, supportedClass = ConceptReferenceTerm.class, supportedOpenmrsVersions = {
		"1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class ConceptReferenceTermResource1_9 extends
		MetadataDelegatingCrudResource<ConceptReferenceTerm> {

	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("conceptSource", Representation.REF);
			description.addProperty("description");
			description.addProperty("code");
			description.addProperty("version");
			description.addProperty("retired");
			description.addSelfLink();
			description.addLink("full", ".?v="
					+ RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("conceptSource");
			description.addProperty("description");
			description.addProperty("code");
			description.addProperty("version");
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
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("code");
		description.addRequiredProperty("conceptSource");
		description.addProperty("name");
		description.addProperty("description");
		description.addProperty("version");

		return description;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource#getDisplayString(org.openmrs.OpenmrsMetadata)
	 */
	@Override
	@PropertyGetter("display")
	public String getDisplayString(ConceptReferenceTerm delegate) {
		if (delegate.getConceptSource() == null) {
			return "";
		}
		String display = delegate.getConceptSource().getName() + ": "
				+ delegate.getCode();

		if (!StringUtils.isBlank(delegate.getName())) {
			display += " (" + delegate.getName() + ")";
		}

		return display;
	}

	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public ConceptReferenceTerm newDelegate() {
		return new ConceptReferenceTerm();
	}

	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public ConceptReferenceTerm save(ConceptReferenceTerm conceptReferenceTerm) {
		return Context.getConceptService().saveConceptReferenceTerm(
				conceptReferenceTerm);
	}

	/**
	 * Fetches a conceptReferenceTerm by uuid
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public ConceptReferenceTerm getByUniqueId(String uuid) {
		return Context.getConceptService().getConceptReferenceTermByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(ConceptReferenceTerm conceptReferenceTerm,
			RequestContext context) throws ResponseException {
		if (conceptReferenceTerm == null)
			return;
		Context.getConceptService().purgeConceptReferenceTerm(
				conceptReferenceTerm);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<ConceptReferenceTerm> doGetAll(RequestContext context) {
		return new NeedsPaging<ConceptReferenceTerm>(Context
				.getConceptService().getConceptReferenceTerms(
						context.getIncludeAll()), context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		ConceptService cs = Context.getConceptService();
		String query = context.getParameter("q");
		if (query == null)
			return new EmptySearchResult();
		List<ConceptReferenceTerm> terms = cs.getConceptReferenceTerms(query,
				null, context.getStartIndex(), context.getLimit(),
				context.getIncludeAll());
		int count = cs.getCountOfConceptReferenceTerms(query, null,
				context.getIncludeAll());
		boolean hasMore = count > context.getStartIndex() + context.getLimit();
		return new AlreadyPaged<ConceptReferenceTerm>(context, terms, hasMore);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "1.9";
	}
}
