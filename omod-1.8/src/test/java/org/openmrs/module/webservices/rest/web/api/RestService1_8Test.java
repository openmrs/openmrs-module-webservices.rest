package org.openmrs.module.webservices.rest.web.api;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RestService1_8Test extends BaseModuleContextSensitiveTest {
	private static final String ENCOUNTER_WITH_OBS = "EncounterWithObsGroup.xml";
	private RestService service;
	
	@Before
	public void before() throws Exception {
		executeDataSet(ENCOUNTER_WITH_OBS);
		service = Context.getService(RestService.class);
	}
	
	/**
	 * @see RestService#getResourceBySupportedClass(Class)
	 * @verifies handle proxy classes
	 */
	@Test
	public void getResourceBySupportedClass_shouldHandleProxyClasses() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(RestTestConstants1_8.ENCOUNTER_UUID);
		Object patient = PropertyUtils.getProperty(encounter, "patient");
		Resource resource = service.getResourceBySupportedClass(patient.getClass());
		// We should get back a PatientResource
		Assert.assertTrue(PatientResource1_8.class.isAssignableFrom(resource.getClass()));
	}
}
