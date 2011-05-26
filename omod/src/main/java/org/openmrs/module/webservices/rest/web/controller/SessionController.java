package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller that lets a client check the status of their session, and log out. (Authenticating is handled through a filter, and may happen through this or
 * any other resource.
 */
@Controller
@RequestMapping(value = "/rest/session")
public class SessionController extends BaseRestController {
	
	/**
	 * Tells the user their sessionId, and whether or not they are authenticated.
	 * 
	 * @param request
	 * @return
	 * @should return the session id if the user is authenticated
	 * @should return the session id if the user is not authenticated
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Object get(WebRequest request) {
		return new SimpleObject().add("sessionId", request.getSessionId()).add("authenticated", Context.isAuthenticated());
	}
	
	/**
	 * Logs the client out
	 * @should log the client out
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void delete() {
		Context.logout();
		// Context.clearUserContext(); // TODO determine whether this is necessary. (I copied it from Zabil)
	}
	
}
