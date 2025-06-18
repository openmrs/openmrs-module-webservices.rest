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
                }
            }
            
            log.info("ClassLoader setup complete: " + urls.size() + " URLs");
            return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup ClassLoader", e);
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
        } catch (Exception e) {
            log.error(" FAILED: Error disabling context: " + e.getMessage());
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
            log.error("This means RestConstants is not in the classpath");        } catch (NoSuchFieldException e) {
            log.error(" FAILED: RestConstants field not found: " + e.getMessage());
        } catch (Exception e) {
            log.error(" FAILED: RestConstants not accessible: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
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
    
    private void testPatientResourceInstantiation(Class<?> resourceClazz) {
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
            
        } catch (Exception e) {
            log.error(" FAILED: Unexpected error during instantiation");
            log.error("Error type: " + e.getClass().getSimpleName());        
            log.error("Error message: " + e.getMessage());
            e.printStackTrace();    
        }
    }

    private void testBasicMethodCall(Object instance) {
        try {
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
            } catch (Exception e) {
            log.warn(" Basic method calls failed: " + e.getMessage());
        }
    }

    private void testGetRepresentationDescription(Object instance) {
        try {
            log.info("=== Testing getRepresentationDescription Method ===");
            
            //testRestConstantsInMethodContext();
            
            Class<?> representationClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.representation.Representation");
            Class<?> defaultRepClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation");
            Class<?> fullRepClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.representation.FullRepresentation");
            
            Object defaultRep = defaultRepClass.getDeclaredConstructor().newInstance();
            Object fullRep = fullRepClass.getDeclaredConstructor().newInstance();
            
            Method method = instance.getClass().getMethod("getRepresentationDescription", representationClass);
            
            Object defaultResult = method.invoke(instance, defaultRep);
            log.info(" DEFAULT result: " + defaultResult);
            
            Object fullResult = method.invoke(instance, fullRep);
            log.info(" FULL result: " + fullResult);        if (defaultResult != null) {
                extractSchemaFromDescription(defaultResult, "DEFAULT");
            }
            
            if (fullResult != null) {
                extractSchemaFromDescription(fullResult, "FULL");
            }
            
        } catch (Exception e) {
            log.error("Method invocation failed: " + e.getMessage());
        }
    }

    private void testRestConstantsInMethodContext() {
        try {
            log.info("--- Verifying RestConstants in method execution context ---");        
            Class<?> restConstants = classLoader.loadClass("org.openmrs.module.webservices.rest.web.RestConstants");
            
            Object version1 = restConstants.getField("VERSION_1").get(null);
            log.info(" RestConstants accessible in method context: VERSION_1 = " + version1);
            
            Object repDefault = restConstants.getField("REPRESENTATION_DEFAULT").get(null);
            Object repFull = restConstants.getField("REPRESENTATION_FULL").get(null);
            
            log.info(" Representation constants accessible:");
            log.info("   - REPRESENTATION_DEFAULT = " + repDefault);
            log.info("   - REPRESENTATION_FULL = " + repFull);
            
            log.info(" RestConstants should be accessible to getRepresentationDescription()");
            
        } catch (Exception e) {
            log.error(" RestConstants not accessible in method context: " + e.getMessage());
        }
    }

    private void extractSchemaFromDescription(Object description, String representationType) {
        try {
            log.info("=== Extracting Schema from " + representationType + " ===");
            
            Method getPropertiesMethod = description.getClass().getMethod("getProperties");
            Object properties = getPropertiesMethod.invoke(description);
            
            if (properties instanceof Map) {
                Map<?, ?> propertyMap = (Map<?, ?>) properties;
                log.info("Found " + propertyMap.size() + " properties:");
                
                for (Map.Entry<?, ?> entry : propertyMap.entrySet()) {
                    String propertyName = entry.getKey().toString();
                    Object propertyValue = entry.getValue();
                    log.info("  - " + propertyName + " -> " + propertyValue);
                }
            }
            
            Method getLinksMethod = description.getClass().getMethod("getLinks");
            Object links = getLinksMethod.invoke(description);
            if (links instanceof Map) {
                Map<?, ?> linkMap = (Map<?, ?>) links;
                log.info("Found " + linkMap.size() + " links:");
                
                for (Map.Entry<?, ?> entry : linkMap.entrySet()) {
                    log.info("  - LINK: " + entry.getKey() + " -> " + entry.getValue());
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to extract schema: " + e.getMessage());
        }
    }
}
