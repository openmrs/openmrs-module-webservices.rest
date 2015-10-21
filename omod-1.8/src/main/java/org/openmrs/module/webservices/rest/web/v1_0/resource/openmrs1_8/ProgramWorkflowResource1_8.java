package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/workflow", supportedClass = ProgramWorkflow.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"} , order = 1)
public class ProgramWorkflowResource1_8 extends MetadataDelegatingCrudResource<ProgramWorkflow> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("concept",Representation.DEFAULT);
            description.addProperty("description");
            description.addProperty("retired");
            description.addProperty("states",Representation.DEFAULT);
            description.addProperty("concept", Representation.REF);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("concept",Representation.FULL);
            description.addProperty("description");
            description.addProperty("retired");
            description.addProperty("states",Representation.FULL);
            description.addProperty("concept");
            description.addSelfLink();
            return description;
        } else if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("concept",Representation.REF);
            description.addProperty("retired");
            description.addProperty("states",Representation.REF);
            description.addSelfLink();
            return description;
        }
        return null;
    }


    @Override
    public ProgramWorkflow getByUniqueId(String uniqueId) {
        return Context.getProgramWorkflowService().getWorkflowByUuid(uniqueId);
    }

    @Override
    public ProgramWorkflow newDelegate() {
        return new ProgramWorkflow();
    }

    @Override
    public ProgramWorkflow save(ProgramWorkflow delegate) {
        Program parent = delegate.getProgram();
        parent.addWorkflow(delegate);
        Context.getProgramWorkflowService().saveProgram(parent);
        return delegate;
    }

    @Override
    public void purge(ProgramWorkflow delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }
}

