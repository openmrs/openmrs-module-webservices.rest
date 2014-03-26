/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import org.hibernate.collection.PersistentSet;
import org.junit.Assert;
import org.openmrs.ConceptClass;
import org.openmrs.OrderType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_10;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderTypeResource1_10Test extends BaseDelegatingResourceTest<OrderTypeResource1_10, OrderType> {

    @Override
    public OrderType newObject() {
        return Context.getOrderService().getOrderTypeByUuid(getUuidProperty());
    }

    @Override
    public String getDisplayProperty() {
        return "Test order";
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_10.ORDER_TYPE_UUID;
    }

    @Override
    public void assertPropEquals(String property, Object value) {
        if (value instanceof PersistentSet) {
            ArrayList<SimpleObject> conceptClassesFromRep = (ArrayList)getRepresentation().get(property);
            ArrayList<ConceptClass> conceptClasses = new ArrayList<ConceptClass>();
            conceptClasses.addAll((PersistentSet) value);
            String[] uuids = new String[conceptClassesFromRep.size()];
            Assert.assertEquals(conceptClasses.size(), conceptClassesFromRep.size());

            for (int i = 0; i < conceptClassesFromRep.size(); i++) {
                uuids[i] = conceptClassesFromRep.get(i).get("uuid").toString();
            }

            for (ConceptClass cc : conceptClasses) {
                Assert.assertTrue(Arrays.asList(uuids).contains(cc.getUuid()));
            }
        } else {
            super.assertPropEquals(property, value);
        }
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("name", getObject().getName());
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("javaClassName", getObject().getJavaClassName());
        assertPropEquals("retired", getObject().isRetired());
        assertPropEquals("conceptClasses", getObject().getConceptClasses());
        assertPropEquals("parent", getObject().getParent());
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("name", getObject().getName());
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("javaClassName", getObject().getJavaClassName());
        assertPropEquals("retired", getObject().isRetired());
        assertPropEquals("conceptClasses", getObject().getConceptClasses());
        assertPropEquals("parent", getObject().getParent());

    }

}
