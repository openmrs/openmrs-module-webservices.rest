package org.openmrs.module.webservices.rest.resource;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.RepHandler;
import org.openmrs.module.webservices.rest.annotation.Resource;
import org.openmrs.module.webservices.rest.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.representation.Representation;
import org.openmrs.util.HandlerUtil;

/**
 * A base implementation of a {@link CrudResource} that delegates CRUD operations to a wrapped object
 * @param <T> the class we're delegating to
 */
public abstract class DelegatingCrudResource<T> implements CrudResource, DelegateConverter<T> {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	protected T delegate;
	
	protected DelegatingCrudResource() {
		this(null);
	}
	
	/**
	 * Subclasses should call this super constructor, passing the delegate to use for properties
	 * @param delegate
	 */
	protected DelegatingCrudResource(T delegate) {
		this.delegate = delegate;
	}
	
	/**
     * @return the delegate
     */
    public T getDelegate() {
    	return delegate;
    }
	
    /**
     * @param delegate the delegate to set
     */
    @Override
    public void setDelegate(T delegate) {
    	this.delegate = delegate;
    }

    /**
	 * Assumes that the delegate property is the already-retrieved object with the given uuid
	 * @see org.openmrs.module.webservices.rest.resource.Retrievable#retrieve(java.lang.String, org.openmrs.module.webservices.rest.representation.Representation)
	 */
	@Override
	public Object retrieve(String uuid, RequestContext context) {
		return retrieve(context);
	}
	
