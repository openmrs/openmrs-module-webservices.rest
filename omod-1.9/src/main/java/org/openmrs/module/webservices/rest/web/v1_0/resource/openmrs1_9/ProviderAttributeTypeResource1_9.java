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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.ProviderAttributeType;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Allows standard CRUD for the {@link ProviderAttributeType} domain object
 */
@Resource(name = RestConstants.VERSION_1 + "/providerattributetype", supportedClass = ProviderAttributeType.class, supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class ProviderAttributeTypeResource1_9 extends BaseAttributeTypeCrudResource1_9<ProviderAttributeType> {
	
	public ProviderAttributeTypeResource1_9() {
	}
	
	private ProviderService service() {
		return Context.getProviderService();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public ProviderAttributeType getByUniqueId(String uniqueId) {
		return service().getProviderAttributeTypeByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<ProviderAttributeType> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<ProviderAttributeType>(service().getAllProviderAttributeTypes(), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public ProviderAttributeType newDelegate() {
		return new ProviderAttributeType();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public ProviderAttributeType save(ProviderAttributeType delegate) {
		return service().saveProviderAttributeType(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(ProviderAttributeType delegate, RequestContext context) throws ResponseException {
		service().purgeProviderAttributeType(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<ProviderAttributeType> doSearch(RequestContext context) {
		// TODO: Should be a ProviderAttributeType search method in ProviderService
		List<ProviderAttributeType> allAttrs = service().getAllProviderAttributeTypes();
		List<ProviderAttributeType> queryResult = new ArrayList<ProviderAttributeType>();
		for (ProviderAttributeType pAttr : allAttrs) {
			if (Pattern.compile(Pattern.quote(context.getParameter("q")), Pattern.CASE_INSENSITIVE).matcher(pAttr.getName())
			        .find()) {
				queryResult.add(pAttr);
			}
		}
		return new NeedsPaging<ProviderAttributeType>(queryResult, context);
	}
}
