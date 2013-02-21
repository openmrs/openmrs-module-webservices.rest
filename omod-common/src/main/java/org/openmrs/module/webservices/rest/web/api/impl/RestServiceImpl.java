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
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleException;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.webservices.rest.web.OpenmrsClassScanner;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.util.OpenmrsConstants;

/**
 * Default implementation of the {@link RestService}
 */
public class RestServiceImpl implements RestService {
	
	private volatile Map<String, ResourceDefinition> resourceDefinitionsByNames;
	
	private volatile Map<Class<?>, Resource> resourcesBySupportedClasses;
	
	private volatile Map<SearchHandlerKey, SearchHandler> searchHandlers;
	
	public RestServiceImpl() {
	}
	
	private static class ResourceDefinition {
		
		public Resource resource;
		
		public int order;
		
		public ResourceDefinition(Resource resource, int order) {
			this.resource = resource;
			this.order = order;
		}
		
	}
	
	private static class SearchHandlerKey {
		
		public String supportedResource;
		
		public Set<String> searchParameters;
		
		public SearchHandlerKey(String supportedResource, Set<String> searchParameters) {
			this.supportedResource = supportedResource;
			this.searchParameters = searchParameters;
		}
		
		public SearchHandlerKey(SearchHandler searchHandler) {
			this.supportedResource = searchHandler.getSupportedResource();
			this.searchParameters = searchHandler.getSearchParameters();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((searchParameters == null) ? 0 : searchParameters.hashCode());
			result = prime * result + ((supportedResource == null) ? 0 : supportedResource.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SearchHandlerKey other = (SearchHandlerKey) obj;
			if (searchParameters == null) {
				if (other.searchParameters != null)
					return false;
			} else if (!searchParameters.equals(other.searchParameters))
				return false;
			if (supportedResource == null) {
				if (other.supportedResource != null)
					return false;
			} else if (!supportedResource.equals(other.supportedResource))
				return false;
			return true;
		}
		
	}
	
	private void initializeResources() {
		if (resourceDefinitionsByNames != null) {
			return;
		}
		
		Map<String, ResourceDefinition> tempResourceDefinitionsByNames = new HashMap<String, ResourceDefinition>();
		Map<Class<?>, Resource> tempResourcesBySupportedClasses = new HashMap<Class<?>, Resource>();
		
		List<Class<? extends Resource>> resources;
		try {
			resources = OpenmrsClassScanner.getInstance().getClasses(Resource.class, true);
		}
		catch (IOException e) {
			throw new APIException("Cannot access REST resources", e);
		}
		
		for (Class<? extends Resource> resource : resources) {
			org.openmrs.module.webservices.rest.web.annotation.Resource resourceAnnotation = null;
			try {
				resourceAnnotation = resource
				        .getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class);
			}
			catch (Exception e) {
				//Missing class
				continue;
			}
			
			String name = null;
			String[] supportedOpenmrsVersions = null;
			Class<?> supportedClass = null;
			int order = Integer.MAX_VALUE;
			
			if (resourceAnnotation == null) {
				SubResource subresourceAnnotation = null;
				org.openmrs.module.webservices.rest.web.annotation.Resource parentResourceAnnotation = null;
				try {
					subresourceAnnotation = resource.getAnnotation(SubResource.class);
					parentResourceAnnotation = subresourceAnnotation.parent().getAnnotation(
					    org.openmrs.module.webservices.rest.web.annotation.Resource.class);
				}
				catch (Exception e) {
					//Missing class
					continue;
				}
				
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
			
			ResourceDefinition existingResourceDef = tempResourceDefinitionsByNames.get(name);
			
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
				
				tempResourceDefinitionsByNames.put(name, new ResourceDefinition(newResource, order));
				tempResourcesBySupportedClasses.put(supportedClass, newResource);
			}
			
		}
		
		Map<SearchHandlerKey, SearchHandler> tempSearchHandlers = new HashMap<SearchHandlerKey, SearchHandler>();
		
		List<SearchHandler> allSearchHandlers = Context.getRegisteredComponents(SearchHandler.class);
		for (SearchHandler searchHandler : allSearchHandlers) {
			for (String supportedVersion : searchHandler.getSupportedOpenmrsVersions()) {
				try {
					ModuleUtil.checkRequiredVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT, supportedVersion);
					//if supported then
					SearchHandlerKey searchHandlerKey = new SearchHandlerKey(searchHandler);
					SearchHandler previousSearchHandler = tempSearchHandlers.put(searchHandlerKey, searchHandler);
					if (previousSearchHandler != null) {
						if (previousSearchHandler.getOrder() < searchHandler.getOrder()) {
							tempSearchHandlers.put(searchHandlerKey, previousSearchHandler);
						} else if (previousSearchHandler.getOrder() == searchHandler.getOrder()) {
							throw new IllegalStateException("Two search handlers (" + searchHandler.getClass() + ", "
							        + previousSearchHandler.getClass() + ") for the same resource ("
							        + searchHandler.getSupportedResource() + ") and the same search parameters ("
							        + searchHandler.getSearchParameters() + ") must not have the same order ("
							        + searchHandler.getOrder() + ")");
						}
					}
				}
				catch (ModuleException e) {
					//Not supported OpenMRS version
				}
			}
		}
		
		searchHandlers = tempSearchHandlers;
		resourcesBySupportedClasses = tempResourcesBySupportedClasses;
		resourceDefinitionsByNames = tempResourceDefinitionsByNames;
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
		initializeResources();
		
		ResourceDefinition resourceDefinition = resourceDefinitionsByNames.get(name);
		if (resourceDefinition == null) {
			throw new APIException("Unknown resource: " + name);
		} else {
			return resourceDefinition.resource;
		}
	}
	
	@Override
	public Resource getResourceBySupportedClass(Class<?> resourceClass) throws APIException {
		initializeResources();
		
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
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getSearchHandler(java.lang.String, java.util.Set)
	 */
	@Override
	public SearchHandler getSearchHandler(String resourceName, Set<String> searchParameters) throws APIException {
		return searchHandlers.get(new SearchHandlerKey(resourceName, searchParameters));
	}
	
}
