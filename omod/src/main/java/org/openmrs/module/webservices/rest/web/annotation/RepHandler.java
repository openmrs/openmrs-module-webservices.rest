package org.openmrs.module.webservices.rest.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openmrs.module.webservices.rest.web.representation.Representation;

/**
 * Method-level annotation, which marks a method as being the "get" handler for a particular
 * representation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RepHandler {
	
	Class<? extends Representation> value();
	
	String name() default "";
	
}
