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
package org.openmrs.module.webservices.rest.web.v1_0.converter.openmrs1_8;

import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword;

/**
 * An implementation of Converter to be able to create a representation from a User
 * when User is used in another resource. Currently UserAndPassword doesn't convert User
 */
@Handler(supports = User.class, order = 0)
public class UserConverter implements Converter<User> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#getByUniqueId(java.lang.String)
	 */
	@Override
	public User getByUniqueId(String string) {
		return Context.getUserService().getUserByUuid(string);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#newInstance(java.lang.String)
	 */
	@Override
	public User newInstance(String type) {
		return new User();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#asRepresentation(T, org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public SimpleObject asRepresentation(User instance, Representation rep) throws ConversionException {
		UserAndPassword userPass = new UserAndPassword(instance);
		UserResource userResource = (UserResource) Context.getService(RestService.class).getResourceByName("user");
		return userResource.asRepresentation(userPass, rep);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#getProperty(T, java.lang.String)
	 */
	@Override
	public Object getProperty(User instance, String propertyName) throws ConversionException {
		UserAndPassword userPass = new UserAndPassword(instance);
		UserResource userResource = (UserResource) Context.getService(RestService.class).getResourceByName("user");
		return userResource.getProperty(userPass, propertyName);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#setProperty(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setProperty(Object instance, String propertyName, Object value) throws ConversionException {
		UserAndPassword userPass = new UserAndPassword((User) instance);
		UserResource userResource = (UserResource) Context.getService(RestService.class).getResourceByName("user");
		userResource.setProperty(userPass, propertyName, value);
	}
}
