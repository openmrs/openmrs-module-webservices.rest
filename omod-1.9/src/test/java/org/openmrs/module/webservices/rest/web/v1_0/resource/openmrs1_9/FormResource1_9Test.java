package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class FormResource1_9Test extends BaseDelegatingResourceTest<FormResource1_9, Form> {
    @Before
    public void setUp() throws Exception {
        executeDataSet(RestTestConstants1_9.FORM_RESOURCE_DATA_SET);
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropPresent("resources");
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("resources");
    }

    @Override
    public Form newObject() {
        return Context.getFormService().getFormByUuid(getUuidProperty());
    }

    @Override
    public String getDisplayProperty() {
        return getObject().getName();
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_9.FORM_UUID;
    }
}
