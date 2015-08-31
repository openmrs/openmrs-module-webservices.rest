/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

public class CustomDatatypeRepresentation {
	
	private String uuid;
	
	private String datatypeClassname;
	
	private List<String> handlerClassnames = new ArrayList<String>();
	
    public String getUuid() {
	    return uuid;
    }
    
    public String getTextToDisplay() {
    	String display = "";
    	if (datatypeClassname != null && datatypeClassname.lastIndexOf(".") != -1) {
    		String classname = datatypeClassname.substring(datatypeClassname.lastIndexOf(".") + 1);
    		String[] splittedClassname = StringUtils.splitByCharacterTypeCamelCase(classname);
    		
    		for (int i = 0; i < splittedClassname.length; i++) {
    			splittedClassname[i] = StringUtils.capitalize(splittedClassname[i]);
    		}
    		display = StringUtils.join(splittedClassname, " ");
    	}
    	
    	return display;
    }
	
	public String getDatatypeClassname() {
		return datatypeClassname;
	}
	
	public void setDatatypeClassname(String datatypeClass) {
		this.datatypeClassname = datatypeClass;
		this.uuid = UUID.nameUUIDFromBytes(datatypeClassname.getBytes()).toString();
	}
	
	public List<String> getHandlerClassnames() {
		return handlerClassnames;
	}
	
	public void setHandlerClassnames(List<String> handlerClassnames) {
		this.handlerClassnames = handlerClassnames;
	}
}
