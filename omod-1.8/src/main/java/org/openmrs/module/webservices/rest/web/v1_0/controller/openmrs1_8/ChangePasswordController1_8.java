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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.module.webservices.validation.ValidationException;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/password")
public class ChangePasswordController1_8 extends BaseRestController {

	@Qualifier("userService")
	@Autowired
	private UserService userService;

	private final Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void changeOwnPassword(@RequestBody Map<String, String> body, HttpServletRequest servletRequest) {
		String oldPassword = body.get("oldPassword");
		String newPassword = body.get("newPassword");
		if (!Context.isAuthenticated()) {
			throw new APIAuthenticationException("Must be authenticated to change your own password");
		}
		try {
			userService.changePassword(oldPassword, newPassword);
			SessionListener.invalidateOtherSessions(Context.getAuthenticatedUser().getUuid(), servletRequest.getSession());
		} catch (APIException ex) {
			// this happens if they give the wrong oldPassword
			log.error("Change password failed", ex);
			throw new ValidationException(ex.getMessage());
		} catch (Exception e) {
			log.error("Change password failed", e);
		}
	}

	@RequestMapping(value = "/{userUuid}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void changeOthersPassword(@PathVariable("userUuid") String userUuid, @RequestBody Map<String, String> body) {
		String newPassword = body.get("newPassword");
		Context.addProxyPrivilege(PrivilegeConstants.VIEW_USERS);
		Context.addProxyPrivilege("Get Users"); // support later versions of OpenMRS
		User user;
		try {
			user = userService.getUserByUuid(userUuid);
		} finally {
			Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
			Context.removeProxyPrivilege("Get Users");
		}

		if (user == null || user.getUserId() == null) {
			throw new NullPointerException();
		} else {
			userService.changePassword(user, newPassword);
			SessionListener.invalidateAllSessions(user.getUuid());
		}
	}

	// This probably belongs in the base class, but we don't want to test all the behaviors that would change
	@ExceptionHandler(NullPointerException.class)
	@ResponseBody
	public SimpleObject handleNotFound(NullPointerException exception, HttpServletRequest request,
									   HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return RestUtil.wrapErrorResponse(exception, "User not found");
	}

	static class SessionListener {
		private static final Log log = LogFactory.getLog(SessionListener.class);

		private static final Map<String, List<HttpSession>> map = new HashMap<>();

		public static void sessionCreated(String userUuid, HttpSession httpSession) {
			if (!map.containsKey(userUuid))
				map.put(userUuid, new ArrayList<>());

			List<HttpSession> sessions = map.get(userUuid);
			if (sessions.contains(httpSession))
				return;

			sessions.add(httpSession);
			log.info(String.format("Added new session. Total sessions for user: %s = %d", userUuid, map.get(userUuid).size()));
		}

		public static void invalidateOtherSessions(String userUuid, HttpSession currentSession) {
			log.info(String.format("Finding other sessions for the user: %s, for session: %s", userUuid, currentSession));
			List<HttpSession> sessions = map.get(userUuid);
			for (HttpSession session : sessions) {
				if (!currentSession.getId().equals(session.getId())) {
					session.invalidate();
				}
			}
			ArrayList<HttpSession> httpSessions = new ArrayList<>();
			httpSessions.add(currentSession);
			map.put(userUuid, httpSessions);
			log.info(String.format("Invalidated %d other sessions for the user with this session", sessions.size() - 1));
		}

		public static void invalidateAllSessions(String userUuid) {
			log.info(String.format("Finding other sessions for the user: %s", userUuid));

			List<HttpSession> sessions = map.get(userUuid);
			if (sessions == null) {
				log.info("No sessions found for this user");
				return;
			}

			sessions.forEach(HttpSession::invalidate);
			map.remove(userUuid);
			log.info(String.format("Found %d sessions for the user: %s", sessions.size(), userUuid));
		}
	}
}
