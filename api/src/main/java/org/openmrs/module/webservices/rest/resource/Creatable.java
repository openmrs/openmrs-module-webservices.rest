package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;


/**
 * Interface implemented by resources that have the standard-pattern Create operation
 */
public interface Creatable extends Resource {
	
	/**
	 * Creates a resource (presumably saving it to some persistent storage) and returns the resource created 
	 * @param post
	 * @param context
	 * @return
	 */
	public Object create(SimpleObject post, RequestContext context) throws ResourceCreationException;

}
