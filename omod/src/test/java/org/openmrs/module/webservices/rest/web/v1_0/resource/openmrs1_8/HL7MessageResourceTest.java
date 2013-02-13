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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7InQueue;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.util.IncomingHl7Message;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.HL7MessageResource;

public class HL7MessageResourceTest extends BaseDelegatingResourceTest<HL7MessageResource, IncomingHl7Message> {
	
	@Before
	public void before() throws Exception {
		executeDataSet(ResourceTestConstants.RESOURCE_TEST_DATASET);
	}
	
	@Override
	public IncomingHl7Message newObject() {
		HL7InQueue msg = new HL7InQueue();
		msg.setHL7Data("hl7Data");
		msg.setHL7SourceKey("sourceKey");
		msg.setHL7Source(Context.getHL7Service().getHL7SourceByName(ResourceTestConstants.HL7_SOURCE_NAME));
		Context.getHL7Service().saveHL7InQueue(msg);
		return new IncomingHl7Message(msg);
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("messageState", getObject().getMessageState());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("source");
		assertPropEquals("sourceKey", getObject().getSourceKey());
		assertPropEquals("data", getObject().getData());
		assertPropEquals("messageState", getObject().getMessageState());
	}
	
	@Override
	public String getDisplayProperty() {
		return "sourceKey";
	}
	
	@Override
	public String getUuidProperty() {
		return getObject().getUuid();
	}
	
}
