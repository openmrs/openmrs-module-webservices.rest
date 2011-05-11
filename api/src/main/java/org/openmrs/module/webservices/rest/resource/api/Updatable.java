package org.openmrs.module.webservices.rest.resource.api;

import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources that have the standard-pattern Update operation, which takes a map
 * of properties to update on the resource
 */
public interface Updatable extends Resource {
	
	/**
	 * Sets the given properties on the resource identified by the given uuid
	 * @param uuid 
	 * @param propertiesToUpdate
	 * @param context
	 * @return
	 * @throws ResponseException 
	 */
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException;

}
