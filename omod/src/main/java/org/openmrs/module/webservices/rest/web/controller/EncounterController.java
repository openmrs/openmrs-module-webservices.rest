package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.EncounterResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for REST web service access to the Encounter resource.
 */
@Controller
@RequestMapping(value = "/rest/encounter")
public class EncounterController {
	
	@RequestMapping(value = "/{encounterUuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object getEncounter(
			@PathVariable("encounterUuid") Encounter encounter,
			WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		EncounterResource resource = new EncounterResource(encounter);
		return resource.asRepresentation(context.getRepresentation());
	}

	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createEncounter(@RequestBody SimpleObject post,
	                              WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		EncounterResource resource = new EncounterResource();
		resource.create(post, context);
		return resource.asRepresentation(Representation.DEFAULT);
	}
	
	@RequestMapping(value = "/{encounterUuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object updateEncounter(
			@PathVariable("encounterUuid") Encounter encounter,
			@RequestBody SimpleObject post,
			WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		EncounterResource resource = new EncounterResource(encounter);
		resource.update(post, context);
		return resource.asRepresentation(Representation.DEFAULT);
	}
	
	@RequestMapping(value = "/{encounterUuid}", method = RequestMethod.DELETE, params="!purge")
	public void voidEncounter(
			@PathVariable("encounterUuid") Encounter encounter,
			@RequestParam(value="reason", defaultValue="web service call") String reason,
			WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		EncounterResource resource = new EncounterResource(encounter);
		resource.delete(reason, context);
	}
	
	@RequestMapping(value = "/{encounterUuid}", method = RequestMethod.DELETE, params="purge=true")
	public void purgeEncounter(
			@PathVariable("encounterUuid") Encounter encounter,
			WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		EncounterResource resource = new EncounterResource(encounter);
		resource.purge(context);
	}
	
}
