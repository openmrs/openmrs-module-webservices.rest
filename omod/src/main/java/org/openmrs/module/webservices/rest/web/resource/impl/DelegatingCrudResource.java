package org.openmrs.module.webservices.rest.web.resource.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.CrudResource;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
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
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Creatable#create(org.springframework.web.context.request.WebRequest)
	 */
	@Override
	public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
		T delegate = newDelegate();
		ConversionUtil.setConvertedProperties(delegate, propertiesToCreate);
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
		ConversionUtil.setConvertedProperties(delegate, propertiesToUpdate);
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
	public List<Object> search(String query, RequestContext context) throws ResponseException {
		List<Object> ret = new ArrayList<Object>();
		for (T match : doSearch(query, context))
			ret.add(asRepresentation(match, context.getRepresentation()));
		return ret;
	}
	
	/**
	 * Implementations should override this method if they are actually searchable.
	 */
	protected List<T> doSearch(String query, RequestContext context) {
		return Collections.emptyList();
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
	 * @param bean
	 * @param property
	 * @return the given property on the given bean, if it exists and is accessible. returns null
	 *         otherwise.
	 */
	private Object getPropertyIfExists(Object bean, String property) {
		try {
			if (PropertyUtils.isReadable(bean, property))
				return PropertyUtils.getProperty(bean, property);
		}
		catch (Exception ex) {}
		return null;
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
	public String getUri(Object delegate) {
		SubResource sub = getClass().getAnnotation(SubResource.class);
		if (sub != null) {
			return getSubResourceUri(sub, (T) delegate);
		}
		Resource res = getClass().getAnnotation(Resource.class);
		if (res != null) {
			return getResourceUri(res, (T) delegate);
		}
		throw new RuntimeException(getClass() + " needs a @Resource or @SubResource annotation");
		
	}
	
	private String getResourceUri(Resource res, T delegate) {
		return "someprefix://" + res.value() + "/" + getUniqueId(delegate);
	}
	
	private String getSubResourceUri(SubResource sub, T delegate) {
		try {
			org.openmrs.module.webservices.rest.web.resource.api.Resource parentResource = Context.getService(
			    RestService.class).getResource(sub.parent());
			Object parentInstance = PropertyUtils.getProperty(delegate, sub.parentProperty());
			String parentUri = parentResource.getUri(parentInstance);
			return parentUri + "/" + sub.path() + "/" + getUniqueId(delegate);
		}
		catch (Exception ex) {
			throw new RuntimeException("Failed to get URI from sub-resource " + delegate + " with annotation " + sub);
		}
	}
		
}
