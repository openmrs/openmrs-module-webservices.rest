/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.resource.impl;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.RepresentationDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Used by implementations of {@link DelegatingCrudResource} to indicate what delegate properties, and what
 * methods they want to include in a particular representation 
 */
public class DelegatingResourceDescription implements RepresentationDescription {
	
	Map<String, Property> properties = new LinkedHashMap<String, Property>();

	public void addProperty(String propertyName) {
	    addProperty(propertyName, propertyName, null);
    }
	
	public void addProperty(String propertyName, Representation rep) {
	    addProperty(propertyName, propertyName, rep);
    }
	
	public void addProperty(String propertyName, Method method) {
		addProperty(propertyName, method, null);
    }

	public void addProperty(String propertyName, String delegatePropertyName, Representation rep) {
		if (rep == null)
			rep = Representation.DEFAULT;
	    properties.put(propertyName, new Property(delegatePropertyName, rep));
    }

	public void addProperty(String propertyName, Method method, Representation rep) {
		if (rep == null)
			rep = Representation.DEFAULT;
	    properties.put(propertyName, new Property(method, rep));
    }

    /**
     * @return the properties
     */
    public Map<String, Property> getProperties() {
    	return properties;
    }

	class Property {
    	private String delegateProperty;
    	private Method method;
    	private Representation rep;
    	
    	public Property(String delegateProperty, Representation rep) {
    		this.delegateProperty = delegateProperty;
    		this.rep = rep;
    	}
    	
    	public Property(Method method, Representation rep) {
    		this.method = method;
    		this.rep = rep;
    	}
		
        /**
         * @return the delegateProperty
         */
        public String getDelegateProperty() {
        	return delegateProperty;
        }
		
        /**
         * @param delegateProperty the delegateProperty to set
         */
        public void setDelegateProperty(String delegateProperty) {
        	this.delegateProperty = delegateProperty;
        }
		
        /**
         * @return the method
         */
        public Method getMethod() {
        	return method;
        }
		
        /**
         * @param method the method to set
         */
        public void setMethod(Method method) {
        	this.method = method;
        }
		
        /**
         * @return the rep
         */
        public Representation getRep() {
        	return rep;
        }
		
        /**
         * @param rep the rep to set
         */
        public void setRep(Representation rep) {
        	this.rep = rep;
        }

		public Object evaluate(BaseDelegatingResource<?> converter, Object delegate) throws ConversionException {
	        if (delegateProperty != null) {
	        	return ConversionUtil.getPropertyWithRepresentation(delegate, delegateProperty, rep);
	        } else if (method != null) {
	        	try {
	        		return method.invoke(converter, delegate);
	            }
	            catch (Exception ex) {
		            throw new ConversionException("method " + method, ex);
	            }
	        } else {
	        	throw new RuntimeException("Property with no delegateProperty or method specified");
	        }
        }

    }
    
}
