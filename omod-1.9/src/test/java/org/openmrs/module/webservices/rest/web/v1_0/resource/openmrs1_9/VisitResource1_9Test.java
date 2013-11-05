/**
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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

import java.util.Date;

/**
 * Contains tests for the {@link VisitResource1_9}
 */
public class VisitResource1_9Test extends BaseDelegatingResourceTest<VisitResource1_9, Visit> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#newObject()
	 */
	@Override
	public Visit newObject() {
		return Context.getVisitService().getVisitByUuid(getUuidProperty());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#validateDefaultRepresentation()
	 */
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("patient");
		assertPropPresent("visitType");
		assertPropPresent("indication");
		assertPropPresent("location");
		assertPropPresent("encounters");
		assertPropEquals("startDatetime", getObject().getStartDatetime());
		assertPropEquals("stopDatetime", getObject().getStopDatetime());
		assertPropPresent("attributes");
		assertPropEquals("voided", getObject().isVoided());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#validateFullRepresentation()
	 */
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("patient");
		assertPropPresent("visitType");
		assertPropPresent("indication");
		assertPropPresent("location");
		assertPropPresent("encounters");
		assertPropEquals("startDatetime", getObject().getStartDatetime());
		assertPropEquals("stopDatetime", getObject().getStopDatetime());
		assertPropPresent("attributes");
		assertPropEquals("voided", getObject().isVoided());
		assertPropPresent("auditInfo");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#getDisplayProperty()
	 */
	@Override
	public String getDisplayProperty() {
		return "Initial HIV Clinic Visit @ Unknown Location - 01/01/2005 00:00";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#getUuidProperty()
	 */
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_9.VISIT_UUID;
	}

    @Test
    public void create_shouldCreateVisitWithPatientUuidAndVisitTypeUuid() {
        SimpleObject visitProperties = new SimpleObject();
        visitProperties.add("patient", new SimpleObject().add("uuid", RestTestConstants1_8.PATIENT_UUID));
        visitProperties.add("visitType", new SimpleObject().add("uuid", RestTestConstants1_9.VISIT_TYPE_UUID));
        visitProperties.add("startDatetime", new Date());

        SimpleObject savedVisit = (SimpleObject)getResource().create(visitProperties, new RequestContext());

        Assert.assertNotNull(Context.getVisitService().getVisitByUuid(String.valueOf(savedVisit.get("uuid"))));
    }
}
