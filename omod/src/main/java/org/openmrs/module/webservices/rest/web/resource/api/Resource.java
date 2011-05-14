package org.openmrs.module.webservices.rest.web.resource.api;

/**
 * Marker interface for resources
 */
public interface Resource {
	
	/**
	 * Gets the URI of the given instance of this resource. (If instance is null, this should return the
	 * base URI for creating and searching the resource.)
	 * @param instance
	 * @return
	 */
	String getUri(Object instance);

}
