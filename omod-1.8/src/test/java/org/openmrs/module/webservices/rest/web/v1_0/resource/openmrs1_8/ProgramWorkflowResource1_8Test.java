package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;


public class ProgramWorkflowResource1_8Test extends BaseDelegatingResourceTest<ProgramWorkflowResource1_8, ProgramWorkflow> {

    @Override
    public ProgramWorkflow newObject() {
        return Context.getProgramWorkflowService().getWorkflowByUuid(getUuidProperty());
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("retired", getObject().getRetired());
        assertPropPresent("concept");
    }

    @Override
    public void validateRefRepresentation() throws Exception {
        super.validateRefRepresentation();
        assertPropEquals("retired", getObject().getRetired());
        assertPropPresent("concept");
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("retired", getObject().getRetired());
        assertPropPresent("concept");
    }

    @Override
    public String getDisplayProperty() {
        return getObject().getName();
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_8.WORKFLOW_UUID;
    }
}