/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_5;

import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.DiagnosisResource2_2;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.DiagnosisResource2_2Test;

/**
 * Tests functionality of {@link DiagnosisResource2_2}.
 */
public class DiagnosisResource2_5Test extends DiagnosisResource2_2Test {
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("formNamespace");
		assertPropPresent("formPath");
		assertPropPresent("formNamespaceAndPath");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("formNamespace");
		assertPropPresent("formPath");
		assertPropPresent("formNamespaceAndPath");
	}
}
