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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.RepresentationDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.util.ReflectionUtils;

public abstract class BaseDelegatingResource<T> implements Converter<T> {

	/**
	 * Implementations should define mappings for properties that they want to expose with other
	 * names. (Map from the exposed property name to the actual property name.)
	 */
	protected Map<String, String> remappedProperties = new HashMap<String, String>();
	
	/**
	 * Gets the delegate object with the given unique id. Implementations may decide whether
	 * "unique id" means a uuid, or if they also want to retrieve delegates based on a unique
	 * human-readable property.
	 * 
	 * @param uniqueId
	 * @return the delegate for the given uniqueId
	 */
	@Override
	public abstract T getByUniqueId(String uniqueId);
		
	/**
	 * @return a new instance of the delegate class
	 */
	protected abstract T newDelegate();

	/**
	 * Writes the delegate to the database
	 * 
	 * @return the saved instance
	 */
	protected abstract T save(T delegate);
	
	/**
	 * Void or retire delegate, whichever action is appropriate for the resource type. Subclasses
	 * need to override this method, which is called internally by
	 * {@link #delete(String, String, RequestContext)}.
	 * 
	 * @param delegate
	 * @param reason
	 * @param context
	 * @throws ResponseException
	 */
	protected abstract void delete(T delegate, String reason, RequestContext context) throws ResponseException;
	
	/**
	 * Purge delegate from persistent storage. Subclasses need to override this method, which is
	 * called internally by {@link #purge(String, RequestContext)}.
	 * 
	 * @param delegate
	 * @param context
	 * @throws ResponseException
	 */
	public abstract void purge(T delegate, RequestContext context) throws ResponseException;
	
	/**
	 * Gets the {@link RepresentationDescription} for the given representation for this resource, if
	 * it exists
	 * 
	 * @param rep
	 * @return
	 */
	public abstract DelegatingResourceDescription getRepresentationDescription(Representation rep);
	
	/**
	 * Implementations should override this method if T is not uniquely identified by a "uuid" property.
	 * @param delegate
	 * @return the uuid property of delegate
	 */
	protected String getUniqueId(T delegate) {
		try {
			return (String) PropertyUtils.getProperty(delegate, "uuid");
		}
		catch (Exception ex) {
			throw new RuntimeException("Cannot find String uuid property on " + delegate.getClass(), null);
		}
	}
	
	/**
	 * Creates an object of the given representation, pulling values from fields and methods as
	 * specified by a subclass
	 * 
	 * @param representation
	 * @return
	 */
	@Override
	public Object asRepresentation(T delegate, Representation representation) throws ConversionException {
		if (delegate == null)
			throw new NullPointerException();
		
		// first call getRepresentationDescription()
		DelegatingResourceDescription repDescription = getRepresentationDescription(representation);
		if (repDescription != null) {
			return convertDelegateToRepresentation(delegate, repDescription);
		}
		
		// otherwise look for a method annotated to handle this representation
		Method meth = findAnnotatedMethodForRepresentation(representation);
		if (meth != null) {
			try {
				// TODO verify that the method takes 1 or 2 parameters
				if (meth.getParameterTypes().length == 1)
					return meth.invoke(this, delegate);
				else
					return meth.invoke(this, delegate, representation);
			}
			catch (Exception ex) {
				throw new ConversionException(null, ex);
			}
		}
		
		throw new ConversionException("Don't know how to get " + getClass().getSimpleName() + " as " + representation, null);
	}
	
	protected SimpleObject convertDelegateToRepresentation(T delegate, DelegatingResourceDescription rep)
	                                                                                                     throws ConversionException {
		if (delegate == null)
			throw new NullPointerException();
		SimpleObject ret = new SimpleObject();
		for (Entry<String, Property> e : rep.getProperties().entrySet()) {
			ret.put(e.getKey(), e.getValue().evaluate(this, delegate));
		}
		return ret;
	}
	
	/**
	 * @param delegate
	 * @param propertiesToCreate
	 * @throws ConversionException 
	 */
	protected void setConvertedProperties(T delegate, Map<String, Object> propertyMap) throws ConversionException {
		for (Map.Entry<String, Object> prop : propertyMap.entrySet()) {
			setProperty(delegate, prop.getKey(), prop.getValue());
		}
    }
	
	/**
	 * Finds a method in this class or a superclass annotated with a {@link RepHandler} for the
	 * given representation
	 * 
	 * @param clazz
	 * @return
	 */
	private Method findAnnotatedMethodForRepresentation(Representation rep) {
		// TODO I assume Java takes care of this, but make sure if there are multiple annotated methods we take the one on the subclass
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
	
	protected Method findMethod(String name) {
		// TODO replace this with something that looks specifically for a method that takes a single T argument 
		Method ret = ReflectionUtils.findMethod(getClass(), name, (Class<?>[]) null);
		if (ret == null)
			throw new RuntimeException("No suitable method \"" + name + "\" in " + getClass());
		return ret;
	}
	
	@Override
	public Object getProperty(T instance, String propertyName) throws ConversionException {
		try {
			String override = remappedProperties.get(propertyName);
			if (override != null)
				propertyName = override;
			return PropertyUtils.getProperty(instance, propertyName);
		}
		catch (Exception ex) {
			throw new ConversionException(propertyName, ex);
		}
	}

	@Override
	public void setProperty(T instance, String propertyName, Object value) throws ConversionException {
		try {
			// first, try to find a @PropertySetter-annotated method
			Method annotatedSetter = findSetterMethod(propertyName);
			if (annotatedSetter != null) {
				Class<?> expectedType = annotatedSetter.getParameterTypes()[1];
				if (value != null && !expectedType.isAssignableFrom(value.getClass()))
					value = ConversionUtil.convert(value, expectedType);
				annotatedSetter.invoke(null, instance, value);
				return;
			}
			
			// next use standard bean methods
			String override = remappedProperties.get(propertyName);
			if (override != null)
				propertyName = override;
			Class<?> expectedType = PropertyUtils.getPropertyType(instance, propertyName);
			if (value != null && !expectedType.isAssignableFrom(value.getClass()))
				value = ConversionUtil.convert(value, expectedType);
			PropertyUtils.setProperty(instance, propertyName, value);
		}
		catch (Exception ex) {
			throw new ConversionException(propertyName + " on " + instance.getClass(), ex);
		}
	}
	
	private Method findSetterMethod(String propName) {
		for (Method candidate : getClass().getMethods()) {
			PropertySetter ann = candidate.getAnnotation(PropertySetter.class);
			if (ann != null && ann.value().equals(propName))
				return candidate;
		}
		return null;
	}
	
}
