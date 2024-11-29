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

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.StringProperty;
import org.openmrs.Diagnosis;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.DiagnosisResource2_2;

/**
 * {@link Resource} for Diagnosis, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/patientdiagnoses", order = 1, supportedClass = Diagnosis.class, supportedOpenmrsVersions = {
        "2.5.* - 9.*" })
public class DiagnosisResource2_5 extends DiagnosisResource2_2 {

    /**
     * @see DelegatingCrudResource#getRepresentationDescription(Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = super.getRepresentationDescription(rep);
        if (description != null) {
            description.addProperty("formFieldNamespace");
            description.addProperty("formFieldPath");
        }
        return description;
    }

    /**
     * @see BaseDelegatingResource#getCreatableProperties()
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = super.getCreatableProperties();
        description.addProperty("formFieldNamespace");
        description.addProperty("formFieldPath");
        return description;
    }

    /**
     * @see BaseDelegatingResource#getUpdatableProperties()
     */
    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = super.getUpdatableProperties();
        description.addProperty("formFieldNamespace");
        description.addProperty("formFieldPath");
        return description;
    }

    private Model addNewProperties(Model model, Representation rep) {
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            ((ModelImpl) model)
                    .property("formFieldNamespace", new StringProperty())
                    .property("formFieldPath", new StringProperty());
        }
        return model;
    }

    /**
     * Annotated setter for formFieldPath
     *
     * @param diagnosis
     * @param formFieldPath
     */
    @PropertySetter("formFieldPath")
    public static void setFormFieldPath(Diagnosis diagnosis, Object formFieldPath) {
        diagnosis.setFormField(diagnosis.getFormFieldNamespace(), (String)formFieldPath);
    }

    /**
     * Annotated setter for formFieldNamespace
     *
     * @param diagnosis
     * @param namespace
     */
    @PropertySetter("formFieldNamespace")
    public static void setFormFieldNamespace(Diagnosis diagnosis, Object namespace) {
        diagnosis.setFormField((String)namespace, diagnosis.getFormFieldPath());
    }
}
