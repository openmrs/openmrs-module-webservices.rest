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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptSourceResource1_8;

/**
 * {@link Resource} for {@link ConceptSource}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptsource", supportedClass = ConceptSource.class, supportedOpenmrsVersions = {"2.0.*", "2.1.*"})
public class ConceptSourceResource2_0 extends ConceptSourceResource1_8 {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<ConceptSource> doGetAll(RequestContext context) {
		return doGetAll(context, Context.getConceptService().getAllConceptSources(context.getIncludeAll()));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<ConceptSource> doSearch(RequestContext context) {
		return doSearch(context, Context.getConceptService().getAllConceptSources(context.getIncludeAll()));
	}
}
