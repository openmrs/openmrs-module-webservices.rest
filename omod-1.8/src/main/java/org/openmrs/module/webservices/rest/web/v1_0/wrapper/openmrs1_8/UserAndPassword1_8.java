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
package org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8;

import java.io.Serializable;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.User;

/**
 * This class is a wrapper for org.openmrs.User and password 
 * that needs to be sent for creating a new User by a webservice call.
 * 
 * Requires extending BaseOpenmrsMetadata to be able to interact with
 * MetadataDelegatingCrudResource and making instance of metadata type
 * @see org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource
 */
public class UserAndPassword1_8 extends BaseOpenmrsMetadata implements Serializable {
	
	public static final long serialVersionUID = 1L;
	
	//Fields
	private String password;
	
	private User user;
	
	//Constructors
	/** default constructor */
	public UserAndPassword1_8() {
		user = new User();
	}
	
	/**
	 * @param user
	 */
	public UserAndPassword1_8(User user) {
		this.user = user;
	}
	
	/**
	 * @return password 
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @param password the password to set 
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return user the User property 
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @param user the user to set 
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return id
	 */
	@Override
	public Integer getId() {
		return getUser().getId();
	}
	
	/**
	 * @param integer the Id to set
	 */
	@Override
	public void setId(Integer integer) {
		getUser().setId(integer);
	}
	
	/**
	 * @see org.openmrs.BaseOpenmrsObject#getUuid()
	 */
	@Override
	public String getUuid() {
		return getUser().getUuid();
	}
	
	/**
	 * @see org.openmrs.BaseOpenmrsObject#setUuid(java.lang.String)
	 */
	@Override
	public void setUuid(String uuid) {
		getUser().setUuid(uuid);
	}
}
