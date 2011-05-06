package org.openmrs.module.webservices.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openmrs.module.webservices.rest.Representation;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RepClassHandler {

	Class<? extends Representation> value();

}
