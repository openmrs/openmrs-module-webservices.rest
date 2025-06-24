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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

@Mojo(name = "openapi", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class OpenAPIMojo extends AbstractMojo {
    
    private static final Logger log = LoggerFactory.getLogger(OpenAPIMojo.class);
    
    private static final String TARGET_CLASS_NAME = "org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.CohortResource1_8";
    
    private URLClassLoader classLoader;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("=== OPENAPI GENERATION STARTED ===");
        
        this.classLoader = setupProjectClassLoader();
        
        disableOpenMRSContext();
        
        //testRestConstantsAccessibility();
        
        Class<?> resourceClazz = loadClass(TARGET_CLASS_NAME);

        testPatientResourceInstantiation(resourceClazz);
        
        log.info("==============");
    }    private URLClassLoader setupProjectClassLoader() {
        try {
            List<URL> urls = new ArrayList<>();
            
            String outputDirectory = project.getBuild().getOutputDirectory();
            File outputDir = new File(outputDirectory);
            if (outputDir.exists()) {
                urls.add(outputDir.toURI().toURL());
                log.info("Added project output directory: " + outputDirectory);
            } else {
                log.warn("Project output directory does not exist: " + outputDirectory);
            }
            
            Set<Artifact> allArtifacts = project.getArtifacts();
            if (allArtifacts != null) {
                log.debug("Found " + allArtifacts.size() + " project artifacts");
                for (Artifact artifact : allArtifacts) {
                    File file = artifact.getFile();
                    if (file != null && file.exists()) {
                        urls.add(file.toURI().toURL());
                        log.debug("Added artifact: " + artifact.getGroupId() + ":" + artifact.getArtifactId() + " -> " + file.getPath());
                    } else {
                        log.warn("Artifact file not found: " + artifact.getGroupId() + ":" + artifact.getArtifactId());
                    }
                }            }
            
            log.info("ClassLoader setup complete: " + urls.size() + " URLs");
            return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
            
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("Invalid URL in classpath elements: " + e.getMessage(), e);
        }
    }

    private void disableOpenMRSContext() {
        log.info("=== Disabling OpenMRS Context for Build-Time Use ===");
        
        try {
            Class<?> restUtilClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.RestUtil");
            Method disableContextMethod = restUtilClass.getMethod("disableContext");
            disableContextMethod.invoke(null);
              log.info(" SUCCESS: OpenMRS Context disabled successfully");
            log.debug("   RestUtil.contextEnabled is now false");
            log.debug("   Static initializers will not attempt Context access");
              } catch (ClassNotFoundException e) {
            log.error(" FAILED: RestUtil class not found: " + e.getMessage());
            throw new RuntimeException("Cannot disable OpenMRS context", e);
        } catch (NoSuchMethodException e) {
            log.error(" FAILED: disableContext method not found: " + e.getMessage());
            throw new RuntimeException("Cannot disable OpenMRS context", e);
        } catch (IllegalAccessException e) {
            log.error(" FAILED: Cannot access disableContext method: " + e.getMessage());
            throw new RuntimeException("Cannot disable OpenMRS context", e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.error(" FAILED: disableContext method invocation failed: " + e.getCause().getMessage());
            throw new RuntimeException("Cannot disable OpenMRS context", e);
        }
    }

    private void testRestConstantsAccessibility() {        
        log.info("=== Testing RestConstants Accessibility ===");
        
        try {
            Class<?> restConstants = classLoader.loadClass("org.openmrs.module.webservices.rest.web.RestConstants");
            log.info(" RestConstants class loaded successfully: " + restConstants.getName());
            
            Object version1 = restConstants.getField("VERSION_1").get(null);
            Object uriPrefix = restConstants.getField("URI_PREFIX").get(null);
            Object representationDefault = restConstants.getField("REPRESENTATION_DEFAULT").get(null);
            Object representationFull = restConstants.getField("REPRESENTATION_FULL").get(null);
            
            log.info(" RestConstants.VERSION_1 = " + version1);
            log.info(" RestConstants.URI_PREFIX = " + uriPrefix);
            log.info(" RestConstants.REPRESENTATION_DEFAULT = " + representationDefault);
            log.info(" RestConstants.REPRESENTATION_FULL = " + representationFull);
            
            if ("v1".equals(version1)) {
                log.info(" VERSION_1 has expected value");
            } else {
                log.warn(" VERSION_1 unexpected value: " + version1);
            }
            
            if (uriPrefix != null && uriPrefix.toString().contains("/ws/rest")) {
                log.info(" URI_PREFIX contains expected path");
            } else {
                log.warn(" URI_PREFIX unexpected value: " + uriPrefix);
            }
            
            if ("default".equals(representationDefault)) {
                log.info(" REPRESENTATION_DEFAULT has expected value");
            } else {
                log.warn(" REPRESENTATION_DEFAULT unexpected value: " + representationDefault);
            }
            
            if ("full".equals(representationFull)) {
                log.info(" REPRESENTATION_FULL has expected value");
            } else {
                log.warn(" REPRESENTATION_FULL unexpected value: " + representationFull);
            }
              log.info(" SUCCESS: RestConstants is fully accessible at build time!");
            
        } catch (ClassNotFoundException e) {
            log.error(" FAILED: RestConstants class not found in ClassLoader");
            log.error("This means RestConstants is not in the classpath");
        } catch (NoSuchFieldException e) {
            log.error(" FAILED: RestConstants field not found: " + e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(" FAILED: Cannot access RestConstants fields: " + e.getMessage());
        }    
    }

    private Class<?> loadClass(String className) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            log.info("Loaded class: " + clazz.getName());
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class: " + className, e);
        }
    }    
    private void testPatientResourceInstantiation(Class<?> resourceClazz) throws MojoExecutionException {
        log.debug("=== Testing PatientResource Instance Creation ===");
        
        try {
            log.debug("Attempting to create PatientResource1_8 instance using normal constructor...");
            
            Object instance = resourceClazz.getDeclaredConstructor().newInstance();
            
            log.info(" SUCCESS! PatientResource1_8 instance created successfully!");
            log.info("Instance class: " + instance.getClass().getName());
            log.info("Instance toString: " + instance.toString());
            
            testBasicMethodCall(instance);

            testGetRepresentationDescription(instance);
            
        } catch (ExceptionInInitializerError e) {
            log.error(" FAILED: Static initialization error");
            log.error("Root cause: " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
            log.error("This means parent classes have problematic static blocks");
            
        } catch (NoClassDefFoundError e) {
            log.error(" FAILED: Missing class dependency");
            log.error("Missing class: " + e.getMessage());
            log.error("This means some required classes aren't in the classpath");
            
        } catch (InstantiationException e) {
            log.error(" FAILED: Cannot instantiate class");
            log.error("Reason: " + e.getMessage());            
        } catch (IllegalAccessException e) {
            log.error(" FAILED: Constructor not accessible");
            log.error("Reason: " + e.getMessage());
            
        } catch (NoSuchMethodException e) {
            log.error(" FAILED: Default constructor not found");
            log.error("Reason: " + e.getMessage());
            
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.error(" FAILED: Constructor execution failed");
            log.error("Cause: " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
        }
    }    private void testBasicMethodCall(Object instance) {
        log.debug("--- Testing Basic Method Calls ---");
        
        String toStringResult = instance.toString();
        log.debug(" toString() works: " + toStringResult);
        
        Class<?> instanceClass = instance.getClass();
        log.debug(" getClass() works: " + instanceClass.getName());
        
        Method[] methods = instanceClass.getDeclaredMethods();
        log.debug(" Found " + methods.length + " declared methods");
        
        for (Method method : methods) {
            if (method.getName().equals("getRepresentationDescription")) {
                log.info(" Found getRepresentationDescription method: " + method.toString());
            }
        }
    }private void testGetRepresentationDescription(Object instance) throws MojoExecutionException {
        log.info("=== Testing getRepresentationDescription Method ===");
        
        try {
            Class<?> representationClass = loadRequiredClass("org.openmrs.module.webservices.rest.web.representation.Representation");
            Class<?> defaultRepClass = loadRequiredClass("org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation");
            Class<?> fullRepClass = loadRequiredClass("org.openmrs.module.webservices.rest.web.representation.FullRepresentation");
            
            Object defaultRep = createRequiredInstance(defaultRepClass);
            Object fullRep = createRequiredInstance(fullRepClass);
            
            Method method = instance.getClass().getMethod("getRepresentationDescription", representationClass);
            
            Object defaultResult = invokeRepresentationMethod(method, instance, defaultRep, "DEFAULT");
            Object fullResult = invokeRepresentationMethod(method, instance, fullRep, "FULL");
            
            tryExtractSchema(defaultResult, "DEFAULT");
            tryExtractSchema(fullResult, "FULL");
            
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Required OpenMRS representation classes not found in classpath. " +
                                           "Ensure OpenMRS webservices.rest module is properly included in dependencies.", e);
        } catch (NoSuchMethodException e) {
            throw new MojoExecutionException("getRepresentationDescription method not found on resource class. " +
                                           "This OpenMRS version may not be supported.", e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MojoExecutionException("Cannot create or access OpenMRS representation instances. " +
                                           "Check OpenMRS version compatibility.", e);
        }
    }

    private Class<?> loadRequiredClass(String className) throws ClassNotFoundException {
        return classLoader.loadClass(className);
    }

    private Object createRequiredInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new InstantiationException("No default constructor found for " + clazz.getSimpleName());
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new InstantiationException("Constructor failed: " + e.getCause().getMessage());
        }
    }
    
    private Object invokeRepresentationMethod(Method method, Object instance, Object representation, String type) {
        try {
            Object result = method.invoke(instance, representation);
            log.info("{} result: {}", type, result);
            return result;
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.warn("Failed to invoke getRepresentationDescription for {}: {}", type, e.getCause().getMessage());
            if (log.isDebugEnabled()) {
                log.debug("Method invocation error details", e.getCause());
            }
            return null;
        } catch (IllegalAccessException e) {
            log.warn("Cannot access getRepresentationDescription method for {}: {}", type, e.getMessage());
            return null;
        }
    }
    
    private boolean tryExtractSchema(Object description, String representationType) {
        if (description == null) {
            log.info("No {} representation to extract schema from", representationType);
            return false;
        }
        
        log.info("=== Extracting Schema from {} ===", representationType);
        
        boolean propertiesSuccess = tryExtractProperties(description, representationType);
        if (!propertiesSuccess) {
            log.warn("Could not extract properties for {}", representationType);
        }
        
        boolean linksSuccess = tryExtractLinks(description, representationType);
        if (!linksSuccess) {
            log.warn("Could not extract links for {}", representationType);
        }
        
        return propertiesSuccess || linksSuccess;
        
    }
    
    private boolean tryExtractProperties(Object description, String type) {
        try {
            Method getPropertiesMethod = description.getClass().getMethod("getProperties");
            Object properties = getPropertiesMethod.invoke(description);
            
            if (properties instanceof Map) {
                Map<?, ?> propertyMap = (Map<?, ?>) properties;
                log.info("Found {} properties:", propertyMap.size());
                
                for (Map.Entry<?, ?> entry : propertyMap.entrySet()) {
                    String propertyName = String.valueOf(entry.getKey());
                    Object propertyValue = entry.getValue();
                    log.info("  - {} -> {}", propertyName, propertyValue);
                }
                return true;
            } else {
                log.warn("getProperties() returned non-Map type for {}: {}", type, 
                        properties != null ? properties.getClass().getSimpleName() : "null");
                return false;
            }
            
        } catch (NoSuchMethodException e) {
            log.warn("getProperties method not available for {} (OpenMRS version compatibility)", type);
            return false;
        } catch (IllegalAccessException e) {
            log.warn("Cannot access getProperties method for {}", type);
            return false;
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.warn("getProperties method failed for {}: {}", type, e.getCause().getMessage());
            if (log.isDebugEnabled()) {
                log.debug("getProperties error details", e.getCause());
            }
            return false;
        } catch (ClassCastException e) {
            log.warn("getProperties returned unexpected type for {}: {}", type, e.getMessage());
            return false;
        }
    }
  
    private boolean tryExtractLinks(Object description, String type) {
        try {
            Method getLinksMethod = description.getClass().getMethod("getLinks");
            Object links = getLinksMethod.invoke(description);
            
            if (links instanceof Map) {
                Map<?, ?> linkMap = (Map<?, ?>) links;
                log.info("Found {} links:", linkMap.size());
                
                for (Map.Entry<?, ?> entry : linkMap.entrySet()) {
                    log.info("  - LINK: {} -> {}", entry.getKey(), entry.getValue());
                }
                return true;
            } else {
                log.warn("getLinks() returned non-Map type for {}: {}", type,
                        links != null ? links.getClass().getSimpleName() : "null");
                return false;
            }
            
        } catch (NoSuchMethodException e) {
            log.warn("getLinks method not available for {} (OpenMRS version compatibility)", type);
            return false;
        } catch (IllegalAccessException e) {
            log.warn("Cannot access getLinks method for {}", type);
            return false;
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.warn("getLinks method failed for {}: {}", type, e.getCause().getMessage());
            if (log.isDebugEnabled()) {
                log.debug("getLinks error details", e.getCause());
            }
            return false;        } catch (ClassCastException e) {
            log.warn("getLinks returned unexpected type for {}: {}", type, e.getMessage());
            return false;
        }
    }
}
