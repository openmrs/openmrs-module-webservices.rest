package org.openmrs.module.webservices.rest.web.resource.impl;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.RepresentationDescription;

/**
 * Used by implementations of {@link DelegatingCrudResource} to indicate what delegate properties, and what
 * methods they want to include in a particular representation 
 */
public class DelegatingResourceDescription implements RepresentationDescription {
	
	Map<String, Representation> properties = new LinkedHashMap<String, Representation>();
	Map<String, Method> methodProperties = new LinkedHashMap<String, Method>();

	public void addProperty(String propertyName) {
	    addProperty(propertyName, null);
    }

	public void addProperty(String propertyName, Representation rep) {
		if (rep == null)
			rep = Representation.DEFAULT;
	    properties.put(propertyName, rep);
    }

	public void addMethodProperty(String string, Method method) {
	    methodProperties.put(string, method);
    }

	
    /**
     * @return the methodProperties
     */
    public Map<String, Method> getMethodProperties() {
    	return methodProperties;
    }

	
    /**
     * @param methodProperties the methodProperties to set
     */
    public void setMethodProperties(Map<String, Method> methodProperties) {
    	this.methodProperties = methodProperties;
    }

	
    /**
     * @return the properties
     */
    public Map<String, Representation> getProperties() {
    	return properties;
    }

	
    /**
     * @param properties the properties to set
     */
    public void setProperties(Map<String, Representation> properties) {
    	this.properties = properties;
    }

}
