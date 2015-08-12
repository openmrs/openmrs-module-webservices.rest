package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;


import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ProgramEnrollmentResource1_8;
import org.openmrs.util.OpenmrsUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
            description.addProperty("states", Representation.REF);
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
            description.addProperty("states", Representation.DEFAULT);
            description.addSelfLink();
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription d = new DelegatingResourceDescription();
        d.addRequiredProperty("patient");
        d.addRequiredProperty("program");
        d.addRequiredProperty("dateEnrolled");
        d.addProperty("dateCompleted");
        d.addProperty("states");
        d.addProperty("outcome");
        d.addProperty("location");
        d.addProperty("voided");
        return d;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        DelegatingResourceDescription d = new DelegatingResourceDescription();
        d.addRequiredProperty("dateEnrolled");
        d.addProperty("states");
        d.addProperty("outcome");
        d.addProperty("location");
        d.addProperty("voided");
        d.addProperty("dateCompleted");
        return d;
    }

    /**
     * Sets states to PatientProgram.
     *
     * @param instance
     * @param states
     */
    @PropertySetter("states")
    public static void setStates(PatientProgram instance, List<PatientState> patientStates) {
        sortPatientStatesBasedOnStartDate(patientStates);
        if(!patientStates.isEmpty()) {
            for(PatientState patientState: patientStates) {
                PatientState existingState = getMatchingPatientState(patientState, instance.getStates());
                if(existingState == null) {
                    Date onDate = patientState.getStartDate() == null ? new Date() : patientState.getStartDate();
                    instance.transitionToState(Context.getProgramWorkflowService().getStateByUuid(patientState.getState().getUuid()), onDate);
                }else{
                    existingState.setStartDate(patientState.getStartDate());
                    existingState.setEndDate(patientState.getEndDate());
                    existingState.setVoided(patientState.getVoided());
                }
            }
        }
    }

    private static void sortPatientStatesBasedOnStartDate(List<PatientState> patientStates) {
        Collections.sort(patientStates, new Comparator<PatientState>() {
            @Override
            public int compare(PatientState o1, PatientState o2) {
                return OpenmrsUtil.compareWithNullAsLatest(o1.getStartDate(), o2.getEndDate());
            }
        });
    }

    private static PatientState getMatchingPatientState(PatientState patientState, Set<PatientState> patientStates) {
        for (PatientState existingState : patientStates) {
            if (existingState.equals(patientState)) return existingState;
        }
        return null;
    }

}
