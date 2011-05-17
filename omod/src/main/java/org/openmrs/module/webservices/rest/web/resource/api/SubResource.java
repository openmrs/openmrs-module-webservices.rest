package org.openmrs.module.webservices.rest.web.resource.api;

import java.util.List;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;


public interface SubResource extends Resource {

	Object create(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException;

	Object retrieve(String parentUniqueId, String uuid, RequestContext context) throws ResponseException;
	
	Object update(String parentUniqueId, String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException;
	
	void delete(String parentUniqueId, String uuid, String reason, RequestContext context) throws ResponseException;
	
	void purge(String parentUniqueId, String uuid, RequestContext context) throws ResponseException;
	
	List<Object> getAll(String parentUniqueId, RequestContext context) throws ResponseException;
	
}
