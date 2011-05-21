package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.CohortMemberResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping(value = "/rest/cohort/{parentUuid}/patients")
public class CohortMemberController extends BaseSubResourceController<CohortMemberResource> {
	
	public CohortMemberResource getResource() {
		return Context.getService(RestService.class).getResource(CohortMemberResource.class);
	}
	
	@Override
	public Object retrieve(String parentUuid, String uuid, WebRequest request) throws ResponseException {
	    // TODO Auto-generated method stub
	    return super.retrieve(parentUuid, uuid, request);
	}
}
