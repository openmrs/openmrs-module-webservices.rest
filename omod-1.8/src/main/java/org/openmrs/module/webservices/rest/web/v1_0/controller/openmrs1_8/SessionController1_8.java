/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.apache.commons.lang3.LocaleUtils;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Controller that lets a client check the status of their session, and log out. (Authenticating is
 * handled through a filter, and may happen through this or any other resource.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/session")
public class SessionController1_8 extends BaseRestController {
	
	@Autowired
	RestService restService;
	
	private final static String LOCATION_SESSION_ATTRIBUTE = "emrContext.sessionLocationId";
	
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
		
		UserContext userContext = Context.getUserContext();
		Provider currentProvider = null;
		if (userContext != null && userContext.getAuthenticatedUser() != null) {
			User currentUser = userContext.getAuthenticatedUser();
			Collection<Provider> providers = new HashSet<Provider>();
			try {
				Context.addProxyPrivilege(PrivilegeConstants.VIEW_PROVIDERS);
				if (currentUser.getPerson() != null) {
					providers = Context.getProviderService().getProvidersByPerson(currentUser.getPerson(), false);
				}
			}
			finally {
				Context.removeProxyPrivilege(PrivilegeConstants.VIEW_PROVIDERS);
			}
			if (providers.size() > 1) {
				throw new IllegalStateException("Can't handle users with multiple provider accounts");
			} else if (providers.size() == 1) {
				currentProvider = providers.iterator().next();
			}
		}
		Integer locationId = (Integer) request.getAttribute(LOCATION_SESSION_ATTRIBUTE, WebRequest.SCOPE_REQUEST);
		Location sessionLocation = null;
		if (locationId != null) {
			sessionLocation = Context.getLocationService().getLocation(locationId);
		}
		
		session.add("sessionId", request.getSessionId()).add("authenticated", authenticated);
		if (authenticated) {
			String repParam = request.getParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION);
			Representation rep = (repParam != null) ? restService.getRepresentation(repParam) : Representation.DEFAULT;
			session.add("locale", Context.getLocale());
			session.add("currentProvider", ConversionUtil.convertToRepresentation(currentProvider, rep));
			session.add("sessionLocation", ConversionUtil.convertToRepresentation(sessionLocation, rep));
			session.add("user", ConversionUtil.convertToRepresentation(Context.getAuthenticatedUser(), rep));
			session.add("allowedLocales", Context.getAdministrationService().getAllowedLocales());
		}
		return session;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void post(@RequestBody Map<String, String> body) {
		String localeStr = body.get("locale");
		Locale locale = null;
		try {
			locale = LocaleUtils.toLocale(localeStr);
		}
		catch (IllegalArgumentException e) {
			throw new APIException(" '" + localeStr + "' does not represent a valid locale.");
		}
		Set<Locale> allowedLocales = new HashSet<Locale>(Context.getAdministrationService().getAllowedLocales());
		if (allowedLocales.contains(locale)) {
			Context.setLocale(locale);
		} else {
			throw new APIException(" '" + localeStr + "' is not in the list of allowed locales.");
		}
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
