/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Set;

/**
 * {@link Resource} for Role, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/role", supportedClass = Role.class, supportedOpenmrsVersions = { "1.8.* - 9.*" })
public class RoleResource1_8 extends DelegatingCrudResource<Role> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	public Role getByUniqueId(String uniqueId) {
		return Context.getUserService().getRoleByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	public Role newDelegate() {
		return new Role();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#save(java.lang.Object)
	 */
	public Role save(Role delegate) {
		return Context.getUserService().saveRole(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
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
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
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
			description.addProperty("privileges", Representation.DEFAULT);
			description.addProperty("inheritedRoles", Representation.DEFAULT);
			description.addProperty("allInheritedRoles", Representation.DEFAULT);
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}

	@RepHandler(RefRepresentation.class)
	public SimpleObject convertToRef(Role delegate) throws ConversionException {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("display");
		rep.addSelfLink();
		return convertDelegateToRepresentation(delegate, rep);
	}
	
	@RepHandler(DefaultRepresentation.class)
	public SimpleObject asDefaultRep(Role delegate) throws Exception {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("display");
		rep.addProperty("name");
		rep.addProperty("description");
		rep.addSelfLink();
		rep.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		return convertDelegateToRepresentation(delegate, rep);
	}
	
	@RepHandler(FullRepresentation.class)
	public SimpleObject asFullRep(Role delegate) throws Exception {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("display");
		rep.addProperty("name");
		rep.addProperty("description");
		rep.addProperty("auditInfo");
		rep.addSelfLink();
		return convertDelegateToRepresentation(delegate, rep);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		// you cannot edit the name of an existing role, since that is the PK
		description.addProperty("description");
		description.addProperty("privileges");
		description.addProperty("inheritedRoles");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getGETModel(Representation)
	 */
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation) {
			model
					.property("uuid", new StringProperty())
					.property("display", new StringProperty())
					.property("name", new StringProperty())
					.property("description", new StringProperty())
					.property("privileges", new ArrayProperty(new RefProperty("#/definitions/PrivilegeGetRef")))
					.property("inheritedRoles", new ArrayProperty(new RefProperty("#/definitions/RoleGetRef")));
		}
		if (rep instanceof FullRepresentation) {
			model
					.property("uuid", new StringProperty())
					.property("display", new StringProperty())
					.property("name", new StringProperty())
					.property("description", new StringProperty())
					.property("privileges", new ArrayProperty(new RefProperty("#/definitions/PrivilegeGet")))
					.property("inheritedRoles", new ArrayProperty(new RefProperty("#/definitions/RoleGet")))
					.property("allInheritedRoles", new ArrayProperty(new RefProperty("#/definitions/RoleGet")));
		}
		return model;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCREATEModel(Representation)
	 */
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
				.property("name", new StringProperty())
				.property("description", new StringProperty())
				.required("name")
				.property("privileges", new ArrayProperty(new RefProperty("#/definitions/PrivilegeCreate")))
				.property("inheritedRoles", new ArrayProperty(new RefProperty("#/definitions/RoleCreate")));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUPDATEModel(Representation) ()
	 */
	public Model getUPDATEModel(Representation rep) {
		return new ModelImpl()
				.property("description", new StringProperty())
				.property("privileges", new ArrayProperty(new RefProperty("#/definitions/PrivilegeCreate")))
				.property("inheritedRoles", new ArrayProperty(new RefProperty("#/definitions/RoleCreate")));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
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
	 * 
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
	 * 
	 * @param role delegate object
	 * @return A set of privileges associated with this role
	 */
	@PropertyGetter("allInheritedRoles")
	public Set<Role> getAllInheritedRoles(Role role) {
		return role.getAllParentRoles();
	}
	
	/**
	 * Returns roles this role inherits from
	 * 
	 * @param role delegate object
	 * @return A set of privileges associated with this role
	 */
	@PropertyGetter("inheritedRoles")
	public Set<Role> getInheritedRoles(Role role) {
		if (role.getInheritedRoles() == null)
			return null;
		
		return role.getInheritedRoles();
	}
	
	/**
	 * Returns roles this role inherits from
	 * 
	 * @param role delegate object
	 * @return A set of privileges associated with this role
	 */
	@PropertyGetter("privileges")
	public Set<Privilege> getPrivileges(Role role) {
		if (role.getPrivileges() == null)
			return null;
		
		return RestUtil.removeRetiredData(role.getPrivileges());
	}

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

	@Override
	protected void delete(Role delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}
