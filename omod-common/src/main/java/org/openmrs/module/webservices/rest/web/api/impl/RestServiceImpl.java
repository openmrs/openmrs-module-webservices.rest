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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.proxy.HibernateProxy;
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
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.util.OpenmrsConstants;

/**
 * Default implementation of the {@link RestService}
 */
public class RestServiceImpl implements RestService {
	
	volatile Map<String, ResourceDefinition> resourceDefinitionsByNames;
	
	volatile Map<Class<?>, Resource> resourcesBySupportedClasses;
	
	private volatile Map<SearchHandlerParameterKey, Set<SearchHandler>> searchHandlersByParameter;
	
	private volatile Map<SearchHandlerIdKey, SearchHandler> searchHandlersByIds;
	
	private volatile List<SearchHandler> allSearchHandlers;
	
	public RestServiceImpl() {
	}
	
	static class ResourceDefinition {
		
		public Resource resource;
		
		public int order;
		
		public ResourceDefinition(Resource resource, int order) {
			this.resource = resource;
			this.order = order;
		}
		
	}
	
	private static class SearchHandlerParameterKey {
		
		public String supportedResource;
		
		public String parameter;
		
		public SearchHandlerParameterKey(String supportedResource, String parameter) {
			this.supportedResource = supportedResource;
			this.parameter = parameter;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
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
			SearchHandlerParameterKey other = (SearchHandlerParameterKey) obj;
			if (parameter == null) {
				if (other.parameter != null)
					return false;
			} else if (!parameter.equals(other.parameter))
				return false;
			if (supportedResource == null) {
				if (other.supportedResource != null)
					return false;
			} else if (!supportedResource.equals(other.supportedResource))
				return false;
			return true;
		}
		
	}
	
	private static class SearchHandlerIdKey {
		
		public String supportedResource;
		
		public String id;
		
		public SearchHandlerIdKey(String supportedResource, String id) {
			this.supportedResource = supportedResource;
			this.id = id;
		}
		
		public SearchHandlerIdKey(SearchHandler searchHandler) {
			this.supportedResource = searchHandler.getSearchConfig().getSupportedResource();
			this.id = searchHandler.getSearchConfig().getId();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
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
			SearchHandlerIdKey other = (SearchHandlerIdKey) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (supportedResource == null) {
				if (other.supportedResource != null)
					return false;
			} else if (!supportedResource.equals(other.supportedResource))
				return false;
			return true;
		}
		
	}
	
	/**
	 * It should be used in TESTS ONLY.
	 * 
	 * @param searchHandler
	 */
	void addSupportedSearchHandler(SearchHandler searchHandler) {
		if (searchHandlersByIds == null) {
			searchHandlersByIds = new HashMap<RestServiceImpl.SearchHandlerIdKey, SearchHandler>();
		}
		if (searchHandlersByParameter == null) {
			searchHandlersByParameter = new HashMap<SearchHandlerParameterKey, Set<SearchHandler>>();
		}
		
		addSupportedSearchHandler(searchHandlersByIds, searchHandlersByParameter, searchHandler);
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
		
		resourcesBySupportedClasses = tempResourcesBySupportedClasses;
		resourceDefinitionsByNames = tempResourceDefinitionsByNames;
	}
	
	private void initializeSearchHandlers() {
		if (searchHandlersByIds != null) {
			return;
		}
		
		Map<SearchHandlerIdKey, SearchHandler> tempSearchHandlersByIds = new HashMap<RestServiceImpl.SearchHandlerIdKey, SearchHandler>();
		Map<SearchHandlerParameterKey, Set<SearchHandler>> tempSearchHandlersByParameters = new HashMap<SearchHandlerParameterKey, Set<SearchHandler>>();
		
		List<SearchHandler> allSearchHandlers = Context.getRegisteredComponents(SearchHandler.class);
		for (SearchHandler searchHandler : allSearchHandlers) {
			addSearchHandler(tempSearchHandlersByIds, tempSearchHandlersByParameters, searchHandler);
		}
		this.allSearchHandlers = allSearchHandlers;
		searchHandlersByParameter = tempSearchHandlersByParameters;
		searchHandlersByIds = tempSearchHandlersByIds;
	}
	
