package org.openmrs.module.webservices.rest.web.resource.api;

import java.util.List;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources so that all rows in the database can be fetched and returned
 * as a list
 */
public interface Listable extends Resource {
	
	/**
	 * Fetches for all instances of the given resource in the database
	 * 
	 * @param context the {@link RequestContext} object
	 * @return a list of all objects in the database represented by the implementing resource unless
	 *         includeDeleted is set to false
	 * @throws ResponseException
	 */
	public List<Object> getAll(RequestContext context) throws ResponseException;
	
}
