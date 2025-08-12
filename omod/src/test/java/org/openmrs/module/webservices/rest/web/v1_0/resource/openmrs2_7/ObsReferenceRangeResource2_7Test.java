/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_7;

import org.openmrs.ObsReferenceRange;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

/**
 * Tests functionality of {@link ObsReferenceRangeResource2_7}.
 */
public class ObsReferenceRangeResource2_7Test extends BaseDelegatingResourceTest<ObsReferenceRangeResource2_7, ObsReferenceRange> {
	
	@Override
	public ObsReferenceRange newObject() {
		ObsReferenceRange referenceRange = new ObsReferenceRange();
		referenceRange.setUuid(RestConstants2_7.OBS_REFERENCE_RANGE_UUID);
		return referenceRange;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("display", "");
		assertPropEquals("uuid", getObject().getUuid());
		assertPropEquals("hiNormal", getObject().getHiNormal());
		assertPropEquals("hiAbsolute", getObject().getHiAbsolute());
		assertPropEquals("hiCritical", getObject().getHiCritical());
		assertPropEquals("lowNormal", getObject().getLowNormal());
		assertPropEquals("lowAbsolute", getObject().getLowAbsolute());
		assertPropEquals("lowCritical", getObject().getLowCritical());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		validateDefaultRepresentation();
	}

	@Override
	public String getDisplayProperty() {
		return "";
	}

	@Override
	public String getUuidProperty() {
		return RestConstants2_7.OBS_REFERENCE_RANGE_UUID;
	}
}
