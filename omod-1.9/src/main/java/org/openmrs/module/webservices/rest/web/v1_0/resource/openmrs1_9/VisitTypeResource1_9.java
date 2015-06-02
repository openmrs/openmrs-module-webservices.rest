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

import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Resource} for {@link VisitType}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/visittype", supportedClass = VisitType.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class VisitTypeResource1_9 extends MetadataDelegatingCrudResource<VisitType> {
	
	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		// superclass has the desired behavior
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addRequiredProperty("name");
		description.addProperty("description");
		
		return description;
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public VisitType newDelegate() {
		return new VisitType();
	}
	
	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public VisitType save(VisitType visitType) {
		return Context.getVisitService().saveVisitType(visitType);
	}
	
	/**
	 * Fetches a visitType by uuid, if no match is found, it tries to look up one with a matching
	 * name with the assumption that the passed parameter is a visitType name
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public VisitType getByUniqueId(String uuid) {
		VisitType visitType = Context.getVisitService().getVisitTypeByUuid(uuid);
		//We assume the caller was fetching by name, 1.9.0 has no method to fetch by name
		if (visitType == null) {
			List<VisitType> visitTypes = Context.getVisitService().getAllVisitTypes();
			for (VisitType possibleVisitType : visitTypes) {
				if (possibleVisitType.getName().equalsIgnoreCase(uuid))
					return possibleVisitType;
			}
		}
		
		return visitType;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(VisitType visitType, RequestContext context) throws ResponseException {
		if (visitType == null)
			return;
		Context.getVisitService().purgeVisitType(visitType);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<VisitType> doGetAll(RequestContext context) {
		//Apparently, in 1.9.0 this method returns all and has no argument for excluding retired ones
		List<VisitType> visitTypes = Context.getVisitService().getAllVisitTypes();
		List<VisitType> unRetiredVisitTypes = new ArrayList<VisitType>();
		for (VisitType visitType : visitTypes) {
			if (!visitType.isRetired())
				unRetiredVisitTypes.add(visitType);
		}
		return new NeedsPaging<VisitType>(unRetiredVisitTypes, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<VisitType> doSearch(RequestContext context) {
		List<VisitType> visitTypes = Context.getVisitService().getVisitTypes(context.getParameter("q"));
		List<VisitType> unRetiredVisitTypes = new ArrayList<VisitType>();
		for (VisitType visitType : visitTypes) {
			if (!visitType.isRetired())
				unRetiredVisitTypes.add(visitType);
		}
		return new NeedsPaging<VisitType>(unRetiredVisitTypes, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "1.9";
	}
}
