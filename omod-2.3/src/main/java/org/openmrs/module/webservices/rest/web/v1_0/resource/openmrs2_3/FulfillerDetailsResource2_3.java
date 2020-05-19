package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_3;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.StringProperty;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.FulfillerDetails2_2;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.OrderResource2_2;

@SubResource(parent = OrderResource2_2.class, path = "fulfillerdetails", supportedClass = FulfillerDetails2_2.class, supportedOpenmrsVersions = {
        "2.3.*", "2.4.*" })
public class FulfillerDetailsResource2_3 extends DelegatingSubResource<FulfillerDetails2_3, Order, OrderResource2_2> {

    @Override
    public FulfillerDetails2_3 newDelegate() {
        return new FulfillerDetails2_3();
    }

    @Override
    public FulfillerDetails2_3 save(FulfillerDetails2_3 delegate) {
        Context.getOrderService().updateOrderFulfillerStatus(delegate.getOrder(), delegate.getFulfillerStatus(),
                delegate.getFulfillerComment());
        return null;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
        delegatingResourceDescription.addProperty("fulfillerStatus");
        delegatingResourceDescription.addProperty("fulfillerComment");
        return delegatingResourceDescription;
    }

    @Override
    public Model getCREATEModel(Representation rep) {
        return new ModelImpl()
                .property("fulfillerComment", new StringProperty())
                .property("fulfillerStatus", new EnumProperty(Order.FulfillerStatus.class));
    }

    @Override
    public Order getParent(FulfillerDetails2_3 instance) {
        return instance.getOrder();
    }

    @Override
    public void setParent(FulfillerDetails2_3 instance, Order parent) {
        instance.setOrder(parent);
    }

    @Override
    public PageableResult doGetAll(Order parent, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public FulfillerDetails2_3 getByUniqueId(String uniqueId) {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    protected void delete(FulfillerDetails2_3 delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public void purge(FulfillerDetails2_3 delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        return getRepresentationDescription(null);
    }

}
