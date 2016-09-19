package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;


import org.openmrs.Program;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ProgramResource1_8;

@Resource(name = RestConstants.VERSION_1 + "/program", supportedClass = Program.class, supportedOpenmrsVersions = {"1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class ProgramResource1_10 extends ProgramResource1_8{

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("name");
            description.addProperty("uuid");
            description.addProperty("retired");
            description.addProperty("description");
            description.addProperty("concept", Representation.REF);
            description.addProperty("allWorkflows", Representation.DEFAULT);
            description.addProperty("outcomesConcept", Representation.FULL);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("retired");
            description.addProperty("concept");
            description.addProperty("outcomesConcept");
            description.addProperty("allWorkflows", Representation.FULL);
            description.addSelfLink();
            description.addProperty("auditInfo");
            return description;
        } else if(rep instanceof RefRepresentation){
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("allWorkflows", Representation.REF);
            description.addSelfLink();
            return description;
        }
        return null;
    }


    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("name");
        description.addRequiredProperty("description");
        description.addRequiredProperty("concept");

        description.addProperty("outcomesConcept");
        description.addProperty("retired");
        return description;
    }

}
