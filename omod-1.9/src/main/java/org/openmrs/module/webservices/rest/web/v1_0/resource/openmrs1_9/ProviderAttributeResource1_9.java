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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.Provider;
import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

/**
 * {@link Resource} for ProviderAttributes, supporting standard CRUD operations
 */
@SubResource(parent = ProviderResource1_9.class, path = "attribute", order = 100, supportedClass = ProviderAttribute.class, supportedOpenmrsVersions = {
		"1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class ProviderAttributeResource1_9
		extends
		BaseAttributeCrudResource1_9<ProviderAttribute, Provider, ProviderResource1_9> {

	/**
	 * Sets attributes on the given provider.
	 * 
	 * @param instance
	 * @param attr
	 */
	@PropertySetter("attributeType")
	public static void setAttributeType(ProviderAttribute instance,
			ProviderAttributeType attr) {
		instance.setAttributeType(attr);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Provider getParent(ProviderAttribute instance) {
		return instance.getProvider();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public ProviderAttribute newDelegate() {
		return new ProviderAttribute();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(ProviderAttribute instance, Provider provider) {
		instance.setProvider(provider);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public ProviderAttribute getByUniqueId(String uniqueId) {
		return Context.getProviderService()
				.getProviderAttributeByUuid(uniqueId);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<ProviderAttribute> doGetAll(Provider parent,
			RequestContext context) throws ResponseException {
		return new NeedsPaging<ProviderAttribute>(
				(List<ProviderAttribute>) parent.getActiveAttributes(), context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public ProviderAttribute save(ProviderAttribute delegate) {
		// make sure it has not already been added to the provider
		boolean needToAdd = true;
		for (ProviderAttribute pa : delegate.getProvider()
				.getActiveAttributes()) {
			if (pa.equals(delegate)) {
				needToAdd = false;
				break;
			}
		}
		if (needToAdd) {
			delegate.getProvider().addAttribute(delegate);
		}
		Context.getProviderService().saveProvider(delegate.getProvider());
		return delegate;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
	 *      java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(ProviderAttribute delegate, String reason,
			RequestContext context) throws ResponseException {
		delegate.setVoided(true);
		delegate.setVoidReason(reason);
		Context.getProviderService().saveProvider(delegate.getProvider());
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(ProviderAttribute delegate, RequestContext context)
			throws ResponseException {
		throw new UnsupportedOperationException(
				"Cannot purge ProviderAttribute");
	}
}
