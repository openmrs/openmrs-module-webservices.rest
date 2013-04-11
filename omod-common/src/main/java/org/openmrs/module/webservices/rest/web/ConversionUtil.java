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
package org.openmrs.module.webservices.rest.web;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.LocaleUtility;

public class ConversionUtil {
	
	static final Log log = LogFactory.getLog(ConversionUtil.class);
	
	@SuppressWarnings("unchecked")
	public static <T> Converter<T> getConverter(Class<T> clazz) {
		try {
			
			try {
				Resource resource = Context.getService(RestService.class).getResourceBySupportedClass(clazz);
				
				if (resource instanceof Converter) {
					return (Converter<T>) resource;
				}
			}
			catch (APIException e) {}
			
			Converter<T> converter = HandlerUtil.getPreferredHandler(Converter.class, clazz);
			return converter;
		}
		catch (APIException ex) {
			return null;
		}
	}
	
	/**
	 * Converts the given object to the given type
	 * 
	 * @param object
	 * @param toType a simple class or generic type
	 * @return
	 * @throws ConversionException
	 * @should convert strings to locales
	 * @should convert strings to enum values
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" })
	public static Object convert(Object object, Type toType) throws ConversionException {
		if (object == null)
			return object;
		
		Class<?> toClass = toType instanceof Class ? ((Class<?>) toType) : (Class<?>) (((ParameterizedType) toType)
		        .getRawType());
		
		// if we're trying to convert _to_ a collection, handle it as a special case
		if (Collection.class.isAssignableFrom(toClass)) {
			if (!(object instanceof Collection))
				throw new ConversionException("Can only convert a Collection to a Collection. Not " + object.getClass()
				        + " to " + toType, null);
			
			Collection ret = null;
			if (SortedSet.class.isAssignableFrom(toClass))
				ret = new TreeSet();
			else if (Set.class.isAssignableFrom(toClass))
				ret = new HashSet();
			else if (List.class.isAssignableFrom(toClass))
				ret = new ArrayList();
			else
				throw new ConversionException("Don't know how to handle collection class: " + toClass, null);
			
			if (toType instanceof ParameterizedType) {
				// if we have generic type information for the target collection, we can use it to do conversion
				ParameterizedType toParameterizedType = (ParameterizedType) toType;
				Type targetElementType = toParameterizedType.getActualTypeArguments()[0];
				for (Object element : (Collection) object)
					ret.add(convert(element, targetElementType));
			} else {
				// otherwise we must just add all items in a non-type-safe manner
				ret.addAll((Collection) object);
			}
			return ret;
		}
		
		// otherwise we're converting _to_ a non-collection type
		
		if (toClass.isAssignableFrom(object.getClass()))
			return object;
		
		// Numbers with a decimal are always assumed to be Double, so convert to Float, if necessary
		if (toClass.isAssignableFrom(Float.class) && object instanceof Double) {
			return new Float((Double) object);
		}
		
		if (object instanceof String) {
			String string = (String) object;
			Converter<?> converter = getConverter(toClass);
			if (converter != null)
				return converter.getByUniqueId(string);
			
			if (toClass.isAssignableFrom(Date.class)) {
				ParseException pex = null;
				String[] supportedFormats = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS",
				        "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };
				for (int i = 0; i < supportedFormats.length; i++) {
					try {
						Date date = new SimpleDateFormat(supportedFormats[i]).parse(string);
						return date;
					}
					catch (ParseException ex) {
						pex = ex;
					}
				}
				throw new ConversionException(
				        "Error converting date - correct format (ISO8601 Long): yyyy-MM-dd'T'HH:mm:ss.SSSZ", pex);
			} else if (toClass.isAssignableFrom(Locale.class)) {
				return LocaleUtility.fromSpecification(object.toString());
			} else if (toClass.isEnum()) {
				return Enum.valueOf((Class<? extends Enum>) toClass, object.toString());
			}
			// look for a static valueOf(String) method (e.g. Double, Integer, Boolean)
			try {
				Method method = toClass.getMethod("valueOf", String.class);
				if (Modifier.isStatic(method.getModifiers()) && toClass.isAssignableFrom(method.getReturnType())) {
					return method.invoke(null, string);
				}
			}
			catch (Exception ex) {}
		} else if (object instanceof Map) {
			return convertMap((Map<String, ?>) object, toClass);
		}
		throw new ConversionException("Don't know how to convert from " + object.getClass() + " to " + toType, null);
	}
	
	/**
	 * Converts a map to the given type, using the registered converter
	 * 
	 * @param map the map (typically a SimpleObject submitted as json) to convert
	 * @param toClass the class to convert map to
	 * @return the result of using a converter to instantiate a new class and set map's properties
	 *         on it
	 * @throws ConversionException
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" })
	public static Object convertMap(Map<String, ?> map, Class<?> toClass) throws ConversionException {
		// TODO handle refs by fetching the object at their URI
		Converter converter = getConverter(toClass);
		String type = (String) map.get(RestConstants.PROPERTY_FOR_TYPE);
		Object ret = converter.newInstance(type);
		for (Map.Entry<String, ?> prop : map.entrySet()) {
			if (RestConstants.PROPERTY_FOR_TYPE.equals(prop.getKey()))
				continue;
			converter.setProperty(ret, prop.getKey(), prop.getValue());
		}
		return ret;
	}
	
	/**
	 * Gets a property from the delegate, with the given representation
	 * 
	 * @param propertyName
	 * @param rep
	 * @return
	 * @throws ConversionException
	 */
	public static Object getPropertyWithRepresentation(Object bean, String propertyName, Representation rep)
	        throws ConversionException {
		Object o;
		try {
			o = PropertyUtils.getProperty(bean, propertyName);
		}
		catch (Exception ex) {
			throw new ConversionException(null, ex);
		}
		if (o instanceof Collection) {
			List<Object> ret = new ArrayList<Object>();
			for (Object element : (Collection<?>) o)
				ret.add(convertToRepresentation(element, rep));
			return ret;
		} else {
			o = convertToRepresentation(o, rep);
			return o;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <S> Object convertToRepresentation(S o, Representation rep) throws ConversionException {
		if (o == null)
			return null;
		Converter<S> converter = (Converter<S>) getConverter(Hibernate.getClass(o));
		if (converter == null) {
			// try a few known datatypes
			if (o instanceof Date) {
				return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format((Date) o);
			}
			// otherwise we have no choice but to return the plain object
			return o;
		}
		try {
			return converter.asRepresentation(o, rep);
		}
		catch (Exception ex) {
			throw new ConversionException("converting " + o.getClass() + " to " + rep, ex);
		}
	}
	
}
