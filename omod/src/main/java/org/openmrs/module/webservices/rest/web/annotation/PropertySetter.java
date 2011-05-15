package org.openmrs.module.webservices.rest.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;

/**
 * Use this annotation to mark a method in a {@link DelegatingCrudResource} implementation that describes how
 * to set a property on a delegate. (You would use this, for example, if you want to expose a "preferredName"
 * property in the resource, which doesn't have a direct setter on the delegate.)
 * The "setter" method should have the form "void setXyz(T delegate, Object value)" and may be static. 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertySetter {
	
	/**
	 * @return the name of the property the annotated method is a "setter" for.
	 */
	String value();

}
