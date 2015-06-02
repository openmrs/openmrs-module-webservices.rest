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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.util.ReflectionUtil;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;

/**
 * {@link Resource} for User, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/user", supportedClass = UserAndPassword1_8.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*, 1.10.*", "1.11.*", "1.12.*"})
public class UserResource1_8 extends MetadataDelegatingCrudResource<UserAndPassword1_8> {
	
	public UserResource1_8() {
		
	}
	
	@RepHandler(RefRepresentation.class)
	public SimpleObject asRef(UserAndPassword1_8 delegate) throws ConversionException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("display");
		if (delegate.isRetired()) {
			description.addProperty("retired");
		}
		description.addSelfLink();
		return convertDelegateToRepresentation(delegate, description);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("username");
			description.addProperty("systemId");
			description.addProperty("userProperties");
			description.addProperty("person", Representation.REF);
			description.addProperty("privileges", Representation.REF);
			description.addProperty("roles", Representation.REF);
			description.addProperty("retired");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("username");
			description.addProperty("systemId");
			description.addProperty("userProperties");
			description.addProperty("person", Representation.DEFAULT);
			description.addProperty("privileges", Representation.DEFAULT);
			description.addProperty("roles", Representation.DEFAULT);
			description.addProperty("allRoles", Representation.DEFAULT);
			description.addProperty("proficientLocales");
			description.addProperty("secretQuestion");
			description.addProperty("retired");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("username");
		description.addRequiredProperty("password");
		description.addRequiredProperty("person");
		
		description.addProperty("systemId");
		description.addProperty("userProperties");
		description.addProperty("roles");
		description.addProperty("proficientLocales");
		description.addProperty("secretQuestion");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
	 */
	@Override
	public UserAndPassword1_8 newDelegate() {
		return new UserAndPassword1_8();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public UserAndPassword1_8 save(UserAndPassword1_8 user) {
		User openmrsUser = new User();
		String password = user.getPassword();
		openmrsUser = Context.getUserService().saveUser(user.getUser(), password);
		return new UserAndPassword1_8(openmrsUser);
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public UserAndPassword1_8 getByUniqueId(String uuid) {
		return new UserAndPassword1_8(Context.getUserService().getUserByUuid(uuid));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(UserAndPassword1_8 user, String reason, RequestContext context) throws ResponseException {
		if (user.isRetired()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getUserService().retireUser(Context.getUserService().getUser(user.getId()), reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(UserAndPassword1_8 user, RequestContext context) throws ResponseException {
		if (user == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getUserService().purgeUser(user.getUser());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<UserAndPassword1_8> doSearch(RequestContext context) {
		List<UserAndPassword1_8> users = new ArrayList<UserAndPassword1_8>();
		for (User user : Context.getUserService().getUsers(context.getParameter("q"), null, context.getIncludeAll())) {
			users.add(new UserAndPassword1_8(user));
		}
		return new NeedsPaging<UserAndPassword1_8>(users, context);
	}
	
	/**
	 * Overrides BaseDelegatingResource getProperty method to get properties from User property of
	 * UserAndPassword instead of UserAndPassword itself
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#setProperty(T,
	 *      java.lang.String, java.lang.Object)
	 * @param instance
	 * @param propertyName
	 * @throws ConversionException
	 */
	@Override
	public Object getProperty(UserAndPassword1_8 instance, String propertyName) throws ConversionException {
		try {
			if (propertyName.equals("password")) {
				return instance.getPassword();
			} else {
				// try to find a @PropertyGetter-annotated method
				Method annotatedGetter = ReflectionUtil.findPropertyGetterMethod(this, propertyName);
				if (annotatedGetter != null) {
					return annotatedGetter.invoke(this, instance);
				}
				return PropertyUtils.getProperty(instance.getUser(), propertyName);
			}
		}
		catch (Exception ex) {
			throw new ConversionException(propertyName, ex);
		}
	}
	
	/**
	 * Overrides BaseDelegatingResource setProperty method to allow properties to be set on User
	 * property of UserAndPassword instead of UserAndPassword itself
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#setProperty(T,
	 *      java.lang.String, java.lang.Object)
	 * @param instance
	 * @param propertyName
	 * @param value
	 * @throws ConversionException
	 */
	@Override
	public void setProperty(Object instance, String propertyName, Object value) throws ConversionException {
		try {
			UserAndPassword1_8 userAndPassword = (UserAndPassword1_8) instance;
			if (propertyName.equals("password")) {
				userAndPassword.setPassword(value != null ? value.toString() : null);
			} else {
				// just treat every other property like a we're on the User object
				super.setProperty(userAndPassword.getUser(), propertyName, value);
			}
		}
		catch (Exception ex) {
			throw new ConversionException(propertyName, ex);
		}
	}
	
	/**
	 * @param user
	 * @return roles for user
	 * @see User#getRoles()
	 */
	public Set<Role> getRoles(UserAndPassword1_8 user) {
		if (user.getUser().getRoles() == null)
			return null;
		return RestUtil.removeRetiredData(user.getUser().getRoles());
	}
	
	/**
	 * @param user
	 * @return all roles for user
	 * @see User#getAllRoles()
	 */
	public Set<Role> getAllRoles(UserAndPassword1_8 user) {
		if (user.getUser().getRoles() == null)
			return null;
		return RestUtil.removeRetiredData(user.getUser().getAllRoles()); //Get all active roles, including inherited roles
	}
	
	/**
	 * @param user
	 * @return username or systemId (for concise display purposes)
	 */
	@PropertyGetter("display")
	public String getDisplayString(UserAndPassword1_8 user) {
		StringBuilder ret = new StringBuilder();
		User u = user.getUser();
		ret.append(StringUtils.isNotEmpty(u.getUsername()) ? u.getUsername() : u.getSystemId());
		return ret.toString();
	}
	
	/**
	 * Overridden here since the unique id is not on UserAndPassword directly
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUniqueId(java.lang.Object)
	 */
	@Override
	protected String getUniqueId(UserAndPassword1_8 delegate) {
		return delegate.getUser().getUuid();
	}
	
	/**
	 * Overridden here since the auditInfo is not on UserAndPassword directly, but on the User
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource#getAuditInfo(java.lang.Object)
	 */
	@Override
	public SimpleObject getAuditInfo(UserAndPassword1_8 delegate) throws Exception {
		User user = delegate.getUser();
		SimpleObject ret = new SimpleObject();
		ret.put("creator", ConversionUtil.getPropertyWithRepresentation(user, "creator", Representation.REF));
		ret.put("dateCreated", ConversionUtil.convertToRepresentation(user.getDateCreated(), Representation.DEFAULT));
		if (user.isRetired()) {
			ret.put("retiredBy", ConversionUtil.getPropertyWithRepresentation(user, "retiredBy", Representation.REF));
			ret.put("dateRetired", ConversionUtil.convertToRepresentation(user.getDateRetired(), Representation.DEFAULT));
			ret.put("retireReason", ConversionUtil.convertToRepresentation(user.getRetireReason(), Representation.DEFAULT));
		}
		ret.put("changedBy", ConversionUtil.getPropertyWithRepresentation(user, "changedBy", Representation.REF));
		ret.put("dateChanged", ConversionUtil.convertToRepresentation(user.getDateChanged(), Representation.DEFAULT));
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<UserAndPassword1_8> doGetAll(RequestContext context) {
		List<UserAndPassword1_8> users = new ArrayList<UserAndPassword1_8>();
		for (User user : Context.getUserService().getAllUsers()) {
			users.add(new UserAndPassword1_8(user));
		}
		return new NeedsPaging<UserAndPassword1_8>(users, context);
	}
}
