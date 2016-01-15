package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import java.util.List;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.openmrs.Allergies;

public class AllergiesResource2_0Test extends BaseResource2_0Test {

	@Autowired
	private ConceptService conceptService;

	@Test
	public void testGetForPatientWithAllergies() throws Exception {
		SimpleObject response = toSimpleObject(handle(request(
				RequestMethod.GET,
				"allergies/da7f524f-27ce-4bb2-86d6-6d1d05312bd5")));
		Assert.assertThat((String) response.get("status"),
				Is.is(Allergies.SEE_LIST));
		List<SimpleObject> allergies = (List<SimpleObject>) response
				.get("allergies");
		Assert.assertThat(allergies, IsIterableContainingInAnyOrder
				.containsInAnyOrder(
						allergyMatcher(conceptService.getConcept(792),
								conceptService.getConcept(23),
								conceptService.getConcept(3),
								conceptService.getConcept(4)),
						allergyMatcher(conceptService.getConcept(5089),
								conceptService.getConcept(23),
								conceptService.getConcept(5),
								conceptService.getConcept(6)),
						allergyMatcher(conceptService.getConcept(5497),
								conceptService.getConcept(23)),
						allergyMatcher(conceptService.getConcept(18),
								conceptService.getConcept(23)),
						allergyMatcher(conceptService.getConcept(88),
								conceptService.getConcept(23))));
	}

	@Test
	public void testGetForPatientWithoutAllergiesSpecified() throws Exception {
		SimpleObject response = toSimpleObject(handle(request(
				RequestMethod.GET,
				"allergies/5946f880-b197-400b-9caa-a3c661d23041")));
		Assert.assertThat((String) response.get("status"),
				Is.is(Allergies.UNKNOWN));
		Assert.assertThat(((List) response.get("allergies")).size(), Is.is(0));
	}

}