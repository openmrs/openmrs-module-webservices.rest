/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.docs.swagger;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.openmrs.module.webservices.rest.web.representation.Representation.DEFAULT;
import static org.openmrs.module.webservices.rest.web.representation.Representation.FULL;
import static org.openmrs.module.webservices.rest.web.representation.Representation.REF;

/**
 * This class provides methods to dynamically generate schemas for GET, CREATE, and UPDATE operations
 * based on the resource's representations and properties. It maps Java types and OpenMRS-specific
 * resource definitions to Swagger-compatible schema definitions.
 */
public class SwaggerGenerationUtil {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerGenerationUtil.class);

    private static final Map<Class<?>, DelegatingResourceHandler<?>> resourceHandlers = new HashMap<Class<?>, DelegatingResourceHandler<?>>();

    public static void addResourceHandler(DelegatingResourceHandler<?> resourceHandler) {
        resourceHandlers.put(resourceHandler.getClass(), resourceHandler);
    }

    /**
     * Generates the model for GET operations.
     */
    public static Model generateGETModel(DelegatingResourceHandler<?> resourceHandler, Representation representation) {
        ModelImpl model = new ModelImpl();

        if (representation.equals(DEFAULT)) {
            model = addDefaultProperties(resourceHandler);
        } else if (representation.equals(REF)) {
            model = addRefProperties(resourceHandler);
        } else if (representation.equals(FULL)) {
            model = addFullProperties(resourceHandler);
        } else {
            throw new IllegalArgumentException("Unsupported representation: " + representation);
        }

        return model;
    }

    /**
     * Generates the model for CREATE operations.
     */
    public static Model generateCREATEModel(DelegatingResourceHandler<?> resourceHandler, Representation representation) {
        ModelImpl model = new ModelImpl();

        if (representation.equals(DEFAULT)) {
            model = addCreatableProperties(resourceHandler, "Create");
        } else if (representation.equals(FULL)) {
            model = addCreatableProperties(resourceHandler, "CreateFull");
        } else {
            throw new IllegalArgumentException("Unsupported representation: " + representation);
        }

        return model;
    }

    /**
     * Generates the model for UPDATE operations.
     */
    public static Model generateUPDATEModel(DelegatingResourceHandler<?> resourceHandler, Representation representation) {
        ModelImpl model = new ModelImpl();

        if (representation.equals(DEFAULT)) {
            model = addUpdatableProperties(resourceHandler);
        } else {
            throw new IllegalArgumentException("Unsupported representation: " + representation);
        }

        return model;
    }

    /**
     * Adds creatable properties to the schema based on the resource handler's creatable properties.
     */
    private static ModelImpl addCreatableProperties(DelegatingResourceHandler<?> resourceHandler, String operationType) {
        ModelImpl model = new ModelImpl();
        addResourceHandler(resourceHandler);
        DelegatingResourceDescription description = resourceHandler.getCreatableProperties();
        if (description != null) {
            for (String property : description.getProperties().keySet()) {
                model.property(property, determinePropertyForField(resourceHandler, property, operationType));
            }
        }

        return model;
    }

    /**
     * Adds updatable properties to the schema based on the resource handler's updatable properties.
     */
    private static ModelImpl addUpdatableProperties(DelegatingResourceHandler<?> resourceHandler) {
        ModelImpl model = new ModelImpl();
        addResourceHandler(resourceHandler);
        DelegatingResourceDescription description = resourceHandler.getUpdatableProperties();
        if (description != null) {
            for (String property : description.getProperties().keySet()) {
                model.property(property, determinePropertyForField(resourceHandler, property, "Update"));
            }
        }

        return model;
    }

    /**
     * Adds default properties to the schema based on the resource handler's DEFAULT representation.
     */
    private static ModelImpl addDefaultProperties(DelegatingResourceHandler<?> resourceHandler) {
        ModelImpl model = new ModelImpl();
        addResourceHandler(resourceHandler);
        model.property("uuid", new StringProperty().description("Unique identifier of the resource"));
        model.property("display", new StringProperty().description("Display name of the resource"));

        DelegatingResourceDescription description = resourceHandler.getRepresentationDescription(DEFAULT);
        if (description != null) {
            for (String property : description.getProperties().keySet()) {
                model.property(property, determinePropertyForField(resourceHandler, property, "Get"));
            }
        }

        return model;
    }

    /**
     * Adds reference properties to the schema based on the resource handler's REF representation.
     */
    private static ModelImpl addRefProperties(DelegatingResourceHandler<?> resourceHandler) {
        ModelImpl model = new ModelImpl();
        addResourceHandler(resourceHandler);
        model.property("uuid", new StringProperty().description("Unique identifier of the resource"));
        model.property("display", new StringProperty().description("Display name of the resource"));

        DelegatingResourceDescription description = resourceHandler.getRepresentationDescription(REF);
        if (description != null) {
            for (String property : description.getProperties().keySet()) {
                model.property(property, determinePropertyForField(resourceHandler, property, "GetRef"));
            }
        }

        return model;
    }

    /**
     * Adds full properties to the schema based on the resource handler's FULL representation.
     */
    private static ModelImpl addFullProperties(DelegatingResourceHandler<?> resourceHandler) {
        ModelImpl model = new ModelImpl();
        addResourceHandler(resourceHandler);

        DelegatingResourceDescription description = resourceHandler.getRepresentationDescription(FULL);
        if (description != null) {
            for (String property : description.getProperties().keySet()) {
                model.property(property, determinePropertyForField(resourceHandler, property, "GetFull"));
            }
        }

        return model;
    }

    /**
     * Determines the property for a field based on the resource handler and property name.
     */
    public static Property determinePropertyForField(DelegatingResourceHandler<?> resourceHandler, String propertyName, String operationType) {
        Class<?> genericType = getGenericType(resourceHandler.getClass());
        if (genericType == null) {
            // Worst case scenario, no parameterized superclass / interface found in the class hierarchy
            throw new IllegalArgumentException("No generic type for resource handler");
        }

        try {
            Field field = genericType.getDeclaredField(propertyName);
            return createPropertyForType(field.getType(), operationType, field);
        } catch (NoSuchFieldException e) {
            logger.warn("Field {} not found in class {}", propertyName, genericType.getName());
            return new StringProperty();
        }
    }

    /**
     * Maps Java types to their corresponding Swagger properties.
     */
    @SuppressWarnings("unchecked")
    public static Property createPropertyForType(Class<?> type, String operationType, Field field) {
        if (String.class.equals(type)) {
            return new StringProperty();
        } else if (Integer.class.equals(type) || int.class.equals(type)) {
            return new IntegerProperty();
        } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            return new BooleanProperty();
        } else if (UUID.class.equals(type)) {
            return new StringProperty().description("uuid");
        } else if (java.util.Date.class.equals(type)) {
            return new DateProperty();
        } else if (Double.class.equals(type)) {
            return new DoubleProperty();
        } else if (isOpenMRSResource(type)) {
            if (type.isEnum()) {
                return new EnumProperty((Class<? extends Enum<?>>) type);
            } else {
                String resourceName = getResourceNameBySupportedClass(type);
                if (resourceName == null) {
                    return new StringProperty();
                }
                return new RefProperty("#/definitions/" + StringUtils.capitalize(resourceName) + operationType);
            }
        }  else if (Set.class.equals(type) || List.class.equals(type)) {
            Class<?> elementType = getGenericTypeFromField(field);
            if (isOpenMRSResource(elementType)) {
                String resourceName = getSubResourceNameBySupportedClass(elementType);
                if (resourceName == null) {
                    return new StringProperty();
                }
                return new ArrayProperty(new RefProperty("#/definitions/" + StringUtils.capitalize(resourceName) + operationType));
            }
            return new ArrayProperty();
        } else {
            return new ObjectProperty();
        }
    }

    /**
     * Retrieves the name of a resource or sub-resource associated with a given class.
     */
    public static String getResourceNameBySupportedClass(Class<?> supportedClass) {
        for (DelegatingResourceHandler<?> resourceHandler : resourceHandlers.values()) {
            Resource annotation = resourceHandler.getClass().getAnnotation(Resource.class);
            SubResource subResourceAnnotation = resourceHandler.getClass().getAnnotation(SubResource.class);

            if (annotation != null && annotation.supportedClass().equals(supportedClass)) {
                return annotation.name().substring(annotation.name().indexOf('/') + 1);
            } else if (subResourceAnnotation != null && subResourceAnnotation.supportedClass().equals(supportedClass)) {
                Resource parentResourceAnnotation = subResourceAnnotation.parent().getAnnotation(Resource.class);

                String resourceName = subResourceAnnotation.path();
                String resourceParentName = parentResourceAnnotation.name().substring(
                        parentResourceAnnotation.name().indexOf('/') + 1);

                String combinedName = capitalize(resourceParentName) + capitalize(resourceName);
                return combinedName.replace("/", "");
            }
        }
        return null;
    }

    public static String getSubResourceNameBySupportedClass(Class<?> supportedClass) {
        List<DelegatingResourceHandler<?>> resourceHandlers = Context.getService(RestService.class).getResourceHandlers();
        for (DelegatingResourceHandler<?> resourceHandler : resourceHandlers) {
            if (resourceHandler == null) {
                return null;
            }

            Resource annotation = resourceHandler.getClass().getAnnotation(Resource.class);
            SubResource subResourceAnnotation = resourceHandler.getClass().getAnnotation(SubResource.class);

            if (annotation != null && annotation.supportedClass().equals(supportedClass)) {
                return annotation.name().substring(annotation.name().indexOf('/') + 1);
            } else if (subResourceAnnotation != null && subResourceAnnotation.supportedClass().equals(supportedClass)) {
                Resource parentResourceAnnotation = subResourceAnnotation.parent().getAnnotation(Resource.class);

                String resourceName = subResourceAnnotation.path();
                String resourceParentName = parentResourceAnnotation.name().substring(
                        parentResourceAnnotation.name().indexOf('/') + 1);

                String combinedName = capitalize(resourceParentName) + capitalize(resourceName);
                return combinedName.replace("/", "");
            }
        }
        return null;
    }

    public static String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Checks whether a class is an OpenMRS resource (e.g., references an OpenMRS data object).
     */
    private static boolean isOpenMRSResource(Class<?> type) {
        if (type == null) {
            return false;
        }

        Package pkg = type.getPackage();
        return pkg != null && pkg.getName().startsWith("org.openmrs");
    }

    public static String getModelName(String qualifiedName) {
        if (qualifiedName == null || !qualifiedName.contains(".")) {
            return qualifiedName;
        }

        String simpleName = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        simpleName = simpleName.replace("$", "");
        return simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1);
    }

    /**
     * Extracts the generic type argument of a field that represents a parameterized collection (e.g., List<T>, Set<T>).
     * If the field is not parameterized or the generic type cannot be determined, it returns {@code null}.
     */
    private static Class<?> getGenericTypeFromField(Field field) {
        try {
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                    return (Class<?>) typeArguments[0];
                }
            }
        } catch (Exception e) {
            logger.warn("Could not determine the generic type for field: {}. This may not affect functionality.", field.getName(), e);
        }
        return null;
    }

    /**
     * Extracts the generic type parameter from a specified class or its superclasses
     * that implement a parameterized interface or extend a parameterized class.
     * <pre>{@code
     * Class<?> genericType = GenericTypeUtils.getGenericType(PatientResource1_8.class);
     * System.out.println(genericType); // Output: class Patient
     * }</pre>
     *
     * @param resourceHandlerClass the class implementing or extending a parameterized type
     * @return the {@link Class} representing the generic type parameter, or {@code null}
     *         if the generic type cannot be determined
     */
    public static Class<?> getGenericType(Class<?> resourceHandlerClass) {
        Class<?> currentClass = resourceHandlerClass;

        while (currentClass != null) {
            Type[] genericInterfaces = currentClass.getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();

                    if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?>) {
                        return (Class<?>) typeArguments[0];
                    }
                }
            }

            Type genericSuperclass = currentClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();

                if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?>) {
                    return (Class<?>) typeArguments[0];
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return null;
    }
}
