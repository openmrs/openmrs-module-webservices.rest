package org.openmrs.module.webservices.rest.web.resource.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.util.ReflectionUtil;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.Hyperlink;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A base implementation of a converter that can transform something that is _not_ a full resource
 * into a representation for sending over the wire. Typically you'd use this as a convenience to
 * convert some domain object that is contained in a proper resource (but is not a sub-resource)
 * Direct subclasses should typically be annotated with @Handler(supports = T.class). (If you
 * subclass one of the Resource subclasses of this class, you don't need to do that, but would use a @Resource
 * annotation.)
 * 
 * @param <T> the class we're delegating to
 */
public abstract class BaseDelegatingConverter<T> implements Converter<T>, DelegatingPropertyAccessor<T> {
	
	/**
	 * Gets the {@link DelegatingResourceDescription} for the given representation for this
	 * resource, if it exists
	 * 
	 * @param rep
	 * @return
	 */
	public abstract DelegatingResourceDescription getRepresentationDescription(Representation rep);
	
	/**
	 * (Note that this method doesn't support @RepHandler; the implementation in
	 * BaseDelegatingResource does.)
	 * 
	 * @param delegate
	 * @param rep
	 * @return
	 * @throws ConversionException
	 */
	@Override
	public SimpleObject asRepresentation(T delegate, Representation rep) throws ConversionException {
		if (delegate == null)
			throw new NullPointerException();
		
		DelegatingResourceDescription description = getRepresentationDescription(rep);
		SimpleObject simple = convertDelegateToRepresentation(delegate, description);
		return simple;
	}
	
	@Override
	public Object getProperty(T instance, String propertyName) throws ConversionException {
		try {
			Method annotatedGetter = ReflectionUtil.findPropertyGetterMethod(this, propertyName);
			if (annotatedGetter != null) {
				return annotatedGetter.invoke(this, instance);
			}
			
			return PropertyUtils.getProperty(instance, propertyName);
		}
		catch (Exception ex) {
			throw new ConversionException("Unable to get property " + propertyName, ex);
		}
	}
	
	@Override
	public void setProperty(Object instance, String propertyName, Object value) throws ConversionException {
		try {
			// try to find a @PropertySetter-annotated method
			Method annotatedSetter = ReflectionUtil.findPropertySetterMethod(this, propertyName);
			if (annotatedSetter != null) {
				Type expectedType = annotatedSetter.getGenericParameterTypes()[1];
				value = ConversionUtil.convert(value, expectedType);
				annotatedSetter.invoke(this, instance, value);
				return;
			}
			
			// we need the generic type of this property, not just the class
			Method setter = PropertyUtils.getPropertyDescriptor(instance, propertyName).getWriteMethod();
			
			// Convert the value to the specified type
			value = ConversionUtil.convert(value, setter.getGenericParameterTypes()[0], instance);
			
			setPropertyWhichMayBeAHibernateCollection(instance, propertyName, value);
		}
		catch (Exception ex) {
			throw new ConversionException(propertyName + " on " + instance.getClass(), ex);
		}
	}
	
	protected void setPropertyWhichMayBeAHibernateCollection(Object instance, String propertyName, Object value)
	        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (value instanceof Collection) {
			//We need to handle collections in a way that Hibernate can track.
			Collection<?> newCollection = (Collection<?>) value;
			Object oldValue = PropertyUtils.getProperty(instance, propertyName);
			if (oldValue instanceof Collection) {
				Collection collection = (Collection) oldValue;
				collection.clear();
				collection.addAll(newCollection);
			} else {
				PropertyUtils.setProperty(instance, propertyName, value);
			}
		} else {
			PropertyUtils.setProperty(instance, propertyName, value);
		}
	}
	
	/**
	 * If rep contains any links, and you are not extending a subclass (e.g. BaseDelegatingResource)
	 * that implements getUri, this will throw an exception
	 * 
	 * @param delegate
	 * @param rep
	 * @return
	 * @throws ConversionException
	 */
	protected SimpleObject convertDelegateToRepresentation(T delegate, DelegatingResourceDescription rep)
	        throws ConversionException {
		if (delegate == null)
			throw new NullPointerException();
		SimpleObject ret = new SimpleObject();
		for (Map.Entry<String, DelegatingResourceDescription.Property> e : rep.getProperties().entrySet()) {
			ret.put(e.getKey(), e.getValue().evaluate(this, delegate));
		}
		List<Hyperlink> links = new ArrayList<Hyperlink>();
		for (Hyperlink link : rep.getLinks()) {
			if (link.getUri().startsWith(".")) {
				link = new Hyperlink(link.getRel(), getUri(delegate) + link.getUri().substring(1));
			}
			
			org.openmrs.module.webservices.rest.web.annotation.Resource res = getClass().getAnnotation(
			    org.openmrs.module.webservices.rest.web.annotation.Resource.class);
			if (res != null) {
				String name = res.name();
				if (name.contains("/")) {
					name = name.substring(name.lastIndexOf("/") + 1);
				}
				link.setResourceAlias(name);
			} else {
				SubResource sub = getClass().getAnnotation(SubResource.class);
				if (sub != null) {
					link.setResourceAlias(sub.path());
				}
			}
			links.add(link);
		}
		if (links.size() > 0)
			ret.put("links", links);
		return ret;
	}
	
	/**
	 * Subclasses that represent resources with a URI need to override this. (This implementation
	 * throws an exception.)
	 * 
	 * @param delegate
	 * @return
	 */
	public String getUri(Object delegate) {
		throw new IllegalStateException(
		        "representation description includes a link, but this converter doesn't define a URI: "
		                + getClass().getName());
	}
	
}
