package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.module.webservices.rest.web.resource.PatientIdentifierTypeResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controllers that allows access to the {@link PatientIdentifierTypeResource} for CRUD
 */
@Controller
@RequestMapping(value = "/rest/patientidentifiertype")
public class PatientIdentifierTypeController extends BaseCrudController<PatientIdentifierTypeResource> {

}
