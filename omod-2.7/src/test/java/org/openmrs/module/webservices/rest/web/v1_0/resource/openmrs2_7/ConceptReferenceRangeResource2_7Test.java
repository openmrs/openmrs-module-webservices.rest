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

import org.openmrs.ConceptReferenceRange;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

/**
 * Tests functionality of {@link ConceptReferenceRangeResource2_7}.
 */
public class ConceptReferenceRangeResource2_7Test extends BaseDelegatingResourceTest<ConceptReferenceRangeResource2_7, ConceptReferenceRange> {
	
	@Override
	public ConceptReferenceRange newObject() {
		ConceptReferenceRange referenceRange = new ConceptReferenceRange();
		referenceRange.setUuid(getUuidProperty());
		referenceRange.setConceptNumeric(Context.getConceptService().getConceptNumericByUuid(RestConstants2_7.CONCEPT_NUMERIC_UUID));
		return referenceRange;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("display", RestConstants2_7.DISPLAY_DIASTOLIC_BLOOD_PRESSURE);
		assertPropEquals("uuid", getObject().getUuid());
		assertPropEquals("concept", getObject().getConceptNumeric().getUuid());
		assertPropEquals("hiNormal", getObject().getHiNormal());
		assertPropEquals("hiAbsolute", getObject().getHiAbsolute());
		assertPropEquals("hiCritical", getObject().getHiCritical());
		assertPropEquals("lowNormal", getObject().getLowNormal());
		assertPropEquals("lowAbsolute", getObject().getLowAbsolute());
		assertPropEquals("lowCritical", getObject().getLowCritical());
		assertPropEquals("units", getObject().getConceptNumeric().getUnits());
		assertPropEquals("precise", getObject().getConceptNumeric().getAllowDecimal());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("display", RestConstants2_7.DISPLAY_DIASTOLIC_BLOOD_PRESSURE);
		assertPropEquals("uuid", getObject().getUuid());
		assertPropEquals("concept", getObject().getConceptNumeric().getUuid());
		assertPropEquals("hiNormal", getObject().getHiNormal());
		assertPropEquals("hiAbsolute", getObject().getHiAbsolute());
		assertPropEquals("hiCritical", getObject().getHiCritical());
		assertPropEquals("lowNormal", getObject().getLowNormal());
		assertPropEquals("lowAbsolute", getObject().getLowAbsolute());
		assertPropEquals("lowCritical", getObject().getLowCritical());
		assertPropEquals("units", getObject().getConceptNumeric().getUnits());
		assertPropEquals("precise", getObject().getConceptNumeric().getAllowDecimal());
	}

	@Override
	public void validateRefRepresentation() throws Exception {
		super.validateRefRepresentation();
		assertPropEquals("display", RestConstants2_7.DISPLAY_DIASTOLIC_BLOOD_PRESSURE);
		assertPropEquals("uuid", getObject().getUuid());
	}

	@Override
	public String getDisplayProperty() {
		return RestConstants2_7.DISPLAY_DIASTOLIC_BLOOD_PRESSURE;
	}

	@Override
	public String getUuidProperty() {
		return RestConstants2_7.CONCEPT_REFERENCE_RANGE_UUID;
	}
}
