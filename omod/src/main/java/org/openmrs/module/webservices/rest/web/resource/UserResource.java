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
package org.openmrs.module.webservices.rest.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.UserAndPassword;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for User, supporting standard CRUD operations
 */
@Resource("user")
@Handler(supports = UserAndPassword.class, order = 0)
public class UserResource extends MetadataDelegatingCrudResource<UserAndPassword> {
	
	public UserResource() {
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("username");
			description.addProperty("systemId");
			description.addProperty("userProperties");
			description.addProperty("person", Representation.REF);
			description.addProperty("roles", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("username");
			description.addProperty("systemId");
			description.addProperty("userProperties");
			description.addProperty("person", Representation.DEFAULT);
			description.addProperty("roles", Representation.DEFAULT);
			description.addProperty("proficientLocales");
			description.addProperty("secretQuestion");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
	 */
	@Override
	public UserAndPassword newDelegate() {
		return new UserAndPassword();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public UserAndPassword save(UserAndPassword user) {
		User openmrsUser = new User();
		String password = user.getPassword();
		openmrsUser = Context.getUserService().saveUser(user.getUser(), password);
		return new UserAndPassword(openmrsUser);
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public UserAndPassword getByUniqueId(String uuid) {
		return new UserAndPassword(Context.getUserService().getUserByUuid(uuid));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object, java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(UserAndPassword user, String reason, RequestContext context) throws ResponseException {
		if (user.isRetired()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getUserService().retireUser(Context.getUserService().getUser(user.getId()), reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(UserAndPassword user, RequestContext context) throws ResponseException {
		if (user == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getUserService().purgeUser(user.getUser());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<UserAndPassword> doSearch(String query, RequestContext context) {
		List<UserAndPassword> users = new ArrayList<UserAndPassword>();
		for (User user : Context.getUserService().getUsers(query, null, false)) {
			users.add(new UserAndPassword(user));
		}
		return new NeedsPaging<UserAndPassword>(users, context);
	}
	
	/**
	 * Overrides BaseDelegatingResource getProperty method to get properties from User property of UserAndPassword
	 * instead of UserAndPassword itself
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#setProperty(T, java.lang.String, java.lang.Object)
	 * @param instance
	 * @param propertyName
	 * @param value
	 * @throws ConversionException 
	 */
	@Override
	public Object getProperty(UserAndPassword instance, String propertyName) throws ConversionException {
		try {
			if (propertyName.equals("password"))
				return instance.getPassword();
			else
				return PropertyUtils.getProperty(instance.getUser(), propertyName);
		}
		catch (Exception ex) {
			throw new ConversionException(propertyName, ex);
		}
	}
	
	/**
	 * Overrides BaseDelegatingResource setProperty method to allow properties to be set on User property of UserAndPassword
	 * instead of UserAndPassword itself
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#setProperty(T, java.lang.String, java.lang.Object)
	 * @param instance
	 * @param propertyName
	 * @param value
	 * @throws ConversionException 
	 */
	@Override
	public void setProperty(UserAndPassword instance, String propertyName, Object value) throws ConversionException {
		try {
			if (propertyName.equals("password")) {
				instance.setPassword(value != null ? value.toString() : null);
			} else {
				Class<?> expectedType = PropertyUtils.getPropertyType(instance.getUser(), propertyName);
				if (value != null && !expectedType.isAssignableFrom(value.getClass()))
					value = ConversionUtil.convert(value, expectedType);
				PropertyUtils.setProperty(instance.getUser(), propertyName, value);
			}
		}
		catch (Exception ex) {
			throw new ConversionException(propertyName, ex);
		}
	}
	
	/**
	 * @param user
	 * @return username + fullname (for concise display purposes)
	 */
	public String getDisplayString(UserAndPassword user) {
		return user.getUser().getUsername() + " - " + user.getUser().getPersonName().getFullName();
	}
	
}
