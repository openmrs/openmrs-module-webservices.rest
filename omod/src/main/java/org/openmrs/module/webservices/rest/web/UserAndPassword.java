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
package org.openmrs.module.webservices.rest.web;


import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;

/**
 * This class is a custom class extending org.openmrs.User by adding 
 * password to it and being able to add it as a webservice
 */
public class UserAndPassword extends User implements Serializable
{

    public static final long serialVersionUID = 102293L;

    private static final Log log = LogFactory.getLog( UserAndPassword.class );

    //Fields
    private String password;
    
    private User user;

    //Constructors
    /** default constructor */
    public UserAndPassword()
    {
    }

    public UserAndPassword( User user )
    {
        this.setId( user.getId() );
        this.setPerson( user.getPerson() );
        this.setRoles( user.getRoles() );
        this.setSecretQuestion( user.getSecretQuestion() );
        this.setSystemId( user.getSystemId() );
        this.setUserId( user.getUserId() );
        this.setUserProperties( user.getUserProperties() );
        this.setUsername( user.getUsername() );
        this.setUuid( user.getUuid() );
    }

    /**
     * Compares two objects for similarity. This passes through to the parent object
     * (org.openmrs.User) in order to check similarity of User-UserAndPassword
     * 
     * @param obj
     * @return boolean true/false whether or not they are the same objects
     * @see org.openmrs.User#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj )
    {
        return super.equals( obj );
    }

    /**
     * The hashcode for a patient/person is used to index the objects in a tree This must pass
     * through to the parent object (org.openmrs.Person) in order to get similarity of
     * person/patient objects
     * 
     * @see org.openmrs.User#hashCode()
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    /**
     * @return password 
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set 
     */
    public void setPassword( String password )
    {
        this.password = password;
    }
}
