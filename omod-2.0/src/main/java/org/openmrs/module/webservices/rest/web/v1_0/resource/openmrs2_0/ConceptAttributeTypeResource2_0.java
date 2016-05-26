/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.openmrs.ConceptAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.BaseAttributeTypeCrudResource1_9;

/**
 * Allows standard CRUD for the {@link ConceptAttributeType} domain object
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptattributetype", supportedClass = ConceptAttributeType.class, supportedOpenmrsVersions = { "2.0.*", "2.1.*" })
public class ConceptAttributeTypeResource2_0 extends BaseAttributeTypeCrudResource1_9<ConceptAttributeType> {

	public ConceptAttributeTypeResource2_0() {
	}

	private ConceptService service() {
		return Context.getConceptService();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(String)
	 */
	@Override
	public ConceptAttributeType getByUniqueId(String uniqueId) {
		return service().getConceptAttributeTypeByUuid(uniqueId);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(RequestContext)
	 */
	@Override
	protected NeedsPaging<ConceptAttributeType> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<ConceptAttributeType>(service().getAllConceptAttributeTypes(), context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public ConceptAttributeType newDelegate() {
		return new ConceptAttributeType();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(Object)
	 */
	@Override
	public ConceptAttributeType save(ConceptAttributeType delegate) {
		return service().saveConceptAttributeType(delegate);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(Object,
	 *      RequestContext)
	 */
	@Override
	public void purge(ConceptAttributeType delegate, RequestContext context) throws ResponseException {
		service().purgeConceptAttributeType(delegate);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(RequestContext)
	 */
	@Override
	protected NeedsPaging<ConceptAttributeType> doSearch(RequestContext context) {
		return new NeedsPaging<ConceptAttributeType>(service().getConceptAttributeTypes(context.getParameter("q")), context);
	}

	@Override
	public String getResourceVersion() {
		return "2.0";
	}
}
