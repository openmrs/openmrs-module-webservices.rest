package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_12;


import org.junit.Before;
import org.openmrs.OrderSetMember;
import org.openmrs.api.OrderSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_12;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;


public class OrderSetMemberResource1_12Test extends BaseDelegatingResourceTest<OrderSetMemberResource1_12, OrderSetMember> {

    private OrderSetService orderSetService;

    @Before
    public void init() throws Exception {
        orderSetService = Context.getOrderSetService();
        executeDataSet(RestTestConstants1_12.TEST_DATA_SET);
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("name", getObject().getName());
        assertPropEquals("uuid", getObject().getUuid());
        assertPropEquals("description", getObject().getDescription());
        assertPropPresent("orderTemplateType");
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("name", getObject().getName());
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("retired", getObject().getRetired());
        assertPropEquals("retireBy", getObject().getRetiredBy());
        assertPropPresent("auditInfo");
        assertPropPresent("orderTemplateType");
    }

    @Override
    public OrderSetMember newObject() {
        return orderSetService.getOrderSetMemberByUuid(RestTestConstants1_12.ORDER_SET_MEMBER_UUID);
    }

    @Override
    public String getDisplayProperty() {
        return null;
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_12.ORDER_SET_MEMBER_UUID;
    }
}
