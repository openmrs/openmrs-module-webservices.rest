package org.openmrs.module.webservices.rest.resource;


import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.NamedRepresentation;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class EncounterCrudResourceTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldRetrieve() throws Exception {
		EncounterCrudResource resource = new EncounterCrudResource(Context.getEncounterService().getEncounterByUuid("6519d653-393b-4118-9c83-a3715b82d4ac"));
		Object result = resource.asRepresentation(new NamedRepresentation("default"));
		System.out.println("As json:");
		new ObjectMapper().writeValue(System.out, result);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void shouldCreate() throws Exception {
		SimpleObject post = new ObjectMapper().readValue("{ \"patient\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\", \"encounterDatetime\": \"2011-04-15\" }", SimpleObject.class);
		EncounterCrudResource resource = new EncounterCrudResource();
		resource.create(post, new RequestContext());
	}
	
	@Test
	public void shouldUpdate() throws Exception {
		SimpleObject post = new ObjectMapper().readValue("{ \"encounterDatetime\": \"2011-04-15\" }", SimpleObject.class);
		EncounterCrudResource resource = new EncounterCrudResource(Context.getEncounterService().getEncounterByUuid("6519d653-393b-4118-9c83-a3715b82d4ac"));
		resource.update(post, new RequestContext());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2011-04-15"), Context.getEncounterService().getEncounterByUuid("6519d653-393b-4118-9c83-a3715b82d4ac").getEncounterDatetime());
	}
	
	@Test
	public void shouldDelete() throws Exception {
		EncounterCrudResource resource = new EncounterCrudResource(Context.getEncounterService().getEncounterByUuid("6519d653-393b-4118-9c83-a3715b82d4ac"));
		resource.delete("for testing", new RequestContext());
		Encounter deleted = Context.getEncounterService().getEncounterByUuid("6519d653-393b-4118-9c83-a3715b82d4ac");
		Assert.assertTrue(deleted.isVoided());
		Assert.assertNotNull(deleted.getDateVoided());
		for (Obs obs : deleted.getAllObs())
			Assert.assertTrue("Void did not cascade to obs", obs.isVoided());
	}

	@Test(expected=/*ResourceDeletion*/Exception.class) // due to batching, the exception doesn't happen until the getByUuid line
	public void shouldFailToPurge() throws Exception {
		EncounterCrudResource resource = new EncounterCrudResource(Context.getEncounterService().getEncounterByUuid("6519d653-393b-4118-9c83-a3715b82d4ac"));
		resource.purge(new RequestContext());
		Encounter purged = Context.getEncounterService().getEncounterByUuid("6519d653-393b-4118-9c83-a3715b82d4ac");
		Assert.assertNull(purged);
	}

}