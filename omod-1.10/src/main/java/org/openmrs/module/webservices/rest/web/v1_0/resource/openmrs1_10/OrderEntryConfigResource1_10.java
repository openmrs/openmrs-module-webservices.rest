package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/orderentryconfig", supportedClass = Object.class, supportedOpenmrsVersions = {"1.10.*", "1.11.*", "1.12.*"})
public class OrderEntryConfigResource1_10 implements Listable {

    @Override
    public SimpleObject getAll(RequestContext context) throws ResponseException {
        OrderService orderService = Context.getOrderService();

        SimpleObject ret = new SimpleObject();
        // try/catch each of these to avoid failing in the case where one of these is not configured
        try {
            ret.put("drugRoutes", ConversionUtil.convertToRepresentation(orderService.getDrugRoutes(), context.getRepresentation()));
        } catch (Exception ex) { }
        try {
            ret.put("drugDosingUnits", ConversionUtil.convertToRepresentation(orderService.getDrugDosingUnits(), context.getRepresentation()));
        } catch (Exception ex) { }
        try {
            ret.put("drugDispensingUnits", ConversionUtil.convertToRepresentation(orderService.getDrugDispensingUnits(), context.getRepresentation()));
        } catch (Exception ex) { }
        try {
            ret.put("durationUnits", ConversionUtil.convertToRepresentation(orderService.getDurationUnits(), context.getRepresentation()));
        } catch (Exception ex) { }
        try {
            ret.put("testSpecimenSources", ConversionUtil.convertToRepresentation(orderService.getTestSpecimenSources(), context.getRepresentation()));
        } catch (Exception ex) { }
        try {
            ret.put("orderFrequencies", ConversionUtil.convertToRepresentation(orderService.getOrderFrequencies(false), context.getRepresentation()));
        } catch (Exception ex) { }
        return ret;
    }

    @Override
    public String getUri(Object instance) {
        return RestConstants.URI_PREFIX + "/orderentryconfig";
    }
}
