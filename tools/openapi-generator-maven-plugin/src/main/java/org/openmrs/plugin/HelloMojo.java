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
import org.apache.maven.project.MavenProject;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

@Mojo(name = "hello", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class HelloMojo extends AbstractMojo {
    
    private static final String TARGET_CLASS_NAME = "org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8";
    
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hello from OpenMRS Maven Plugin!");
        getLog().info("Starting Javadoc parsing for target class: " + TARGET_CLASS_NAME);
        
        // Convert fully qualified class name to relative path
        String classRelativePath = TARGET_CLASS_NAME.replace('.', File.separatorChar) + ".java";
        
        // Extract simple class name for later use
        String targetClassSimpleName = TARGET_CLASS_NAME.substring(TARGET_CLASS_NAME.lastIndexOf('.') + 1);
        
        // Try to find the source file in compile source roots
        File targetSourceFile = null;
        List<String> sourceRoots = project.getCompileSourceRoots();
        
        for (String sourceRoot : sourceRoots) {
            File possibleFile = new File(sourceRoot, classRelativePath);
            if (possibleFile.exists()) {
                targetSourceFile = possibleFile;
                getLog().info("Found source file: " + possibleFile.getAbsolutePath());
                break;
            }
        }
        
        if (targetSourceFile == null) {
            getLog().warn("Could not find source file for class: " + TARGET_CLASS_NAME);
            return;
        }
        
        // Parse with JavaParser
        try {
            CompilationUnit cu = StaticJavaParser.parse(targetSourceFile);
            Optional<ClassOrInterfaceDeclaration> classOptional = cu.getClassByName(targetClassSimpleName);
            
            if (classOptional.isPresent()) {
                ClassOrInterfaceDeclaration classDecl = classOptional.get();
                Optional<JavadocComment> javadocOptional = classDecl.getJavadocComment();
                
                if (javadocOptional.isPresent()) {
                    String javadocText = javadocOptional.get().getContent();
                    getLog().info("Class Javadoc for " + TARGET_CLASS_NAME + ":\n" + javadocText);
                } else {
                    getLog().info("No class-level Javadoc found for " + TARGET_CLASS_NAME);
                }
            } else {
                getLog().warn("Could not find class " + targetClassSimpleName + " in parsed file " + targetSourceFile.getName());
            }
        } catch (FileNotFoundException e) {
            getLog().error("Could not find source file for Javadoc parsing: " + targetSourceFile.getAbsolutePath(), e);
        } catch (Exception e) {
            getLog().error("Error parsing Javadoc for " + TARGET_CLASS_NAME, e);
        }
    }
}