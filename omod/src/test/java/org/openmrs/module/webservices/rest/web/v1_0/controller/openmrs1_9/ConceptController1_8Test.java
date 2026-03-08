/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSet;
import org.openmrs.Drug;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests functionality of {@link ConceptController}.
 */
public class ConceptController1_8Test extends MainResourceControllerTest {
	
	private ConceptService service;
	
	@BeforeEach
	public void before() {
		this.service = Context.getConceptService();
	}
	
	@Test
	public void shouldGetAConceptByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/15f83cd6-64e9-4e06-a5f9-364d3b14a43d");
		SimpleObject result = deserialize(handle(req));
		Util.log("Concept fetched", result);
		Assertions.assertNotNull(result);
		Assertions.assertEquals("15f83cd6-64e9-4e06-a5f9-364d3b14a43d", PropertyUtils.getProperty(result, "uuid"));
		Assertions.assertEquals("ASPIRIN", PropertyUtils.getProperty(PropertyUtils.getProperty(result, "name"), "name"));
	}
	
	@Test
	public void shouldGetAConceptByUuidInXML() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/15f83cd6-64e9-4e06-a5f9-364d3b14a43d");
		req.addHeader("Accept", "application/xml");
		MockHttpServletResponse result = handle(req);
		
		String xml = result.getContentAsString();
		printXML(xml);
		
		Assertions.assertEquals("15f83cd6-64e9-4e06-a5f9-364d3b14a43d", evaluateXPath(xml, "//uuid"));
		Assertions.assertEquals("ASPIRIN", evaluateXPath(xml, "//name/name"));
	}
	
	@Test
	public void shouldReturnFullRepXML() throws Exception {
		MockHttpServletRequest request = newGetRequest(getURI() + "/" + getUuid(), new Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL));
		request.addHeader("Accept", "application/xml");
		
		MockHttpServletResponse result = handle(request);
		
		String xml = result.getContentAsString();
		printXML(xml);
	}
	
	@Test
	public void shouldListAllUnRetiredConcepts() throws Exception {
		int totalCount = service.getAllConcepts(null, true, true).size();
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		Assertions.assertNotNull(result);
		Assertions.assertTrue(totalCount > result.size());
		Assertions.assertEquals(getAllCount(), Util.getResultsList(result).size());
	}
	
	@Test
	public void shouldGetRefRepresentationForGetAllByDefault() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		Object aResult = Util.getResultsList(result).get(0);
		Assertions.assertNull(PropertyUtils.getProperty(aResult, "datatype"));
	}
	
	@Test
	public void shouldGetSpecifiedRepresentationForGetAll() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_DEFAULT);
		SimpleObject result = deserialize(handle(req));
		Object aResult = Util.getResultsList(result).get(0);
		Assertions.assertNotNull(PropertyUtils.getProperty(aResult, "datatype"));
	}
	
	@Test
	public void shouldCreateAConcept() throws Exception {
		int originalCount = service.getAllConcepts().size();
		String json = "{ \"names\": [{\"name\":\"test concept\", \"locale\":\"en\", \"conceptNameType\":\""
		        + ConceptNameType.FULLY_SPECIFIED
		        + "\"}], \"datatype\":\"8d4a4c94-c2cc-11de-8d13-0010c6dffd0f\", \"conceptClass\":\"Diagnosis\" }";
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		Object newConcept = deserialize(handle(req));
		Assertions.assertNotNull(PropertyUtils.getProperty(newConcept, "uuid"));
		Assertions.assertEquals(originalCount + 1, service.getAllConcepts().size());
	}
	
	@Test
	public void shouldEditFullySpecifiedNameOfAConcept() throws Exception {
		final String changedName = "TESTING NAME";
		String json = "{ \"name\":\"" + changedName + "\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/f923524a-b90c-4870-a948-4125638606fd");
		req.setContent(json.getBytes());
		handle(req);
		Concept updated = service.getConceptByUuid("f923524a-b90c-4870-a948-4125638606fd");
		Assertions.assertNotNull(updated);
		Assertions.assertEquals(changedName, updated.getFullySpecifiedName(Context.getLocale()).getName());
	}
	
	@Test
	public void shouldEditAConcept() throws Exception {
		final String changedVersion = "1.2.3";
		String json = "{ \"version\":\"" + changedVersion + "\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/f923524a-b90c-4870-a948-4125638606fd");
		req.setContent(json.getBytes());
		handle(req);
		Concept updated = service.getConceptByUuid("f923524a-b90c-4870-a948-4125638606fd");
		Assertions.assertNotNull(updated);
		Assertions.assertEquals(changedVersion, updated.getVersion());
	}
	
	@Test
	public void shouldRetireAConcept() throws Exception {
		String uuid = "0a9afe04-088b-44ca-9291-0a8c3b5c96fa";
		Concept concept = service.getConceptByUuid(uuid);
		Assertions.assertFalse(concept.isRetired());
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("!purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		concept = service.getConceptByUuid(uuid);
		Assertions.assertTrue(concept.isRetired());
		Assertions.assertEquals("really ridiculous random reason", concept.getRetireReason());
	}
	
	@Test
	public void shouldPurgeAConcept() throws Exception {
		int originalCount = service.getAllConcepts().size();
		String uuid = "11716f9c-1434-4f8d-b9fc-9aa14c4d6129";
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("purge", "true");
		handle(req);
		
		Assertions.assertNull(service.getConceptByUuid(uuid));
		Assertions.assertEquals(originalCount - 1, service.getAllConcepts().size());
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/0dde1358-7fcf-4341-a330-f119241a46e8");
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	//Custom matcher
	FeatureMatcher<Object, String> hasUuid(String uuid) {
		return new FeatureMatcher<Object, String>(
		                                          CoreMatchers.equalTo(uuid), "uuid", "uuid") {
			
			@Override
			protected String featureValueOf(Object o) {
				try {
					return (String) PropertyUtils.getProperty(o, "uuid");
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	@Test
	public void shouldSearchAndReturnConceptsThatEqualsToClassAndName() throws Exception {
		service.updateConceptIndexes();
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result;
		List<Object> hits;
		
		String conceptClassUuid = "3d065ed4-b0b9-4710-9a17-6d8c4fd259b7"; // DRUG
		String name = "Aspirin"; //ASPIRIN
		String searchType = "equals";
		
		req.addParameter("class", conceptClassUuid);
		req.addParameter("name", name);
		req.addParameter("searchType", searchType);
		
		result = deserialize(handle(req));
		hits = result.get("results");
		
		assertThat(hits, contains(hasUuid("15f83cd6-64e9-4e06-a5f9-364d3b14a43d")));
		
		//Should not find it when it has partial name:
		name = "Asp";
		req.setParameter("name", name);
		
		result = deserialize(handle(req));
		hits = result.get("results");
		
		assertThat(hits, is(empty()));
	}
	
	@Test
	public void shouldNotReturnAnythingWhenConceptDoesntMatchClass() throws Exception {
		service.updateConceptIndex(service.getConceptByUuid("a09ab2c5-878e-4905-b25d-5784167d0216"));
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result;
		List<Object> hits;
		
		String conceptClassUuid = "97097dd9-b092-4b68-a2dc-e5e5be961d42"; // TEST
		String name = "CD4 COU"; //CD4 COUNT
		String searchType = "fuzzy";
		
		req.addParameter("class", conceptClassUuid);
		req.addParameter("name", name);
		req.addParameter("searchType", searchType);
		
		result = deserialize(handle(req));
		hits = result.get("results");
		
		assertThat(hits, contains(hasUuid("a09ab2c5-878e-4905-b25d-5784167d0216")));
		
		//Should not find it when it has partial name:
		conceptClassUuid = "3d065ed4-b0b9-4710-9a17-6d8c4fd259b7"; // DRUG
		req.setParameter("class", conceptClassUuid);
		
		result = deserialize(handle(req));
		hits = result.get("results");
		
		assertThat(hits, is(empty()));
	}
	
	@Test
	public void shouldSearchAndReturnConceptsThatContainsNamePartInRequest() throws Exception {
		service.updateConceptIndexes();
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result;
		List<Object> hits;
		
		String conceptClassUuid = "3d065ed4-b0b9-4710-9a17-6d8c4fd259b7"; // DRUG
		String name = "Asp"; //ASPIRIN
		String searchType = "fuzzy";
		
		req.addParameter("class", conceptClassUuid);
		req.addParameter("name", name);
		req.addParameter("searchType", searchType);
		
		result = deserialize(handle(req));
		hits = result.get("results");
		
		assertThat(hits, contains(hasUuid("15f83cd6-64e9-4e06-a5f9-364d3b14a43d")));
	}
	
	@Test
	public void shouldThrowExceptionWhenSearchRequiredParametersAreCalledTwice() throws Exception {
		assertThrows(IllegalStateException.class, () -> {
			new SearchQuery.Builder("Some search description").withRequiredParameters("source").withRequiredParameters("name") // <- Exception
			        .withOptionalParameters("code").build();
		});
	}
	
	@Test
	public void shouldThrowExceptionWhenSearchOptionalParametersAreCalledTwice() throws Exception {
		assertThrows(IllegalStateException.class, () -> {
			new SearchQuery.Builder("Some search description").withRequiredParameters("source").withOptionalParameters("name") // <- Exception
			        .withOptionalParameters("code").build();
		});
	}
	
	@Test
	public void shouldThrowExceptionWhenSearchTypeParameterIsInvalid() throws Exception {
		assertThrows(InvalidSearchException.class, () -> {
		
			MockHttpServletRequest req = request(RequestMethod.GET, getURI());
			SimpleObject result;
		
			String conceptClassUuid = "3d065ed4-b0b9-4710-9a17-6d8c4fd259b7"; // DRUG
			String name = "Aspirin"; //ASPIRIN
			String searchType = "equalz";
		
			req.addParameter("class", conceptClassUuid);
			req.addParameter("name", name);
			req.addParameter("searchType", searchType);
		
			result = deserialize(handle(req));
		});
	}
	
	@Test
	@Disabled("TRUNK-1956: H2 cannot execute the generated SQL because it requires all fetched columns to be included in the group by clause")
	public void shouldSearchAndReturnAListOfConceptsMatchingTheQueryString() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "food");
		SimpleObject result = deserialize(handle(req));
		
		List<Object> hits = (List<Object>) result.get("results");
		Assertions.assertEquals(2, hits.size());
		Assertions.assertEquals("0dde1358-7fcf-4341-a330-f119241a46e8", PropertyUtils.getProperty(hits.get(0), "uuid"));
		Assertions.assertEquals("0f97e14e-cdc2-49ac-9255-b5126f8a5147", PropertyUtils.getProperty(hits.get(1), "uuid"));
	}
	
	@Test
	@Disabled("TRUNK-1956: H2 cannot execute the generated SQL because it requires all fetched columns to be included in the group by clause")
	public void doSearch_shouldReturnMembersOfConcept() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		String memberOfUuid = "0f97e14e-cdc2-49ac-9255-b5126f8a5147"; // FOOD CONSTRUCT
		req.addParameter("memberOf", memberOfUuid);
		req.addParameter("q", "no");
		SimpleObject result = deserialize(handle(req));
		
		List<Object> hits = (List<Object>) result.get("results");
		Assertions.assertEquals(1, hits.size());
		Assertions.assertEquals("f4d0b584-6ce5-40e2-9ce5-fa7ec07b32b4", PropertyUtils.getProperty(hits.get(0), "uuid")); // FAVORITE FOOD, NON-CODED
	}
	
	@Test
	@Disabled("TRUNK-1956: H2 cannot execute the generated SQL because it requires all fetched columns to be included in the group by clause")
	public void doSearch_shouldReturnAnswersToConcept() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		
		String answerToUuid = "95312123-e0c2-466d-b6b1-cb6e990d0d65"; // FOOD ASSISTANCE FOR ENTIRE FAMILY
		req.addParameter("answerTo", answerToUuid);
		req.addParameter("q", "no");
		
		SimpleObject result = deserialize(handle(req));
		List<Object> hits = (List<Object>) result.get("results");
		Assertions.assertEquals(1, hits.size());
		Assertions.assertEquals("b98a6ed4-77e7-4cee-aae2-81957fcd7f48", PropertyUtils.getProperty(hits.get(0), "uuid")); // NO
	}
	
	/**
	 * {@link ConceptResource1_8#getByUniqueId(String)}
	 * 
	 * @throws Exception
	 */
	
	/**
	 * @see MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "concept";
	}
	
	/**
	 * @see MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.CONCEPT_UUID;
	}
	
	/**
	 * @see MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getAllConcepts(null, true, false).size();
	}
	
	@Test
	public void shouldAddSetMembersToConcept() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		String json = "{ \"setMembers\": [\"0dde1358-7fcf-4341-a330-f119241a46e8\", \"54d2dce5-0357-4253-a91a-85ce519137f5\"] }";
		request.setContent(json.getBytes());
		
		handle(request);
		
		Concept concept = Context.getConceptService().getConceptByUuid(getUuid());
		Assertions.assertEquals(2, concept.getSetMembers().size());
	}
	
	@Test
	public void shouldModifySetMembersOnConcept() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		String json = "{ \"setMembers\": [\"0dde1358-7fcf-4341-a330-f119241a46e8\", \"54d2dce5-0357-4253-a91a-85ce519137f5\"] }";
		request.setContent(json.getBytes());
		handle(request);
		
		Concept concept = Context.getConceptService().getConceptByUuid(getUuid());
		Assertions.assertEquals(2, concept.getSetMembers().size());
		
		json = "{ \"setMembers\": [\"0dde1358-7fcf-4341-a330-f119241a46e8\"] }";
		request.setContent(json.getBytes());
		handle(request);
		
		concept = Context.getConceptService().getConceptByUuid(getUuid());
		Assertions.assertEquals(1, concept.getSetMembers().size());
	}
	
	@Test
	public void shouldAddAnswersToConcept() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		String json = "{ \"answers\": [\"0dde1358-7fcf-4341-a330-f119241a46e8\", \"54d2dce5-0357-4253-a91a-85ce519137f5\", \"05ec820a-d297-44e3-be6e-698531d9dd3f\"] }";
		request.setContent(json.getBytes());
		
		handle(request);
		
		Concept concept = service.getConceptByUuid(getUuid());
		Concept answer1 = service.getConceptByUuid("0dde1358-7fcf-4341-a330-f119241a46e8");
		Concept answer2 = service.getConceptByUuid("54d2dce5-0357-4253-a91a-85ce519137f5");
		Drug drug = Context.getConceptService().getDrugByUuid("05ec820a-d297-44e3-be6e-698531d9dd3f");
		Assertions.assertTrue(hasAnswer(concept, answer1));
		Assertions.assertTrue(hasAnswer(concept, answer2));
		Assertions.assertTrue(hasAnswer(concept, drug));
	}
	
	/**
	 * Convenience helper method to look for the given answer amongst the answers on the question
	 * concept
	 * 
	 * @param question the concept on which to call getAnswers()
	 * @param answer the concept that is hidden in a ConceptAnswer object on the given concept
	 *            (maybe)
	 * @return true if the answer is found on the concept
	 */
	private boolean hasAnswer(Concept question, Concept answer) {
		for (ConceptAnswer conceptAnswerObject : question.getAnswers()) {
			if (conceptAnswerObject.getAnswerConcept().equals(answer))
				return true;
		}
		// answer was not found
		return false;
	}
	
	/**
	 * Convenience helper method to look for the given answer amongst the answers on the question
	 * concept
	 * 
	 * @param question the concept on which to call getAnswers()
	 * @param druganswer the drug that is hidden in a ConceptAnswer object on the given concept
	 *            (maybe)
	 * @return true if the answer is found on the concept
	 */
	private boolean hasAnswer(Concept question, Drug druganswer) {
		for (ConceptAnswer conceptAnswerObject : question.getAnswers()) {
			if (conceptAnswerObject.getAnswerDrug() != null && conceptAnswerObject.getAnswerDrug().equals(druganswer))
				return true;
		}
		// answer was not found
		return false;
	}
	
	@Test
	public void shouldRemoveAnswersFromConcept() throws Exception {
		String conceptWithAnswersUuid = "95312123-e0c2-466d-b6b1-cb6e990d0d65";
		String existingAnswerUuid = "b055abd8-a420-4a11-8b98-02ee170a7b54";
		String newAnswerUuid = "32d3611a-6699-4d52-823f-b4b788bac3e3";
		
		// sanity check to make sure this concept has the existing answer and not the other
		Concept concept = Context.getConceptService().getConceptByUuid(conceptWithAnswersUuid);
		Assertions.assertTrue(hasAnswer(concept, service.getConceptByUuid(existingAnswerUuid)));
		Assertions.assertFalse(hasAnswer(concept, service.getConceptByUuid(newAnswerUuid)));
		Assertions.assertEquals(3, concept.getAnswers().size());
		
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + conceptWithAnswersUuid);
		String json = "{ \"answers\": [\"" + existingAnswerUuid + "\", \"" + newAnswerUuid + "\"] }";
		request.setContent(json.getBytes());
		
		handle(request);
		
		// get the object again so we have the new answers (will this bork hibernate because we fetched it earlier?)
		concept = Context.getConceptService().getConceptByUuid(conceptWithAnswersUuid);
		Concept answer1 = service.getConceptByUuid(existingAnswerUuid);
		Concept answer2 = service.getConceptByUuid(newAnswerUuid);
		Assertions.assertTrue(hasAnswer(concept, answer1));
		Assertions.assertTrue(hasAnswer(concept, answer2));
		Assertions.assertEquals(2, concept.getAnswers().size());
	}
	
	@Test
	public void shouldSetMappingsOnConcept() throws Exception {
		//before adding
		Concept concept = service.getConceptByUuid(getUuid());
		assertThat(concept.getConceptMappings().size(), is(0));
		
		//add one mapping
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		ConceptReferenceTerm referenceTerm = service.getAllConceptReferenceTerms().get(0);
		String mapTypeUuid = service.getDefaultConceptMapType().getUuid();
		String json = "{ \"mappings\": [{\"conceptReferenceTerm\":\"" + referenceTerm.getUuid() + "\",\"conceptMapType\":\""
		        + mapTypeUuid + "\"}]}";
		request.setContent(json.getBytes());
		
		handle(request);
		
		concept = service.getConceptByUuid(getUuid());
		assertThat(concept.getConceptMappings().size(), is(1));
		assertThat(concept.getConceptMappings(), hasItem(hasTerm(referenceTerm)));
		
		//set mappings to empty
		MockHttpServletRequest requestEmpty = request(RequestMethod.POST, getURI() + "/" + getUuid());
		String jsonEmpty = "{ \"mappings\": []}";
		requestEmpty.setContent(jsonEmpty.getBytes());
		
		handle(requestEmpty);
		
		assertThat(concept.getConceptMappings().size(), is(0));
	}
	
	private Matcher<ConceptMap> hasTerm(final ConceptReferenceTerm term) {
		return new FeatureMatcher<ConceptMap, ConceptReferenceTerm>(
		                                                            equalTo(term), "conceptReferenceTerm",
		                                                            "conceptReferenceTerm") {
			
			@Override
			protected ConceptReferenceTerm featureValueOf(final ConceptMap actual) {
				return actual.getConceptReferenceTerm();
			}
		};
	}
	
	@Test
	public void shouldReturnDefaultAndSelfLinkForCustomUuid() throws Exception {
		String conceptUuid = "95312123-e0c2-466d-b6b1-cb6e990d0d65";
		
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + conceptUuid);
		request.addParameter("v", "custom:(links)");
		MockHttpServletResponse response = handle(request);
		SimpleObject object = deserialize(response);
		List<Map<String, String>> data = (List<Map<String, String>>) object.get("links");
		MatcherAssert.assertThat(
		    data,
		    contains(
		        allOf(hasEntry("rel", "self"),
		            hasEntry("uri", "http://localhost/ws/rest/v1/concept/95312123-e0c2-466d-b6b1-cb6e990d0d65")),
		        allOf(hasEntry("rel", "default"),
		            hasEntry("uri", "http://localhost/ws/rest/v1/concept/95312123-e0c2-466d-b6b1-cb6e990d0d65?v=default"))));
	}
	
	@Test
	public void shouldReturnCustomRepresentation() throws Exception {
		String conceptUuid = "95312123-e0c2-466d-b6b1-cb6e990d0d65";
		
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + conceptUuid);
		request.addParameter("v", "custom:(uuid,datatype:(uuid,name),conceptClass,names:ref)");
		MockHttpServletResponse response = handle(request);
		SimpleObject object = deserialize(response);
		
		Assertions.assertEquals("95312123-e0c2-466d-b6b1-cb6e990d0d65", object.get("uuid"));
		Assertions.assertNotNull(object.get("resourceVersion"));
		Assertions.assertEquals(5, object.size()); // note that `resourceVersion` is implicitly included
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> datatype = (Map<Object, Object>) object.get("datatype");
		Assertions.assertEquals(3, datatype.size());
		Assertions.assertEquals("8d4a48b6-c2cc-11de-8d13-0010c6dffd0f", datatype.get("uuid"));
		Assertions.assertEquals("Coded", datatype.get("name"));
		Assertions.assertNotNull(datatype.get("resourceVersion"));
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> conceptClass = (Map<Object, Object>) object.get("conceptClass");
		Assertions.assertEquals(7, conceptClass.size());
		Assertions.assertEquals("a82ef63c-e4e4-48d6-988a-fdd74d7541a7", conceptClass.get("uuid"));
		Assertions.assertEquals("Question", conceptClass.get("display"));
		Assertions.assertEquals("Question", conceptClass.get("name"));
		Assertions.assertEquals("Question (eg, patient history, SF36 items)", conceptClass.get("description"));
		Assertions.assertEquals(false, conceptClass.get("retired"));
		Assertions.assertNotNull(conceptClass.get("links"));
		Assertions.assertNotNull(conceptClass.get("resourceVersion"));
		
		@SuppressWarnings("unchecked")
		List<Object> names = (List<Object>) object.get("names");
		Assertions.assertEquals(1, names.size());
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> name = (Map<Object, Object>) names.get(0);
		Assertions.assertEquals(3, name.size());
		Assertions.assertEquals("325391a8-db12-4e24-863f-5d66f7a4d713", name.get("uuid"));
		Assertions.assertEquals("FOOD ASSISTANCE FOR ENTIRE FAMILY", name.get("display"));
		Assertions.assertNotNull(name.get("links"));
		Assertions.assertNull(name.get("resourceVersion")); // resourceVersion is not included in ref representation
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldIncludeAllUnretiredConceptAnswersForAQuestionConcept() throws Exception {
		final String conceptUuid = "89ca642a-dab6-4f20-b712-e12ca4fc6d36";
		Concept questionConcept = service.getConceptByUuid(conceptUuid);
		//for the test to stay valid, the sort weights should always be the same(null in this case)
		for (ConceptAnswer ca : questionConcept.getAnswers(false)) {
			Assertions.assertNull(ca.getSortWeight());
		}
		
		int expectedAnswerCount = service.getConceptByUuid(conceptUuid).getAnswers(false).size();
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + conceptUuid);
		SimpleObject result = deserialize(handle(req));
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(expectedAnswerCount, ((List<Object>) PropertyUtils.getProperty(result, "answers")).size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldIncludeAllSetMembersForAConceptSet() throws Exception {
		final String conceptUuid = "0f97e14e-cdc2-49ac-9255-b5126f8a5147";
		Concept parentConcept = service.getConceptByUuid(conceptUuid);
		//for testing purposes set the same weight for the set members
		for (ConceptSet conceptSet : parentConcept.getConceptSets()) {
			conceptSet.setSortWeight(2.0);
		}
		service.saveConcept(parentConcept);
		
		int expectedMemberCount = service.getConceptByUuid(conceptUuid).getConceptSets().size();
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + conceptUuid);
		SimpleObject result = deserialize(handle(req));
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(expectedMemberCount, ((List<Object>) PropertyUtils.getProperty(result, "setMembers")).size());
	}
	
	@Test
	public void shouldFindConceptsBySourceAndCode() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("source",
		        "Some Standardized Terminology"), new Parameter("code", "WGT234"))));
		List<Object> results = Util.getResultsList(response);
		
		assertThat(results.size(), is(1));
		Object next = results.iterator().next();
		assertThat((String) PropertyUtils.getProperty(next, "uuid"), is("c607c80f-1ea9-4da3-bb88-6276ce8868dd"));
	}
	
	@Test
	public void shouldFindConceptsBySource() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("source",
		        "Some Standardized Terminology"))));
		List<Object> results = Util.getResultsList(response);
		
		assertThat(results.size(), is(10));
	}
	
	@Test
	public void shouldFindConceptsBySourceUuid() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("source",
		        "00001827-639f-4cb4-961f-1e025bf80000"))));
		List<Object> results = Util.getResultsList(response);
		
		assertThat(results.size(), is(10));
	}
	
	@Test
	public void shouldFindConceptsByName() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("name", "WEIGHT (KG)"))));
		List<Object> results = Util.getResultsList(response);
		Assertions.assertEquals(1, results.size());
		Object next = results.iterator().next();
		MatcherAssert.assertThat((String) PropertyUtils.getProperty(next, "uuid"), is("c607c80f-1ea9-4da3-bb88-6276ce8868dd"));
	}
	
	@Test
	public void shouldFailToFetchAConceptByNameIfTheNameIsNeitherPreferredNorFullySpecified() throws Exception {
		assertThrows(APIException.class, () -> {
			SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("name", "WT"))));
			List<Object> results = Util.getResultsList(response);
			Assertions.assertEquals(0, results.size());
		});
	}
	
	@Test
	public void shouldReturnFullSetMembersOnAllLevelsForFullChildren() throws Exception {
		Concept conceptLevel1 = newConcept("level1");
		Concept conceptLevel2 = newConcept("level2");
		Concept conceptLevel3 = newConcept("level3");
		Concept conceptLevel4 = newConcept("level4");
		Concept conceptLevel5 = newConcept("level5");

		conceptLevel1.addSetMember(conceptLevel2);
		conceptLevel2.addSetMember(conceptLevel3);
		conceptLevel3.addSetMember(conceptLevel4);
		conceptLevel4.addSetMember(conceptLevel5);
		
		service.saveConcept(conceptLevel5);
		service.saveConcept(conceptLevel4);
		service.saveConcept(conceptLevel3);
		service.saveConcept(conceptLevel2);
		service.saveConcept(conceptLevel1);
		
		//should include levels when accessing directly
		SimpleObject level1 = deserialize(handle(newGetRequest(getURI() + "/" + conceptLevel1.getUuid(), new Parameter("v",
		        "fullchildren"))));
		
		assertThatLevelsIncluded(level1, conceptLevel2, conceptLevel3, conceptLevel4, conceptLevel5);
		
		//should include levels when searching
		level1 = deserialize(handle(newGetRequest(getURI(), new Parameter("v", "fullchildren"), new Parameter("name",
		        "level1"))));
		
		List<Object> results = Util.getResultsList(level1);
		assertThatLevelsIncluded((Map<String, Object>) results.get(0), conceptLevel2, conceptLevel3, conceptLevel4,
		    conceptLevel5);
	}
	
	@Test
	public void shouldFailForFullChildrenWhenCyclesDetected() throws Exception {
		assertThrows(ConversionException.class, () -> {
			Concept conceptLevel1 = newConcept("level1");
			Concept conceptLevel2 = newConcept("level2");
			Concept conceptLevel3 = newConcept("level3");
			Concept conceptLevel4 = newConcept("level4");
			Concept conceptLevel5 = newConcept("level5");
		
			conceptLevel1.addSetMember(conceptLevel2);
			conceptLevel2.addSetMember(conceptLevel3);
			conceptLevel3.addSetMember(conceptLevel4);
			conceptLevel4.addSetMember(conceptLevel5);
		
			service.saveConcept(conceptLevel5);
			service.saveConcept(conceptLevel4);
			service.saveConcept(conceptLevel3);
			service.saveConcept(conceptLevel2);
			service.saveConcept(conceptLevel1);
		
			//Create cycle
			conceptLevel5.addSetMember(conceptLevel1);
			service.saveConcept(conceptLevel5);
		
			deserialize(handle(newGetRequest(getURI() + "/" + conceptLevel1.getUuid(), new Parameter("v", "fullchildren"))));
		});
	}
	
	private void assertThatLevelsIncluded(Map<String, Object> level1, Concept conceptLevel2, Concept conceptLevel3,
	        Concept conceptLevel4, Concept conceptLevel5) {
		Map<String, Object> level2 = getFirst(level1.get("setMembers"));
		assertThat((String) level2.get("uuid"), is(conceptLevel2.getUuid()));
		assertThat(level2.get("auditInfo"), is(notNullValue()));
		
		Map<String, Object> level3 = getFirst(level2.get("setMembers"));
		assertThat((String) level3.get("uuid"), is(conceptLevel3.getUuid()));
		assertThat(level3.get("auditInfo"), is(notNullValue()));
		
		Map<String, Object> level4 = getFirst(level3.get("setMembers"));
		assertThat((String) level4.get("uuid"), is(conceptLevel4.getUuid()));
		assertThat(level4.get("auditInfo"), is(notNullValue()));
		
		Map<String, Object> level5 = getFirst(level4.get("setMembers"));
		assertThat((String) level5.get("uuid"), is(conceptLevel5.getUuid()));
		assertThat(level5.get("auditInfo"), is(notNullValue()));
	}
	
	private Map<String, Object> getFirst(Object object) {
		return ((List<Map<String, Object>>) object).get(0);
	}
	
	private Concept newConcept(String name) {
		Concept concept = new Concept();
		concept.addName(new ConceptName(name, Locale.ENGLISH));
		concept.setDatatype(service.getConceptDatatype(1));
		concept.setConceptClass(service.getConceptClass(1));
		return concept;
	}
	
}
