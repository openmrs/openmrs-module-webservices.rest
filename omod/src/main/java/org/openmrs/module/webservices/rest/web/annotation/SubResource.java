package org.openmrs.module.webservices.rest.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openmrs.module.webservices.rest.web.resource.api.Resource;

/**
 * Indicates that the annotated class is a sub-resource of another Resource
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SubResource {

	Class<? extends Resource> parent();
	
	String path();
	
	String parentProperty();
	
}
