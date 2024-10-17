/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.unrelatedtest.rest.resource;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openmrs.module.unrelatedtest.UnrelatedGenericChild;
import org.openmrs.module.webservices.rest.doc.SwaggerSpecificationCreatorTest;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.v1_0.test.GenericChildResource;

/**
 * A test resource that is unrelated to the main webservices package.
 * 
 * @see SwaggerSpecificationCreatorTest#testUnrelatedResourceDefinitions()
 */
@Resource(name = RestConstants.VERSION_1 + "/unrelated", supportedClass = UnrelatedGenericChild.class, supportedOpenmrsVersions = { "1.9.* - 9.*" })
public class UnrelatedGenericChildResource extends GenericChildResource {
	
	public static boolean getGETCalled = false;
	
	public static boolean getCREATECalled = false;
	
	public static boolean getUPDATECalled = false;
	
	/*******************************
	 * TEST METHOD IMPLEMENTATIONS * These methods are the ones we want to test against. There
	 * implementation is unimportant, they just set flags so we can assert the methods were called
	 * correctly by the reflector.
	 */

	@Override
	public Schema<?> getGETSchema(Representation rep) {
		getGETCalled = true;
		System.out.println("getGETSchema called");
		Schema<?> schema = super.getGETSchema(rep);
		if (schema == null) {
			schema = new ObjectSchema();
		}

		schema.addProperty("someProperty", new StringSchema());
		return schema;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		getCREATECalled = true;
		System.out.println("getCREATESchema called");
		return super.getCREATESchema(rep);
	}

	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		getUPDATECalled = true;
		System.out.println("getUPDATESchema called");
		return super.getUPDATESchema(rep);
	}
}
