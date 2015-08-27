package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name= RestConstants.VERSION_1 + "/clobdata", supportedClass = ClobDatatypeStorage.class,
        supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class ClobDatatypeStorageResource1_9 extends DelegatingCrudResource<ClobDatatypeStorage> {

    @RepHandler(RefRepresentation.class)
    public SimpleObject convertToRef(ClobDatatypeStorage delegate) throws ConversionException {
        DelegatingResourceDescription rep = new DelegatingResourceDescription();
        rep.addProperty("uuid");
        rep.addProperty("display");
        rep.addSelfLink();
        return convertDelegateToRepresentation(delegate, rep);
    }

    @RepHandler(DefaultRepresentation.class)
    public SimpleObject asDefaultRep(ClobDatatypeStorage delegate) throws Exception {
        DelegatingResourceDescription rep = new DelegatingResourceDescription();
        rep.addProperty("uuid");
        rep.addProperty("display");
        rep.addSelfLink();
        rep.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
        return convertDelegateToRepresentation(delegate, rep);
    }

    @RepHandler(FullRepresentation.class)
    public SimpleObject asFullRep(ClobDatatypeStorage delegate) throws Exception {
        DelegatingResourceDescription rep = new DelegatingResourceDescription();
        rep.addProperty("uuid");
        rep.addProperty("display");
        rep.addProperty("value");
        rep.addSelfLink();
        return convertDelegateToRepresentation(delegate, rep);
    }

    @PropertyGetter("display")
    public String getDisplayProperty(ClobDatatypeStorage delegate) {
        return delegate.getUuid();
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("value");
        return description;
    }

    @Override
    public ClobDatatypeStorage getByUniqueId(String uniqueId) {
        return null;
    }

    @Override
    protected void delete(ClobDatatypeStorage delegate, String reason, RequestContext context) throws ResponseException {

    }

    @Override
    public ClobDatatypeStorage newDelegate() {
        return null;
    }

    @Override
    public ClobDatatypeStorage save(ClobDatatypeStorage delegate) {
        return null;
    }

    @Override
    public void purge(ClobDatatypeStorage delegate, RequestContext context) throws ResponseException {

    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        return null;
    }
}
