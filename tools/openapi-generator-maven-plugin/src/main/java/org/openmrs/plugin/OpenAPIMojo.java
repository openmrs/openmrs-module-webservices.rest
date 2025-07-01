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
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
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
    
    private URLClassLoader classLoader;
    
    private Class<?> representationClass;
    private Class<?> defaultRepresentationClass;
    private Class<?> fullRepresentationClass;
    private Class<?> requestMappingClass;
    private Class<?> restUtilClass;
    private Class<?> restConstantsClass;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;
    
    @Parameter
    private List<String> scanPackages;

    @Parameter(defaultValue = "false")
    private boolean skipEmptyModules;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("=== OPENAPI GENERATION STARTED ===");
        log.info("Processing module: {}", project.getArtifactId());
        
        this.classLoader = setupProjectClassLoader();
        loadCommonClasses();
        disableOpenMRSContext();
        
        List<Class<?>> resourceClasses = discoverResourceClasses();
        
        if (resourceClasses.isEmpty()) {
            log.info("No resources found in this module - skipping OpenAPI generation");
            return;
        }
        
        log.info("Found {} resource classes to process", resourceClasses.size());
        
        for (Class<?> resourceClass : resourceClasses) {
            processResourceClass(resourceClass);
        }
        
        log.info("=== OPENAPI GENERATION COMPLETED ===");
    }
    
    private URLClassLoader setupProjectClassLoader() {
        try {
            List<URL> urls = new ArrayList<>();
            
            String outputDirectory = project.getBuild().getOutputDirectory();
            File outputDir = new File(outputDirectory);
            if (outputDir.exists()) {
                urls.add(outputDir.toURI().toURL());
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
            
            return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
            
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("Invalid URL in classpath elements: " + e.getMessage(), e);
        }
    }
    
    private void loadCommonClasses() {
        log.debug("=== Loading Common Classes (One-time Cache) ===");
        
        try {
            representationClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.representation.Representation");
            defaultRepresentationClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation");
            fullRepresentationClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.representation.FullRepresentation");
            log.debug("Cached representation classes");
            
            restUtilClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.RestUtil");
            restConstantsClass = classLoader.loadClass("org.openmrs.module.webservices.rest.web.RestConstants");
            log.debug("Cached REST utility classes");
            
            try {
                requestMappingClass = classLoader.loadClass("org.springframework.web.bind.annotation.RequestMapping");
                log.debug("Cached RequestMapping annotation class");
            } catch (ClassNotFoundException e) {
                log.debug("RequestMapping annotation not available - will skip annotation checks");
                requestMappingClass = null;
            }            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load required OpenMRS classes. Ensure webservices.rest module is in dependencies.", e);
        }
    }

    private void disableOpenMRSContext() {        
        try {
            Method disableContextMethod = restUtilClass.getMethod("disableContext");
            disableContextMethod.invoke(null);
            log.info("SUCCESS: OpenMRS Context disabled successfully");
            log.debug("   RestUtil.contextEnabled is now false");
            log.debug("   Static initializers will not attempt Context access");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error("FAILED: Cannot disable OpenMRS context", e);
                throw new RuntimeException("Cannot disable OpenMRS context", e);
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
    
    private List<Class<?>> discoverResourceClasses() throws MojoExecutionException {
        List<Class<?>> discoveredClasses = new ArrayList<>();
        
        if (scanPackages != null && !scanPackages.isEmpty()) {
            log.info("Scanning packages: {}", scanPackages);
            discoveredClasses = scanPackagesForResources(scanPackages);
        }
        
        else {
            throw new MojoExecutionException(
                "No resource discovery configuration provided. " +
                "Please specify either 'targetClasses' or 'scanPackages' in plugin configuration."
            );
        }
        
        if (discoveredClasses.isEmpty() && !skipEmptyModules) {
            throw new MojoExecutionException("No REST resource classes found in this module");
        }
        
        log.info("Discovered {} resource classes", discoveredClasses.size());
        return discoveredClasses;
    }
    
    private List<Class<?>> scanPackagesForResources(List<String> packages) {
        List<Class<?>> resourceClasses = new ArrayList<>();
        
        for (String packageName : packages) {
            log.debug("Scanning package: {}", packageName);
            
            String packagePath = packageName.replace('.', '/');
            
            String outputDirectory = project.getBuild().getOutputDirectory();
            File packageDir = new File(outputDirectory, packagePath);
            
            if (!packageDir.exists()) {
                log.warn("Package directory does not exist: {}", packageDir.getAbsolutePath());
                continue;
            }
            
            List<File> classFiles = findClassFiles(packageDir);
            
            for (File classFile : classFiles) {
                String className = getClassNameFromFile(classFile, outputDirectory);
                
                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    
                    if (isRestResourceClass(clazz)) {
                        resourceClasses.add(clazz);
                        log.info("Found REST resource: {}", className);
                    } else {
                        log.debug("Skipped non-resource class: {}", className);
                    }
                    
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    log.debug("Could not load class {}: {}", className, e.getMessage());
                }
            }
        }
        
        return resourceClasses;
    }
    
    private boolean isRestResourceClass(Class<?> clazz) {
        if (clazz.isInterface() || 
            java.lang.reflect.Modifier.isAbstract(clazz.getModifiers()) ||
            clazz.getName().contains("Test")) {
            return false;
        }
        
        if (!clazz.getName().contains(".resource.")) {
            return false;
        }
        
        try {
            Method getRepDescMethod = clazz.getMethod("getRepresentationDescription", representationClass);
            
            if (getRepDescMethod != null) {
                log.debug("Class {} has getRepresentationDescription method", clazz.getSimpleName());
                return true;
            }
            
        } catch (NoSuchMethodException e) {
            Class<?> current = clazz;
            while (current != null) {
                String baseClassName = current.getName();
                if (baseClassName.contains("DelegatingResourceHandler") ||
                    baseClassName.contains("DelegatingCrudResource") ||
                    baseClassName.contains("BaseRestController")) {
                    log.debug("Class {} extends REST resource base class", clazz.getSimpleName());
                    return true;
                }
                current = current.getSuperclass();
            }
        }
        
        if (requestMappingClass != null) {
            try {
                if (clazz.isAnnotationPresent(requestMappingClass.asSubclass(java.lang.annotation.Annotation.class))) {
                    log.debug("Class {} has REST annotations", clazz.getSimpleName());
                    return true;
                }
            } catch (ClassCastException e) {
                log.debug("RequestMapping class is not an annotation, skipping annotation check");
            }
        }
        
        return false;
    }
    
    private List<File> findClassFiles(File directory) {
        List<File> classFiles = new ArrayList<>();
        
        if (!directory.exists() || !directory.isDirectory()) {
            return classFiles;
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classFiles.addAll(findClassFiles(file));
                } else if (file.getName().endsWith(".class")) {
                    classFiles.add(file);
                }
            }
        }
        
        return classFiles;
    }
    
    private String getClassNameFromFile(File classFile, String outputDirectory) {
        String filePath = classFile.getAbsolutePath();
        String outputPath = new File(outputDirectory).getAbsolutePath();
        
        String relativePath = filePath.substring(outputPath.length() + 1);
        String className = relativePath.substring(0, relativePath.length() - 6); // Remove ".class"
        
        return className.replace(File.separatorChar, '.');
    }
    
    private void processResourceClass(Class<?> resourceClass) {
        log.info("=== Resource: {} ===", resourceClass.getSimpleName());
        
        try {
            Object instance = resourceClass.getDeclaredConstructor().newInstance();
            
            processGetRepresentationDescription(instance);
                        
        } catch (Exception e) {
            log.warn("Failed to process resource {}: {}", resourceClass.getSimpleName(), e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("Resource processing error details", e);
            }
        }
    }
    
    private void processGetRepresentationDescription(Object instance) throws MojoExecutionException {
        
        try {
            Object defaultRep = createRequiredInstance(defaultRepresentationClass);
            Object fullRep = createRequiredInstance(fullRepresentationClass);
            
            Method method = instance.getClass().getMethod("getRepresentationDescription", representationClass);
            
            Object defaultResult = invokeRepresentationMethod(method, instance, defaultRep, "DEFAULT");
            Object fullResult = invokeRepresentationMethod(method, instance, fullRep, "FULL");
            
            tryExtractSchema(defaultResult, "DEFAULT");
            tryExtractSchema(fullResult, "FULL");
            
        } catch (NoSuchMethodException e) {
            throw new MojoExecutionException("getRepresentationDescription method not found on resource class. " +
                                           "This OpenMRS version may not be supported.", e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MojoExecutionException("Cannot create or access OpenMRS representation instances. " +
                                           "Check OpenMRS version compatibility.", e);
        }
    }

    private Object createRequiredInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException e) {
            String message = e instanceof NoSuchMethodException ? 
            "No default constructor found for " + clazz.getSimpleName() :
            "Constructor failed: " + ((InvocationTargetException)e).getCause().getMessage();
            throw new InstantiationException(message);
        }
    }
    
    private Object invokeRepresentationMethod(Method method, Object instance, Object representation, String type) {
        try {
            Object result = method.invoke(instance, representation);
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
                
        boolean propertiesSuccess = tryExtractProperties(description, representationType);
        if (!propertiesSuccess) {
            log.warn("Could not extract properties for {}", representationType);
        }
        
        return propertiesSuccess;
        
    }
    
    private boolean tryExtractProperties(Object description, String type) {
        try {
            Method getPropertiesMethod = description.getClass().getMethod("getProperties");
            Object properties = getPropertiesMethod.invoke(description);
            
            if (properties instanceof Map) {
                Map<?, ?> propertyMap = (Map<?, ?>) properties;
                log.info("Found {} properties in {}", propertyMap.size(), type);
                return true;
            } else {
                log.warn("getProperties() returned non-Map type for {}: {}", type, 
                        properties != null ? properties.getClass().getSimpleName() : "null");
                return false;
            }
            
        } catch (NoSuchMethodException | IllegalAccessException | ClassCastException e) {
            log.warn("Cannot extract properties for {}: {}", type, e.getMessage());
            return false;
        } catch (InvocationTargetException e) {
            log.warn("getProperties method failed for {}: {}", type, e.getCause().getMessage());
            if (log.isDebugEnabled()) {
            log.debug("getProperties error details", e.getCause());
        }
        return false;
        }
    }
}
