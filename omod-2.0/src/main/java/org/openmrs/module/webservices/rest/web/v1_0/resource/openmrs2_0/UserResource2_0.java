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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;

/**
 * {@link Resource} for User, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/user", supportedClass = UserAndPassword1_8.class, supportedOpenmrsVersions = {"2.0.*", "2.1.*"})
public class UserResource2_0 extends UserResource1_8 {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public UserAndPassword1_8 save(UserAndPassword1_8 user) {
		User openmrsUser = new User();
		if (user.getUser().getUserId() == null) {
			openmrsUser = Context.getUserService().createUser(user.getUser(), user.getPassword());
		} else {
			openmrsUser = Context.getUserService().saveUser(user.getUser());
			Context.refreshAuthenticatedUser();
			if (openmrsUser.getId() != null && StringUtils.isNotBlank(user.getPassword())) {
				Context.getUserService().changePassword(openmrsUser, user.getPassword());
			}
		}
		
		return new UserAndPassword1_8(openmrsUser);
		
	}
}
