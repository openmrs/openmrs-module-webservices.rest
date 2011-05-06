package org.openmrs.module.webservices.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openmrs.module.webservices.rest.resource.DelegatingCrudResource;

/**
 * indicates which properties of the delegate should be exposed by a {@link DelegatingCrudResource} 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeProperties {

	String rep() default "default";
	
	String[] properties();

}
