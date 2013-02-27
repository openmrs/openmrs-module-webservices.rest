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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.util.ReflectionUtil;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.Hyperlink;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.util.ReflectionUtils;

/**
 * A base implementation of a resource or sub-resource that delegates operations to a wrapped
 * object. Implementations generally should extend either {@link DelegatingCrudResource} or
 * {@link DelegatingSubResource} rather than this class directly.
 * 
 * @param <T> the class we're delegating to
 */
public abstract class BaseDelegatingResource<T> implements Converter<T>, Resource, DelegatingResourceHandler<T> {
	
	private final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Properties that should silently be ignored if you try to get them. Implementations should
	 * generally configure this property with a list of properties that were added to their
	 * underlying domain object after the minimum OpenMRS version required by this module. For
	 * example PatientIdentifierTypeResource will allow "locationBehavior" to be missing, since it
	 * wasn't added to PatientIdentifierType until OpenMRS 1.9. delegate class
	 */
	protected Set<String> allowedMissingProperties = new HashSet<String>();
	
	/**
	 * Implementations should define mappings for properties that they want to expose with other
	 * names. (Map from the exposed property name to the actual property name.)
	 */
	protected Map<String, String> remappedProperties = new HashMap<String, String>();
	
	/**
	 * If this resource represents a class hierarchy (rather than a single class), this will hold
	 * handlers for each subclass
	 */
	protected volatile List<DelegatingSubclassHandler<T, ? extends T>> subclassHandlers;
	
	/**
	 * All our resources support letting modules register subclass handlers. If any are registered,
	 * then the resource represents a class hierarchy, e.g. requiring a "type" parameter when
	 * creating a new instance.
	 * 
	 * @return whether there are any subclass handlers registered with this resource
	 */
	public boolean hasTypesDefined() {
		return subclassHandlers != null && subclassHandlers.size() > 0;
	}
	
	/**
	 * This will be automatically called with the first call to {@link #getSubclassHandler(Class)} or {@link #getSubclassHandler(String)}.
	 * It finds all subclass handlers intented for this resource, and registers them.
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public void init() {
		List<DelegatingSubclassHandler<T, ? extends T>> tmpSubclassHandlers = new ArrayList<DelegatingSubclassHandler<T, ? extends T>>();
		
		List<DelegatingSubclassHandler> handlers = Context.getRegisteredComponents(DelegatingSubclassHandler.class);
		for (DelegatingSubclassHandler handler : handlers) {
			Class forDelegateClass = ReflectionUtil.getParameterizedTypeFromInterface(handler.getClass(),
			    DelegatingSubclassHandler.class, 0);
			Resource resourceForHandler = Context.getService(RestService.class)
			        .getResourceBySupportedClass(forDelegateClass);
			if (getClass().equals(resourceForHandler.getClass())) {
				tmpSubclassHandlers.add(handler);
			}
		}
		
		subclassHandlers = tmpSubclassHandlers;
	}
	
	/**
	 * Registers the given subclass handler.
	 * 
	 * @param handler
	 */
	public void registerSubclassHandler(DelegatingSubclassHandler<T, ? extends T> handler) {
		if (subclassHandlers == null) {
			init();
		}
		for (DelegatingSubclassHandler<T, ? extends T> current : subclassHandlers) {
			if (current.getClass().equals(handler.getClass())) {
				log.info("Tried to register a subclass handler, but the class is already registered: " + handler.getClass());
				return;
			}
		}
		subclassHandlers.add(handler);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants.PROPERTY_FOR_RESOURCE_VERSION_DEFAULT_VALUE;
	}
	
