package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import org.openmrs.OrderFrequency;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class OrderFrequencyResource1_10Test extends BaseDelegatingResourceTest<OrderFrequencyResource1_10, OrderFrequency> {
    
    @Override
    public OrderFrequency newObject() {
        return Context.getOrderService().getOrderFrequencyByUuid(getUuidProperty());
    }

    @Override
    public String getDisplayProperty() {
        return "1/day x 7 days/week";
    }

    @Override
    public String getUuidProperty() {
        return "28090760-7c38-11e3-baa7-0800200c9a66";
    }
    
}
