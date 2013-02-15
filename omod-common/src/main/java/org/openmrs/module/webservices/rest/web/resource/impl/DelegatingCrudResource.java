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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.CrudResource;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * A base implementation of a {@link CrudResource} that delegates CRUD operations to a wrapped
 * object
 * 
 * @param <T> the class we're delegating to
 */
public abstract class DelegatingCrudResource<T> extends BaseDelegatingResource<T> implements CrudResource, Searchable, Listable {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Retrievable#retrieve(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public Object retrieve(String uuid, RequestContext context) throws ResponseException {
		T delegate = getByUniqueId(uuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		
		SimpleObject ret = asRepresentation(delegate, context.getRepresentation());
		if (hasTypesDefined())
			ret.add(RestConstants.PROPERTY_FOR_TYPE, getTypeName(delegate));
		return ret;
	}
	
	/**
	 * Default implementation that returns REF, DEFAULT, and FULL
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Retrievable#getAvailableRepresentations()
	 */
	@Override
	public List<Representation> getAvailableRepresentations() {
		return Arrays.asList(Representation.DEFAULT, Representation.FULL, Representation.REF);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Creatable#create(org.springframework.web.context.request.WebRequest)
	 */
	@Override
	public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
		DelegatingResourceHandler<? extends T> handler;
		if (hasTypesDefined()) {
			String type = (String) propertiesToCreate.remove(RestConstants.PROPERTY_FOR_TYPE);
			if (type == null)
				throw new IllegalArgumentException(
				        "When creating a resource that supports subclasses, you must indicate the particular subclass with a "
				                + RestConstants.PROPERTY_FOR_TYPE + " property");
			handler = getResourceHandler(type);
		} else {
			handler = this;
		}
		
		T delegate = handler.newDelegate();
		setConvertedProperties(delegate, propertiesToCreate, handler.getCreatableProperties(), true);
		delegate = save(delegate);
		SimpleObject ret = (SimpleObject) ConversionUtil.convertToRepresentation(delegate, Representation.DEFAULT);
		
		// add the 'type' discriminator if we support subclasses
		if (hasTypesDefined()) {
			ret.add(RestConstants.PROPERTY_FOR_TYPE, getTypeName(delegate));
		}
		
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Updatable#update(java.lang.String,
	 *      org.openmrs.module.webservices.rest.SimpleObject)
	 */
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		T delegate = getByUniqueId(uuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		
		if (hasTypesDefined()) {
			// if they specify a type discriminator it must match the expected one--type can't me modified
			if (propertiesToUpdate.containsKey(RestConstants.PROPERTY_FOR_TYPE)) {
				String type = (String) propertiesToUpdate.remove(RestConstants.PROPERTY_FOR_TYPE);
				if (!delegate.getClass().equals(getActualSubclass(type))) {
					String nameToShow = getTypeName(delegate);
					if (nameToShow == null)
						nameToShow = delegate.getClass().getName();
					throw new IllegalArgumentException("You passed " + RestConstants.PROPERTY_FOR_TYPE + "=" + type
					        + " but this instance is a " + nameToShow);
				}
			}
		}
		
		DelegatingResourceHandler<? extends T> handler = getResourceHandler(delegate);
		
		setConvertedProperties(delegate, propertiesToUpdate, handler.getUpdatableProperties(), false);
		delegate = save(delegate);
		return delegate;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Deletable#delete(java.lang.String)
	 */
	@Override
	public void delete(String uuid, String reason, RequestContext context) throws ResponseException {
		T delegate = getByUniqueId(uuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		delete(delegate, reason, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Purgeable#purge(java.lang.String)
	 */
	@Override
	public void purge(String uuid, RequestContext context) throws ResponseException {
		T delegate = getByUniqueId(uuid);
		if (delegate == null) {
			// HTTP DELETE is idempotent, so if we can't find the object, we assume it's already deleted and return success
			return;
		}
		purge(delegate, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Searchable#search(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public SimpleObject search(String query, RequestContext context) throws ResponseException {
		PageableResult result = doSearch(query, context);
		return result.toSimpleObject();
	}
	
	/**
	 * Implementations should override this method if they are actually searchable.
	 */
	protected PageableResult doSearch(String query, RequestContext context) {
		return new EmptySearchResult();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Listable#getAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public SimpleObject getAll(RequestContext context) throws ResponseException {
		if (context.getType() != null) {
			if (!hasTypesDefined())
				throw new IllegalArgumentException(getClass() + " does not support "
				        + RestConstants.REQUEST_PROPERTY_FOR_TYPE);
			if (context.getType().equals(getResourceName()))
				throw new IllegalArgumentException("You may not specify " + RestConstants.REQUEST_PROPERTY_FOR_TYPE + "="
				        + context.getType() + " because it is the default behavior for this resource");
			DelegatingSubclassHandler<T, ? extends T> handler = getSubclassHandler(context.getType());
			if (handler == null)
				throw new IllegalArgumentException("No handler is specified for " + RestConstants.REQUEST_PROPERTY_FOR_TYPE
				        + "=" + context.getType());
			PageableResult result = handler.getAllByType(context);
			return result.toSimpleObject();
		} else {
			PageableResult result = doGetAll(context);
			return result.toSimpleObject();
		}
	}
	
	/**
	 * Implementations should override this method to return a list of all instances represented by
	 * the specified rest resource in the database.
	 * 
	 * (If the resource supports subclasses, this method should return all of its documents regardless of their
	 * type/subclass.)
	 * 
	 * @throws ResponseException
	 */
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * TODO
	 * 
	 * @param delegateUuid
	 * @param subResourceName
	 * @param rep
	 * @return
	 * @throws ResponseException
	 */
	public Object listSubResource(String delegateUuid, String subResourceName, Representation rep) throws ResponseException {
		// TODO SUBCLASSHANDLER
		List<String> legal = getPropertiesToExposeAsSubResources();
		if (legal == null || !legal.contains(subResourceName))
			throw new IllegalPropertyException();
		T delegate = getByUniqueId(delegateUuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		return ConversionUtil.getPropertyWithRepresentation(delegate, subResourceName, rep);
	}
	
	/**
	 * Resources provided by this module itself are published without any particular namespace (e.g. /ws/rest/v1/concept)
	 * but when modules publish resources, they should be namespaced (e.g. /ws/rest/v1/moduleId/moduleresource).
	 * 
	 * We recommend that module resources override this method and return their module id as a namespace. (Note that you
	 * also need to include that same namespace in your resource's controller.)
	 * 
	 * The default implementation just returns null.
	 * 
	 * @return a namespace that you want appended to the standard URI prefix before your resource's name
	 */
	protected String getNamespacePrefix() {
		return null;
	}
	
	/**
	 * @param delegate
	 * @return the URI for the given delegate object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getUri(Object delegate) {
		if (delegate == null)
			return "";
		
		Resource res = getClass().getAnnotation(Resource.class);
		if (res != null) {
			//TODO Deciding the version number from here is bad, it should be passed in to this method
			
			// if this resource defines a namespace, we need to append that
			String namespacePrefix = "";
			if (StringUtils.isNotBlank(getNamespacePrefix())) {
				namespacePrefix = getNamespacePrefix().concat("/");
			}
			// TODO: Better if the @Resource annotation had version property in it
			if (getClass().getName().contains("v2_0")) {
				return RestConstants.URI_PREFIX + RestConstants.VERSION_2 + "/" + namespacePrefix + res.name() + "/"
				        + getUniqueId((T) delegate);
			} else if (getClass().getName().contains("v1_0")) {
				return RestConstants.URI_PREFIX + RestConstants.VERSION_1 + "/" + namespacePrefix + res.name() + "/"
				        + getUniqueId((T) delegate);
			}
		}
		throw new RuntimeException(getClass() + " needs a @Resource or @SubResource annotation");
	}
	
}