	private void addSearchHandler(Map<SearchHandlerIdKey, SearchHandler> tempSearchHandlersByIds,
	        Map<SearchHandlerParameterKey, Set<SearchHandler>> tempSearchHandlersByParameters, SearchHandler searchHandler) {
		for (String supportedVersion : searchHandler.getSearchConfig().getSupportedOpenmrsVersions()) {
			try {
				ModuleUtil.checkRequiredVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT, supportedVersion);
				//If the OpenMRS version is supported then
				addSupportedSearchHandler(tempSearchHandlersByIds, tempSearchHandlersByParameters, searchHandler);
			}
			catch (ModuleException e) {
				//Not supported OpenMRS version
			}
		}
	}
	
	private void addSupportedSearchHandler(Map<SearchHandlerIdKey, SearchHandler> tempSearchHandlersByIds,
	        Map<SearchHandlerParameterKey, Set<SearchHandler>> tempSearchHandlersByParameters, SearchHandler searchHandler) {
		SearchHandlerIdKey searchHanlderIdKey = new SearchHandlerIdKey(searchHandler);
		SearchHandler previousSearchHandler = tempSearchHandlersByIds.put(searchHanlderIdKey, searchHandler);
		if (previousSearchHandler != null) {
			SearchConfig config = searchHandler.getSearchConfig();
			throw new IllegalStateException("Two search handlers (" + searchHandler.getClass() + ", "
			        + previousSearchHandler.getClass() + ") for the same resource (" + config.getSupportedResource()
			        + ") must not have the same ID (" + config.getId() + ")");
		}
		
		addSearchHandlerToParametersMap(tempSearchHandlersByParameters, searchHandler);
	}
	
	private void addSearchHandlerToParametersMap(
	        Map<SearchHandlerParameterKey, Set<SearchHandler>> tempSearchHandlersByParameters, SearchHandler searchHandler) {
		for (SearchQuery searchQueries : searchHandler.getSearchConfig().getSearchQueries()) {
			Set<String> parameters = new HashSet<String>(searchQueries.getRequiredParameters());
			parameters.addAll(searchQueries.getOptionalParameters());
			
			for (String parameter : parameters) {
				SearchHandlerParameterKey parameterKey = new SearchHandlerParameterKey(searchHandler.getSearchConfig()
				        .getSupportedResource(), parameter);
				Set<SearchHandler> list = tempSearchHandlersByParameters.get(parameterKey);
				if (list == null) {
					list = new HashSet<SearchHandler>();
					tempSearchHandlersByParameters.put(parameterKey, list);
				}
				list.add(searchHandler);
			}
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
		
		if (HibernateProxy.class.isAssignableFrom(resourceClass)) {
			resourceClass = resourceClass.getSuperclass();
		}
		
		Resource resource = resourcesBySupportedClasses.get(resourceClass);
		
		if (resource == null) {
			Entry<Class<?>, Resource> bestResourceEntry = null;
			
			for (Entry<Class<?>, Resource> resourceEntry : resourcesBySupportedClasses.entrySet()) {
				if (resourceEntry.getKey().isAssignableFrom(resourceClass)) {
					if (bestResourceEntry == null || bestResourceEntry.getKey().isAssignableFrom(resourceEntry.getKey())) {
						bestResourceEntry = resourceEntry;
					}
				}
			}
			
			if (bestResourceEntry != null) {
				resource = bestResourceEntry.getValue();
			}
		}
		
		if (resource == null) {
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
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getSearchHandler(java.lang.String,
	 *      java.util.Map)
	 * @should throw exception if no handler with id
	 * @should return handler by id if exists
	 * @should throw ambiguous exception if case 1
	 * @should return handler if case 2
	 */
	@Override
	public SearchHandler getSearchHandler(String resourceName, Map<String, String[]> parameters) throws APIException {
		initializeSearchHandlers();
		
		String[] searchIds = parameters.get(RestConstants.REQUEST_PROPERTY_FOR_SEARCH_ID);
		if (searchIds != null && searchIds.length > 0) {
			SearchHandler searchHandler = searchHandlersByIds.get(new SearchHandlerIdKey(resourceName, searchIds[0]));
			if (searchHandler == null) {
				throw new InvalidSearchException("The search with id '" + searchIds[0] + "' for '" + resourceName
				        + "' resource is not recognized");
			} else {
				return searchHandler;
			}
		}
		
		Set<String> searchParameters = new HashSet<String>(parameters.keySet());
		searchParameters.removeAll(RestConstants.SPECIAL_REQUEST_PARAMETERS);
		
		Set<SearchHandler> candidateSearchHandlers = null;
		for (String searchParameter : searchParameters) {
			Set<SearchHandler> searchHandlers = searchHandlersByParameter.get(new SearchHandlerParameterKey(resourceName,
			        searchParameter));
			if (searchHandlers == null) {
				return null; //Missing parameter so there's no handler.
			} else if (candidateSearchHandlers == null) {
				candidateSearchHandlers = new HashSet<SearchHandler>();
				candidateSearchHandlers.addAll(searchHandlers);
			} else {
				//Eliminate candidate search handlers that do not include all parameters
				candidateSearchHandlers.retainAll(searchHandlers);
			}
		}
		
		if (candidateSearchHandlers == null) {
			return null;
		} else {
			eliminateCandidateSearchHandlersWithMissingRequiredParameters(candidateSearchHandlers, searchParameters);
			
			if (candidateSearchHandlers.isEmpty()) {
				return null;
			} else if (candidateSearchHandlers.size() == 1) {
				return candidateSearchHandlers.iterator().next();
			} else {
				List<String> candidateSearchHandlerIds = new ArrayList<String>();
				for (SearchHandler candidateSearchHandler : candidateSearchHandlers) {
					candidateSearchHandlerIds.add(RestConstants.REQUEST_PROPERTY_FOR_SEARCH_ID + "="
					        + candidateSearchHandler.getSearchConfig().getId());
				}
				throw new InvalidSearchException("The search is ambiguous. Please specify "
				        + StringUtils.join(candidateSearchHandlerIds, " or "));
			}
		}
	}
	
	/**
	 * Eliminate search handlers with missing required parameters.
	 * 
	 * @param candidateSearchHandlers
	 * @param searchParameters
	 */
	private void eliminateCandidateSearchHandlersWithMissingRequiredParameters(Set<SearchHandler> candidateSearchHandlers,
	        Set<String> searchParameters) {
		Iterator<SearchHandler> it = candidateSearchHandlers.iterator();
		while (it.hasNext()) {
			SearchHandler candidateSearchHandler = it.next();
			boolean remove = true;
			
			for (SearchQuery candidateSearchQueries : candidateSearchHandler.getSearchConfig().getSearchQueries()) {
				Set<String> requiredParameters = new HashSet<String>(candidateSearchQueries.getRequiredParameters());
				requiredParameters.removeAll(searchParameters);
				if (requiredParameters.isEmpty()) {
					remove = false;
					break;
				}
			}
			
			if (remove) {
				it.remove();
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getResourceHandlers()
	 */
	@Override
	public List<DelegatingResourceHandler<?>> getResourceHandlers() throws APIException {
		initializeResources();
		
		List<DelegatingResourceHandler<?>> resourceHandlers = new ArrayList<DelegatingResourceHandler<?>>();
		
		for (Resource resource : resourcesBySupportedClasses.values()) {
			if (resource instanceof DelegatingResourceHandler) {
				resourceHandlers.add((DelegatingResourceHandler<?>) resource);
			}
		}
		
		@SuppressWarnings("rawtypes")
		List<DelegatingSubclassHandler> subclassHandlers = Context.getRegisteredComponents(DelegatingSubclassHandler.class);
		
		for (@SuppressWarnings("rawtypes")
		DelegatingSubclassHandler subclassHandler : subclassHandlers) {
			resourceHandlers.add(subclassHandler);
		}
		
		return resourceHandlers;
	}
	
	public List<SearchHandler> getAllSearchHandlers() {
		
		return allSearchHandlers;
	}
	
	@Override
	public void initialize() {
		
		// first clear out any existing values
		resourceDefinitionsByNames = null;
		resourcesBySupportedClasses = null;
		searchHandlersByIds = null;
		searchHandlersByParameter = null;
		
		initializeResources();
		initializeSearchHandlers();
	}
	
}