	/**
	 * Convenience method for handling a {@link #retrieve(String, Representation)} action, since this class
	 * is already configured with a delegate.
	 * @param representation
	 * @return
	 */
	public Object retrieve(RequestContext context) {
		try {
			return asRepresentation(context.getRepresentation());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.Creatable#create(org.springframework.web.context.request.WebRequest)
	 */
	@Override
	public DelegatingCrudResource<T> create(SimpleObject post, RequestContext context) throws ResourceCreationException {
		try {
			delegate = newDelegate();
			setPropertiesOnDelegate(post);
			delegate = saveDelegate();
			return this;
		} catch (Exception ex) {
			throw new ResourceCreationException(ex);
		}
	}
	
	/**
	 * @throws ResourceUpdateException 
	 * @see org.openmrs.module.webservices.rest.resource.Updatable#update(java.lang.String, org.openmrs.module.webservices.rest.SimpleObject)
	 */
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResourceUpdateException {
	    // TODO check that delegate has the correct uuid, and we aren't about to update the wrong thing
		return update(propertiesToUpdate, context);
	}
	
	/**
	 * Convenience version of {@link #update(String, SimpleObject)}, since this resource already has a
	 * delegate set
	 * @param propertiesToUpdate
	 * @param context 
	 * @return
	 * @throws ResourceUpdateException
	 */
	public Object update(SimpleObject propertiesToUpdate, RequestContext context) throws ResourceUpdateException {
		try {
			setPropertiesOnDelegate(propertiesToUpdate);
			delegate = saveDelegate();
			return delegate;
		} catch (Exception ex) {
			throw new ResourceUpdateException(ex);
		}
	}

	/**
	 * @see org.openmrs.module.webservices.rest.resource.Deletable#delete(java.lang.String)
	 */
	@Override
	public void delete(String uuid, String reason, RequestContext context) throws ResourceDeletionException {
		// TODO check that delegate has the correct uuid, and we aren't about to delete the wrong thing
	    delete(reason, context);
	}
	
	/**
	 * Convenience version of {@link #delete(String,String)}, since this resource already has a delegate set
	 * @throws ResourceDeletionException 
	 * @see Deletable#delete(String,String)
	 */
	public abstract void delete(String reason, RequestContext context) throws ResourceDeletionException;
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.Purgeable#purge(java.lang.String)
	 */
	@Override
	public void purge(String uuid, RequestContext context) throws ResourceDeletionException {
		// TODO check that delegate has the correct uuid, and we aren't about to purge the wrong thing
		purge(context);
	}
	
    /**
     * Convenience version of {@link #purge(String)}, since this resource already has a delegate set
     * @param context 
     * @throws ResourceDeletionException 
     * @see Purgeable#purge(String)
     */
    public abstract void purge(RequestContext context) throws ResourceDeletionException;
    
	/**
     * Writes the delegate to the database
     * @return the saved instance
     */
    protected abstract T saveDelegate();

	/**
     * @return a new instance of the delegate class
     */
    protected abstract T newDelegate();

	/**
     * Takes all properties on propertyMap, sets them on delegate (performing type conversion automatically) 
     * @param propertyMap
     */
    protected void setPropertiesOnDelegate(SimpleObject propertyMap) throws Exception {
    	for (Map.Entry<String, Object> prop : propertyMap.entrySet()) {
    		setPropertyOnDelegate(prop.getKey(), prop.getValue());
    	}
    }

	/**
	 * Sets the given property on delegate to the given value
	 * @param property
	 * @param value
	 * @throws Exception
	 */
	protected void setPropertyOnDelegate(String property, Object value) throws Exception {
	    log.info("applying " + property + " which is a " + value.getClass() + " = " + value);
	    PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(delegate, property);
	    log.info("property exists and is a: " + pd.getPropertyType());
	    if (value == null || pd.getPropertyType().isAssignableFrom(value.getClass())) {
	    	log.info("compatible type, so setting directly");
	    	pd.getWriteMethod().invoke(delegate, value);
	    } else {
	    	log.info("need to convert " + value.getClass() + " to " + pd.getPropertyType());
	    	Object converted = convert(value, pd.getPropertyType());
	    	pd.getWriteMethod().invoke(delegate, converted);
	    }
    }

	/**
	 * TODO move to utility class
	 * Converts the given object to the given type
	 * @param object
	 * @param toType
	 * @return
	 * @throws Exception
	 */
	protected Object convert(Object object, Class<?> toType) throws Exception {
		if (object instanceof String) {
			String string = (String) object;
			DelegateConverter<?> converter = null;
			try {
				converter = HandlerUtil.getPreferredHandler(DelegateConverter.class, toType);
				return converter.fromString(string);
			} catch (APIException ex) {
				// it's okay if there's no registered handler
			}

			if (toType.isAssignableFrom(Date.class)) {
				try {
					return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(string);
				} catch (ParseException ex) {
					try {
						return new SimpleDateFormat("yyyy-MM-dd").parse(string);
					} catch (ParseException ex2) {
						throw ex;
					}
				}
			}
		}
	    throw new RuntimeException("Don't know how to convert from " + object.getClass() + " to " + toType);
    }

	/**
     * Creates an object of the given representation, pulling fields and methods from the delegate object
     * and class methods as specified by the {@link IncludeProperties} annotation 
     * @param representation
     * @return
     */
	@Override
    public Object asRepresentation(Representation representation) throws Exception {
    	if (delegate == null)
   			throw new NullPointerException();

    	Method meth = findAnnotatedMethodForRepresentation(representation);
    	if (meth == null)
    		throw new IllegalArgumentException("Don't know how to get " + getClass().getSimpleName() + " as " + representation);
    	if (meth.getParameterTypes().length == 0)
			return meth.invoke(this);
		else
			return meth.invoke(this, representation);
    }

	protected SimpleObject convertDelegateToRepresentation(DelegatingResourceRepresentation rep) throws Exception {
    	if (delegate == null)
   			throw new NullPointerException();
    	SimpleObject ret = new SimpleObject();
    	for (Map.Entry<String, Representation> e : rep.getProperties().entrySet())
    		ret.put(e.getKey(), getPropertyWithRepresentation(e.getKey(), e.getValue()));
    	return ret;
    }
	
	/**
	 * Finds a method in this class or a superclass annotated with a {@link RepHandler} for the given
	 * representation
	 * @param clazz
	 * @return
	 */
	private Method findAnnotatedMethodForRepresentation(Representation rep) throws Exception {
		// TODO make sure if there are multiple annotated methods we take the one on the subclass
	    for (Method method : getClass().getMethods()) {
	    	RepHandler ann = method.getAnnotation(RepHandler.class);
	    	if (ann != null) {
	    		if (ann.value().isAssignableFrom(rep.getClass())) {
		    		if (rep instanceof NamedRepresentation && !((NamedRepresentation) rep).matchesAnnotation(ann))
		    			continue;
		    		return method;
	    		}
	    	}
	    }
	    return null;
    }

	/**
	 * Gets a property from the delegate, with the given representation
	 * @param propertyName
	 * @param rep
	 * @return
	 * @throws Exception 
	 */
	public Object getPropertyWithRepresentation(String propertyName, Representation rep) throws Exception {
		Object o = PropertyUtils.getProperty(delegate, propertyName);
		if (o instanceof Collection) {
			List<Object> ret = new ArrayList<Object>();
			for (Object element : (Collection<?>) o)
				ret.add(convertObjectToRep(element, rep));
			return ret;
		} else {
			o = convertObjectToRep(o, rep);
			return o;
		}
    }
		
	@SuppressWarnings("unchecked")
    private <S> Object convertObjectToRep(S o, Representation rep) throws Exception {
		if (o == null)
			return null;
		DelegateConverter<S> converter = null;
		try {
			converter = HandlerUtil.getPreferredHandler(DelegateConverter.class, o.getClass());
		} catch (APIException ex) {
			return o;
		}
		converter = converter.getClass().newInstance();
		converter.setDelegate(o);
		return converter.asRepresentation(rep);
	}

    /**
     * Gets the URI fragment from the @RestResource annotation on the concrete subclass
     * @return
     */
    protected String getUriFragment() {
    	Resource ann = getClass().getAnnotation(Resource.class);
    	if (ann == null)
    		throw new RuntimeException("There is no " + Resource.class + " annotation on " + getClass());
    	if (StringUtils.isEmpty(ann.value()))
    		throw new RuntimeException(Resource.class.getSimpleName() + " annotation on " + getClass() + " must specify a value");
    	return ann.value();
    }
}
