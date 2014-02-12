package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import org.openmrs.CareSetting;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class CareSettingResource1_10Test extends BaseDelegatingResourceTest<CareSettingResource1_10, CareSetting> {
    
    @Override
    public CareSetting newObject() {
        return Context.getOrderService().getCareSetting(1);
    }

    @Override
    public String getDisplayProperty() {
        return "OUTPATIENT";
    }

    @Override
    public String getUuidProperty() {
        return "2ed1e57d-9f18-41d3-b067-2eeaf4b30fb1";
    }
    
}
