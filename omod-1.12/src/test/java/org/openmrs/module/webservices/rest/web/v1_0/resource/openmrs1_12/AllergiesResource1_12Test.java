package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_12;

import org.junit.Test;
import org.openmrs.api.ConceptService;
<<<<<<< HEAD
import org.openmrs.Allergies;
=======
import org.openmrs.allergyapi.Allergies;
>>>>>>> origin/TRUNK-4747-A
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class AllergiesResource1_12Test extends BaseResource1_12Test {

    @Autowired
    private ConceptService conceptService;

    @Test
    public void testGetForPatientWithAllergies() throws Exception {
        SimpleObject response = toSimpleObject(
                handle(request(RequestMethod.GET, "allergies/da7f524f-27ce-4bb2-86d6-6d1d05312bd5")));
        assertThat((String) response.get("status"), is(Allergies.SEE_LIST));
        List<SimpleObject> allergies = (List<SimpleObject>) response.get("allergies");
        assertThat(allergies, containsInAnyOrder(
                allergyMatcher(conceptService.getConcept(792), conceptService.getConcept(23), conceptService.getConcept(3), conceptService.getConcept(4)),
                allergyMatcher(conceptService.getConcept(5089), conceptService.getConcept(23), conceptService.getConcept(5), conceptService.getConcept(6)),
                allergyMatcher(conceptService.getConcept(5497), conceptService.getConcept(23)),
                allergyMatcher(conceptService.getConcept(88), conceptService.getConcept(23))));
    }

    @Test
    public void testGetForPatientWithoutAllergiesSpecified() throws Exception {
        SimpleObject response = toSimpleObject(
                handle(request(RequestMethod.GET, "allergies/5946f880-b197-400b-9caa-a3c661d23041")));
        assertThat((String) response.get("status"), is(Allergies.UNKNOWN));
        assertThat(((List) response.get("allergies")).size(), is(0));
    }

}