package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;


import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ProgramEnrollmentResource1_8;


@Resource(name = RestConstants.VERSION_1 + "/programenrollment", supportedClass = PatientProgram.class, supportedOpenmrsVersions = {"1.10.*", "1.11.*", "1.12.*"})
public class ProgramEnrollmentResource1_10 extends ProgramEnrollmentResource1_8{

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("patient", Representation.REF);
            description.addProperty("program", Representation.REF);
            description.addProperty("display");
            description.addProperty("dateEnrolled");
            description.addProperty("dateCompleted");
            description.addProperty("location", Representation.REF);
            description.addProperty("voided");
            description.addProperty("outcome", Representation.REF);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("patient");
            description.addProperty("program");
            description.addProperty("display");
            description.addProperty("dateEnrolled");
            description.addProperty("dateCompleted");
            description.addProperty("location");
            description.addProperty("voided");
            description.addProperty("outcome");
            description.addSelfLink();
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            return description;
        } else {
            return null;
        }
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription d = new DelegatingResourceDescription();
        d.addRequiredProperty("patient");
        d.addRequiredProperty("program");
        d.addRequiredProperty("dateEnrolled");

        d.addProperty("dateCompleted");
        d.addProperty("outcome");
        d.addProperty("location");
        d.addProperty("voided");
        return d;
    }
}
