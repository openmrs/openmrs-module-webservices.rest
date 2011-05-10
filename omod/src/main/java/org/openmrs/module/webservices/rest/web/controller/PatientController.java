package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.RestUtil;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.representation.Representation;
import org.openmrs.module.webservices.rest.resource.PatientResource;
import org.openmrs.module.webservices.rest.web.propertyeditor.UuidEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for REST web service access to the Patient resource. Supports CRUD on the resource
 * itself, and listing and addition of some subresources.
 */
@Controller
@RequestMapping(value = "/rest/patient")
public class PatientController {
	
	@InitBinder
	public void initBinder(WebDataBinder wdb) {
		wdb.registerCustomEditor(Patient.class, new UuidEditor(PatientService.class, "getPatientByUuid"));
	}
	
	/**
	 * @param patient
	 * @param request
	 * @return
	 * @throws Exception
	 * @should get a representation of a patient
	 */
	@RequestMapping(value = "/{patientUuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object getPatient(@PathVariable("patientUuid") Patient patient, WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		PatientResource resource = new PatientResource(patient);
		return resource.retrieve(context);
	}
	
	/**
	 * @param post
	 * @param request
	 * @return
	 * @throws Exception
	 * @should create a new patient
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createPatient(@RequestBody SimpleObject post, WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		PatientResource resource = new PatientResource();
		return resource.create(post, context).asRepresentation(Representation.DEFAULT);
	}
	
	/**
	 * @param patient
	 * @param post
	 * @param request
	 * @return
	 * @throws Exception
	 * @should change a property on a patient
	 */
	@RequestMapping(value = "/{patientUuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object updatePatient(@PathVariable("patientUuid") Patient patient, @RequestBody SimpleObject post,
	                            WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		PatientResource resource = new PatientResource(patient);
		resource.update(post, context);
		return resource.retrieve(context);
	}
	
	/**
	 * @param patient
	 * @param reason
	 * @param request
	 * @throws Exception
	 * @should void a patient
	 */
	@RequestMapping(value = "/{patientUuid}", method = RequestMethod.DELETE, params = "!purge")
	public void voidPatient(@PathVariable("patientUuid") Patient patient,
	                        @RequestParam(value = "reason", defaultValue = "web service call") String reason,
	                        WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		PatientResource resource = new PatientResource(patient);
		resource.delete(reason, context);
	}
	
	/**
	 * @param patient
	 * @param request
	 * @throws Exception
	 * @should fail to purge a patient with dependent data
	 */
	@RequestMapping(value = "/{patientUuid}", method = RequestMethod.DELETE, params = "purge=true")
	public void purgePatient(@PathVariable("patientUuid") Patient patient, WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		PatientResource resource = new PatientResource(patient);
		resource.purge("uuid", context);
	}
	
	/**
	 * @should return a list of names
	 */
	@RequestMapping(value = "/{patientUuid}/names", method = RequestMethod.GET)
	@ResponseBody
	public Object getNames(@PathVariable("patientUuid") Patient patient, WebRequest request) throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		PatientResource patientResource = new PatientResource(patient);
		return patientResource.getPropertyWithRepresentation("names", context.getRepresentation());
	}
	
	/**
	 * @should add a name
	 */
	@RequestMapping(value = "/{patientUuid}/names", method = RequestMethod.POST)
	@ResponseBody
	public Object addName(@RequestBody SimpleObject post, @PathVariable("patientUuid") Patient patient, WebRequest request)
	                                                                                                                       throws Exception {
		RequestContext context = RestUtil.getRequestContext(request);
		PatientResource patientResource = new PatientResource(patient);
		return patientResource.createPersonName(post, context).asRepresentation(Representation.DEFAULT);
	}
	
}
