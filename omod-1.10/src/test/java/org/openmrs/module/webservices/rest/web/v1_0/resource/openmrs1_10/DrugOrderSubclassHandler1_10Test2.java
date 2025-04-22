/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;

import static org.junit.Assert.*;

public class DrugOrderSubclassHandler1_10Test2 {

    private DrugOrderSubclassHandler1_10 handler;

    @Before
    public void setup() {
        handler = new DrugOrderSubclassHandler1_10();
    }

    @Test
    public void shouldIncludeDrugStrengthInDisplay() {
        // Create test drug
        Drug drug = new Drug();
        drug.setName("Aspirin");
        drug.setStrength("325 mg");

        // Create test order type
        OrderType drugOrderType = new OrderType();
        drugOrderType.setName("Drug order");

        // Create test order
        DrugOrder order = new DrugOrder();
        order.setDrug(drug);
        order.setOrderType(drugOrderType);
        order.setAction(Order.Action.NEW);

        // Test the display string
        String display = DrugOrderSubclassHandler1_10.getDisplay(order);
        
        // Verify results
        assertNotNull("Display should not be null", display);
        assertTrue("Display should contain drug name: " + display, display.contains("Aspirin"));
        assertTrue("Display should contain strength: " + display, display.contains("325 mg"));
    }
}