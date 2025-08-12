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

import org.openmrs.Obs;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_1.ObsResource2_1;

/**
 * Resource for `obs`, supporting the new referenceRange property added in openmrs-core 2.7
 */
@Resource(name = RestConstants.VERSION_1 + "/obs", supportedClass = Obs.class, supportedOpenmrsVersions = { "2.7.* - 9.*" }, order = 1)
public class ObsResource2_7 extends ObsResource2_1 {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		if (description != null) {
			description.addProperty("referenceRange", Representation.DEFAULT);
		}
		return description;
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants2_7.RESOURCE_VERSION;
	}
}
