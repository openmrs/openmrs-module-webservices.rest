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
package org.openmrs.module.webservices.rest.web.api.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;

/**
 * Default implementation of the {@link RestService}
 */
public class RestServiceImpl implements RestService {
	
	private Map<Class<? extends Resource>, Resource> resourceSingletons = new HashMap<Class<? extends Resource>, Resource>();
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getRepresentation(java.lang.String)
	 */
	@Override
	public Representation getRepresentation(String requested) {
		if (StringUtils.isEmpty(requested)) {
			return Representation.DEFAULT;
		} else {
			if (RestConstants.REPRESENTATION_REF.equals(requested)) {
				return Representation.REF;
			} else if (RestConstants.REPRESENTATION_DEFAULT.equals(requested)) {
				return Representation.DEFAULT;
			} else if (RestConstants.REPRESENTATION_FULL.equals(requested)) {
				return Representation.FULL;
			} else if (requested.startsWith(RestConstants.REPRESENTATION_CUSTOM_PREFIX)) {
				return new CustomRepresentation(requested.replace(RestConstants.REPRESENTATION_CUSTOM_PREFIX, ""));
			}
		}
		return new NamedRepresentation(requested);
	}
	
	/**
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getResource(java.lang.Class)
	 */
	@Override
	public <R extends Resource> R getResource(Class<R> resourceClass) {
		Resource resource = resourceSingletons.get(resourceClass);
		if (resource == null) {
			try {
				resource = resourceClass.newInstance();
				// if the resource has an init() method, we invoke it
				try {
					Method method = resource.getClass().getMethod("init");
					method.invoke(resource);
				}
				catch (Exception ex) {}
			}
			catch (Exception ex) {
				throw new APIException("Failed to instantiate " + resourceClass, ex);
			}
			resourceSingletons.put(resourceClass, resource);
		}
		return (R) resource;
	}
	
}
