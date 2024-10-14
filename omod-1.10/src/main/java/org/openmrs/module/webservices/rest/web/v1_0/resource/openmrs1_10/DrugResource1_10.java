/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.openmrs.Drug;
import org.openmrs.DrugReferenceMap;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.DrugResource1_8;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for {@link org.openmrs.Drug},
 * supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/drug", order = 3, supportedClass = Drug.class, supportedOpenmrsVersions = { "1.10.*" })
public class DrugResource1_10 extends DrugResource1_8 {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants1_10.RESOURCE_VERSION;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription repDesc = super.getRepresentationDescription(rep);
		if (rep instanceof DefaultRepresentation) {
			repDesc.addProperty("strength");
			repDesc.addProperty("drugReferenceMaps", Representation.REF);
		} else if (rep instanceof FullRepresentation) {
			repDesc.addProperty("strength");
			repDesc.addProperty("drugReferenceMaps", Representation.DEFAULT);
		}
		return repDesc;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addProperty("strength");
		description.addRequiredProperty("name");
		description.addProperty("drugReferenceMaps");
		
		return description;
	}

	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> schema = super.getGETSchema(rep);
		if (schema != null) {
            if (rep instanceof DefaultRepresentation) {
				schema.addProperty("drugReferenceMaps", new Schema<DrugReferenceMap>().$ref("#/components/schemas/DrugreferencemapGetRef"));
			} else if (rep instanceof FullRepresentation) {
				schema.addProperty("drugReferenceMaps", new Schema<DrugReferenceMap>().$ref("#/components/schemas/DrugreferencemapGet"));
			}
		}
		return schema;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		return new ObjectSchema()
				.addProperty("drugReferenceMaps", new Schema<DrugReferenceMap>().$ref("#/components/schemas/DrugreferencemapCreate"));
	}
}
