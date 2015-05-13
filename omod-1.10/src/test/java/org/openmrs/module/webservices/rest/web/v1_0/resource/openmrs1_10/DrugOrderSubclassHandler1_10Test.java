package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class DrugOrderSubclassHandler1_10Test {

    @Test
    public void getDisplayString_shouldNotFailForDcOrder() throws Exception {
        Drug drug = new Drug();
        drug.setName("Aspirin");

        DrugOrder order = new DrugOrder();
        order.setAction(Order.Action.DISCONTINUE);
        order.setDrug(drug);

        String actual = DrugOrderSubclassHandler1_10.getDisplay(order);
        assertThat(actual, is("(DISCONTINUE) Aspirin"));
    }
}