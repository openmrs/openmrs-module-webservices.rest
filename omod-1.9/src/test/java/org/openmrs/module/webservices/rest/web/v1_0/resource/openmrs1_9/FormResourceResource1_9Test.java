package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class FormResourceResource1_9Test extends BaseDelegatingResourceTest<FormResourceResource1_9,
        FormResource> {

    @Before
    public void setUp() throws Exception {
        executeDataSet(RestTestConstants1_9.FORM_RESOURCE_DATA_SET);
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        assertPropPresent("uuid");
        assertPropPresent("name");
        assertPropPresent("valueReference");
        assertPropPresent("display");

        assertPropNotPresent("dataType");
        assertPropNotPresent("handler");
        assertPropNotPresent("handlerConfig");

        assertPropEquals("uuid", getUuidProperty());
        assertPropEquals("display", getDisplayProperty());
        assertPropEquals("name", getObject().getName());
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        assertPropPresent("uuid");
        assertPropPresent("name");
        assertPropPresent("valueReference");
        assertPropPresent("dataType");
        assertPropPresent("handler");
        assertPropPresent("handlerConfig");
        assertPropPresent("display");

        assertPropEquals("uuid", getUuidProperty());
        assertPropEquals("display", getDisplayProperty());
        assertPropEquals("name", getObject().getName());
        assertPropEquals("dataType", getObject().getDatatypeClassname());
        assertPropEquals("handler", getObject().getPreferredHandlerClassname());
        assertPropEquals("handlerConfig", getObject().getHandlerConfig());
    }

    @Override
    public void validateRefRepresentation() throws Exception {
        assertPropPresent("uuid");
        assertPropPresent("display");

        assertPropNotPresent("dataType");
        assertPropNotPresent("handler");
        assertPropNotPresent("handlerConfig");
        assertPropNotPresent("name");

        assertPropEquals("uuid", getUuidProperty());
        assertPropEquals("display", getDisplayProperty());
    }

    @Override
    public FormResource newObject() {
        return Context.getFormService().getFormResourceByUuid(getUuidProperty());
    }

    @Override
    public String getDisplayProperty() {
        return getObject().getName();
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_9.FORM_RESOURCE_UUID;
    }
}