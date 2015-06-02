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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Set;

/**
 * {@link Resource} for Role, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/role", supportedClass = Role.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class RoleResource1_8 extends MetadataDelegatingCrudResource<Role> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Role getByUniqueId(String uniqueId) {
		return Context.getUserService().getRoleByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public Role newDelegate() {
		return new Role();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#save(java.lang.Object)
	 */
	@Override
	public Role save(Role delegate) {
		return Context.getUserService().saveRole(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Role delegate, RequestContext context) throws ResponseException {
		if (delegate == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getUserService().purgeRole(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("retired");
			description.addProperty("privileges", Representation.REF);
			description.addProperty("inheritedRoles", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("retired");
			description.addProperty("privileges", Representation.DEFAULT);
			description.addProperty("inheritedRoles", Representation.DEFAULT);
			description.addProperty("allInheritedRoles", Representation.DEFAULT);
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		// you cannot edit the name of an existing role, since that is the PK
		description.addProperty("description");
		description.addProperty("privileges");
		description.addProperty("inheritedRoles");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		
		description.addProperty("description");
		description.addProperty("privileges");
		description.addProperty("inheritedRoles");
		
		return description;
	}
	
	/**
	 * Retrieve the role name
	 * @param role
	 * @return
	 */
	@PropertyGetter("name")
	public static String getRoleName(Role role) {
		return role.getRole(); // The role "name" is actually stored in the role property.
	}
	
	/**
	 * Set the role name
	 * 
	 * @param role delegate object
	 * @param name of role
	 */
	@PropertySetter("name")
	public static void setRoleName(Role role, Object name) {
		role.setRole((String) name); // The role "name" is actually stored in the role property.
	}
	
	/**
	 * Returns roles this role inherits from
	 * @param role delegate object
	 * @return A set of privileges associated with this role
	 */
	@PropertyGetter("allInheritedRoles")
	public Set<Role> getAllInheritedRoles(Role role) {
		return RestUtil.removeRetiredData(role.getAllParentRoles());
	}
	
	/**
	 * Returns roles this role inherits from
	 * @param role delegate object
	 * @return A set of privileges associated with this role
	 */
	@PropertyGetter("inheritedRoles")
	public Set<Role> getInheritedRoles(Role role) {
		if (role.getInheritedRoles() == null)
			return null;
		
		return RestUtil.removeRetiredData(role.getInheritedRoles());
	}
	
	/**
	 * Returns roles this role inherits from
	 * @param role delegate object
	 * @return A set of privileges associated with this role
	 */
	@PropertyGetter("privileges")
	public Set<Privilege> getPrivileges(Role role) {
		if (role.getPrivileges() == null)
			return null;
		
		return RestUtil.removeRetiredData(role.getPrivileges());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource#getDisplayString(org.openmrs.OpenmrsMetadata)
	 */
	@Override
	@PropertyGetter("display")
	public String getDisplayString(Role delegate) {
		// TODO can we delegate to superclass for message-based i18n?
		String ret = getRoleName(delegate);
		return StringUtils.isNotBlank(ret) ? ret : "[No Name]";
	}
	
	@Override
	protected NeedsPaging<Role> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<Role>(Context.getUserService().getAllRoles(), context);
	}
	
}
