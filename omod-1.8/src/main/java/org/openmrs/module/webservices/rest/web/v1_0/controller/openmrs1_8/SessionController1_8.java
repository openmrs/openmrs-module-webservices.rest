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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.module.webservices.rest.web.v1_0.converter.openmrs1_8.UserConverter1_8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller that lets a client check the status of their session, and log out. (Authenticating is
 * handled through a filter, and may happen through this or any other resource.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/session")
public class SessionController1_8 extends BaseRestController {
	
       @Autowired
       RestService restService;
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
            boolean authenticated = Context.isAuthenticated();
            SimpleObject session = new SimpleObject();
            session.add("sessionId", request.getSessionId()).add("authenticated", authenticated);
            if (authenticated) {
                String repParam = request.getParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION);
                Representation rep = (repParam != null) ? restService.getRepresentation(repParam) : Representation.DEFAULT;
                session.add("user",ConversionUtil.convertToRepresentation(Context.getAuthenticatedUser(), rep));
            }
            return session;
	}
	
	/**
	 * Logs the client out
	 * 
	 * @should log the client out
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void delete() {
		Context.logout();
	}
	
}
