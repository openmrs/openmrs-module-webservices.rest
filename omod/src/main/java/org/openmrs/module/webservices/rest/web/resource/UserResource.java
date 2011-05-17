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

import org.openmrs.module.webservices.rest.web.UserAndPassword;
import java.util.ArrayList;
import java.util.List;
import org.openmrs.User;

import org.openmrs.annotation.Handler;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for User, supporting standard CRUD operations
 */
@Resource("user")
@Handler(supports=UserAndPassword.class, order=0)
public class UserResource extends MetadataDelegatingCrudResource<UserAndPassword> {
	
    public UserResource() {
        
    }
    
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("uri", findMethod("getUri"));
			description.addProperty("display", findMethod("getDisplayString"));
			return description;
        }else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("username");
            description.addProperty("systemId");
            description.addProperty("userProperties");
			description.addProperty("person", Representation.REF);
			description.addProperty("roles", Representation.REF);
	    	description.addProperty("uri", findMethod("getUri"));
	    	return description;
	    } else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("username");
            description.addProperty("systemId");
            description.addProperty("userProperties");
			description.addProperty("person", Representation.DEFAULT);
			description.addProperty("roles", Representation.REF);
			description.addProperty("proficientLocales");
			description.addProperty("secretQuestion");
			description.addProperty("uri", findMethod("getUri"));
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
        UserService us = Context.getUserService();
        UserAndPassword userPass;
        if(us.getUserByUuid( user.getUuid()) !=null ){
            userPass =  new UserAndPassword (us.saveUser( us.getUserByUuid( user.getUuid()), user.getPassword()));
        } else{
            userPass =  new UserAndPassword (us.saveUser( new User(user.getPerson()), user.getPassword() ));
        }
        return userPass;
    }
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public UserAndPassword getByUniqueId(String uuid) {
	    return new UserAndPassword(Context.getUserService().getUserByUuid( uuid ));
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
	    Context.getUserService().retireUser( Context.getUserService().getUser( user.getId() ), reason );
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
		Context.getUserService().purgeUser( user );
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected List<UserAndPassword> doSearch(String query, RequestContext context) {
        ArrayList users = new ArrayList();
        for(User user : Context.getUserService().getUsers( query, null, false ))
        {
            users.add( user );
        }
	    return users;
	}
    
	/**
	 * @param user
	 * @return username + fullname (for concise display purposes)
	 */
	public String getDisplayString(UserAndPassword user) {
		return user.getUsername() + " - " + user.getPersonName().getFullName();
	}
	
}
