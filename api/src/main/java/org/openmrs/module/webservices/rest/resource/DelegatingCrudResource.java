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
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ObjectMismatchException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
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
	public Object retrieve(String uuid, RequestContext context) throws ResponseException {
		return retrieve(context);
	}
	
	/**
	 * Convenience method for handling a {@link #retrieve(String, Representation)} action, since this class
	 * is already configured with a delegate.
	 * @param representation
	 * @return
	 */
	public Object retrieve(RequestContext context) throws ResponseException {
		if (delegate == null)
			throw new ObjectNotFoundException();
		return asRepresentation(context.getRepresentation());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.Creatable#create(org.springframework.web.context.request.WebRequest)
	 */
	@Override
	public DelegatingCrudResource<T> create(SimpleObject post, RequestContext context) throws ResponseException {
		delegate = newDelegate();
		setPropertiesOnDelegate(post);
		delegate = saveDelegate();
		return this;
	}
	
	/**
	 * @throws ResourceUpdateException 
	 * @see org.openmrs.module.webservices.rest.resource.Updatable#update(java.lang.String, org.openmrs.module.webservices.rest.SimpleObject)
	 */
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
	    verifyDelegateUuid(uuid);
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
	public Object update(SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		if (delegate == null)
			throw new ObjectNotFoundException();
		setPropertiesOnDelegate(propertiesToUpdate);
		delegate = saveDelegate();
		return delegate;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.resource.Deletable#delete(java.lang.String)
	 */
	@Override
	public void delete(String uuid, String reason, RequestContext context) throws ResponseException {
		if (delegate == null)
			throw new ObjectNotFoundException();
		verifyDelegateUuid(uuid);
	    delete(reason, context);
	}
	
	/**
	 * Verifies that the delegate is non-null, and that its uuid matches the given uuid
	 * @param uuid
	 * @throws ResponseException
	 */
	private void verifyDelegateUuid(String uuid) throws ResponseException {
		if (delegate == null)
			throw new ObjectNotFoundException();
		String delegateUuid = (String) getPropertyIfExists(delegate, "uuid");
		if (!uuid.equals(delegateUuid))
			throw new ObjectMismatchException("uuid does not match delegate.uuid", null);
    }

	/**
	 * Convenience version of {@link #delete(String,String)}, since this resource already has a delegate set
	 * @throws ResponseException
	 * @see Deletable#delete(String,String)
	 */
	public abstract void delete(String reason, RequestContext context) throws ResponseException;
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.Purgeable#purge(java.lang.String)
	 */
	@Override
	public void purge(String uuid, RequestContext context) throws ResponseException {
		// DELETE is idempotent, so if we can't find the object, assume it's already deleted, and return success
		if (delegate == null)
			return;
		verifyDelegateUuid(uuid);
		purge(context);
	}
	
	/**
	 * @param bean
	 * @param property
	 * @return the given property on the given bean, if it exists and is accessible. returns null otherwise.
	 */
    private Object getPropertyIfExists(Object bean, String property) {
	    try {
	    	if (PropertyUtils.isReadable(bean, property))
	    		return PropertyUtils.getProperty(bean, property);
	    } catch (Exception ex) { }
	    return null;
    }

	/**
     * Convenience version of {@link #purge(String)}, since this resource already has a delegate set
     * @param context 
     * @throws ResponseException 
     * @see Purgeable#purge(String)
     */
    public abstract void purge(RequestContext context) throws ResponseException;
    
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
	 * @throws ConversionException 
     */
    protected void setPropertiesOnDelegate(SimpleObject propertyMap) throws ConversionException {
    	for (Map.Entry<String, Object> prop : propertyMap.entrySet()) {
    		setPropertyOnDelegate(prop.getKey(), prop.getValue());
    	}
    }

	/**
	 * Sets the given property on delegate to the given value
	 * @param property
	 * @param value
	 * @throws ConversionException 
	 */
	protected void setPropertyOnDelegate(String property, Object value) throws ConversionException {
		try {
		    log.trace("applying " + property + " which is a " + value.getClass() + " = " + value);
		    PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(delegate, property);
		    log.trace("property exists and is a: " + pd.getPropertyType());
		    if (value == null || pd.getPropertyType().isAssignableFrom(value.getClass())) {
		    	log.trace("compatible type, so setting directly");
		    	pd.getWriteMethod().invoke(delegate, value);
		    } else {
		    	log.trace("need to convert " + value.getClass() + " to " + pd.getPropertyType());
		    	Object converted = convert(value, pd.getPropertyType());
		    	pd.getWriteMethod().invoke(delegate, converted);
		    }
		} catch (Exception ex) {
			throw new ConversionException("setting " + property + " on " + delegate.getClass(), ex);
		}
    }

	/**
	 * TODO move to utility class
	 * Converts the given object to the given type
	 * @param object
	 * @param toType
	 * @return
	 * @throws ConversionException 
	 */
	protected Object convert(Object object, Class<?> toType) throws ConversionException {
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
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(string);
				} catch (ParseException ex) {
					try {
						return new SimpleDateFormat("yyyy-MM-dd").parse(string);
					} catch (ParseException ex2) {
						throw new ConversionException("converting date", ex);
					}
				}
			}
		}
	    throw new ConversionException("Don't know how to convert from " + object.getClass() + " to " + toType, null);
    }

	/**
     * Creates an object of the given representation, pulling fields and methods from the delegate object
     * and class methods as specified by the {@link IncludeProperties} annotation 
     * @param representation
     * @return
     */
	@Override
    public Object asRepresentation(Representation representation) throws ConversionException {
    	if (delegate == null)
   			throw new NullPointerException();

    	Method meth = findAnnotatedMethodForRepresentation(representation);
    	if (meth == null)
    		throw new ConversionException("Don't know how to get " + getClass().getSimpleName() + " as " + representation, null);
    	try {
	    	if (meth.getParameterTypes().length == 0)
				return meth.invoke(this);
			else
				return meth.invoke(this, representation);
    	} catch (Exception ex) {
    		throw new ConversionException(null, ex);
    	}
    }

	protected SimpleObject convertDelegateToRepresentation(DelegatingResourceRepresentation rep) throws ConversionException {
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
	private Method findAnnotatedMethodForRepresentation(Representation rep) {
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
	 * @throws ConversionException
	 */
	public Object getPropertyWithRepresentation(String propertyName, Representation rep) throws ConversionException {
		Object o;
		try {
			o = PropertyUtils.getProperty(delegate, propertyName);
		} catch (Exception ex) {
			throw new ConversionException(null, ex);
		}
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
    private <S> Object convertObjectToRep(S o, Representation rep) throws ConversionException {
		if (o == null)
			return null;
		DelegateConverter<S> converter = null;
		try {
			converter = HandlerUtil.getPreferredHandler(DelegateConverter.class, o.getClass());
		} catch (APIException ex) {
			// try a few known datatypes
			if (o instanceof Date) {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format((Date) o);
			}
			// otherwise we have no choice but to return the plain object
			return o;
		}
		try {
			converter = converter.getClass().newInstance();
			converter.setDelegate(o);
			return converter.asRepresentation(rep);
		} catch (Exception ex) {
			throw new ConversionException("converting " + o.getClass() + " to " + rep, ex);
		}
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
