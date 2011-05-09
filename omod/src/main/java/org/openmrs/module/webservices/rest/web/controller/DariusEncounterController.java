package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.Representation;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSUtil;
import org.openmrs.module.webservices.rest.api.RestService;
import org.openmrs.module.webservices.rest.resource.EncounterCrudResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping(value = "/rest/darius/encounter")
public class DariusEncounterController {
	
	@RequestMapping(value = "/{encounterUuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object getEncounter(
			@PathVariable("encounterUuid") Encounter encounter,
			WebRequest request) throws Exception {

		Representation rep = Context.getService(RestService.class).getRepresentation(WSUtil.getRepresentation(request));
		EncounterCrudResource resource = new EncounterCrudResource(encounter);
		return resource.asRepresentation(rep);
	}

	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createEncounter(@RequestBody SimpleObject post) throws Exception {

		EncounterCrudResource resource = new EncounterCrudResource();
		resource.create(post);
		return resource.asRepresentation(Representation.DEFAULT);
	}
	
	@RequestMapping(value = "/{encounterUuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object updateEncounter(
			@PathVariable("encounterUuid") Encounter encounter,
			@RequestBody SimpleObject post) throws Exception {

		EncounterCrudResource resource = new EncounterCrudResource(encounter);
		resource.update(post);
		return resource.asRepresentation(Representation.DEFAULT);
	}
	
	@RequestMapping(value = "/{encounterUuid}", method = RequestMethod.DELETE, params="!purge")
	public void voidEncounter(
			@PathVariable("encounterUuid") Encounter encounter,
			@RequestParam(value="reason", defaultValue="web service call") String reason) throws Exception {

		EncounterCrudResource resource = new EncounterCrudResource(encounter);
		resource.delete(reason);
	}
	
	@RequestMapping(value = "/{encounterUuid}", method = RequestMethod.DELETE, params="purge=true")
	public void purgeEncounter(
			@PathVariable("encounterUuid") Encounter encounter) throws Exception {

		EncounterCrudResource resource = new EncounterCrudResource(encounter);
		resource.purge("uuid");
	}
	
}
