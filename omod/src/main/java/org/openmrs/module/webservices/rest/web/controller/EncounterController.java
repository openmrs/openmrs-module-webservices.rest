package org.openmrs.module.webservices.rest.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.resource.EncounterResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Encounter resource. Supports CRUD on the resource
 * itself, and listing and addition of some subresources.
 */
@Controller
@RequestMapping(value = "/rest/encounter")
public class EncounterController extends BaseCrudController<EncounterResource> {
	
	/**
	 * Fetch encounters for a given patient
	 * @param patientUniqueId
	 * @param request
	 * @param response
	 * @return encounters for the given patient
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "patient")
	@WSDoc("Fetch all non-retired encounters for a given patient")
	@ResponseBody
	public SimpleObject searchByPatient(@RequestParam("patient") String patientUniqueId, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		EncounterResource er = getResource();
		RequestContext context = RestUtil.getRequestContext(request);
		return er.getEncountersByPatient(patientUniqueId, context);
	}
	
}
