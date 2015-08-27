package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.junit.Test;
import org.openmrs.FormResource;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class FormResourceResource1_9Test extends BaseDelegatingResourceTest<FormResourceResource1_9,
        FormResource> {
    private static final String VALUE_REFERENCE = "b5c27abf-0d6c-4824-a58d-1afbfac66f7e";
    private static final String TEST_RESOURCE_NAME = "test resource";
    @Override
    public void validateDefaultRepresentation() throws Exception {
        assertPropPresent("uuid");
        assertPropPresent("form");
        assertPropPresent("name");
        assertPropPresent("valuereference");
        assertPropPresent("display");

        assertPropNotPresent("datatype");
        assertPropNotPresent("handler");
        assertPropNotPresent("handlerconfig");

        assertPropEquals("uuid", RestTestConstants1_9.FORM_RESOURCE_UUID);
        assertPropEquals("display", TEST_RESOURCE_NAME);
        assertPropEquals("valuereference", VALUE_REFERENCE);
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        assertPropPresent("uuid");
        assertPropPresent("form");
        assertPropPresent("name");
        assertPropPresent("valuereference");
        assertPropPresent("datatype");
        assertPropPresent("handler");
        assertPropPresent("handlerconfig");
        assertPropPresent("display");

        assertPropEquals("uuid", RestTestConstants1_9.FORM_RESOURCE_UUID);
        assertPropEquals("display", TEST_RESOURCE_NAME);
        assertPropEquals("valuereference", VALUE_REFERENCE);
    }

    @Override
    public void validateRefRepresentation() throws Exception {
        assertPropPresent("uuid");
        assertPropPresent("form");
        assertPropPresent("valuereference");
        assertPropPresent("display");

        assertPropNotPresent("datatype");
        assertPropNotPresent("handler");
        assertPropNotPresent("handlerconfig");
        assertPropNotPresent("name");

        assertPropEquals("uuid", RestTestConstants1_9.FORM_RESOURCE_UUID);
        assertPropEquals("display", TEST_RESOURCE_NAME);
        assertPropEquals("valuereference", VALUE_REFERENCE);
    }

    @Override
    public FormResource newObject() {
        String valueReference = "b5c27abf-0d6c-4824-a58d-1afbfac66f7e";
        FormResource formResource = new FormResource();
        formResource.setId(1);
        formResource.setUuid(getUuidProperty());
        formResource.setValueReferenceInternal(VALUE_REFERENCE);
        formResource.setName(TEST_RESOURCE_NAME);
        return formResource;
    }

    @Override
    public String getDisplayProperty() {
        return getUuidProperty();
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_9.FORM_RESOURCE_UUID;
    }
}