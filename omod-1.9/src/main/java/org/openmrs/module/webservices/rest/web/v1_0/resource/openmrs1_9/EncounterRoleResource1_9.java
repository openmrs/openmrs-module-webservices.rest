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

import org.openmrs.EncounterRole;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link EncounterRole}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/encounterrole", supportedClass = EncounterRole.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"})

public class EncounterRoleResource1_9 extends MetadataDelegatingCrudResource<EncounterRole> {

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addRequiredProperty("name");
		description.addRequiredProperty("description");
		
		return description;
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public EncounterRole newDelegate() {
		return new EncounterRole();
	}

	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public EncounterRole save(EncounterRole encounterRole) {
		return Context.getEncounterService().saveEncounterRole(encounterRole);
	}
	
	/**
	 * Fetches a encounterRole by uuid, if no match is found, it tries to look up one with a
	 * matching name with the assumption that the passed parameter is a encounterRole name
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public EncounterRole getByUniqueId(String uuid) {
		return Context.getEncounterService().getEncounterRoleByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(EncounterRole encounterRole, RequestContext context) throws ResponseException {
		if (encounterRole == null) {
			return;
		}
		
		Context.getEncounterService().purgeEncounterRole(encounterRole);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<EncounterRole> doGetAll(RequestContext context) {
		return new NeedsPaging<EncounterRole>(Context.getEncounterService().getAllEncounterRoles(context.getIncludeAll()),
		        context);
	}
}
