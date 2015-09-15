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

import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

/**
 * {@link Resource} for {@link Drug}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/drug", supportedClass = Drug.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*"})
public class DrugResource1_8 extends MetadataDelegatingCrudResource<Drug> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Drug getByUniqueId(String uniqueId) {
		return Context.getConceptService().getDrugByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public Drug newDelegate() {
		return new Drug();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public Drug save(Drug delegate) {
		return Context.getConceptService().saveDrug(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Drug delegate, RequestContext context) throws ResponseException {
		if (delegate == null)
			return;
		Context.getConceptService().purgeDrug(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("retired");
			description.addProperty("dosageForm", Representation.REF);
			description.addProperty("doseStrength");
			description.addProperty("maximumDailyDose");
			description.addProperty("minimumDailyDose");
			description.addProperty("units");
			description.addProperty("concept", Representation.REF);
			description.addProperty("combination");
			description.addProperty("route", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("retired");
			description.addProperty("dosageForm", Representation.REF);
			description.addProperty("doseStrength");
			description.addProperty("maximumDailyDose");
			description.addProperty("minimumDailyDose");
			description.addProperty("units");
			description.addProperty("concept", Representation.REF);
			description.addProperty("combination");
			description.addProperty("route", Representation.REF);
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		//Let the superclass handle this
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addRequiredProperty("combination");
		description.addRequiredProperty("concept");
		
		description.addProperty("name");
		description.addProperty("doseStrength");
		description.addProperty("maximumDailyDose");
		description.addProperty("minimumDailyDose");
		description.addProperty("units");
		description.addProperty("dosageForm");
		description.addProperty("route");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<Drug> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<Drug>(Context.getConceptService().getAllDrugs(context.getIncludeAll()), context);
	}

    /**
     * Drug searches support the following query parameters:
     * <ul>
     * <li>q=(name): searches drug with name containing the query string
     * </li>
     * </ul>
     *
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(RequestContext)
     */
    @Override
    protected PageableResult doSearch(RequestContext ctx) {
        boolean searchOnPhrase = true;
        boolean searchDrugConceptNames = false;
        boolean includeRetired = ctx.getIncludeAll();
        Integer startIndex = ctx.getStartIndex();
        Integer limit = ctx.getLimit();
        String drugName = ctx.getParameter("q");

        Integer countOfDrugs = Context.getConceptService().getCountOfDrugs(drugName, null, searchOnPhrase, searchDrugConceptNames, includeRetired);
        List<Drug> drugs = Context.getConceptService().getDrugs(drugName, null, searchOnPhrase, searchDrugConceptNames, includeRetired, startIndex, limit);
        boolean hasMore = countOfDrugs > startIndex + limit;
        return new AlreadyPaged<Drug>(ctx, drugs, hasMore);
    }
}
