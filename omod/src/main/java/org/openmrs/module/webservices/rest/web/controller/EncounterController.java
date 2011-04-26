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
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
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
public class EncounterController extends BaseResourceController {

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Get a encounter object.<br/>
	 * Returns 404 HTTP Status if no encounter with given uuid is found
	 * 
	 * @param encounter
	 * @param request
	 * @return Encounter object.
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/encounter/{encounterUuid}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getEncounter(
			@PathVariable("encounterUuid") Encounter encounter,
			WebRequest request) throws ResponseException {

		if (encounter == null) {
			throw new ObjectNotFound();
		}

		String representation = WSUtil.getRepresentation(request);
		try {
			return wsUtil.convert(encounter, representation);
		} catch (Exception e) {
			log.error("Unable to convert " + encounter, e);
			throw new ConversionException();
		}
	}

	/**
	 * Gets a list of people to matching the given query.
	 * 
	 * @see EncounterService#getEncounters(Patient, org.openmrs.Location,
	 *      java.util.Date, java.util.Date, java.util.Collection,
	 *      java.util.Collection, java.util.Collection, boolean)
	 * 
	 * @param query
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/encounter/", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleObject> getEncounters(
			@RequestParam(value = "patient", required = false) Patient patient,
			WebRequest request) throws ResponseException {

		String representation = WSUtil.getRepresentation(request);

		List<Encounter> searchResults = Context.getEncounterService()
				.getEncounters(patient, null, null, null, null, null, null,
						false);

		try {
			return wsUtil.convertList(null, searchResults, representation,
					WSUtil.getLimit(request));
		} catch (Exception e) {
			log.error("Unable get people with patient " + patient, e);
			throw new ConversionException();
		}
	}

	/**
	 * @param request
	 * @param encounterValues
	 * @return the newly created encounter object with default representation
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/encounter/", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject createEncounter(WebRequest request,
			@RequestBody Map<String, Object> encounterValues)
			throws ResponseException {

		Encounter p = new Encounter();
		wsUtil.setValues(p, encounterValues);

		Context.getEncounterService().saveEncounter(p);

		// return the newly changed encounter object
		try {
			return wsUtil.convert(p, WSConstants.REPRESENTATION_DEFAULT);
		} catch (Exception e) {
			// uh oh, this is really bad, we JUST made this object!
			log.error("Unable to convert the newly created encounter " + p, e);
			throw new ConversionException();
		}
	}

	/**
	 * @param encounter
	 * @param encounterValues
	 * @return the newly changed encounter with the default representation
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/encounter/{encounterUuid}", method = RequestMethod.PUT)
	@ResponseBody
	public SimpleObject updateEncounter(
			@PathVariable("encounterUuid") Encounter encounter,
			@RequestBody Map<String, Object> encounterValues)
			throws ResponseException {

		// looks up setters, determines if its settable, converts it potentially
		wsUtil.setValues(encounter, encounterValues);

		Context.getEncounterService().saveEncounter(encounter);

		// return the newly changed encounter object
		try {
			return wsUtil
					.convert(encounter, WSConstants.REPRESENTATION_DEFAULT);
		} catch (Exception e) {
			log.error("Unable to convert the newly changed encounter "
					+ encounter, e);
			throw new ConversionException();
		}
	}

}
