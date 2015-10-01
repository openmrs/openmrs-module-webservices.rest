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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_12;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
<<<<<<< HEAD
import org.openmrs.Allergies;
=======
import org.openmrs.allergyapi.Allergies;
>>>>>>> origin/TRUNK-4747-A
import org.openmrs.api.PatientService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/allergies", supportedClass = Allergies.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class AllergiesResource1_12 implements Retrievable {

    @Override
    public Object retrieve(String patientUuid, RequestContext requestContext) throws ResponseException {
        // TODO figure out how to make tests work and do this instead of directly using PatientService
        // DelegatingCrudResource<Patient> patientResource = (DelegatingCrudResource<Patient>) Context.getService(RestService.class).getResourceByName("patient");
        // Patient patient = patientResource.getByUniqueId(patientUuid);

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        if (patient == null) {
            throw new NullPointerException();
        }
        Allergies allergies = Context.getService(PatientService.class).getAllergies(patient);

        SimpleObject ret = new SimpleObject();
        ret.add("status", allergies.getAllergyStatus());
        ret.add("allergies", ConversionUtil.convertToRepresentation(allergies, requestContext.getRepresentation()));
        return ret;
    }

    @Override
    public List<Representation> getAvailableRepresentations() {
        return Arrays.asList(Representation.REF, Representation.DEFAULT, Representation.FULL);
    }

    @Override
    public String getUri(Object delegate) {
        // TODO cannot be implemented because Allergies doesn't have a Patient on it
        return null;
    }
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	@Override
	 */
	public String getResourceVersion() {
		return RestConstants1_12.RESOURCE_VERSION;
	}
}
