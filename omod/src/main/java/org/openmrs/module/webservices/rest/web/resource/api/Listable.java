package org.openmrs.module.webservices.rest.web.resource.api;

import java.util.List;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources that can return a complete listing all their instances.
 * Typically this will be implemented by metadata resources, and not by data resources
 */
public interface Listable extends Resource {
	
	/**
	 * Fetches for all instances of the given resource in the database
	 * 
	 * @param context the {@link RequestContext} object
	 * @return a list of all objects in the database represented by the implementing resource
	 * @throws ResponseException
	 */
	public List<Object> getAll(RequestContext context) throws ResponseException;
	
}
