/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;
import org.openmrs.util.PrivilegeConstants;

/**
 * {@link Resource} for User, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/user", supportedClass = UserAndPassword1_8.class, supportedOpenmrsVersions = {
        "2.0.* - 2.1.*" })
public class UserResource2_0 extends UserResource1_8 {
	
	private static final String USER_PROPERTIES = "userProperties";
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		if (description != null) {
			description.removeProperty("secretQuestion");
		}
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(Object)
	 */
	@Override
	public UserAndPassword1_8 save(UserAndPassword1_8 user) {
		final User savedUser = createOrUpdateUser(user);
		refreshAuthenticatedUserIfNeeded(savedUser);
		return new UserAndPassword1_8(savedUser);
	}

	/**
	 * Lets an authenticated user save their own user properties (default locale, default location,
	 * favourites, ...) without the Edit Users privilege, through
	 * {@link org.openmrs.api.UserService#saveUserProperties(Map)}. This is what the user menu,
	 * location picker and similar settings in the frontend send. A request that changes anything
	 * else, or another user, goes through the usual update and still requires Edit Users.
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#update(String,
	 *      SimpleObject, RequestContext)
	 */
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		if (!isOwnUserPropertiesUpdate(uuid, propertiesToUpdate)) {
			return super.update(uuid, propertiesToUpdate, context);
		}
		
		Map<?, ?> requested = (Map<?, ?>) propertiesToUpdate.get(USER_PROPERTIES);
		Map<String, String> userProperties = new LinkedHashMap<String, String>();
		for (Map.Entry<?, ?> entry : requested.entrySet()) {
			if (entry.getKey() != null && entry.getValue() != null) {
				userProperties.put(entry.getKey().toString(), entry.getValue().toString());
			}
		}
		
		User savedUser = Context.getUserService().saveUserProperties(userProperties);
		// a user may read their own record: reloading it applies a changed default locale or location
		// to the current session, and the response describes it
		try {
			Context.addProxyPrivilege(PrivilegeConstants.GET_USERS);
			Context.refreshAuthenticatedUser();
			return ConversionUtil.convertToRepresentation(new UserAndPassword1_8(savedUser), context.getRepresentation());
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.GET_USERS);
		}
	}
	
	/**
	 * @return true if the request only replaces the user properties of the authenticated user, and
	 *         that user does not hold Edit Users (users who do keep the usual update)
	 */
	private boolean isOwnUserPropertiesUpdate(String uuid, SimpleObject propertiesToUpdate) {
		User authenticatedUser = Context.getAuthenticatedUser();
		return authenticatedUser != null && uuid != null && uuid.equals(authenticatedUser.getUuid())
		        && propertiesToUpdate != null && propertiesToUpdate.size() == 1
		        && propertiesToUpdate.get(USER_PROPERTIES) instanceof Map
		        && !Context.hasPrivilege(PrivilegeConstants.EDIT_USERS);
	}
	
	private User createOrUpdateUser(UserAndPassword1_8 user) {
		final User openmrsUser;

		if (user.getUser().getUserId() == null) {
			openmrsUser = Context.getUserService().createUser(user.getUser(), user.getPassword());
		} else {
			openmrsUser = Context.getUserService().saveUser(user.getUser());
			if (user.getPassword() != null) {
				Context.getUserService().changePassword(user.getUser(), user.getPassword());
			}
		}

		return openmrsUser;
	}

	private void refreshAuthenticatedUserIfNeeded(User savedUser) {
		final User authenticatedUser = Context.getAuthenticatedUser();
		if (authenticatedUser != null && authenticatedUser.getId().equals(savedUser.getId())) {
			Context.refreshAuthenticatedUser();
		}
	}
}
