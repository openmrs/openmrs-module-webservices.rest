package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8.ProgramEnrollmentController1_8Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProgramEnrollmentResource1_10Test extends ProgramEnrollmentController1_8Test {

    @Test
    public void shouldTransitPatientState() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stateStartDate = "2015-08-04";
        String json = "{ \"states\": [{ \"state\": {\"uuid\" : \"" + RestTestConstants1_8.STATE_UUID + "\"}, \"startDate\": \"" + stateStartDate + "\"}]}";

        MockHttpServletRequest req = newPostRequest(getURI() + "/" + getUuid(), SimpleObject.parseJson(json));
        SimpleObject result = deserialize(handle(req));

        PatientProgram patientProgram = service.getPatientProgramByUuid(getUuid());
        Assert.assertNotNull(result);
        List<PatientState> states = new ArrayList<PatientState>(patientProgram.getStates());
        Assert.assertEquals(2, states.size());
        Assert.assertEquals(RestTestConstants1_8.STATE_UUID, states.get(1).getState().getUuid());
        String existingStateEndDate = dateFormat.format(states.get(0).getEndDate());
        Assert.assertEquals(stateStartDate, existingStateEndDate);
    }
}