/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openmrs.Auditable;
import org.openmrs.ConceptName;
import org.openmrs.OpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.Voidable;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.security.Authorize;
import org.openmrs.security.DomainObjectAuthorizationRule;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.LocaleUtility;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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

import static org.openmrs.module.webservices.rest.web.representation.Representation.DEFAULT;
import static org.openmrs.module.webservices.rest.web.representation.Representation.FULL;
import static org.openmrs.module.webservices.rest.web.representation.Representation.REF;

public class ConversionUtil {
	
	static final Log log = LogFactory.getLog(ConversionUtil.class);
	
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static final Object PRIVILEGE_DENIED = new Object();
	
	/**
	 * The property a representation carries in place of an object a
	 * {@link DomainObjectAuthorizationRule} refused, so a reader can tell data that is hidden from data
	 * that does not exist.
	 */
	public static final String MASKED_PROPERTY = "masked";

	/**
	 * What a display string says when a {@link DomainObjectAuthorizationRule} refuses a record the
	 * display was read from. A resource that computes one has to substitute this itself, because a
	 * derived value is not an object this funnel ever sees: see {@link #isRefusedByARule(Object)}.
	 * <p>
	 * The same literal {@code Authorize} prints for a mask's own {@code toString()}, so a reader meets
	 * one spelling of "hidden" however the value reached them.
	 */
	public static final String MASKED_DISPLAY = "[hidden]";

	// This would better be a Map<Pair<Class, String>, Type> but adding the dependency for
	//  org.apache.commons.lang3.tuple.Pair (through omrs-api) messed up other tests
	private static final Map<String, Type> typeVariableMap = new ConcurrentHashMap<String, Type>();
	
	private static ConcurrentMap<Class<?>, Converter> converterCache;
	
	private static final Converter nullConverter;
	
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
	 * <strong>Should</strong> resolve TypeVariables to actual type
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
	 * <strong>Should</strong> convert strings to locales
	 * <strong>Should</strong> convert strings to enum values
	 * <strong>Should</strong> convert to an array
	 * <strong>Should</strong> convert to a class
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object convert(Object object, Type toType) throws ConversionException {
		if (object == null) {
			return null;
		}
		
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
				for (Object element : input) {
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
				for (Object element : (Collection) object) {
					ret.add(convert(element, targetElementType));
				}
			} else {
				// otherwise we must just add all items in a non-type-safe manner
				ret.addAll((Collection) object);
			}
			return ret;
		}
		
		// otherwise we're converting _to_ a non-collection type
		
