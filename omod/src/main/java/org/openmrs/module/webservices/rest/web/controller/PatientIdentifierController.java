package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.PatientIdentifierResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/rest/patient/{parentUuid}/identifiers")
public class PatientIdentifierController extends BaseSubResourceController<PatientIdentifierResource> {
	
	public PatientIdentifierResource getResource() {
		return Context.getService(RestService.class).getResource(PatientIdentifierResource.class); 
	}
	
}
