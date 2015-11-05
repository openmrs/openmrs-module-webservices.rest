/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;


@Resource(name = RestConstants.VERSION_1 + "/program", supportedClass = Program.class, supportedOpenmrsVersions = {"1.8.*, 1.9.*"} , order = 1)
public class ProgramResource1_8 extends MetadataDelegatingCrudResource<Program> {

    @Override
    public Program getByUniqueId(String uniqueId) {
        Program programByUuid = Context.getProgramWorkflowService().getProgramByUuid(uniqueId);
        //We assume the caller was fetching by name
        if(programByUuid == null) {
            programByUuid = Context.getProgramWorkflowService().getProgramByName(uniqueId);
        }
        return programByUuid;
    }

    @Override
    public Program newDelegate() {
        return new Program();
    }

    @Override
    public Program save(Program program) {
        return Context.getProgramWorkflowService().saveProgram(program);
    }

    @Override
    public void purge(Program program, RequestContext context) throws ResponseException {
        Context.getProgramWorkflowService().purgeProgram(program);
    }

    @Override
    protected NeedsPaging<Program> doGetAll(RequestContext context) {
        return new NeedsPaging<Program>(Context.getProgramWorkflowService().getAllPrograms(), context);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("retired");
            description.addProperty("allWorkflows", Representation.DEFAULT);
            description.addProperty("concept", Representation.REF);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("retired");
            description.addProperty("allWorkflows", Representation.FULL);
            description.addProperty("concept");
            description.addSelfLink();
            description.addProperty("auditInfo");
            return description;
        } else if(rep instanceof RefRepresentation){
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("retired");
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

        description.addProperty("retired");
        return description;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        String query = context.getParameter("q");

        if (query != null) {
            List<Program> programs = Context.getProgramWorkflowService().getPrograms(query);
            return new NeedsPaging<Program>(programs, context);
        }
        return new EmptySearchResult();
    }

}
