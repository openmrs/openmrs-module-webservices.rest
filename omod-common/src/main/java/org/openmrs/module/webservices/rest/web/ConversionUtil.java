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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.LocaleUtility;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConversionUtil {
	
	static final Log log = LogFactory.getLog(ConversionUtil.class);
	
	// This would better be a Map<Pair<Class, String>, Type> but adding the dependency for
	//  org.apache.commons.lang3.tuple.Pair (through omrs-api) messed up other tests
	private static Map<String, Type> typeVariableMap = new ConcurrentHashMap<String, Type>();
	
	private static ConcurrentMap<Class<?>, Converter> converterCache;
	
	private static Converter nullConverter;
	
	static {
		converterCache = new ConcurrentHashMap<Class<?>, Converter>();
		nullConverter = new Converter() {
			
			@Override
			public Object newInstance(String type) {
				return null;
			}
			
			@Override
			public Object getByUniqueId(String string) {
				return null;
			}
			
			@Override
			public SimpleObject asRepresentation(Object instance, Representation rep) throws ConversionException {
				return null;
			}
			
			@Override
			public Object getProperty(Object instance, String propertyName) throws ConversionException {
				return null;
			}
			
			@Override
			public void setProperty(Object instance, String propertyName, Object value) throws ConversionException {
				
			}
		};
	}
	
	public static void clearCache() {
		converterCache = new ConcurrentHashMap<Class<?>, Converter>();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Converter<T> getConverter(Class<T> clazz) {
		Converter<T> result = converterCache.get(clazz);
		if (result != null) {
			return result == nullConverter ? null : result;
		}
		
		try {
			try {
				Resource resource = Context.getService(RestService.class).getResourceBySupportedClass(clazz);
				
				if (resource instanceof Converter) {
					result = (Converter<T>) resource;
				}
			}
			catch (APIException e) {}
			
			if (result == null) {
				result = HandlerUtil.getPreferredHandler(Converter.class, clazz);
			}
		}
		catch (APIException ex) {
			result = null;
		}
		
		// At this point, we don't really care if a result was found or not, we cache it regardless so that repeated
		// searches are not performed.
		if (result == null) {
			converterCache.put(clazz, nullConverter);
		} else {
			converterCache.put(clazz, result);
		}
		
		return result;
	}
	
	/**
	 * Converts the given object to the given type
	 * 
	 * @param object The value to convert
	 * @param toType The type to convert the value to
	 * @param instance The source object instance
	 * @return The specified object converted to the specified type
	 * @should resolve TypeVariables to actual type
	 */
	public static Object convert(Object object, Type toType, Object instance) throws ConversionException {
		if (instance != null && toType instanceof TypeVariable<?>) {
			TypeVariable<?> temp = ((TypeVariable<?>) toType);
			toType = getTypeVariableClass(instance.getClass(), temp);
		}
		
		return convert(object, toType);
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
	 * @should convert to an array
	 * @should convert to a class
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" })
	public static Object convert(Object object, Type toType) throws ConversionException {
		if (object == null)
			return object;
		
		Class<?> toClass = toType instanceof Class ? ((Class<?>) toType) : (Class<?>) (((ParameterizedType) toType)
		        .getRawType());
		
		// if we're trying to convert _to_ a collection, handle it as a special case
		if (Collection.class.isAssignableFrom(toClass) || toClass.isArray()) {
			if (!(object instanceof Collection))
				throw new ConversionException("Can only convert a Collection to a Collection/Array. Not "
				        + object.getClass() + " to " + toType, null);
			
			if (toClass.isArray()) {
				Class<?> targetElementType = toClass.getComponentType();
				Collection input = (Collection) object;
				Object ret = Array.newInstance(targetElementType, input.size());
				
				int i = 0;
				for (Object element : (Collection) object) {
					Array.set(ret, i, convert(element, targetElementType));
					++i;
				}
				return ret;
			}
			
			Collection ret = null;
			if (SortedSet.class.isAssignableFrom(toClass)) {
				ret = new TreeSet();
			} else if (Set.class.isAssignableFrom(toClass)) {
				ret = new HashSet();
			} else if (List.class.isAssignableFrom(toClass)) {
				ret = new ArrayList();
			} else {
				throw new ConversionException("Don't know how to handle collection class: " + toClass, null);
			}
			
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
			} else if (toClass.isAssignableFrom(Class.class)) {
				try {
					return Context.loadClass((String) object);
				}
				catch (ClassNotFoundException e) {
					throw new ConversionException("Could not convert from " + object.getClass() + " to " + toType, e);
				}
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
		if (toClass.isAssignableFrom(Double.class) && object instanceof Number) {
			return ((Number) object).doubleValue();
		} else if (toClass.isAssignableFrom(Integer.class) && object instanceof Number) {
			return ((Number) object).intValue();
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
		
		Object ret = null;
		Object uuid = map.get(RestConstants.PROPERTY_UUID);
		if (uuid instanceof String) {
			ret = converter.getByUniqueId(uuid.toString());
		}
		
		if (ret == null) {
			String type = (String) map.get(RestConstants.PROPERTY_FOR_TYPE);
			ret = converter.newInstance(type);
		}
		
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
		return convertToRepresentation(o, rep, (Converter) null);
	}
	
	@SuppressWarnings("unchecked")
	public static <S> Object convertToRepresentation(S o, Representation rep, Class<?> convertAs) throws ConversionException {
		Converter<?> converter = convertAs != null ? getConverter(convertAs) : null;
		return convertToRepresentation(o, rep, converter);
	}
	
	public static <S> Object convertToRepresentation(S o, Representation rep, Converter specificConverter)
	        throws ConversionException {
		if (o == null)
			return null;
		o = new HibernateLazyLoader().load(o);
		
		if (o instanceof Collection) {
			List ret = new ArrayList();
			for (Object item : ((Collection) o)) {
				ret.add(convertToRepresentation(item, rep, specificConverter));
			}
			return ret;
			
		} else {
			Converter<S> converter = specificConverter != null ? specificConverter : (Converter) getConverter(o.getClass());
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
	
	/**
	 * Gets the type for the specified generic type variable.
	 * 
	 * @param instanceClass An instance of the class with the specified generic type variable.
	 * @param typeVariable The generic type variable.
	 * @return The actual type of the generic type variable or {@code null} if not found.
	 * @should return the actual type if defined on the parent class
	 * @should return the actual type if defined on the grand-parent class
	 * @should return null when actual type cannot be found
	 * @should return the correct actual type if there are multiple generic types
	 * @should throw IllegalArgumentException when instance class is null
	 * @should throw IllegalArgumentException when typeVariable is null
	 */
	public static Type getTypeVariableClass(Class<?> instanceClass, TypeVariable<?> typeVariable) {
		if (instanceClass == null) {
			throw new IllegalArgumentException("The instance class is required.");
		}
		if (typeVariable == null) {
			throw new IllegalArgumentException("The type variable is required.");
		}
		
		String genericTypeName = typeVariable.getName();
		Type type = instanceClass;
		
		// Check to see if type variable has already been cached
		Type result = typeVariableMap.get(instanceClass.getName().concat(genericTypeName));
		
		// Walk the inheritance chain up and try to find the generic type with the specified name
		while (result == null && type != null && !type.equals(Object.class)) {
			if (type instanceof Class) {
				type = ((Class) type).getGenericSuperclass();
			} else {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> rawType = (Class) parameterizedType.getRawType();
				
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					String name = typeParameters[i].getName();
					Type actualType = actualTypeArguments[i];
					
					// Cache each generic type's actual type
					typeVariableMap.put(instanceClass.getName().concat(name), actualType);
					
					if (name.equals(genericTypeName)) {
						// Found it
						result = actualType;
						break;
					}
				}
				
				// Move up to the parent class
				type = rawType.getGenericSuperclass();
			}
		}
		
		return result;
	}
}
