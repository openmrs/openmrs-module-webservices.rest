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
package org.openmrs.module.webservices.rest.web.resource.impl;

import org.openmrs.OpenmrsData;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.util.ReflectionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Helpful base implementation of {@link DelegatingSubclassHandler}
 */
public abstract class BaseDelegatingSubclassHandler<Superclass, Subclass extends Superclass> implements DelegatingSubclassHandler<Superclass, Subclass> {
	
	/**
	 * Uses introspection into the generic interface to determine the superclass plugged into by
	 * this handler
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler#getSuperclass()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<Superclass> getSuperclass() {
		return ReflectionUtil.getParameterizedTypeFromInterface(getClass(), DelegatingSubclassHandler.class, 0);
	}
	
	/**
	 * Uses introspection into the generic interface to determine the subclass handled by this
	 * handler
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler#getSubclassHandled()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<Subclass> getSubclassHandled() {
		return ReflectionUtil.getParameterizedTypeFromInterface(getClass(), DelegatingSubclassHandler.class, 1);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		return getCreatableProperties();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Subclass save(Subclass delegate) {
		return (Subclass) getResource().save(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	public void purge(Subclass delegate, RequestContext context) throws ResponseException {
		getResource().purge(delegate, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants.PROPERTY_FOR_RESOURCE_VERSION_DEFAULT_VALUE;
	}
	
	/**
	 * @return the resource this handler works with
	 */
	@SuppressWarnings("unchecked")
	public DelegatingCrudResource<Superclass> getResource() {
		// get the service-managed singleton version of the resource
		Resource resource = Context.getService(RestService.class).getResourceBySupportedClass(getSuperclass());
		
		return (DelegatingCrudResource<Superclass>) resource;
	};
	
	/**
	 * Assumes we can get a "display" property
	 * 
	 * @param delegate
	 * @return standard REF representation of delegate
	 * @throws ConversionException
	 */
	@RepHandler(RefRepresentation.class)
	public SimpleObject convertToRef(Subclass delegate) throws ConversionException {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("display");
		if (delegate instanceof OpenmrsData) {
			if (((OpenmrsData) delegate).isVoided())
				rep.addProperty("voided");
		} else if (delegate instanceof OpenmrsMetadata) {
			if (((OpenmrsMetadata) delegate).isRetired())
				rep.addProperty("retired");
		}
		rep.addSelfLink();
		return getResource().convertDelegateToRepresentation(delegate, rep);
	}
}
