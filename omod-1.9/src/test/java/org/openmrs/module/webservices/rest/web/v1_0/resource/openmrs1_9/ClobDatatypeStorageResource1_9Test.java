package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ClobDatatypeStorageResource1_9Test extends
        BaseDelegatingResourceTest<ClobDatatypeStorageResource1_9, ClobDatatypeStorage> {

    @Override
    public void validateRefRepresentation() throws Exception {
        assertPropPresent("uuid");
        assertPropPresent("display");
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        validateRefRepresentation();
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        validateRefRepresentation();
        assertPropPresent("value");
    }

    @Override
    public ClobDatatypeStorage newObject() {
        return new ClobDatatypeStorage();
    }

    @Override
    public String getDisplayProperty() {
        return getUuidProperty();
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_9.CLOBDATATYPESTORAGE_RESOURCE_UUID;
    }
}