		if (toClass.isAssignableFrom(object.getClass())) {
			return object;
		}
		
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
				IllegalArgumentException pex = null;
				String[] supportedFormats = { DATE_FORMAT, "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ssZ",
				        "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };
				for (int i = 0; i < supportedFormats.length; i++) {
					try {
						return DateTime.parse(string, DateTimeFormat.forPattern(supportedFormats[i])).toDate();
					}
					catch (IllegalArgumentException ex) {
						pex = ex;
					}
				}
				throw new ConversionException(
				        "Error converting date - correct format (ISO8601 Long): yyyy-MM-dd'T'HH:mm:ss.SSSZ", pex);
			} else if (toClass.isAssignableFrom(Locale.class)) {
				return LocaleUtility.fromSpecification(object.toString());
			} else if (toClass.isEnum()) {
				return Enum.valueOf((Class<? extends Enum>) toClass, object.toString().toUpperCase());
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
		
		if (toClass.isAssignableFrom(String.class) && object instanceof Boolean) {
			return String.valueOf(object);
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		
		// If the converter is a resource handler use the order of properties of its default representation
		if (converter instanceof DelegatingResourceHandler) {
			
			DelegatingResourceHandler handler = (DelegatingResourceHandler) converter;
			DelegatingResourceDescription resDesc = handler.getRepresentationDescription(new DefaultRepresentation());
			
			// Some resources do not have delegating resource description
			if (resDesc != null) {
				for (Map.Entry<String, Property> prop : resDesc.getProperties().entrySet()) {
					if (map.containsKey(prop.getKey()) && !RestConstants.PROPERTY_FOR_TYPE.equals(prop.getKey())) {
						converter.setProperty(ret, prop.getKey(), map.get(prop.getKey()));
					}
				}
			}
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
			for (Object element : (Collection<?>) o) {
				Object converted = convertToRepresentation(element, rep);
				if (converted == PRIVILEGE_DENIED) {
					return PRIVILEGE_DENIED;
				}
				ret.add(converted);
			}
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
				Object converted = convertToRepresentation(item, rep, specificConverter);
				if (converted == PRIVILEGE_DENIED) {
					return PRIVILEGE_DENIED;
				}
				ret.add(converted);
			}
			return ret;
		} else if (isAMask(o)) {
			// a getter that resolved its own record and found it refused: already the stand-in, and the
			// Map branch below would take it apart. See maskedRepresentation()
			return o;
		} else if (o instanceof Map) {
			if (rep instanceof CustomRepresentation) {
				return convertToCustomRepresentation(o, (CustomRepresentation) rep);
			}
			SimpleObject ret = new SimpleObject();
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
				Object converted = convertToRepresentation(entry.getValue(), Representation.REF, (Converter) null);
				if (converted == PRIVILEGE_DENIED) {
					return PRIVILEGE_DENIED;
				}
				ret.put(entry.getKey().toString(), converted);
			}
			return ret;
		} else {
			Converter<S> converter = specificConverter != null ? specificConverter : (Converter) getConverter(o.getClass());
			if (converter instanceof BaseDelegatingResource) {
				String requiredPrivilege = ((BaseDelegatingResource<?>) converter).getRequiredGetPrivilege();
				if (requiredPrivilege != null && !Context.hasPrivilege(requiredPrivilege)) {
					return PRIVILEGE_DENIED;
				}
			}
			// after the privilege check, so a privilege denial keeps dropping the property as it always has,
			// but before the no-converter shortcut, which would otherwise hand the object to the serializer whole
			if (isRefusedByARule(o)) {
				return maskedRepresentation();
			}
			if (converter == null) {
				// try a few known datatypes
				if (o instanceof Date) {
					return new SimpleDateFormat(DATE_FORMAT).format((Date) o);
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
	 * Whether a registered {@link DomainObjectAuthorizationRule} refuses this object to the current
	 * user. Asked here because this is the one place every object a representation contains passes
	 * through, nested ones included, and by the time they do they have been unwrapped to real entities.
	 * <p>
	 * A service method has usually applied the same rules already, through {@code @PostFilter} or a
	 * {@code hasPermission} guard, and then this agrees with it and changes nothing. It earns its place
	 * on what the service never saw: a nested object reached by following a property off the object the
	 * service returned.
	 * <p>
	 * <strong>A value a resource derives from a record never passes through here.</strong> Only objects
	 * a representation <em>contains</em> do. A {@code display} string built by reading a related
	 * record's fields is a plain String by the time it is a property, so masking the record does
	 * nothing for it and the same response can carry both the mask and what it hides. That is why this
	 * is public: <strong>any display that reads a record other than its own delegate has to ask this
	 * itself and substitute {@link #MASKED_DISPLAY}</strong>. A refused delegate never reaches its own
	 * display, because the representation is masked before the converter runs; a record reached off it
	 * is not covered by anything.
	 * <p>
	 * Deliberately no list of the getters that do: there are too many to keep in step with, and a list
	 * that falls behind reads as a complete one.
	 * <p>
	 * A {@link ConceptName} is judged by the concept it names as well as by itself: see below.
	 *
	 * @param o the object to ask the rules about, may be null or of any type
	 * @return true if a registered rule refuses {@code o} to the current user, or refuses the concept
	 *         {@code o} names when it is a {@link ConceptName}
	 */
	public static boolean isRefusedByARule(Object o) {
		if (!(o instanceof OpenmrsObject)) {
			return false;
		}
		if (Authorize.isMasked(o)) {
			return true;
		}
		// Only when there is no identity at all. A rule is asked about an Authentication, which
		// LegacyContextAuthentication takes from the security context when one is published and builds
		// from the UserContext otherwise, so either alone is enough to judge by and only the absence of
		// both leaves nothing to ask with. Testing the session alone would skip the rules on a worker
		// that carries a published Authentication and no UserContext.
		// Nothing is given up by returning false here: a display getter reached with neither is being
		// called on objects the caller built itself, in a unit test of this module or of one depending
		// on it, since nothing can be loaded from the database without an identity. Failing instead
		// would make every guarded getter require a session to run.
		if (!Context.isSessionOpen() && SecurityContextHolder.getContext().getAuthentication() == null) {
			return false;
		}
		if (Authorize.maskIfDenied((OpenmrsObject) o) != o) {
			return true;
		}
		// A ConceptName is how the concept it hangs off is spelled, and it is a record a response puts
		// on its own: obs.valueCodedName and CodedOrFreeText.specificName are both ConceptName
		// properties. Rules are matched by type and judge nothing reachable through what they are
		// handed, so no Concept rule reaches one - without this, a refused concept came back masked
		// under value and spelled out in full under valueCodedName in the same response.
		// Asked here rather than at those two properties so that every ConceptName a representation
		// contains is covered, whichever resource put it there.
		return o instanceof ConceptName && isRefusedByARule(((ConceptName) o).getConcept());
	}
	
	/**
	 * What stands in for a refused object: a marker and nothing else.
	 * <p>
	 * Deliberately not the object's own representation with the fields blanked. Converting the stand-in
	 * through its resource would hand a client a uuid and a self link for a record it may not read and
	 * which resolves to nothing, and the type is itself a disclosure. A marker also keeps a collection's
	 * size honest, where {@link #PRIVILEGE_DENIED} drops the element and leaves the reader unable to
	 * tell six observations with two hidden from four observations.
	 * <p>
	 * Public for the same reason {@link #MASKED_DISPLAY} is, and it is the counterpart of it for a
	 * property that is not a string: <strong>a getter that resolves a record itself, rather than handing
	 * one to this funnel, has to substitute the mask on its own</strong>. The case that needs it is a
	 * getter whose resolution <em>fails</em> - an attribute's value comes back from a service whose
	 * guard throws rather than masking, so there is no object left for
	 * {@link #isRefusedByARule(Object)} to judge, only an exception. Whatever this returns is treated as
	 * final by {@link #convertToRepresentation(Object, Representation, Converter)}, so a getter may
	 * return it under any representation.
	 */
	public static SimpleObject maskedRepresentation() {
		return new SimpleObject().add(MASKED_PROPERTY, Boolean.TRUE);
	}

	/**
	 * Whether this is already the stand-in {@link #maskedRepresentation()} builds, in which case there
	 * is nothing left to convert: it holds a marker and no record. Converting it again would take the
	 * marker for the record - a custom representation asks the mask for the properties it named and
	 * gets {@code {"uuid": null}} back, which has lost the one thing the mask was there to say.
	 */
	private static boolean isAMask(Object o) {
		return o instanceof Map && Boolean.TRUE.equals(((Map<?, ?>) o).get(MASKED_PROPERTY));
	}
	
	/**
	 * Converts an object to its custom representation
	 * This could be used to convert any domain objects that does not have any specific converter associated with them
	 * such as SimpleObject's, Map's, etc
	 */
	private static SimpleObject convertToCustomRepresentation(Object o, CustomRepresentation rep) {
		DelegatingResourceDescription drd = ConversionUtil.getCustomRepresentationDescription(rep);
		
		SimpleObject result = new SimpleObject();
		for (String propertyName : drd.getProperties().keySet()) {
			DelegatingResourceDescription.Property property = drd.getProperties().get(propertyName);
			Object propertyValue = ConversionUtil.getPropertyWithRepresentation(o, propertyName, property.getRep());
			result.add(propertyName, propertyValue);
		}
		
		return result;
	}
	
	/**
	 * Gets the type for the specified generic type variable.
	 * 
	 * @param instanceClass An instance of the class with the specified generic type variable.
	 * @param typeVariable The generic type variable.
	 * @return The actual type of the generic type variable or {@code null} if not found.
	 * <strong>Should</strong> return the actual type if defined on the parent class
	 * <strong>Should</strong> return the actual type if defined on the grand-parent class
	 * <strong>Should</strong> return null when actual type cannot be found
	 * <strong>Should</strong> return the correct actual type if there are multiple generic types
	 * <strong>Should</strong> throw IllegalArgumentException when instance class is null
	 * <strong>Should</strong> throw IllegalArgumentException when typeVariable is null
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
	
	/**
	 * Gets extra book-keeping info, for the full representation
	 * 
	 * @param delegate
	 * @return
	 */
	public static SimpleObject getAuditInfo(Object delegate) {
		SimpleObject ret = new SimpleObject();
		
		if (delegate instanceof Auditable) {
			Auditable auditable = (Auditable) delegate;
			ret.put("creator", getPropertyWithRepresentation(auditable, "creator", Representation.REF));
			ret.put("dateCreated", convertToRepresentation(auditable.getDateCreated(), Representation.DEFAULT));
			ret.put("changedBy", getPropertyWithRepresentation(auditable, "changedBy", Representation.REF));
			ret.put("dateChanged", convertToRepresentation(auditable.getDateChanged(), Representation.DEFAULT));
		}
		if (delegate instanceof Retireable) {
			Retireable retireable = (Retireable) delegate;
			if (retireable.isRetired()) {
				ret.put("retiredBy", getPropertyWithRepresentation(retireable, "retiredBy", Representation.REF));
				ret.put("dateRetired", convertToRepresentation(retireable.getDateRetired(), Representation.DEFAULT));
				ret.put("retireReason", convertToRepresentation(retireable.getRetireReason(), Representation.DEFAULT));
			}
		}
		if (delegate instanceof Voidable) {
			Voidable voidable = (Voidable) delegate;
			if (voidable.isVoided()) {
				ret.put("voidedBy", getPropertyWithRepresentation(voidable, "voidedBy", Representation.REF));
				ret.put("dateVoided", convertToRepresentation(voidable.getDateVoided(), Representation.DEFAULT));
				ret.put("voidReason", convertToRepresentation(voidable.getVoidReason(), Representation.DEFAULT));
			}
		}
		
		return ret;
	}

	/**
	 * <strong>Should</strong> return delegating resource description
	 */
	public static DelegatingResourceDescription getCustomRepresentationDescription(CustomRepresentation representation) {
		DelegatingResourceDescription desc = new DelegatingResourceDescription();

		String def = representation.getRepresentation();
		def = def.startsWith("(") ? def.substring(1) : def;
		def = def.endsWith(")") ? def.substring(0, def.length() - 1) : def;

		int startIndex = 0;
		List<String> properties = new ArrayList<String>();
		int nestingLevel = 0;
		for (int i=0; i < def.length(); i++) {
			char c = def.charAt(i);
			if (c == '(') {
				nestingLevel++;
			}
			else if (c == ')') {
				nestingLevel--;
			}
			else if (c == ',' && nestingLevel == 0) {
				properties.add(def.substring(startIndex, i));
				startIndex = i + 1;
			}
		}
		properties.add(def.substring(startIndex));

		for (String propertyDefinition : properties) {
			if (propertyDefinition.contains(":")) {
				String[] propertyAndRepresentation = propertyDefinition.split(":", 2);
				String property = propertyAndRepresentation[0];
				String rep = propertyAndRepresentation[1];
				Representation r;
				if (rep.startsWith("(")) {
					r = new CustomRepresentation(rep);
				}
				else {
					r = rep.equalsIgnoreCase("REF") ? REF : rep.equalsIgnoreCase("FULL") ? FULL : DEFAULT;
				}
				desc.addProperty(property, r);
			}
			else {
				if (propertyDefinition.equals("links")) {
					desc.addSelfLink();
					desc.addLink("default", ".?v=" + RestConstants.REPRESENTATION_DEFAULT);
				}
				else {
					desc.addProperty(propertyDefinition);
				}
			}
		}

		return desc;
	}
}
