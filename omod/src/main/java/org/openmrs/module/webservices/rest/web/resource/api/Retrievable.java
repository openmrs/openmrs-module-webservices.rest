package org.openmrs.module.webservices.rest.web.resource.api;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources that have the standard-pattern Retrieve operation by UUID
 */
public interface Retrievable extends Resource {
	
	/**
	 * Gets the object with the given uuid, in the given representation
	 * @param uuid
	 * @param context
	 * @return
	 * @throws ResponseException
	 */
	public Object retrieve(String uuid, RequestContext context) throws ResponseException;

}
