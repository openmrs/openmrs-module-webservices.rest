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

import java.util.ArrayList;
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
import org.openmrs.module.webservices.rest.web.resource.api.SearchResult;
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
	 * Implementations should override this method if they support sub-resources
	 * 
	 * @return a list of properties available as sub-resources, if any
	 */
	protected List<String> propertiesToExposeAsSubResources() {
		return null;
	}
	
	/**
	 * Assumes that the delegate property is the already-retrieved object with the given uuid
	 * 
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
		setConvertedProperties(delegate, propertiesToCreate);
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
		setConvertedProperties(delegate, propertiesToUpdate);
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
		SearchResult result = doSearch(query, context);
		return result.toSimpleObject();
	}
	
	/**
	 * Implementations should override this method if they are actually searchable.
	 */
	protected SearchResult doSearch(String query, RequestContext context) {
		return new EmptySearchResult();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Listable#getAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public List<Object> getAll(RequestContext context) throws ResponseException {
		List<Object> ret = new ArrayList<Object>();
		for (T match : doGetAll(context))
			ret.add(asRepresentation(match, context.getRepresentation()));
		return ret;
	}
	
	/**
	 * Implementations should override this method to return a list of all instances represented by
	 * the specified rest resource in the database
	 * 
	 * @throws ResponseException
	 */
	protected List<T> doGetAll(RequestContext context) throws ResponseException {
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
		List<String> legal = propertiesToExposeAsSubResources();
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
			return RestConstants.URI_PREFIX + RestConstants.VERSION_1 + "/" + res.value() + "/" + getUniqueId((T) delegate);
		}
		throw new RuntimeException(getClass() + " needs a @Resource or @SubResource annotation");
		
	}
	
}
