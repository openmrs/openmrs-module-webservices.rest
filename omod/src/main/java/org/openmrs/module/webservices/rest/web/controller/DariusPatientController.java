package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Patient;
import org.openmrs.module.webservices.rest.Representation;
import org.openmrs.module.webservices.rest.RepresentationFactory;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSUtil;
import org.openmrs.module.webservices.rest.resource.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.resource.PatientCrudResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping(value = "/rest/darius/patient")
public class DariusPatientController {

	@RequestMapping(value = "/{patientUuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object getPatient(
			@PathVariable("patientUuid") Patient patient,
			WebRequest request) throws Exception {

		String representation = WSUtil.getRepresentation(request);
		PatientCrudResource resource = new PatientCrudResource(patient);
		return resource.asRepresentation(RepresentationFactory.get(representation));
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createPatient(@RequestBody SimpleObject post) throws Exception {

		PatientCrudResource resource = new PatientCrudResource();
		return resource.create(post).asRepresentation(RepresentationFactory.DEFAULT);
	}
	
	@RequestMapping(value = "/{patientUuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object updatePatient(
			@PathVariable("patientUuid") Patient patient,
			@RequestBody SimpleObject post) throws Exception {

		PatientCrudResource resource = new PatientCrudResource(patient);
		resource.update(post);
		return resource.asRepresentation(RepresentationFactory.DEFAULT);
	}
	
	@RequestMapping(value = "/{patientUuid}", method = RequestMethod.DELETE, params="!purge")
	public void voidPatient(
			@PathVariable("patientUuid") Patient patient,
			@RequestParam(value="reason", defaultValue="web service call") String reason) throws Exception {

		PatientCrudResource resource = new PatientCrudResource(patient);
		resource.delete(reason);
	}
	
	@RequestMapping(value = "/{patientUuid}", method = RequestMethod.DELETE, params="purge=true")
	public void purgePatient(
			@PathVariable("patientUuid") Patient patient) throws Exception {

		PatientCrudResource resource = new PatientCrudResource(patient);
		resource.purge("uuid");
	}
	
	/**
	 * @should return a list of names
	 */
	@RequestMapping(value = "/{patientUuid}/names", method = RequestMethod.GET)
	@ResponseBody
	public Object getNames(@PathVariable("patientUuid") Patient patient,
	                       WebRequest request) throws Exception {
		Representation rep = RepresentationFactory.get(WSUtil.getRepresentation(request));
		PatientCrudResource patientResource = new PatientCrudResource(patient);
		return patientResource.getPropertyWithRepresentation("names", rep);
	}

	/**
	 * @should add a name
	 */
	@RequestMapping(value = "/{patientUuid}/names", method = RequestMethod.POST)
	@ResponseBody
	public Object addName(@RequestBody SimpleObject post,
	                    @PathVariable("patientUuid") Patient patient) throws Exception {
		PatientCrudResource patientResource = new PatientCrudResource(patient);
		return patientResource.createSubResource("names", post).asRepresentation(RepresentationFactory.DEFAULT);
	}

}
