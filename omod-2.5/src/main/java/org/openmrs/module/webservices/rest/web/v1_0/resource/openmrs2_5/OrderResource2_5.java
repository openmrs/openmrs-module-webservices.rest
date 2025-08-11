/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_5;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.OrderResource2_2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Resource(name = RestConstants.VERSION_1 + "/order", supportedClass = Order.class, supportedOpenmrsVersions = { "2.5.* - 9.*" })
public class OrderResource2_5 extends OrderResource2_2 {

    /**
     * Retrieves all non-voided {@link OrderAttribute}s for the given {@link Order}.
     *
     * @param delegate
     * @return ActiveAttributes
     */
    @PropertyGetter("attributes")
    public List<OrderAttribute> getAttributes(Order delegate) {
        return (List<OrderAttribute>) delegate.getActiveAttributes();
    }

    /**
     * Sets attributes on the given Order.
     *
     * @param instance
     * @param attrs
     */
    @PropertySetter("attributes")
    public static void setAttributes(Order instance, List<OrderAttribute> attrs) {
        for (OrderAttribute attr : attrs) {
            attr.setOwner(instance);
            instance.addAttribute(attr);
        }
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#setConvertedProperties(Object, Map, DelegatingResourceDescription, boolean) ()
     */
    @Override
    public void setConvertedProperties(Order delegate, Map<String, Object> propertyMap, DelegatingResourceDescription description, boolean mustIncludeRequiredProperties) throws ConversionException {
        Object attributesValue = propertyMap.get("attributes");
        if (attributesValue != null) {
            propertyMap = new java.util.HashMap<>(propertyMap);
            propertyMap.remove("attributes");
        }

        try {
            super.setConvertedProperties(delegate, propertyMap, description, mustIncludeRequiredProperties);
            if (attributesValue != null) {
                List<OrderAttribute> attrs = new ArrayList<>();
                for (Object obj : (List<?>) attributesValue) {
                    if (obj instanceof Map) {
                        Map<String, Object> attrMap = (Map<String, Object>) obj;
                        OrderAttribute attr = new OrderAttribute();
                        attr.setAttributeType(Context.getOrderService().getOrderAttributeTypeByUuid((String) attrMap.get("attributeType")));
                        attr.setValue(attrMap.get("value"));
                        attr.setValueReferenceInternal(attrMap.get("value").toString());
                        attrs.add(attr);
                    }
                }
                setAttributes(delegate, attrs);
            }
        } catch (ConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new ConversionException("Error setting properties on " + delegate.getClass().getSimpleName(), e);
        }
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getProperty(Object, String)
     */
    @Override
    public Object getProperty(Order instance, String propertyName) throws ConversionException {
        if ("attributes".equals(propertyName)) {
            return getAttributes(instance);
        }

        return super.getProperty(instance, propertyName);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription parentRep = super.getRepresentationDescription(rep);
        parentRep.addProperty("attributes", Representation.REF);
        return parentRep;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = super.getCreatableProperties();
        description.addProperty("attributes");
        return description;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getPropertiesToExposeAsSubResources()
     */
    @Override
    public List<String> getPropertiesToExposeAsSubResources() {
        return Arrays.asList("attributes");
    }
}