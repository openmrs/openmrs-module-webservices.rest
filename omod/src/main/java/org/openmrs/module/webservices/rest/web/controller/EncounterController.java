package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.module.webservices.rest.web.resource.EncounterResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for REST web service access to the Encounter resource. Supports CRUD on the resource
 * itself, and listing and addition of some subresources.
 */
@Controller
@RequestMapping(value = "/rest/encounter")
public class EncounterController extends BaseCrudController<EncounterResource> {
	
}
