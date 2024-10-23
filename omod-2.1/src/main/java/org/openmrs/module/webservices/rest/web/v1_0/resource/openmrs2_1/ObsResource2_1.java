/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_1;

import io.swagger.v3.oas.models.media.Schema;
import org.openmrs.Obs;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.ObsResource1_11;

import java.util.Arrays;

/**
 * Resource for `obs`, supporting the new properties added in openmrs-core 2.1 (status and
 * interpretation)
 */
@Resource(name = RestConstants.VERSION_1 + "/obs", supportedClass = Obs.class, supportedOpenmrsVersions = { "2.1.* - 9.*" })
public class ObsResource2_1 extends ObsResource1_11 {

	@Override
	@SuppressWarnings("unchecked")
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> schema = super.getGETSchema(rep);
		if (schema instanceof Schema) {
            schema
					.addProperty("status", new Schema<Obs.Status>().type("string")._enum(Arrays.asList(Obs.Status.values())))
					.addProperty("interpretation", new Schema<Obs.Interpretation>().type("string")._enum(Arrays.asList(Obs.Interpretation.values())));
		}
		return schema;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Schema<?> getCREATESchema(Representation rep) {
		Schema<?> schema = super.getCREATESchema(rep);
		if (schema instanceof Schema) {
            schema
					.addProperty("status", new Schema<Obs.Status>().type("string")._enum(Arrays.asList(Obs.Status.values())))
					.addProperty("interpretation", new Schema<Obs.Interpretation>().type("string")._enum(Arrays.asList(Obs.Interpretation.values())));
		}
		return schema;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		if (description != null) {
			description.addProperty("status");
			description.addProperty("interpretation");
		}
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addProperty("status");
		description.addProperty("interpretation");
		return description;
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants2_1.RESOURCE_VERSION;
	}
}
