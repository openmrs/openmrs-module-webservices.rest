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
package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * This is a placeholder implementation
 */
@Resource("patientidentifiertype")
@Handler(supports=PatientIdentifierType.class, order=0)
public class PatientIdentifierTypeResource extends MetadataDelegatingCrudResource<PatientIdentifierType> {

	private PatientService service() {
		return Context.getPatientService();
	}
	
	@Override
    public PatientIdentifierType getByUniqueId(String uniqueId) {
	    return service().getPatientIdentifierTypeByUuid(uniqueId);
    }

	@Override
    protected PatientIdentifierType newDelegate() {
	   return new PatientIdentifierType();
    }

	@Override
    protected PatientIdentifierType save(PatientIdentifierType delegate) {
	    return service().savePatientIdentifierType(delegate);
    }

	@Override
    public void purge(PatientIdentifierType delegate, RequestContext context) throws ResponseException {
	    service().purgePatientIdentifierType(delegate);
    }

	@Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
	    return null;
    }

}
