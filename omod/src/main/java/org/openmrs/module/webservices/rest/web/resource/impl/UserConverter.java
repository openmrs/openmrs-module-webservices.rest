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
package org.openmrs.module.webservices.rest.web.resource.impl;

import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.UserAndPassword;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.UserResource;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * An implementation of Converter to be able to create a representation from a User
 * when User is used in another resource. Currently UserAndPassword doesn't convert User
 */
@Handler(supports = User.class, order = 0)
public class UserConverter implements Converter<User> {
	
	@Override
	public User getByUniqueId(String string) {
		return Context.getUserService().getUserByUuid(string);
	}
	
	@Override
	public Object asRepresentation(User instance, Representation rep) throws ConversionException {
		UserAndPassword userPass = new UserAndPassword(instance);
		UserResource userResource = new UserResource();
		return userResource.asRepresentation(userPass, rep);
	}
	
	@Override
	public Object getProperty(User instance, String propertyName) throws ConversionException {
		UserAndPassword userPass = new UserAndPassword(instance);
		UserResource userResource = new UserResource();
		return userResource.getProperty(userPass, propertyName);
	}
	
	@Override
	public void setProperty(User instance, String propertyName, Object value) throws ConversionException {
		UserAndPassword userPass = new UserAndPassword(instance);
		UserResource userResource = new UserResource();
		userResource.setProperty(userPass, propertyName, value);
	}
}
