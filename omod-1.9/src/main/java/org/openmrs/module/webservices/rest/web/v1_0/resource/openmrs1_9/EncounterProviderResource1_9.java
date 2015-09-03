package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.EncounterRole;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for EncounterProvider, supporting standard CRUD operations
 */
@SubResource(path = "encounterprovider", parent = EncounterResource1_9.class, supportedClass = EncounterProvider.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class EncounterProviderResource1_9 extends DelegatingSubResource<EncounterProvider, Encounter, EncounterResource1_9> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("provider", Representation.REF);
            description.addProperty("encounterRole", Representation.REF);
            description.addProperty("voided");
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        }
        if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("provider", Representation.DEFAULT);
            description.addProperty("encounterRole", Representation.DEFAULT);
            description.addProperty("voided");
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("provider");
        description.addProperty("encounter");
        description.addProperty("encounterRole");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("encounterRole");
        description.addProperty("voided");
        description.addProperty("voidReason");
        return description;
    }

    @Override
    public Encounter getParent(EncounterProvider instance) {
        return instance.getEncounter();
    }

    @Override
    public void setParent(EncounterProvider instance, Encounter parent) {
        instance.setEncounter(parent);
    }

    @Override
    public PageableResult doGetAll(Encounter parent, RequestContext context) throws ResponseException {
        List<EncounterProvider> encounterProviders = new ArrayList<EncounterProvider>(parent.getEncounterProviders());
        return new NeedsPaging<EncounterProvider>(encounterProviders, context);
    }

    @Override
    public EncounterProvider getByUniqueId(String uniqueId) {
        return Context.getService(RestHelperService.class).getObjectByUuid(EncounterProvider.class, uniqueId);
    }

    @Override
    protected void delete(EncounterProvider delegate, String reason, RequestContext context) throws ResponseException {
        delegate.getEncounter().removeProvider(delegate.getEncounterRole(), delegate.getProvider());
    }

    @Override
    public EncounterProvider newDelegate() {
        return new EncounterProvider();
    }

    @Override
    public EncounterProvider save(EncounterProvider delegate) {
        delegate.getEncounter().getEncounterProviders().add(delegate);
        return delegate;
    }

    @Override
    public void purge(EncounterProvider delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Display string for EncounterProvider
     *
     * @param encounterProvider
     * @return String uuid
     */
    @PropertyGetter("display")
    public String getDisplayString(EncounterProvider encounterProvider) {
        if (encounterProvider == null){
            return "";
        }

        Provider provider = encounterProvider.getProvider();
        EncounterRole rolePlayed = encounterProvider.getEncounterRole();

        if(rolePlayed == null ) {
            if(provider == null) {
                return null;
            }
            else {
                return provider.getName();
            }
        }
        return provider.getName() + ": " + rolePlayed.getName();
    }

    @Override
    public String getResourceVersion() {
        return RestConstants1_9.RESOURCE_VERSION;
    }
}
