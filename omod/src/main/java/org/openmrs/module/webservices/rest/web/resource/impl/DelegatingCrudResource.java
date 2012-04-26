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
		
		return asRepresentation(delegate, context.getRepresentation());
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
		T delegate = newDelegate();
		setConvertedProperties(delegate, propertiesToCreate, getCreatableProperties(), true);
		delegate = save(delegate);
		return ConversionUtil.convertToRepresentation(delegate, Representation.DEFAULT);
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
		setConvertedProperties(delegate, propertiesToUpdate, getUpdatableProperties(), false);
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
		PageableResult result = doGetAll(context);
		return result.toSimpleObject();
	}
	
	/**
	 * Implementations should override this method to return a list of all instances represented by
	 * the specified rest resource in the database
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
		List<String> legal = getPropertiesToExposeAsSubResources();
		if (legal == null || !legal.contains(subResourceName))
			throw new IllegalPropertyException();
		T delegate = getByUniqueId(delegateUuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		return ConversionUtil.getPropertyWithRepresentation(delegate, subResourceName, rep);
	}
	
	/**
	 * Gets the URI fragment from the @RestResource annotation on the concrete subclass
	 * 
	 * @return
	 */
	protected String getUriFragment() {
		Resource ann = getClass().getAnnotation(Resource.class);
		if (ann == null)
			throw new RuntimeException("There is no " + Resource.class + " annotation on " + getClass());
		if (StringUtils.isEmpty(ann.value()))
			throw new RuntimeException(Resource.class.getSimpleName() + " annotation on " + getClass()
			        + " must specify a value");
		return ann.value();
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
			return RestConstants.URI_PREFIX + RestConstants.VERSION_1 + "/" + namespacePrefix + res.value() + "/"
			        + getUniqueId((T) delegate);
		}
		throw new RuntimeException(getClass() + " needs a @Resource or @SubResource annotation");
	}
	
}
