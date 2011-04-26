/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSConstants;
import org.openmrs.module.webservices.rest.WSUtil;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFound;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * 
 */
@Controller
@RequestMapping(value = "/rest")
public class PatientController extends BaseResourceController {

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Get a patient object.<br/>
	 * Returns 404 HTTP Status if no patient with given uuid is found
	 * 
	 * @param patient
	 * @param request
	 * @return Patient object.
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/patient/{patientUuid}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getPatient(
			@PathVariable("patientUuid") Patient patient, WebRequest request)
			throws ResponseException {

		if (patient == null) {
			throw new ObjectNotFound();
		}

		String representation = WSUtil.getRepresentation(request);
		try {
			return wsUtil.convert(patient, representation);
		} catch (Exception e) {
			log.error("Unable to convert " + patient, e);
			throw new ConversionException();
		}
	}

	/**
	 * Gets a list of people to matching the given query.
	 * 
	 * @see PatientService#getPatients(String)
	 * 
	 * @param query
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/patient/", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleObject> getPatients(
			@RequestParam(value = "q", required = false) String query,
			WebRequest request) throws ResponseException {

		String representation = WSUtil.getRepresentation(request);

		List<Patient> searchResults = Context.getPatientService().getPatients(
				query);

		try {
			return wsUtil.convertList(null, searchResults, representation,
					WSUtil.getLimit(request));
		} catch (Exception e) {
			log.error("Unable get people with query " + query, e);
			throw new ConversionException();
		}
	}

	/**
	 * @param request
	 * @param patientValues
	 * @return the newly created patient object with default representation
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/patient/", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject createPatient(WebRequest request,
			@RequestBody Map<String, Object> patientValues)
			throws ResponseException {

		Patient p = new Patient();
		wsUtil.setValues(p, patientValues);

		Context.getPatientService().savePatient(p);

		// return the newly changed patient object
		try {
			return wsUtil.convert(p, WSConstants.REPRESENTATION_DEFAULT);
		} catch (Exception e) {
			// uh oh, this is really bad, we JUST made this object!
			log.error("Unable to convert the newly created patient " + p, e);
			throw new ConversionException();
		}
	}

	/**
	 * @param patient
	 * @param patientValues
	 * @return the newly changed patient with the default representation
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/patient/{patientUuid}", method = RequestMethod.PUT)
	@ResponseBody
	public SimpleObject updatePatient(
			@PathVariable("patientUuid") Patient patient,
			@RequestBody Map<String, Object> patientValues)
			throws ResponseException {

		// looks up setters, determines if its settable, converts it potentially
		wsUtil.setValues(patient, patientValues);

		Context.getPatientService().savePatient(patient);

		// return the newly changed patient object
		try {
			return wsUtil.convert(patient, WSConstants.REPRESENTATION_DEFAULT);
		} catch (Exception e) {
			log.error("Unable to convert the newly changed patient " + patient,
					e);
			throw new ConversionException();
		}
	}

}
