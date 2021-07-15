package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.junit.Test;

public class ProgramWorkflowController1_8Test extends MainResourceControllerTest {

	@Override
	public String getURI() {
		return "workflow";
	}

	@Override
	public String getUuid() {
		return RestTestConstants1_8.WORKFLOW_UUID;
	}

	@Override
	public long getAllCount() {
		return 1;
	}

	@Override
	@Test(expected = Exception.class)
	public void shouldGetAll() throws Exception {
		// resource doesn't support get all operation
		super.shouldGetAll();
	}

	@Override
//	@Test(expected = Exception.class)
	public void shouldGetRefByUuid() throws Exception {
		// throws org.codehaus.jackson.map.JsonMappingException: Conflicting getter definitions for property "set": org.openmrs.Concept#isSet(0 params) vs org.openmrs.Concept#getSet(0 params)
		super.shouldGetRefByUuid();
	}

	@Override
//	@Test(expected = Exception.class)
	public void shouldGetDefaultByUuid() throws Exception {
		// throws org.codehaus.jackson.map.JsonMappingException: Conflicting getter definitions for property "set": org.openmrs.Concept#isSet(0 params) vs org.openmrs.Concept#getSet(0 params)
		super.shouldGetDefaultByUuid();
	}

	@Override
	//	@Test(expected = Exception.class)
	public void shouldGetFullByUuid() throws Exception {
		// throws org.codehaus.jackson.map.JsonMappingException: Conflicting getter definitions for property "set": org.openmrs.Concept#isSet(0 params) vs org.openmrs.Concept#getSet(0 params)
		super.shouldGetFullByUuid();
	}
}
