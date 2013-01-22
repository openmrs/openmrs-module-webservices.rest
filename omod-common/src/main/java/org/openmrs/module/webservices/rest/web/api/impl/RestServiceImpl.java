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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.webservices.rest.web.OpenmrsClassScanner;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.util.OpenmrsConstants;

/**
 * Default implementation of the {@link RestService}
 */
public class RestServiceImpl implements RestService {
	
	private final Map<String, ResourceDefinition> resourceDefinitionsByNames;
	
	private final Map<Class<?>, Resource> resourcesBySupportedClasses;
	
	public RestServiceImpl() throws IOException {
		resourceDefinitionsByNames = new HashMap<String, ResourceDefinition>();
		resourcesBySupportedClasses = new HashMap<Class<?>, Resource>();
		
		List<Class<? extends Resource>> resources = OpenmrsClassScanner.getInstance().getClasses(Resource.class, true);
		
		for (Class<? extends Resource> resource : resources) {
			org.openmrs.module.webservices.rest.web.annotation.Resource resourceAnnotation = resource
			        .getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class);
			
			String name = null;
			String[] supportedOpenmrsVersions = null;
			Class<?> supportedClass = null;
			int order = Integer.MAX_VALUE;
			
			if (resourceAnnotation == null) {
				SubResource subresourceAnnotation = resource.getAnnotation(SubResource.class);
				org.openmrs.module.webservices.rest.web.annotation.Resource parentResourceAnnotation = subresourceAnnotation
				        .parent().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class);
				
				supportedOpenmrsVersions = subresourceAnnotation.supportedOpenmrsVersions();
				if (supportedOpenmrsVersions.length != 0) {
					boolean supported = false;
					
					for (String supportedVersion : supportedOpenmrsVersions) {
						try {
							ModuleUtil.checkRequiredVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT, supportedVersion);
							supported = true;
							continue;
						}
						catch (Exception e) {}
					}
					
					if (!supported) {
						continue;
					}
				}
				name = parentResourceAnnotation.name() + "/" + subresourceAnnotation.path();
				supportedClass = subresourceAnnotation.supportedClass();
				order = subresourceAnnotation.order();
			} else {
				supportedOpenmrsVersions = resourceAnnotation.supportedOpenmrsVersions();
				if (supportedOpenmrsVersions.length != 0) {
					boolean supported = false;
					
					for (String supportedVersion : supportedOpenmrsVersions) {
						try {
							ModuleUtil.checkRequiredVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT, supportedVersion);
							supported = true;
							continue;
						}
						catch (Exception e) {}
					}
					
					if (!supported) {
						continue;
					}
				}
				name = resourceAnnotation.name();
				supportedClass = resourceAnnotation.supportedClass();
				order = resourceAnnotation.order();
			}
			
			ResourceDefinition existingResourceDef = resourceDefinitionsByNames.get(name);
			
			boolean addResource = true;
			
			if (existingResourceDef != null) {
				if (existingResourceDef.order == order) {
					throw new IllegalStateException("Two resources with the same name (" + name
					        + ") must not have the same order");
				} else if (existingResourceDef.order < order) {
					addResource = false;
				}
			}
			
			if (addResource) {
				Resource newResource = newResource(resource);
				
				resourceDefinitionsByNames.put(name, new ResourceDefinition(newResource, order));
				resourcesBySupportedClasses.put(supportedClass, newResource);
			}
			
		}
	}
	
	private class ResourceDefinition {
		
		public Resource resource;
		
		public int order;
		
		public ResourceDefinition(Resource resource, int order) {
			this.resource = resource;
			this.order = order;
		}
		
	}
	
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
	
	@Override
	public Resource getResourceByName(String name) throws APIException {
		ResourceDefinition resourceDefinition = resourceDefinitionsByNames.get(name);
		if (resourceDefinition == null) {
			throw new APIException("Unknown resource: " + name);
		} else {
			return resourceDefinition.resource;
		}
	}
	
	@Override
	public Resource getResourceBySupportedClass(Class<?> resourceClass) throws APIException {
		Resource resource = resourcesBySupportedClasses.get(resourceClass);
		if (resource == null) {
			for (Entry<Class<?>, Resource> resourceBySupportedClass : resourcesBySupportedClasses.entrySet()) {
				if (resourceBySupportedClass.getKey().isAssignableFrom(resourceClass)) {
					return resourceBySupportedClass.getValue();
				}
			}
			
			throw new APIException("Unknown resource: " + resourceClass);
		} else {
			return resource;
		}
	}
	
	/**
	 * @throws InstantiationException
	 */
	private Resource newResource(Class<? extends Resource> resourceClass) {
		try {
			Resource resource = resourceClass.newInstance();
			
			return resource;
		}
		catch (Exception ex) {
			throw new APIException("Failed to instantiate " + resourceClass, ex);
		}
	}
	
}
