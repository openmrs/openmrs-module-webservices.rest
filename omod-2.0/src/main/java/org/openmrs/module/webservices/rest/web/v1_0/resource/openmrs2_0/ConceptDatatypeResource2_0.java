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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.openmrs.ConceptDatatype;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptDatatypeResource1_8;

/**
 * {@link Resource} for {@link ConceptDatatype}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptdatatype", supportedClass = ConceptDatatype.class, supportedOpenmrsVersions = {"2.0.*", "2.1.*"})
public class ConceptDatatypeResource2_0 extends ConceptDatatypeResource1_8 {
	
	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public ConceptDatatype save(ConceptDatatype conceptDatatype) {
		throw new ResourceDoesNotSupportOperationException();
	}
}
