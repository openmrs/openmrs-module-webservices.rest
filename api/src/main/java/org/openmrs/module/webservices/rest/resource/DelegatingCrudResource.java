package org.openmrs.module.webservices.rest.resource;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.NamedRepresentation;
import org.openmrs.module.webservices.rest.Representation;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.IncludeProperties;
import org.openmrs.module.webservices.rest.annotation.RepClassHandler;
import org.openmrs.module.webservices.rest.api.WSRestService;
import org.openmrs.util.HandlerUtil;

/**
 * A base implementation of a {@link Retrievable} that delegates property access to a wrapped object
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
	 * @see org.openmrs.module.webservices.rest.resource.Retrievable#retrieve(java.lang.String, org.openmrs.module.webservices.rest.Representation)
	 */
	@Override
	public Object retrieve(String uuid, Representation representation) {
		// TODO check that delegate has the correct uuid, and we aren't about to return the wrong thing
		try {
			return asRepresentation(representation);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.Creatable#create(org.springframework.web.context.request.WebRequest)
	 */
	@Override
	public DelegatingCrudResource<T> create(SimpleObject post) throws ResourceCreationException {
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
	public Object update(String uuid, SimpleObject propertiesToUpdate) throws ResourceUpdateException {
	    // TODO check that delegate has the correct uuid, and we aren't about to update the wrong thing
		return update(propertiesToUpdate);
	}
	
	/**
	 * Convenience version of {@link #update(String, SimpleObject)}, since this resource already has a
	 * delegate set
	 * @param propertiesToUpdate
	 * @return
	 * @throws ResourceUpdateException
	 */
	public Object update(SimpleObject propertiesToUpdate) throws ResourceUpdateException {
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
	public void delete(String uuid, String reason) throws ResourceDeletionException {
		// TODO check that delegate has the correct uuid, and we aren't about to delete the wrong thing
	    delete(reason);
	}
	
	/**
	 * Convenience version of {@link #delete(String,String)}, since this resource already has a delegate set
	 * @throws ResourceDeletionException 
	 * @see Deletable#delete(String,String)
	 */
	public abstract void delete(String reason) throws ResourceDeletionException;
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.Purgeable#purge(java.lang.String)
	 */
	@Override
	public void purge(String uuid) throws ResourceDeletionException {
		// TODO check that delegate has the correct uuid, and we aren't about to purge the wrong thing
		purge();
	}
	
    /**
     * Convenience version of {@link #purge(String)}, since this resource already has a delegate set
     * @throws ResourceDeletionException 
     * @see Purgeable#purge(String)
     */
    public abstract void purge() throws ResourceDeletionException;
    
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

    	// if any method in this class or a superclass is annotated with @RepClassHandler for this representation class
    	// we just call that method
    	Method meth = findAnnotatedMethodForRepresentationClass(representation.getClass());
    	if (meth != null) {
    		return meth.invoke(this, representation);
    	}
    	
    	SimpleObject ret = new SimpleObject();
		if (representation instanceof NamedRepresentation) {
	    	Map<String, Representation> propsToInclude = propertiesToInclude((NamedRepresentation) representation);
	    	for (Map.Entry<String, Representation> e : propsToInclude.entrySet()) {
	    		ret.put(e.getKey(), getPropertyWithRepresentation(e.getKey(), e.getValue()));
	    	}
		}
    	return ret;
    }

	/**
	 * Finds a method in this class or a superclass annotated with a {@link RepClassHandler} for the given
	 * representation class
	 * @param clazz
	 * @return
	 */
	private Method findAnnotatedMethodForRepresentationClass(Class<? extends Representation> clazz) throws Exception {
		// TODO make sure if there are multiple annotated methods we take the one on the subclass
	    for (Method method : getClass().getMethods()) {
	    	for (RepClassHandler ann : getAnnotations(method, RepClassHandler.class)) {
	    		if (ann.value().isAssignableFrom(clazz))
	    			return method;
	    	}
	    }
	    return null;
    }

	@SuppressWarnings("unchecked")
    private <Ann> List<Ann> getAnnotations(Method method, Class<Ann> annotationClass) {
		List<Ann> ret = new ArrayList<Ann>();
		for (Annotation ann : method.getAnnotations())
			if (annotationClass.isAssignableFrom(ann.getClass()))
				ret.add((Ann) ann);
	    return ret;
    }

	/**
	 * Gets the properties that should be included, under the given representation
	 * @param representation
	 * @return
	 */
	protected Map<String, Representation> propertiesToInclude(NamedRepresentation representation) {
	    Map<String, Representation> ret = new HashMap<String, Representation>();
	    propertiesToIncludeFromClassAndSuperclasses(ret, getClass(), representation);
	    return ret;
    }

	/**
	 * Looks at the {@link IncludeProperties} annotation on clazz to determine which properties to include,
	 * and recurses to superclasses and interfaces as well.
	 * @param map
	 * @param clazz
	 */
	private void propertiesToIncludeFromClassAndSuperclasses(Map<String, Representation> map,
                                                             Class<?> clazz,
                                                             NamedRepresentation rep) {
		if (clazz == null || !DelegatingCrudResource.class.isAssignableFrom(clazz))
			return;
		
		// handle this class
		for (Annotation ann : clazz.getAnnotations()) {
			if (ann instanceof IncludeProperties) {
				IncludeProperties incProp = (IncludeProperties) ann;
				if (rep.matchesAnnotation(incProp)) {
					for (String prop : incProp.properties()) {
						// TODO create a proper class to represent property/method name and representation
						if (prop.indexOf(':') > 0) {
							String[] temp = prop.split(":");
							map.put(temp[0], Context.getService(WSRestService.class).getRepresentation(temp[1]));
						} else {
							map.put(prop, Representation.DEFAULT);
						}
					}
				}
			}
		}
		
		// recurse
		for (Class<?> interf : clazz.getInterfaces())
			propertiesToIncludeFromClassAndSuperclasses(map, interf, rep);
		propertiesToIncludeFromClassAndSuperclasses(map, clazz.getSuperclass(), rep);
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
		//getConstructor(o.getClass()).newInstance(o);
		return converter.asRepresentation(rep);
	}

}
