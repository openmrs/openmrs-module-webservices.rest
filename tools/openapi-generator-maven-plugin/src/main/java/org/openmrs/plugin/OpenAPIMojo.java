/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.Artifact;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

@Mojo(name = "openapi", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class OpenAPIMojo extends AbstractMojo{
    
    private static final String TARGET_CLASS_NAME = "org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptResource1_8";
    
    private URLClassLoader classLoader;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("=== OPENAPI GENERATION STARTED ===");
        
        // Setup ClassLoader
        this.classLoader = setupProjectClassLoader();
        
        // CRITICAL: Disable OpenMRS Context to prevent static initialization errors
        disableOpenMRSContext();
        
        // Test RestConstants accessibility
        testRestConstantsAccessibility();
        
        // Load Target Resource Class
        Class<?> resourceClazz = loadClass(TARGET_CLASS_NAME);

        // Test PatientResource instance creation
        testPatientResourceInstantiation(resourceClazz);
        
        // Summary
        getLog().info("==============");
    }

    private URLClassLoader setupProjectClassLoader() {
        try {
            List<URL> urls = new ArrayList<>();
            
            // Add project classes
            String outputDirectory = project.getBuild().getOutputDirectory();
            File outputDir = new File(outputDirectory);
            if (outputDir.exists()) {
                urls.add(outputDir.toURI().toURL());
            }
            
            // Add all artifacts
            Set<Artifact> allArtifacts = project.getArtifacts();
            if (allArtifacts != null) {
                for (Artifact artifact : allArtifacts) {
                    File file = artifact.getFile();
                    if (file != null && file.exists()) {
                        urls.add(file.toURI().toURL());
                    }
                }
            }            
            getLog().info("ClassLoader setup complete: " + urls.size() + " URLs");
            return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup ClassLoader", e);
        }
    }

    private void disableOpenMRSContext() {
        getLog().info("=== Disabling OpenMRS Context for Build-Time Use ===");
        
        try {
            // Load RestUtil class and call disableContext()
            Class<?> restUtilClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.RestUtil");
            Method disableContextMethod = restUtilClass.getMethod("disableContext");
            disableContextMethod.invoke(null);
            
            getLog().info(" SUCCESS: OpenMRS Context disabled successfully");
            getLog().info("   RestUtil.contextEnabled is now false");
            getLog().info("   Static initializers will not attempt Context access");
            
        } catch (ClassNotFoundException e) {
            getLog().error(" FAILED: RestUtil class not found: " + e.getMessage());
            throw new RuntimeException("Cannot disable OpenMRS context", e);
        } catch (NoSuchMethodException e) {
            getLog().error(" FAILED: disableContext method not found: " + e.getMessage());
            throw new RuntimeException("Cannot disable OpenMRS context", e);
        } catch (Exception e) {
            getLog().error(" FAILED: Error disabling context: " + e.getMessage());
            throw new RuntimeException("Cannot disable OpenMRS context", e);
        }
    }

    private void testRestConstantsAccessibility() {
        getLog().info("=== Testing RestConstants Accessibility ===");
        
        try {
            // Test 1: Can we load RestConstants in our ClassLoader?
            Class<?> restConstants = classLoader.loadClass("org.openmrs.module.webservices.rest.web.RestConstants");
            getLog().info(" RestConstants class loaded successfully: " + restConstants.getName());
            
            // Test 2: Can we access static fields?
            Object version1 = restConstants.getField("VERSION_1").get(null);
            Object uriPrefix = restConstants.getField("URI_PREFIX").get(null);
            Object representationDefault = restConstants.getField("REPRESENTATION_DEFAULT").get(null);
            Object representationFull = restConstants.getField("REPRESENTATION_FULL").get(null);
            
            // Test 3: Are these values what we expect?
            getLog().info(" RestConstants.VERSION_1 = " + version1);
            getLog().info(" RestConstants.URI_PREFIX = " + uriPrefix);
            getLog().info(" RestConstants.REPRESENTATION_DEFAULT = " + representationDefault);
            getLog().info(" RestConstants.REPRESENTATION_FULL = " + representationFull);
            
            // Test 4: Verify expected values
            if ("v1".equals(version1)) {
                getLog().info(" VERSION_1 has expected value");
            } else {
                getLog().warn(" VERSION_1 unexpected value: " + version1);
            }
            
            if (uriPrefix != null && uriPrefix.toString().contains("/ws/rest")) {
                getLog().info(" URI_PREFIX contains expected path");
            } else {
                getLog().warn(" URI_PREFIX unexpected value: " + uriPrefix);
            }
            
            if ("default".equals(representationDefault)) {
                getLog().info(" REPRESENTATION_DEFAULT has expected value");
            } else {
                getLog().warn(" REPRESENTATION_DEFAULT unexpected value: " + representationDefault);
            }
            
            if ("full".equals(representationFull)) {
                getLog().info(" REPRESENTATION_FULL has expected value");
            } else {
                getLog().warn(" REPRESENTATION_FULL unexpected value: " + representationFull);
            }
            
            getLog().info(" SUCCESS: RestConstants is fully accessible at build time!");
            
        } catch (ClassNotFoundException e) {
            getLog().error(" FAILED: RestConstants class not found in ClassLoader");
            getLog().error("This means RestConstants is not in the classpath");
        } catch (NoSuchFieldException e) {
            getLog().error(" FAILED: RestConstants field not found: " + e.getMessage());
        } catch (Exception e) {
            getLog().error(" FAILED: RestConstants not accessible: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

        private Class<?> loadClass(String className) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            getLog().info("Loaded class: " + clazz.getName());
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class: " + className, e);
        }
    }   private void testPatientResourceInstantiation(Class<?> resourceClazz) {
    getLog().info("=== Testing PatientResource Instance Creation ===");
    
    try {
        getLog().info("Attempting to create PatientResource1_8 instance using normal constructor...");
        
        // Try normal constructor
        Object instance = resourceClazz.getDeclaredConstructor().newInstance();
        
        getLog().info(" SUCCESS! PatientResource1_8 instance created successfully!");
        getLog().info("Instance class: " + instance.getClass().getName());
        getLog().info("Instance toString: " + instance.toString());
        
        // Test if we can call a simple method
        testBasicMethodCall(instance);

        testGetRepresentationDescription(instance);
        
    } catch (ExceptionInInitializerError e) {
        getLog().error(" FAILED: Static initialization error");
        getLog().error("Root cause: " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
        getLog().error("This means parent classes have problematic static blocks");
        
    } catch (NoClassDefFoundError e) {
        getLog().error(" FAILED: Missing class dependency");
        getLog().error("Missing class: " + e.getMessage());
        getLog().error("This means some required classes aren't in the classpath");
        
    } catch (InstantiationException e) {
        getLog().error(" FAILED: Cannot instantiate class");
        getLog().error("Reason: " + e.getMessage());
        
    } catch (IllegalAccessException e) {
        getLog().error(" FAILED: Constructor not accessible");
        getLog().error("Reason: " + e.getMessage());
        
    } catch (Exception e) {
        getLog().error(" FAILED: Unexpected error during instantiation");
        getLog().error("Error type: " + e.getClass().getSimpleName());
        getLog().error("Error message: " + e.getMessage());
        e.printStackTrace();
    }
}

private void testBasicMethodCall(Object instance) {
    try {
        getLog().info("--- Testing Basic Method Calls ---");
        
        // Test toString (should always work)
        String toStringResult = instance.toString();
        getLog().info(" toString() works: " + toStringResult);
        
        // Test getClass (should always work)
        Class<?> instanceClass = instance.getClass();
        getLog().info(" getClass() works: " + instanceClass.getName());
        
        // List all methods available
        Method[] methods = instanceClass.getDeclaredMethods();
        getLog().info(" Found " + methods.length + " declared methods");
        
        // Look for getRepresentationDescription method
        for (Method method : methods) {
            if (method.getName().equals("getRepresentationDescription")) {
                getLog().info(" Found getRepresentationDescription method: " + method.toString());
            }
        }
        
    } catch (Exception e) {
        getLog().warn(" Basic method calls failed: " + e.getMessage());
    }
    }    private void testGetRepresentationDescription(Object instance) {
    try {
        getLog().info("=== Testing getRepresentationDescription Method ===");
        
        // First, verify RestConstants is still accessible in this context
        testRestConstantsInMethodContext();
        
        // Load representation classes
        Class<?> representationClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.representation.Representation");
        Class<?> defaultRepClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation");
        Class<?> fullRepClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.representation.FullRepresentation");
        
        // Create representation instances
        Object defaultRep = defaultRepClass.getDeclaredConstructor().newInstance();
        Object fullRep = fullRepClass.getDeclaredConstructor().newInstance();
        
        // Get the method
        Method method = instance.getClass().getMethod("getRepresentationDescription", representationClass);
        
        // Test DEFAULT representation
        Object defaultResult = method.invoke(instance, defaultRep);
        getLog().info(" DEFAULT result: " + defaultResult);
        
        // Test FULL representation  
        Object fullResult = method.invoke(instance, fullRep);
        getLog().info(" FULL result: " + fullResult);

        if (defaultResult != null) {
        extractSchemaFromDescription(defaultResult, "DEFAULT");
        }

        if (fullResult != null) {
        extractSchemaFromDescription(fullResult, "FULL");
        }
        
    } catch (Exception e) {
        getLog().error("Method invocation failed: " + e.getMessage());
    }
}

    private void testRestConstantsInMethodContext() {
        try {
            getLog().info("--- Verifying RestConstants in method execution context ---");
            
            // Test if RestConstants is accessible from the same thread/context where 
            // getRepresentationDescription will be executed
            Class<?> restConstants = classLoader.loadClass("org.openmrs.module.webservices.rest.web.RestConstants");
            
            // Quick access test
            Object version1 = restConstants.getField("VERSION_1").get(null);
            getLog().info(" RestConstants accessible in method context: VERSION_1 = " + version1);
            
            // Test accessing representation constants that getRepresentationDescription might use
            Object repDefault = restConstants.getField("REPRESENTATION_DEFAULT").get(null);
            Object repFull = restConstants.getField("REPRESENTATION_FULL").get(null);
            
            getLog().info(" Representation constants accessible:");
            getLog().info("   - REPRESENTATION_DEFAULT = " + repDefault);
            getLog().info("   - REPRESENTATION_FULL = " + repFull);
            
            getLog().info(" RestConstants should be accessible to getRepresentationDescription()");
            
        } catch (Exception e) {
            getLog().error(" RestConstants not accessible in method context: " + e.getMessage());
        }
    }

    private void extractSchemaFromDescription(Object description, String representationType) {
    try {
        getLog().info("=== Extracting Schema from " + representationType + " ===");
        
        // Get the properties from DelegatingResourceDescription
        Method getPropertiesMethod = description.getClass().getMethod("getProperties");
        Object properties = getPropertiesMethod.invoke(description);
        
        if (properties instanceof Map) {
            Map<?, ?> propertyMap = (Map<?, ?>) properties;
            getLog().info("Found " + propertyMap.size() + " properties:");
            
            for (Map.Entry<?, ?> entry : propertyMap.entrySet()) {
                String propertyName = entry.getKey().toString();
                Object propertyValue = entry.getValue();
                getLog().info("  - " + propertyName + " -> " + propertyValue);
            }
        }
        
        // Get links if they exist
        Method getLinksMethod = description.getClass().getMethod("getLinks");
        Object links = getLinksMethod.invoke(description);
        
        if (links instanceof Map) {
            Map<?, ?> linkMap = (Map<?, ?>) links;
            getLog().info("Found " + linkMap.size() + " links:");
            
            for (Map.Entry<?, ?> entry : linkMap.entrySet()) {
                getLog().info("  - LINK: " + entry.getKey() + " -> " + entry.getValue());
            }
        }
        
    } catch (Exception e) {
        getLog().error("Failed to extract schema: " + e.getMessage());
    }
}
}
