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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSConstants;
import org.openmrs.module.webservices.rest.WSUtil;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ObjectMismatch;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFound;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.response.SuccessfulDeletion;
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
public class PersonController extends BaseResourceController {

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Get a person object.<br/>
	 * Returns 404 HTTP Status if no person with given uuid is found
	 * 
	 * @param person
	 * @param request
	 * @return Person object.
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/person/{personUuid}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getPerson(@PathVariable("personUuid") Person person,
			WebRequest request) throws ResponseException {

		if (person == null) {
			throw new ObjectNotFound();
		}

		String representation = WSUtil.getRepresentation(request);
		try {
			return wsUtil.convert(person, representation);
		} catch (Exception e) {
			log.error("Unable to convert " + person, e);
			throw new ConversionException();
		}
	}

	/**
	 * Gets a list of people to matching the given query.
	 * 
	 * @see PersonService#getPeople(String, Boolean)
	 * 
	 * @param query
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/person/", method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleObject> getPeople(
			@RequestParam(value = "q", required = false) String query,
			WebRequest request) throws ResponseException {

		String representation = WSUtil.getRepresentation(request);

		List<Person> searchResults = Context.getPersonService().getPeople(
				query, false);

		try {
			return wsUtil.convertList(null, searchResults, representation,
					WSUtil.getLimit(request));
		} catch (Exception e) {
			log.error("Unable get people with query " + query, e);
			throw new ConversionException();
		}
	}

	/**
	 * @param person
	 * @param personName
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = { "/person/{personuuid}/name/{nameuuid}",
			"/person/name/{nameuuid}" }, method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getPersonName(
			@PathVariable("personuuid") Person person,
			@PathVariable("nameuuid") PersonName personName, WebRequest request)
			throws ResponseException {

		// the person uuid passed in is not the proper person for the given
		// personname passed in
		if (person != null && !personName.getPerson().equals(person))
			throw new ObjectMismatch();

		String representation = WSUtil.getRepresentation(request);
		try {
			return wsUtil.convert(personName, representation);
		} catch (Exception e) {
			log.error("Unable get a name: " + personName, e);
			throw new ConversionException();
		}
	}

	/**
	 * Creates a name on a person. This new name will be marked as preferred
	 * while all others are now not preferred.
	 * 
	 * @param person
	 * @param request
	 * @param name
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/person/{personUuid}/name/", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject addNameToPerson(
			@PathVariable("personUuid") Person person, WebRequest request,
			@RequestBody Map<String, Object> nameValues)
			throws ResponseException {

		String representation = WSUtil.getRepresentation(request);

		for (PersonName pn : person.getNames())
			pn.setPreferred(false);

		// TODO get PersonName object from POST content
		PersonName pn = new PersonName();
		wsUtil.setValues(pn, nameValues);
		pn.setPreferred(true);
		person.addName(pn);
		Context.getPersonService().savePerson(person);

		// return the newly added personname object
		try {
			return wsUtil.convert(pn, representation);
		} catch (Exception e) {
			// uh oh, this is really bad
			log.error("Unable to convert newly created person name: " + pn, e);
			throw new ConversionException();
		}
	}

	/**
	 * Marks all other names on a person as "not preferred". Makes the name with
	 * the given uuid preferred
	 * 
	 * @param person
	 * @param personName
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/person/{personUuid}/preferredname/{personNameUuid}", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject setPreferredNameOfAPerson(
			@PathVariable("personUuid") Person person,
			@PathVariable("personNameUuid") PersonName personName)
			throws ResponseException {

		if (person == null)
			throw new ObjectNotFound();

		if (personName == null)
			throw new ObjectNotFound();

		// the person uuid passed in is not the proper person for the given
		// personname passed in
		if (!personName.getPerson().equals(person))
			throw new ObjectMismatch();

		for (PersonName pn : person.getNames())
			pn.setPreferred(false);

		personName.setPreferred(true);

		Context.getPersonService().savePerson(person);

		// return the newly changed person object
		try {
			return wsUtil.convert(personName, WSConstants.REPRESENTATION_DEFAULT);
		} catch (Exception e) {
			log.error("Unable to convert newly preferred person name: " + personName, e);
			throw new ConversionException();
		}
	}

	/**
	 * @param person
	 * @param personName
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/person/{personUuid}/name/{personNameUuid}", method = RequestMethod.DELETE)
	@ResponseBody
	public SimpleObject voidNameOnPerson(
			@PathVariable("personUuid") Person person,
			@PathVariable("personNameUuid") PersonName personName,
			WebRequest request) throws ResponseException {

		personName.setVoided(true);
		personName.setDateVoided(new Date());
		personName.setVoidReason("voided from webservices");
		personName.setVoidedBy(Context.getAuthenticatedUser());

		Context.getPersonService().savePerson(person);

		throw new SuccessfulDeletion();
	}

	/**
	 * @param request
	 * @param personValues
	 * @return the newly created person object with default representation
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/person/", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject createPerson(WebRequest request,
			@RequestBody Map<String, Object> personValues) throws ResponseException {

		Person p = new Person();
		wsUtil.setValues(p, personValues);
		
		Context.getPersonService().savePerson(p);

		// return the newly changed person object
		try {
			return wsUtil.convert(p, WSConstants.REPRESENTATION_DEFAULT);
		} catch (Exception e) {
			// uh oh, this is really bad, we JUST made this object!
			log.error("Unable to convert the newly created person " + p, e);
			throw new ConversionException();
		}
	}

	/**
	 * @param person
	 * @param personValues
	 * @return the newly changed person with the default representation
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/person/{personUuid}", method = RequestMethod.PUT)
	@ResponseBody
	public SimpleObject updatePerson(@PathVariable("personUuid") Person person,
			@RequestBody Map<String, Object> personValues)
			throws ResponseException {

		// looks up setters, determines if its settable, converts it potentially
		wsUtil.setValues(person, personValues);

		Context.getPersonService().savePerson(person);

		// return the newly changed person object
		try {
			return wsUtil.convert(person, WSConstants.REPRESENTATION_DEFAULT);
		} catch (Exception e) {
			log.error("Unable to convert the newly changed person " + person, e);
			throw new ConversionException();
		}
	}

}
