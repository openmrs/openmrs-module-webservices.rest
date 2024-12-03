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

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.module.webservices.docs.swagger.SwaggerGenerationUtil;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SwaggerGenerationUtilTest extends BaseModuleWebContextSensitiveTest {

    @Test
    public void genericType_shouldReturnClassFromParameterizedSuperclass() {
        class StringHandler extends BaseHandler<String> {}
        Class<?> result = SwaggerGenerationUtil.getGenericType(StringHandler.class);

        assertEquals(String.class, result);
    }

    @Test
    public void genericType_shouldReturnTheFirstParameterOfDelegatingSubResourceForSubResource() {
        Class<?> genericType = SwaggerGenerationUtil.getGenericType(SampleSubResourceHandler.class);

        assertNotNull(genericType);
        assertEquals(SampleSubResource.class, genericType);
    }

    @Test
    public void genericType_shouldReturnClassFromParameterizedInterface() {
        class InterfaceHandler implements ParameterizedInterface<Integer> {}
        Class<?> result = SwaggerGenerationUtil.getGenericType(InterfaceHandler.class);

        assertEquals(Integer.class, result);
    }

    @Test
    public void genericType_shouldReturnNullWhenGivenNonGenericClass() {
        class NonGenericHandler {}
        Class<?> result = SwaggerGenerationUtil.getGenericType(NonGenericHandler.class);
        assertNull(result);
    }

    @Test
    public void genericType_shouldReturnNullWhenGivenNullClass() {
        Class<?> result = SwaggerGenerationUtil.getGenericType(null);
        assertNull(result);
    }

    @Test
    public void genericType_shouldReturnClassFromDeepInheritance() {
        class IntermediateHandler extends BaseHandler<Double> {}
        class ConcreteHandler extends IntermediateHandler {}

        Class<?> result = SwaggerGenerationUtil.getGenericType(ConcreteHandler.class);
        assertEquals(Double.class, result);
    }

    @Test
    public void determinePropertyForField_shouldReturnStringPropertyWhenGivenValidStringField() {
        DelegatingResourceHandler<SampleResource> resourceHandler = new SampleResourceHandler();
        Property property = SwaggerGenerationUtil.determinePropertyForField(resourceHandler, "name", "GET");

        assertTrue(property instanceof StringProperty);
    }

    @Test
    public void determinePropertyForField_shouldReturnIntegerPropertyWhenGivenValidIntegerField() {
        DelegatingResourceHandler<SampleResource> resourceHandler = new SampleResourceHandler();
        Property property = SwaggerGenerationUtil.determinePropertyForField(resourceHandler, "age", "GET");

        assertTrue(property instanceof IntegerProperty);
    }

    @Test
    public void determinePropertyForField_shouldReturnUnknownWhenGivenNonExistentFieldUnknownField() {
        DelegatingResourceHandler<SampleResource> resourceHandler = new SampleResourceHandler();
        Property property = SwaggerGenerationUtil.determinePropertyForField(resourceHandler, "nonExistentField", "GET");

        assertTrue(property instanceof StringProperty);
    }

    @Test
    public void generateGETModel_shouldGenerateGETModelWhenGivenDefaultRepresentation() {
        DelegatingResourceHandler<SampleResource> resourceHandler = new SampleResourceHandler();
        Model model = SwaggerGenerationUtil.generateGETModel(resourceHandler, Representation.DEFAULT);

        assertTrue(model instanceof ModelImpl);

        Map<String, Property> propertyMap = model.getProperties();

        assertTrue(propertyMap.containsKey("name"));
        assertTrue(propertyMap.containsKey("age"));
        assertTrue(propertyMap.containsKey("isActive"));
        assertTrue(propertyMap.containsKey("action"));

        assertTrue(propertyMap.get("name") instanceof StringProperty);
        assertTrue(propertyMap.get("age") instanceof IntegerProperty);
        assertTrue(propertyMap.get("isActive") instanceof BooleanProperty);
        assertTrue(propertyMap.get("action") instanceof EnumProperty);
    }

    @Test
    public void generateGETModel_shouldGenerateGETModelWhenGivenRefRepresentation() {
        DelegatingResourceHandler<SampleResource> resourceHandler = new SampleResourceHandler();
        Model model = SwaggerGenerationUtil.generateGETModel(resourceHandler, Representation.REF);

        assertTrue(model instanceof ModelImpl);

        Map<String, Property> propertyMap = model.getProperties();

        assertTrue(propertyMap.containsKey("name"));
        assertTrue(propertyMap.containsKey("age"));
        assertNotEquals(propertyMap.containsKey("isActive"), true);
        assertNotEquals(propertyMap.containsKey("action"), true);

        assertTrue(propertyMap.get("name") instanceof StringProperty);
        assertTrue(propertyMap.get("age") instanceof IntegerProperty);
    }

    @Test
    public void generateCREATEModel_shouldGenerateCREATEModelWhenGivenDefaultRepresentation() {
        DelegatingResourceHandler<SampleResource> resourceHandler = new SampleResourceHandler();
        Model model = SwaggerGenerationUtil.generateCREATEModel(resourceHandler, Representation.DEFAULT);

        assertTrue(model instanceof ModelImpl);
        Map<String, Property> propertyMap = model.getProperties();

        assertTrue(propertyMap.containsKey("name"));
        assertTrue(propertyMap.containsKey("age"));
        assertTrue(propertyMap.containsKey("isActive"));
        assertTrue(propertyMap.containsKey("action"));

        assertTrue(propertyMap.get("name") instanceof StringProperty);
        assertTrue(propertyMap.get("age") instanceof IntegerProperty);
        assertTrue(propertyMap.get("isActive") instanceof BooleanProperty);
        assertTrue(propertyMap.get("action") instanceof EnumProperty);
    }

    @Test
    public void generateCREATEModel_shouldGenerateCREATEModelWhenGivenFullRepresentation() {
        DelegatingResourceHandler<SampleResource> resourceHandler = new SampleResourceHandler();
        Model model = SwaggerGenerationUtil.generateCREATEModel(resourceHandler, Representation.FULL);

        assertTrue(model instanceof ModelImpl);
        Map<String, Property> propertyMap = model.getProperties();

        assertTrue(propertyMap.containsKey("name"));
        assertTrue(propertyMap.containsKey("age"));
        assertTrue(propertyMap.containsKey("isActive"));
        assertTrue(propertyMap.containsKey("action"));

        assertTrue(propertyMap.get("name") instanceof StringProperty);
        assertTrue(propertyMap.get("age") instanceof IntegerProperty);
        assertTrue(propertyMap.get("isActive") instanceof BooleanProperty);
        assertTrue(propertyMap.get("action") instanceof EnumProperty);
    }

    @Test
    public void generateUPDATEModel_shouldGenerateUPDATEModelWhenGivenDefaultRepresentation() {
        DelegatingResourceHandler<SampleResource> resourceHandler = new SampleResourceHandler();
        Model model = SwaggerGenerationUtil.generateUPDATEModel(resourceHandler, Representation.DEFAULT);

        assertTrue(model instanceof ModelImpl);
        Map<String, Property> propertyMap = model.getProperties();

        assertTrue(propertyMap.containsKey("name"));
        assertTrue(propertyMap.containsKey("age"));
        assertTrue(propertyMap.containsKey("isActive"));
        assertTrue(propertyMap.containsKey("action"));

        assertTrue(propertyMap.get("name") instanceof StringProperty);
        assertTrue(propertyMap.get("age") instanceof IntegerProperty);
        assertTrue(propertyMap.get("isActive") instanceof BooleanProperty);
        assertTrue(propertyMap.get("action") instanceof EnumProperty);
    }

    @Test
    public void createPropertyForType_shouldReturnListOfEnumsWhenGivenAnOutterNestedEnum() throws NoSuchFieldException {
        Field actionField = SampleResourceEnum.class.getDeclaredField("sampleResourceOutterEnum");
        Property property = SwaggerGenerationUtil.createPropertyForType(actionField.getType(), "Get", actionField);

        assertTrue(property instanceof StringProperty);
        StringProperty stringProperty = (StringProperty) property;
        assertNotNull(stringProperty.getEnum());

        assertTrue(stringProperty.getEnum().contains("CREATE"));
        assertTrue(stringProperty.getEnum().contains("PATCH"));
        assertTrue(stringProperty.getEnum().contains("UPDATE"));
    }

    @Test
    public void createPropertyForType_shouldReturnListOfEnumsWhenGivenAnInnerNestedEnum() throws NoSuchFieldException {
        Field actionField = SampleResourceEnum.class.getDeclaredField("sampleResourceInnerEnum");
        Property property = SwaggerGenerationUtil.createPropertyForType(
                actionField.getType(), "Create", actionField);

        assertTrue(property instanceof StringProperty);
        StringProperty stringProperty = (StringProperty) property;
        assertNotNull(stringProperty.getEnum());

        assertTrue(stringProperty.getEnum().contains("SCHEDULETASK"));
        assertTrue(stringProperty.getEnum().contains("SHUTDOWNTASK"));
    }

    @Test
    public void createPropertyForType_shouldReturnAnArrayPropertyWithNoRefPropertyWhenFieldIsAList() throws NoSuchFieldException {
        Field attributesField = User.class.getDeclaredField("proficientLocales");
        Property property = SwaggerGenerationUtil.createPropertyForType(
                attributesField.getType(), "GetRef", attributesField);
        System.out.println("prop" + property);

        assertTrue(property instanceof ArrayProperty);
        ArrayProperty arrayProperty = (ArrayProperty) property;
        assertNotEquals(arrayProperty.getItems() instanceof RefProperty, true);
    }

    //classes to be used in this test class
    public static class BaseHandler<T> {}

    public interface ParameterizedInterface<T> {}

    static class SampleResource {
        private String name;
        private int age;
        private boolean isActive;
        private SampleResourceEnum.SampleResourceInnerEnum action;
    }

    static class SampleSubResource {
        private String subName;
        private String subAge;
    }

    public enum SampleResourceOutterEnum {
        CREATE, PATCH, UPDATE;
    }

    public static class SampleResourceEnum {

        public enum SampleResourceInnerEnum {
            SCHEDULETASK, SHUTDOWNTASK, RESCHEDULETASK, RESCHEDULEALLTASKS, DELETE, RUNTASK;
        }

        private SampleResourceInnerEnum sampleResourceInnerEnum;

        private SampleResourceOutterEnum sampleResourceOutterEnum;

        public SampleResourceInnerEnum getAction() {
            return sampleResourceInnerEnum;
        }

        public void setAction(SampleResourceInnerEnum sampleResourceInnerEnum) {
            this.sampleResourceInnerEnum = sampleResourceInnerEnum;
        }
    }

    // Resource Handler for Sub Resource
    static class SampleSubResourceHandler extends DelegatingSubResource<SampleSubResource, SampleResource, SampleResourceHandler> {
        @Override
        public SampleResource getParent(SampleSubResource instance) {
            return null;
        }

        @Override
        public void setParent(SampleSubResource instance, SampleResource parent) {

        }

        @Override
        public PageableResult doGetAll(SampleResource parent, RequestContext context) throws ResponseException {
            return null;
        }

        @Override
        public SampleSubResource getByUniqueId(String uniqueId) {
            return null;
        }

        @Override
        protected void delete(SampleSubResource delegate, String reason, RequestContext context) throws ResponseException {

        }

        @Override
        public SampleSubResource newDelegate() {
            return null;
        }

        @Override
        public SampleSubResource save(SampleSubResource delegate) {
            return null;
        }

        @Override
        public void purge(SampleSubResource delegate, RequestContext context) throws ResponseException {

        }

        @Override
        public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
            return null;
        }
    }

    // Resource Handler for Parent Class
    static class SampleResourceHandler extends DelegatingCrudResource<SampleResource> {
        @Override
        public SampleResource getByUniqueId(String uniqueId) {
            return null;
        }

        @Override
        protected void delete(SampleResource delegate, String reason, RequestContext context) throws ResponseException {

        }

        @Override
        public SampleResource newDelegate() {
            return null;
        }

        @Override
        public SampleResource save(SampleResource delegate) {
            return null;
        }

        @Override
        public void purge(SampleResource delegate, RequestContext context) throws ResponseException {

        }

        @Override
        public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
                description.addProperty("name");
                description.addProperty("age");
                description.addProperty("isActive");
                description.addProperty("action");
            } else if (rep instanceof RefRepresentation) {
                description.addProperty("name");
                description.addProperty("age");
                description.addSelfLink();
            }
            return description;
        }

        @Override
        public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("name");
            description.addProperty("age");
            description.addProperty("isActive");
            description.addProperty("action");
            return description;
        }

        @Override
        public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
            return getCreatableProperties();
        }
    }
}
