package org.openmrs.module.webservices.rest.web.resource.api;

import java.util.List;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources that have the standard-pattern Search operation
 */
public interface Searchable extends Resource {
	
	/**
	 * Searches for all instances of the given resource that use the given query 
	 * @param uuid
	 * @param context
	 * @return
	 * @throws ResponseException
	 */
	public List<Object> search(String query, RequestContext context) throws ResponseException;
	
}
