package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.apache.commons.lang.StringUtils;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
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
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.text.Normalizer;

@Resource(name = RestConstants.VERSION_1 + "/formresource", supportedClass = FormResource.class,
        supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class FormResourceResource1_9 extends DelegatingCrudResource<FormResource> {

    @RepHandler(RefRepresentation.class)
    public SimpleObject convertToRef(FormResource delegate) throws ConversionException {
        DelegatingResourceDescription rep = new DelegatingResourceDescription();
        rep.addProperty("uuid");
        rep.addProperty("form", Representation.REF);
        rep.addProperty("valuereference");
        rep.addProperty("display");
        rep.addSelfLink();
        return convertDelegateToRepresentation(delegate, rep);
    }

    @RepHandler(DefaultRepresentation.class)
    public SimpleObject asDefaultRep(FormResource delegate) throws Exception {
        DelegatingResourceDescription rep = new DelegatingResourceDescription();
        rep.addProperty("uuid");
        rep.addProperty("form", Representation.DEFAULT);
        rep.addProperty("name");
        rep.addProperty("valuereference");
        rep.addProperty("display");
        rep.addSelfLink();
        rep.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
        return convertDelegateToRepresentation(delegate, rep);
    }

    @RepHandler(FullRepresentation.class)
    public SimpleObject asFullRep(FormResource delegate) throws Exception {
        DelegatingResourceDescription rep = new DelegatingResourceDescription();
        rep.addProperty("uuid");
        rep.addProperty("form");
        rep.addProperty("name");
        rep.addProperty("valuereference");
        rep.addProperty("datatype");
        rep.addProperty("handler");
        rep.addProperty("handlerconfig");
        rep.addProperty("display");
        rep.addSelfLink();
        return convertDelegateToRepresentation(delegate, rep);
    }

    @PropertyGetter("display")
    public String getDisplayString(FormResource delegate) {
        return StringUtils.isEmpty(delegate.getName()) ? "" : delegate.getName();
    }

    @PropertyGetter("datatype")
    public String getDatatype(FormResource formResource) {
        return formResource.getDatatypeClassname();
    }

    @PropertyGetter("handler")
    public String getHandler(FormResource formResource) {
        return formResource.getPreferredHandlerClassname();
    }

    @PropertyGetter("handlerconfig")
    public String getHandlerConfig(FormResource formResource) {
        return formResource.getHandlerConfig();
    }

    @PropertyGetter("valuereference")
    public String getValueReference(FormResource formResource) {
        return formResource.getValueReference();
    }

    @Override
    public FormResource getByUniqueId(String uniqueId) {
        return null;
    }

    @Override
    protected void delete(FormResource delegate, String reason, RequestContext context) throws ResponseException {
         purge(delegate, context);
    }

    @Override
    public void purge(FormResource delegate, RequestContext context) throws ResponseException {
        Context.getFormService().purgeFormResource(delegate);
    }

    @Override
    public FormResource newDelegate() {
        return new FormResource();
    }

    @Override
    public FormResource save(FormResource delegate) {
        return Context.getFormService().saveFormResource(delegate);
    }

    @Override
    public String getResourceVersion() {
        return "1.9";
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        // Returning null causes the @RepHandler to be called.
        return null;
    }
}

