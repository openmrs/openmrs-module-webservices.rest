package org.openmrs.module.webservices.rest.web.v1_0.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.EncounterResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/encounter")
public class EncounterController {
	
	@Autowired
	RestService restService;
	
	/**
	 * Fetch encounters for a given patient
	 * @param patientUniqueId
	 * @param request
	 * @param response
	 * @return encounters for the given patient
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "patient")
	@WSDoc("Fetch all non-retired encounters for a patient with the given uuid")
	@ResponseBody
	public SimpleObject searchByPatient(@RequestParam("patient") String patientUniqueId, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		EncounterResource er = (EncounterResource) restService.getResourceByName("encounter");
		RequestContext context = RestUtil.getRequestContext(request);
		return er.getEncountersByPatient(patientUniqueId, context);
	}
	
}
