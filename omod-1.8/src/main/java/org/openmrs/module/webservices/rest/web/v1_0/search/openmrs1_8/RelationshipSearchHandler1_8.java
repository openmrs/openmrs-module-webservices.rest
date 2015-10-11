/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Allows search for relationships by mappings
 */
@Component
public class RelationshipSearchHandler1_8 implements SearchHandler {

	private final SearchConfig searchConfig = new SearchConfig(
			"default",
			RestConstants.VERSION_1 + "/relationship",
			Arrays.asList("1.9.*", "1.10.*", "1.11.*", "1.12.*"),
			Arrays.asList(
					new SearchQuery.Builder(
							"Allows you to find relationship by person uuid")
							.withRequiredParameters("person").build(),
					new SearchQuery.Builder(
							"Allows you to find relationships between two persons by specifying their uuids")
							.withRequiredParameters("person", "relatedPerson")
							.build(),
					new SearchQuery.Builder(
							"Allows you to find relationships where person is related to other by type of relationship")
							.withRequiredParameters("person", "relation")
							.build(),
					new SearchQuery.Builder(
							"Allows you to find relationships of personA of given relationship type ")
							.withRequiredParameters("personA", "relation")
							.build(),
					new SearchQuery.Builder(
							"Allows you to find relationships between personA and personB")
							.withRequiredParameters("personA", "personB")
							.build(),
					new SearchQuery.Builder(
							"Allows you to find relationships between personA and personB given relationship type ")
							.withRequiredParameters("personA", "personB",
									"relation").build(),
					new SearchQuery.Builder(
							"Allows you to find relationships of personB of given relationship type")
							.withRequiredParameters("personB", "relation")
							.build()));

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
	 */
	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context)
			throws ResponseException {

		String person = context.getParameter("person");
		String relatedPerson = context.getParameter("relatedPerson");
		String relation = context.getParameter("relation");
		String personA = context.getParameter("personA");
		String personB = context.getParameter("personB");
		RelationshipType relationshipType = null;
		List<Relationship> relationshipList = null;
		Person personOb = null;
		Person personAOb = null;
		Person personBOb = null;
		Person relatedPersonOb = null;

		PersonService personService = Context.getPersonService();

		if (person != null) {
			personOb = personService.getPersonByUuid(person);
		}

		if (personA != null) {
			personAOb = personService.getPersonByUuid(personA);
		}

		if (personB != null) {
			personBOb = personService.getPersonByUuid(personB);
		}

		if (relatedPerson != null) {
			relatedPersonOb = personService.getPersonByUuid(relatedPerson);
		}

		if (relation != null) {
			if (personService.getRelationshipTypeByUuid(relation) != null) {
				relationshipType = personService
						.getRelationshipTypeByUuid(relation);
			} else {
				List<RelationshipType> relationshipTypes = personService
						.getAllRelationshipTypes();
				for (RelationshipType temp : relationshipTypes) {
					if (temp.getbIsToA().equalsIgnoreCase(relation)
							|| temp.getaIsToB().equalsIgnoreCase(relation)) {
						relationshipType = temp;
						break;
					}
				}
			}
		}

		List<Relationship> tempList;
		if (personOb != null && relatedPersonOb != null) {
			tempList = personService.getRelationships(personOb,
					relatedPersonOb, null);
			tempList.addAll(personService.getRelationships(relatedPersonOb,
					personOb, null));
			relationshipList = tempList;
		} else if (personOb != null && relationshipType != null) {
			tempList = personService.getRelationships(personOb, null,
					relationshipType);
			tempList.addAll(personService.getRelationships(null, personOb,
					relationshipType));
			relationshipList = tempList;
		} else if (personOb != null) {
			relationshipList = personService.getRelationshipsByPerson(personOb);
		} else if (personAOb != null && personBOb != null
				&& relationshipType != null) {
			relationshipList = personService.getRelationships(personAOb,
					personBOb, relationshipType);
		} else if (personAOb != null && personBOb != null) {
			relationshipList = personService.getRelationships(personAOb,
					personBOb, null);
		} else if (personAOb != null && relationshipType != null) {
			relationshipList = personService.getRelationships(personAOb, null,
					relationshipType);
		} else if (personBOb != null && relationshipType != null) {
			relationshipList = personService.getRelationships(null, personBOb,
					relationshipType);
		}

		if (relationshipList == null) {
			return new EmptySearchResult();
		}
		return new NeedsPaging<Relationship>(relationshipList, context);
	}
}