	/**
	 * @return the value of the {@link org.openmrs.module.webservices.rest.web.annotation.Resource}
	 *         annotation on the concrete subclass
	 */
	protected String getResourceName() {
		org.openmrs.module.webservices.rest.web.annotation.Resource ann = getClass().getAnnotation(
		    org.openmrs.module.webservices.rest.web.annotation.Resource.class);
		if (ann == null)
			throw new RuntimeException("There is no " + Resource.class + " annotation on " + getClass());
		if (StringUtils.isEmpty(ann.name()))
			throw new RuntimeException(Resource.class.getSimpleName() + " annotation on " + getClass()
			        + " must specify a name");
		return ann.name();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#newInstance(java.lang.String)
	 */
	@Override
	public T newInstance(String type) {
		if (hasTypesDefined()) {
			if (type == null)
				throw new IllegalArgumentException(getClass().getSimpleName() + " requires a '"
				        + RestConstants.PROPERTY_FOR_TYPE + "' property to create a new object");
			DelegatingResourceHandler<? extends T> handler = getResourceHandler(type);
			return handler.newDelegate();
		} else {
			return newDelegate();
		}
	}
	
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
	 * Gets a description of resource's properties which can be set on creation.
	 * 
	 * @return the description
	 * @throws ResponseException
	 */
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * Gets a description of resource's properties which can be edited.
	 * <p>
	 * By default delegates to {@link #getCreatableProperties()} and removes sub-resources returned
	 * by {@link #getPropertiesToExposeAsSubResources()}.
	 * 
	 * @return the description
	 * @throws ResponseException
	 */
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = getCreatableProperties();
		for (String property : getPropertiesToExposeAsSubResources()) {
			description.getProperties().remove(property);
		}
		return description;
	}
	
	/**
	 * Implementations should override this method if they support sub-resources
	 * 
	 * @return a list of properties available as sub-resources or an empty list
	 */
	public List<String> getPropertiesToExposeAsSubResources() {
		return Collections.emptyList();
	}
	
	/**
	 * Implementations should override this method if T is not uniquely identified by a "uuid"
	 * property.
	 * 
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
	 * @should return valid RefRepresentation
	 * @should return valid DefaultRepresentation
	 * @should return valid FullRepresentation
	 */
	@Override
	public SimpleObject asRepresentation(T delegate, Representation representation) throws ConversionException {
		if (delegate == null)
			throw new NullPointerException();
		
		DelegatingResourceHandler<? extends T> handler = getResourceHandler(delegate);
		
		// first call getRepresentationDescription()
		DelegatingResourceDescription repDescription = handler.getRepresentationDescription(representation);
		if (repDescription != null) {
			SimpleObject simple = convertDelegateToRepresentation(delegate, repDescription);
			
			maybeDecorateWithType(simple, delegate);
			decorateWithResourceVersion(simple, representation);
			
			return simple;
		}
		
		// otherwise look for a method annotated to handle this representation
		Method meth = findAnnotatedMethodForRepresentation(handler.getClass(), representation);
		if (meth != null) {
			try {
				// TODO verify that the method takes 1 or 2 parameters
				SimpleObject simple;
				if (meth.getParameterTypes().length == 1)
					simple = (SimpleObject) meth.invoke(handler, delegate);
				else
					simple = (SimpleObject) meth.invoke(handler, delegate, representation);
				
				maybeDecorateWithType(simple, delegate);
				decorateWithResourceVersion(simple, representation);
				
				return simple;
			}
			catch (Exception ex) {
				throw new ConversionException(null, ex);
			}
		}
		
		// finally if it is a custom representation and not supported by any other handler
		if (representation instanceof CustomRepresentation) {
			repDescription = getCustomRepresentationDescription((CustomRepresentation) representation);
			if (repDescription != null) {
				SimpleObject simple = convertDelegateToRepresentation(delegate, repDescription);
				
				return simple;
			}
		}
		
		throw new ConversionException("Don't know how to get " + getClass().getSimpleName() + "(" + delegate.getClass()
		        + ") as " + representation.getRepresentation(), null);
	}
	
	private DelegatingResourceDescription getCustomRepresentationDescription(CustomRepresentation representation) {
		DelegatingResourceDescription desc = new DelegatingResourceDescription();
		
		String def = representation.getRepresentation();
		def = def.substring(1, def.length() - 1); //remove '(' and ')'
		String[] fragments = def.split(",");
		for (int i = 0; i < fragments.length; i++) {
			String[] field = fragments[i].split(":"); //split into field and representation
			if (field.length == 1) {
				desc.addProperty(field[0]);
			} else {
				String property = field[0];
				String rep = field[1];
				
				// if custom representation
				if (rep.startsWith("(")) {
					StringBuilder customRep = new StringBuilder();
					customRep.append(rep);
					int open = 1;
					for (i = i + 1; i < fragments.length; i++) {
						if (fragments[i].contains("(")) {
							open++;
						} else if (fragments[i].contains(")")) {
							open--;
						}
						
						customRep.append(",");
						customRep.append(fragments[i]);
						
						if (open == 0) {
							break;
						}
					}
					
					desc.addProperty(property, new CustomRepresentation(customRep.toString()));
				} else {
					rep = rep.toUpperCase(); //normalize
					if (rep.equals("REF")) {
						desc.addProperty(property, Representation.REF);
					} else if (rep.equals("FULL")) {
						desc.addProperty(property, Representation.FULL);
					} else if (rep.equals("DEFAULT")) {
						desc.addProperty(property, Representation.DEFAULT);
					}
				}
			}
		}
		
		return desc;
	}
	
	/**
	 * Sets resourceVersion to {@link #getResourceVersion()} for representations other than REF.
	 * 
	 * @param simple the simplified representation which will be decorated with the resource version
	 * @param representation the type of representation
	 */
	private void decorateWithResourceVersion(SimpleObject simple, Representation representation) {
		if (!(representation instanceof RefRepresentation)) {
			simple.put(RestConstants.PROPERTY_FOR_RESOURCE_VERSION, getResourceVersion());
		}
	}
	
	/**
	 * If this resource supports subclasses, then we add a type property to the input, and return it
	 * 
	 * @param simple simplified representation which will be decorated with the user-friendly type
	 *            name
	 * @param delegate the object that simple represents
	 */
	private void maybeDecorateWithType(SimpleObject simple, T delegate) {
		if (hasTypesDefined())
			simple.add(RestConstants.PROPERTY_FOR_TYPE, getTypeName(delegate));
	}
	
	/**
	 * If this resources supports subclasses, this method gets the user-friendly type name for the
	 * given subclass
	 * 
	 * @param subclass
	 * @return
	 */
	protected String getTypeName(Class<? extends T> subclass) {
		if (hasTypesDefined()) {
			DelegatingSubclassHandler<T, ? extends T> handler = getSubclassHandler(subclass);
			if (handler != null)
				return handler.getTypeName();
			if (newDelegate().getClass().equals(subclass)) {
				String resourceName = getResourceName();
				int lastSlash = resourceName.lastIndexOf("/");
				resourceName = resourceName.substring(lastSlash + 1);
				return resourceName;
			}
		}
		return null;
	}
	
	/**
	 * @see #getTypeName(Class)
	 */
	protected String getTypeName(T delegate) {
		return getTypeName((Class<? extends T>) delegate.getClass());
	}
	
	/**
	 * @param type user-friendly type name
	 * @return the actual java class for this type
	 */
	protected Class<? extends T> getActualSubclass(String type) {
		DelegatingSubclassHandler<T, ? extends T> handler = getSubclassHandler(type);
		if (handler != null)
			return handler.getSubclassHandled();
		// otherwise we need to return our own declared class
		return ReflectionUtil.getParameterizedTypeFromInterface(getClass(), DelegatingResourceHandler.class, 0);
	}
	
	/**
	 * @param type user-friendly type name
	 * @return a subclass handler if any is suitable for type, or this resource itself if it is
	 *         suitable
	 */
	protected DelegatingResourceHandler<? extends T> getResourceHandler(String type) {
		if (type == null || !hasTypesDefined())
			return this;
		DelegatingSubclassHandler<T, ? extends T> handler = getSubclassHandler(type);
		if (handler != null)
			return handler;
		if (getResourceName().endsWith(type))
			return this;
		throw new IllegalArgumentException("type=" + type + " is not handled by this resource (" + getClass()
		        + ") or any subclass");
	}
	
	/**
	 * Delegates to @see {@link #getResourceHandler(Class)}
	 */
	@SuppressWarnings("unchecked")
	protected DelegatingResourceHandler<? extends T> getResourceHandler(T delegate) {
		if (!hasTypesDefined())
			return this;
		if (delegate == null)
			return null;
		return getResourceHandler((Class<? extends T>) delegate.getClass());
	}
	
	/**
	 * @param clazz
	 * @return a subclass handler if any is suitable for the given class, or this resource itself if
	 *         no subclass handler works
	 */
	protected DelegatingResourceHandler<? extends T> getResourceHandler(Class<? extends T> clazz) {
		if (!hasTypesDefined())
			return this;
		DelegatingResourceHandler<? extends T> handler = getSubclassHandler(clazz);
		if (handler != null)
			return handler;
		return this;
	}
	
	/**
	 * @param subclass
	 * @return the handler most appropriate for the given subclass, or null if none is suitable
	 */
	protected DelegatingSubclassHandler<T, ? extends T> getSubclassHandler(Class<? extends T> subclass) {
		if (subclassHandlers == null) {
			init();
		}
		
		if (!hasTypesDefined())
			return null;
		// look for an exact match
		for (DelegatingSubclassHandler<T, ? extends T> handler : subclassHandlers) {
			Class<? extends T> subclassHandled = handler.getSubclassHandled();
			if (subclass.equals(subclassHandled))
				return handler;
		}
		
		// TODO should we recurse to subclass's superclass, e.g. so DrugOrderHandler can handle HivDrugOrder if no handler is defined?
		
		// didn't find anything suitable
		return null;
	}
	
	/**
	 * @param type the user-friendly name of a registered subclass handler
	 * @return the handler for the given user-friendly type name
	 */
	protected DelegatingSubclassHandler<T, ? extends T> getSubclassHandler(String type) {
		if (hasTypesDefined()) {
			if (subclassHandlers == null) {
				init();
			}
			for (DelegatingSubclassHandler<T, ? extends T> handler : subclassHandlers) {
				if (type.equals(handler.getTypeName()))
					return handler;
			}
		}
		return null;
	}
	
	protected SimpleObject convertDelegateToRepresentation(T delegate, DelegatingResourceDescription rep)
	        throws ConversionException {
		if (delegate == null)
			throw new NullPointerException();
		SimpleObject ret = new SimpleObject();
		for (Entry<String, Property> e : rep.getProperties().entrySet()) {
			ret.put(e.getKey(), e.getValue().evaluate(this, delegate));
		}
		List<Hyperlink> links = new ArrayList<Hyperlink>();
		for (Hyperlink link : rep.getLinks()) {
			if (link.getUri().startsWith(".")) {
				link = new Hyperlink(link.getRel(), getUri(delegate) + link.getUri().substring(1));
			}
			// If subresource add path to link
			SubResource sub = getClass().getAnnotation(SubResource.class);
			if (sub != null) {
				link.setResourcePath(sub.path());
			}
			links.add(link);
		}
		if (links.size() > 0)
			ret.put("links", links);
		return ret;
	}

	
	/**
	 * @param delegate
	 * @param propertiesToCreate
	 * @throws ResponseException
	 */
	protected void setConvertedProperties(T delegate, Map<String, Object> propertyMap,
	        DelegatingResourceDescription description, boolean mustIncludeRequiredProperties) throws ConversionException {
		Map<String, Property> allowedProperties = new LinkedHashMap<String, Property>(description.getProperties());
		
		//Set properties that are allowed to be changed or fail.
		Set<String> notAllowedProperties = new HashSet<String>();
		for (Map.Entry<String, Object> prop : propertyMap.entrySet()) {
			if (allowedProperties.remove(prop.getKey()) != null && prop.getValue() != null) {
				setProperty(delegate, prop.getKey(), prop.getValue());
			} else {
				notAllowedProperties.add(prop.getKey());
			}
		}
		if (!notAllowedProperties.isEmpty()) {
			throw new ConversionException("Some properties are not allowed to be set: "
			        + StringUtils.join(notAllowedProperties, ","));
		}
		
		if (mustIncludeRequiredProperties) {
			//Fail, if any required properties are missing.
			Set<String> missingProperties = new HashSet<String>();
			for (Entry<String, Property> prop : allowedProperties.entrySet()) {
				if (prop.getValue().isRequired()) {
					missingProperties.add(prop.getKey());
				}
			}
			if (!missingProperties.isEmpty()) {
				throw new ConversionException("Some required properties are missing: "
				        + StringUtils.join(missingProperties, ","));
			}
		}
	}
	
	/**
	 * Finds a method on clazz or a superclass that is annotated with {@link RepHandler} and is
	 * suitable for rep
	 * 
	 * @param clazz
	 * @param rep
	 * @return
	 */
	private Method findAnnotatedMethodForRepresentation(Class<?> clazz, Representation rep) {
		for (Method method : clazz.getMethods()) {
			RepHandler ann = method.getAnnotation(RepHandler.class);
			if (ann != null) {
				if (ann.name().equals(rep.getRepresentation()))
					return method;
				if (ann.value().isAssignableFrom(rep.getClass()))
					return method;
			}
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.util.ReflectionUtil#findMethod(Class, String)
	 */
	protected Method findMethod(String name) {
		// TODO replace this with something that looks specifically for a method that takes a single T argument
		Method ret = ReflectionUtil.findMethod(getClass(), name);
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#getProperty(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public Object getProperty(T instance, String propertyName) throws ConversionException {
		try {
			DelegatingResourceHandler<? extends T> handler = getResourceHandler((T) instance);
			
			// first, try to find a @PropertyGetter-annotated method
			Method annotatedGetter = findGetterMethod(handler, propertyName);
			if (annotatedGetter != null) {
				return annotatedGetter.invoke(handler, instance);
			}
			
			// next use standard bean methods
			// TODO remove remappedProperties, or make them work with subclass handlers
			String override = remappedProperties.get(propertyName);
			if (override != null)
				propertyName = override;
			return PropertyUtils.getProperty(instance, propertyName);
		}
		catch (Exception ex) {
			// some properties are allowed to be missing, since they may have been added in later OpenMRS versions
			if (allowedMissingProperties.contains(propertyName))
				return null;
			throw new ConversionException(propertyName, ex);
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#setProperty(java.lang.Object,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public void setProperty(Object instance, String propertyName, Object value) throws ConversionException {
		try {
			DelegatingResourceHandler<? extends T> handler;
			
			try {
				handler = getResourceHandler((T) instance);
			}
			catch (Exception e) {
				// this try/catch isn't really needed because of java erasure behaviour at run time.
				// but I'm putting in here just in case
				handler = this;
			}
			
			// first, try to find a @PropertySetter-annotated method
			Method annotatedSetter = findSetterMethod(handler, propertyName);
			if (annotatedSetter != null) {
				Type expectedType = annotatedSetter.getGenericParameterTypes()[1];
				value = ConversionUtil.convert(value, expectedType);
				annotatedSetter.invoke(handler, instance, value);
				return;
			}
			
			// next use standard bean methods
			// TODO remove remappedProperties, or make them work with subclass handlers
			String override = remappedProperties.get(propertyName);
			if (override != null)
				propertyName = override;
			
			// we need the generic type of this property, not just the class
			Method setter = PropertyUtils.getPropertyDescriptor(instance, propertyName).getWriteMethod();
			value = ConversionUtil.convert(value, setter.getGenericParameterTypes()[0]);
			
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
		catch (Exception ex) {
			throw new ConversionException(propertyName + " on " + instance.getClass(), ex);
		}
	}
	
	private Method findSetterMethod(DelegatingResourceHandler<? extends T> handler, String propName) {
		for (Method candidate : handler.getClass().getMethods()) {
			PropertySetter ann = candidate.getAnnotation(PropertySetter.class);
			if (ann != null && ann.value().equals(propName)) {
				return candidate;
			}
		}
		return null;
	}
	
	private Method findGetterMethod(DelegatingResourceHandler<? extends T> handler, String propName) {
		for (Method candidate : handler.getClass().getMethods()) {
			PropertyGetter ann = candidate.getAnnotation(PropertyGetter.class);
			if (ann != null && ann.value().equals(propName)) {
				return candidate;
			}
		}
		return null;
	}
	
	/**
	 * Removes any elements from the passed-in collection that aren't of the given type. This is a
	 * convenience method for subclass-aware resources that want to limit query results to a given
	 * type.
	 * 
	 * @param collection
	 * @param type a user-friendly type name
	 */
	protected void filterByType(Collection<T> collection, String type) {
		for (Iterator<T> i = collection.iterator(); i.hasNext();) {
			T instance = i.next();
			if (!getTypeName(instance).equals(type))
				i.remove();
		}
	}
	
	/**
	 * Convenience method that looks for a specific method on the subclass handler for the given
	 * type
	 * 
	 * @param type user-friendly type name
	 * @param methodName
	 * @param argumentTypes
	 * @return the indicated method if it exists, null otherwise
	 */
	protected Method findSubclassHandlerMethod(String type, String methodName, Class<?>... argumentTypes) {
		DelegatingSubclassHandler<T, ? extends T> handler = getSubclassHandler(type);
		if (handler == null)
			return null;
		try {
			Method method = handler.getClass().getMethod(methodName, argumentTypes);
			return method;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Convenience method that finds a specific method on the subclass handler for the given type,
	 * and invokes it
	 * 
	 * @param type user-friendly type name
	 * @param methodName
	 * @param arguments
	 * @return the result of invoking the indicated method, or null if the method wasn't found
	 */
	protected Object findAndInvokeSubclassHandlerMethod(String type, String methodName, Object... arguments) {
		Class<?>[] argumentTypes = new Class<?>[arguments.length];
		for (int i = 0; i < arguments.length; ++i) {
			argumentTypes[i] = arguments[i].getClass();
		}
		Method method = findSubclassHandlerMethod(type, methodName, argumentTypes);
		if (method == null)
			return null;
		try {
			DelegatingSubclassHandler<T, ? extends T> handler = getSubclassHandler(type);
			return method.invoke(handler, arguments);
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
//	public String getPath() {
//		org.openmrs.module.webservices.rest.web.annotation.Resource annot = getClass().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class);
//		return annot.name();
//	}

}
