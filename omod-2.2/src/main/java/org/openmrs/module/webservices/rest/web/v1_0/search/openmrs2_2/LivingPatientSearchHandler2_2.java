/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs2_2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

@Component
public class LivingPatientSearchHandler2_2 implements SearchHandler {
	
	private final SearchConfig searchConfig = new SearchConfig("Serch for living patients", RestConstants.VERSION_1 + "/patient",
	        Arrays.asList("1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*"),
	        Arrays.asList(new SearchQuery.Builder("Allows you to find allpatients or only living patients")
	                .withOptionalParameters("includeDead").build()));

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	
	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		String includeDeadstr = context.getParameter("includeDead");
		
        Boolean includeDead =  StringUtils.isNotBlank(includeDeadstr) ? Boolean.parseBoolean(includeDeadstr) : false;
        List<Patient> allPatients = Context.getPatientService().getAllPatients();
      
        if(includeDead) {
        	 if (allPatients != null && allPatients.size() > 0 ) {
        		 return new NeedsPaging<Patient>(allPatients, context);
     		 }
        	
        }
        else
        {
        	//Adding only living patients to the list
        	List<Patient> livingPatients = new ArrayList<Patient>();
        	 for (Patient patient : allPatients) {        		 
        		 if (allPatients != null && allPatients.size() > 0 && !patient.isDead()) {
        		
        		livingPatients.add(patient) ;        			
       		 }
        		 
        	
        	 }
        	 return new NeedsPaging<Patient>(livingPatients, context);
        }
		return 	new EmptySearchResult();
	}

	
	
	
}
