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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSet;
import org.openmrs.Drug;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ResourceTestConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link ConceptController}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class ConceptControllerTest extends BaseCrudControllerTest {
	
	private ConceptService service;
	
	@Before
	public void before() {
		this.service = Context.getConceptService();
	}
	
	@Test
	public void shouldGetAConceptByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/15f83cd6-64e9-4e06-a5f9-364d3b14a43d");
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Assert.assertEquals("15f83cd6-64e9-4e06-a5f9-364d3b14a43d", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("ASPIRIN", PropertyUtils.getProperty(PropertyUtils.getProperty(result, "name"), "name"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfYouTryToSpecifyDefaultRepOnGetConceptByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/15f83cd6-64e9-4e06-a5f9-364d3b14a43d");
		req.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_DEFAULT);
		deserialize(handle(req));
	}
	
	@Test
	public void shouldGetAConceptByName() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/TREATMENT STATUS");
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Assert.assertEquals("511e03ab-7cbb-4b9f-abe3-d9256d67f27e", PropertyUtils.getProperty(result, "uuid"));
		Assert
		        .assertEquals("TREATMENT STATUS", PropertyUtils.getProperty(PropertyUtils.getProperty(result, "name"),
		            "name"));
	}
	
	@Test
	public void shouldListAllUnRetiredConcepts() throws Exception {
		int totalCount = service.getAllConcepts(null, true, true).size();
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Assert.assertTrue(totalCount > result.size());
		Assert.assertEquals(24, Util.getResultsList(result).size()); // there are 25 concepts and one is retired, so should only get 24 here
	}
	
	@Test
	public void shouldGetRefRepresentationForGetAllByDefault() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		Object aResult = Util.getResultsList(result).get(0);
		Assert.assertNull(PropertyUtils.getProperty(aResult, "datatype"));
	}
	
	@Test
	public void shouldGetSpecifiedRepresentationForGetAll() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_DEFAULT);
		SimpleObject result = deserialize(handle(req));
		Object aResult = Util.getResultsList(result).get(0);
		Assert.assertNotNull(PropertyUtils.getProperty(aResult, "datatype"));
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
		Assert.assertNotNull(PropertyUtils.getProperty(newConcept, "uuid"));
		Assert.assertEquals(originalCount + 1, service.getAllConcepts().size());
	}
	
	@Test
	public void shouldEditFullySpecifiedNameOfAConcept() throws Exception {
		final String changedName = "TESTING NAME";
		String json = "{ \"name\":\"" + changedName + "\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/f923524a-b90c-4870-a948-4125638606fd");
		req.setContent(json.getBytes());
		handle(req);
		Concept updated = service.getConceptByUuid("f923524a-b90c-4870-a948-4125638606fd");
		Assert.assertNotNull(updated);
		Assert.assertEquals(changedName, updated.getFullySpecifiedName(Context.getLocale()).getName());
	}
	
	@Test
	public void shouldEditAConcept() throws Exception {
		final String changedVersion = "1.2.3";
		String json = "{ \"version\":\"" + changedVersion + "\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/f923524a-b90c-4870-a948-4125638606fd");
		req.setContent(json.getBytes());
		handle(req);
		Concept updated = service.getConceptByUuid("f923524a-b90c-4870-a948-4125638606fd");
		Assert.assertNotNull(updated);
		Assert.assertEquals(changedVersion, updated.getVersion());
	}
	
	@Test
	public void shouldRetireAConcept() throws Exception {
		String uuid = "0a9afe04-088b-44ca-9291-0a8c3b5c96fa";
		Concept concept = service.getConceptByUuid(uuid);
		Assert.assertFalse(concept.isRetired());
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("!purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		concept = service.getConceptByUuid(uuid);
		Assert.assertTrue(concept.isRetired());
		Assert.assertEquals("really ridiculous random reason", concept.getRetireReason());
	}
	
	@Test
	public void shouldPurgeAConcept() throws Exception {
		int originalCount = service.getAllConcepts().size();
		String uuid = "11716f9c-1434-4f8d-b9fc-9aa14c4d6129";
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("purge", "");
		handle(req);
		
		Assert.assertNull(service.getConceptByUuid(uuid));
		Assert.assertEquals(originalCount - 1, service.getAllConcepts().size());
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/0dde1358-7fcf-4341-a330-f119241a46e8");
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	@Ignore("TRUNK-1956: H2 cannot execute the generated SQL because it requires all fetched columns to be included in the group by clause")
	public void shouldSearchAndReturnAListOfConceptsMatchingTheQueryString() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "food");
		SimpleObject result = deserialize(handle(req));
		
		List<Object> hits = (List<Object>) result.get("results");
		Assert.assertEquals(2, hits.size());
		Assert.assertEquals("0dde1358-7fcf-4341-a330-f119241a46e8", PropertyUtils.getProperty(hits.get(0), "uuid"));
		Assert.assertEquals("0f97e14e-cdc2-49ac-9255-b5126f8a5147", PropertyUtils.getProperty(hits.get(1), "uuid"));
	}
	
	@Test
	@Ignore("TRUNK-1956: H2 cannot execute the generated SQL because it requires all fetched columns to be included in the group by clause")
	public void doSearch_shouldReturnMembersOfConcept() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		String memberOfUuid = "0f97e14e-cdc2-49ac-9255-b5126f8a5147"; // FOOD CONSTRUCT
		req.addParameter("memberOf", memberOfUuid);
		req.addParameter("q", "no");
		SimpleObject result = deserialize(handle(req));
		
		List<Object> hits = (List<Object>) result.get("results");
		Assert.assertEquals(1, hits.size());
		Assert.assertEquals("f4d0b584-6ce5-40e2-9ce5-fa7ec07b32b4", PropertyUtils.getProperty(hits.get(0), "uuid")); // FAVORITE FOOD, NON-CODED
	}
	
	@Test
	@Ignore("TRUNK-1956: H2 cannot execute the generated SQL because it requires all fetched columns to be included in the group by clause")
	public void doSearch_shouldReturnAnswersToConcept() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		
		String answerToUuid = "95312123-e0c2-466d-b6b1-cb6e990d0d65"; // FOOD ASSISTANCE FOR ENTIRE FAMILY
		req.addParameter("answerTo", answerToUuid);
		req.addParameter("q", "no");
		
		SimpleObject result = deserialize(handle(req));
		List<Object> hits = (List<Object>) result.get("results");
		Assert.assertEquals(1, hits.size());
		Assert.assertEquals("b98a6ed4-77e7-4cee-aae2-81957fcd7f48", PropertyUtils.getProperty(hits.get(0), "uuid")); // NO
	}
	
	/**
	 * {@link ConceptResource1_8#getByUniqueId(String)}
	 * 
	 * @throws Exception
	 */
	@Test(expected = APIException.class)
	public void shouldFailToFetchAConceptByNameIfTheNameIsNeitherPreferredNorFullySpecified() throws Exception {
		///sanity test to ensure that actually a none retired concept exists with this name
		ConceptName name = Context.getConceptService().getConceptNameByUuid("8230adbf-30a9-4e18-b6d7-fc57e0c23cab");
		Assert.assertNotNull(name);
		Concept concept = Context.getConceptService().getConceptByName(name.getName());
		Assert.assertFalse(concept.isRetired());
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + name.getName());
		handle(req);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "concept";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return ResourceTestConstants.CONCEPT_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return 24;
	}
	
	@Test
	public void shouldAddSetMembersToConcept() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		String json = "{ \"setMembers\": [\"0dde1358-7fcf-4341-a330-f119241a46e8\", \"54d2dce5-0357-4253-a91a-85ce519137f5\"] }";
		request.setContent(json.getBytes());
		
		handle(request);
		
		Concept concept = Context.getConceptService().getConceptByUuid(getUuid());
		Assert.assertEquals(2, concept.getSetMembers().size());
	}
	
	@Test
	public void shouldModifySetMembersOnConcept() throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		String json = "{ \"setMembers\": [\"0dde1358-7fcf-4341-a330-f119241a46e8\", \"54d2dce5-0357-4253-a91a-85ce519137f5\"] }";
		request.setContent(json.getBytes());
		handle(request);
		
		Concept concept = Context.getConceptService().getConceptByUuid(getUuid());
		Assert.assertEquals(2, concept.getSetMembers().size());
		
		json = "{ \"setMembers\": [\"0dde1358-7fcf-4341-a330-f119241a46e8\"] }";
		request.setContent(json.getBytes());
		handle(request);
		
		concept = Context.getConceptService().getConceptByUuid(getUuid());
		Assert.assertEquals(1, concept.getSetMembers().size());
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
		Assert.assertTrue(hasAnswer(concept, answer1));
		Assert.assertTrue(hasAnswer(concept, answer2));
		Assert.assertTrue(hasAnswer(concept, drug));
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
		Assert.assertTrue(hasAnswer(concept, service.getConceptByUuid(existingAnswerUuid)));
		Assert.assertFalse(hasAnswer(concept, service.getConceptByUuid(newAnswerUuid)));
		Assert.assertEquals(3, concept.getAnswers().size());
		
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + conceptWithAnswersUuid);
		String json = "{ \"answers\": [\"" + existingAnswerUuid + "\", \"" + newAnswerUuid + "\"] }";
		request.setContent(json.getBytes());
		
		handle(request);
		
		// get the object again so we have the new answers (will this bork hibernate because we fetched it earlier?)
		concept = Context.getConceptService().getConceptByUuid(conceptWithAnswersUuid);
		Concept answer1 = service.getConceptByUuid(existingAnswerUuid);
		Concept answer2 = service.getConceptByUuid(newAnswerUuid);
		Assert.assertTrue(hasAnswer(concept, answer1));
		Assert.assertTrue(hasAnswer(concept, answer2));
		Assert.assertEquals(2, concept.getAnswers().size());
	}
	
	@Test
	public void shouldReturnCustomRepresentation() throws Exception {
		String conceptUuid = "95312123-e0c2-466d-b6b1-cb6e990d0d65";
		
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + conceptUuid);
		request.addParameter("v", "custom:(uuid,datatype:(uuid,name),conceptClass,names:ref)");
		MockHttpServletResponse response = handle(request);
		SimpleObject object = deserialize(response);
		
		Assert.assertEquals("95312123-e0c2-466d-b6b1-cb6e990d0d65", object.get("uuid"));
		Assert.assertEquals(4, object.size());
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> datatype = (Map<Object, Object>) object.get("datatype");
		Assert.assertEquals(2, datatype.size());
		Assert.assertEquals("8d4a48b6-c2cc-11de-8d13-0010c6dffd0f", datatype.get("uuid"));
		Assert.assertEquals("Coded", datatype.get("name"));
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> conceptClass = (Map<Object, Object>) object.get("conceptClass");
		Assert.assertEquals(7, conceptClass.size());
		Assert.assertEquals("a82ef63c-e4e4-48d6-988a-fdd74d7541a7", conceptClass.get("uuid"));
		Assert.assertEquals("Question", conceptClass.get("display"));
		Assert.assertEquals("Question", conceptClass.get("name"));
		Assert.assertEquals("Question (eg, patient history, SF36 items)", conceptClass.get("description"));
		Assert.assertEquals(false, conceptClass.get("retired"));
		Assert.assertNotNull(conceptClass.get("links"));
		Assert.assertNotNull(conceptClass.get("resourceVersion"));
		
		@SuppressWarnings("unchecked")
		List<Object> names = (List<Object>) object.get("names");
		Assert.assertEquals(1, names.size());
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> name = (Map<Object, Object>) names.get(0);
		Assert.assertEquals(3, name.size());
		Assert.assertEquals("325391a8-db12-4e24-863f-5d66f7a4d713", name.get("uuid"));
		Assert.assertEquals("FOOD ASSISTANCE FOR ENTIRE FAMILY", name.get("display"));
		Assert.assertNotNull(name.get("links"));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldIncludeAllUnretiredConceptAnswersForAQuestionConcept() throws Exception {
		final String conceptUuid = "89ca642a-dab6-4f20-b712-e12ca4fc6d36";
		Concept questionConcept = service.getConceptByUuid(conceptUuid);
		//for the test to stay valid, the sort weights should always be the same(null in this case)
		for (ConceptAnswer ca : questionConcept.getAnswers(false)) {
			Assert.assertNull(ca.getSortWeight());
		}
		
		int expectedAnswerCount = service.getConceptByUuid(conceptUuid).getAnswers(false).size();
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + conceptUuid);
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(expectedAnswerCount, ((List<Object>) PropertyUtils.getProperty(result, "answers")).size());
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
		
		Assert.assertNotNull(result);
		Assert.assertEquals(expectedMemberCount, ((List<Object>) PropertyUtils.getProperty(result, "setMembers")).size());
	}
	
	@Test
	public void shouldFindConceptsBySourceNameAndCode() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("sourceName",
		        "Some Standardized Terminology"), new Parameter("code", "WGT234"))));
		List<Object> results = Util.getResultsList(response);
		
		assertThat(results.size(), is(1));
		Object next = results.iterator().next();
		assertThat((String) PropertyUtils.getProperty(next, "uuid"), is("c607c80f-1ea9-4da3-bb88-6276ce8868dd"));
	}
}
