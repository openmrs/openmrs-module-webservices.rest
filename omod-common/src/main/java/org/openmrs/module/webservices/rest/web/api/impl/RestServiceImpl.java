/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.webservices.rest.web.OpenmrsClassScanner;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
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
	
	private volatile Map<String, Set<SearchHandler>> searchHandlersByResource;
	
	private volatile List<SearchHandler> allSearchHandlers;
	
	private RestHelperService restHelperService;
	
	private OpenmrsClassScanner openmrsClassScanner;
	
	public RestHelperService getRestHelperService() {
		return restHelperService;
	}
	
	public void setRestHelperService(RestHelperService restHelperService) {
		this.restHelperService = restHelperService;
	}
	
	public OpenmrsClassScanner getOpenmrsClassScanner() {
		return openmrsClassScanner;
	}
	
	public void setOpenmrsClassScanner(OpenmrsClassScanner openmrsClassScanner) {
		this.openmrsClassScanner = openmrsClassScanner;
	}
	
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
	
	private void initializeResources() {
		if (resourceDefinitionsByNames != null) {
			return;
		}
		
		Map<String, ResourceDefinition> tempResourceDefinitionsByNames = new HashMap<String, ResourceDefinition>();
		Map<Class<?>, Resource> tempResourcesBySupportedClasses = new HashMap<Class<?>, Resource>();
		
		List<Class<? extends Resource>> resources;
		try {
			resources = openmrsClassScanner.getClasses(Resource.class, true);
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
				
				if (!isOpenmrsVersionInVersions(subresourceAnnotation.supportedOpenmrsVersions())) {
					continue;
				}
				name = parentResourceAnnotation.name() + "/" + subresourceAnnotation.path();
				supportedClass = subresourceAnnotation.supportedClass();
				order = subresourceAnnotation.order();
			} else {
				if (!isOpenmrsVersionInVersions(resourceAnnotation.supportedOpenmrsVersions())) {
					continue;
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
	
	/**
	 * Checks if OpenMRS version is in given array of versions.
	 * 
	 * @param versions the array of versions to be checked for the openmrs version
	 * @return true if the openmrs version is in versions and false otherwise
	 */
	private boolean isOpenmrsVersionInVersions(String[] versions) {
		
		if (versions.length == 0) {
			return false;
		}
		
		boolean result = false;
		for (String version : versions) {
			if (ModuleUtil.matchRequiredVersions(OpenmrsConstants.OPENMRS_VERSION_SHORT, version)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	private void initializeSearchHandlers() {
		if (searchHandlersByIds != null) {
			return;
		}
		
		Map<SearchHandlerIdKey, SearchHandler> tempSearchHandlersByIds = new HashMap<RestServiceImpl.SearchHandlerIdKey, SearchHandler>();
		Map<SearchHandlerParameterKey, Set<SearchHandler>> tempSearchHandlersByParameters = new HashMap<SearchHandlerParameterKey, Set<SearchHandler>>();
		Map<String, Set<SearchHandler>> tempSearchHandlersByResource = new HashMap<String, Set<SearchHandler>>();
		
		List<SearchHandler> allSearchHandlers = restHelperService.getRegisteredSearchHandlers();
		for (SearchHandler searchHandler : allSearchHandlers) {
			addSearchHandler(tempSearchHandlersByIds, tempSearchHandlersByParameters, tempSearchHandlersByResource,
			    searchHandler);
		}
		this.allSearchHandlers = allSearchHandlers;
		searchHandlersByParameter = tempSearchHandlersByParameters;
		searchHandlersByIds = tempSearchHandlersByIds;
		searchHandlersByResource = tempSearchHandlersByResource;
	}
	
	private void addSearchHandler(Map<SearchHandlerIdKey, SearchHandler> tempSearchHandlersByIds,
	        Map<SearchHandlerParameterKey, Set<SearchHandler>> tempSearchHandlersByParameters,
	        Map<String, Set<SearchHandler>> tempSearchHandlersByResource, SearchHandler searchHandler) {
		for (String supportedVersion : searchHandler.getSearchConfig().getSupportedOpenmrsVersions()) {
			if (ModuleUtil.matchRequiredVersions(OpenmrsConstants.OPENMRS_VERSION_SHORT, supportedVersion)) {
				addSupportedSearchHandler(tempSearchHandlersByIds, tempSearchHandlersByParameters, searchHandler);
				addSearchHandlerToResourceMap(tempSearchHandlersByResource, searchHandler);
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
	
	private void addSearchHandlerToResourceMap(Map<String, Set<SearchHandler>> tempSearchHandlersByResource,
	        SearchHandler searchHandler) {
		SearchConfig config = searchHandler.getSearchConfig();
		Set<SearchHandler> handlers = tempSearchHandlersByResource.get(config.getSupportedResource());
		if (handlers == null) {
			handlers = new HashSet<SearchHandler>();
			tempSearchHandlersByResource.put(config.getSupportedResource(), handlers);
		}
		handlers.add(searchHandler);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getRepresentation(java.lang.String)
	 * @should return default representation if given null
	 * @should return default representation if given string is empty
	 * @should return reference representation if given string matches the ref representation
	 *         constant
	 * @should return default representation if given string matches the default representation
	 *         constant
	 * @should return full representation if given string matches the full representation constant
	 * @should return an instance of custom representation if given string starts with the custom
	 *         representation prefix
	 * @should return an instance of named representation for given string if it is not empty and
	 *         does not match any other case
	 */
	@Override
	public Representation getRepresentation(String requested) {
		if (StringUtils.isEmpty(requested)) {
			return Representation.DEFAULT;
		}
		
		if (RestConstants.REPRESENTATION_REF.equals(requested)) {
			return Representation.REF;
		} else if (RestConstants.REPRESENTATION_DEFAULT.equals(requested)) {
			return Representation.DEFAULT;
		} else if (RestConstants.REPRESENTATION_FULL.equals(requested)) {
			return Representation.FULL;
		} else if (requested.startsWith(RestConstants.REPRESENTATION_CUSTOM_PREFIX)) {
			return new CustomRepresentation(requested.replace(RestConstants.REPRESENTATION_CUSTOM_PREFIX, ""));
		}
		
		return new NamedRepresentation(requested);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getResourceByName(String)
	 * @should return resource for given name
	 * @should return resource for given name and ignore unannotated resources
	 * @should fail if failed to get resource classes
	 * @should fail if resource for given name cannot be found
	 * @should fail if resource for given name does not support the current openmrs version
	 * @should return subresource for given name
	 * @should fail if subresource for given name does not support the current openmrs version
	 * @should fail if two resources with same name and order are found for given name
	 * @should return resource with lower order value if two resources with the same name are found
	 *         for given name
	 */
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
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getResourceBySupportedClass(Class)
	 * @should return resource supporting given class and current openmrs version
	 * @should fail if no resource supporting given class and current openmrs version was found
	 * @should fail if no resource supporting given class was found
	 * @should return resource supporting superclass of given class if given class is a hibernate
	 *         proxy
	 * @should return resource supporting superclass of given class if no resource supporting given
	 *         class was found
	 * @should return resource supporting direct superclass of given class if no resource supporting
	 *         given class was found but multiple resources supporting multiple superclasses exist
	 * @should fail if failed to get resource classes
	 * @should fail if two resources with same name and order are found for given class
	 * @should return resource with lower order value if two resources with the same name are found
	 *         for given class
	 */
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
	 * Returns a search handler, which supports the given resource and the map of parameters and
	 * values.
	 * <p>
	 * A {@code SearchHandler} is selected according to following steps (in this order):
	 * <ul>
	 * <li>Lookup a {@code SearchHandler} based on its {@code id} ({@code SearchConfig#id}) if
	 * specified in given {@code parameters}. This lookup can fail if no or two
	 * {@code SearchHandler}'s is/are found for given {@code id} and {@code resourceName}.</li>
	 * <li>Lookup a {@code SearchHandler} based on given {@code parameters} if no {@code id} is
	 * specified. The lookup returns the {@code SearcHandler} supporting all requested
	 * {@code parameters} and with {@code parameters} satisfying the {@code SearchHandler}'s
	 * {@code SearchConfig}'s required parameters. This lookup can fail if more than 1
	 * {@code SearchHandler} satisfies the requirements mentioned before.</li>
	 * </ul>
	 * If no {@code SearchHandler} is found, {@code NULL} is returned.
	 * </p>
	 * 
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getSearchHandler(java.lang.String,
	 *      java.util.Map)
	 * @should return search handler matching id set in given parameters
	 * @should fail if parameters contain a search handler id which cannot be found
	 * @should fail if two search handlers for the same resource have the same id
	 * @should return null if parameters do not contain a search handler id and no other non special
	 *         request parameters
	 * @should return search handler providing all request parameters and parameters satisfying its
	 *         required parameters
	 * @should return null if given parameters are missing a parameter required by search handlers
	 *         eligible for given resource name and parameters
	 * @should fail if two search handlers match given resource and parameters and no search handler
	 *         id is specified
	 * @should return null if a non special request parameter in given parameters cannot be found in
	 *         any search handler
	 * @should return null if no search handler is found for given resource name
	 * @should return null if no search handler is found for current openmrs version
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
	 * Eliminate search handlers with at least one required parameter that is not provided in
	 * {@code searchParameters}.
	 * 
	 * @param candidateSearchHandlers the search handlers to filter for required parameters
	 * @param searchParameters the search parameters to be checked against search handlers required
	 *            parameters
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
	 * @should return list of delegating resource handlers including subclass handlers
	 * @should return list with delegating resource with lower order value if two resources with the
	 *         same name are found for given name
	 * @should fail if failed to get resource classes
	 * @should fail if two resources with same name and order are found for a class
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
		
		List<DelegatingSubclassHandler> subclassHandlers = restHelperService.getRegisteredRegisteredSubclassHandlers();
		for (DelegatingSubclassHandler subclassHandler : subclassHandlers) {
			resourceHandlers.add(subclassHandler);
		}
		
		return resourceHandlers;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getAllSearchHandlers()
	 * @should return all search handlers if search handlers have been initialized
	 * @should return null if search handlers have not been initialized
	 */
	public List<SearchHandler> getAllSearchHandlers() {
		
		return allSearchHandlers;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestService#getSearchHandlers(java.lang.String)
	 * @should return search handlers for given resource name
	 * @should return null if no search handler is found for given resource name
	 * @should return null if no search handler is found for current openmrs version
	 * @should return null given null
	 * @should fail if two search handlers for the same resource have the same id
	 */
	@Override
	public Set<SearchHandler> getSearchHandlers(String resourceName) {
		if (searchHandlersByResource == null) {
			initializeSearchHandlers();
		}
		return searchHandlersByResource.get(resourceName);
	}
	
	@Override
	public void initialize() {
		
		// first clear out any existing values
		resourceDefinitionsByNames = null;
		resourcesBySupportedClasses = null;
		searchHandlersByIds = null;
		searchHandlersByParameter = null;
		searchHandlersByResource = null;
		
		initializeResources();
		initializeSearchHandlers();
	}
}
