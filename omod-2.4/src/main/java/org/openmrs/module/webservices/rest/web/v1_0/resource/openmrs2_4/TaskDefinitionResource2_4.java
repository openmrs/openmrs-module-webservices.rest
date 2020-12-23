/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_4;

import org.openmrs.module.webservices.helper.TaskServiceWrapper2_4;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.TaskDefinitionResource1_8;
import org.openmrs.scheduler.TaskDefinition;

import java.util.ArrayList;
import java.util.List;

@Resource(name = RestConstants.VERSION_1
        + "/taskdefinition", order = 2, supportedClass = TaskDefinition.class, supportedOpenmrsVersions = { "2.4.*" })
public class TaskDefinitionResource2_4 extends TaskDefinitionResource1_8 {

    private TaskServiceWrapper2_4 taskServiceWrapper = new TaskServiceWrapper2_4();

    public void setTaskServiceWrapper(TaskServiceWrapper2_4 taskServiceWrapper) {
        this.taskServiceWrapper = taskServiceWrapper;
    }

    @PropertyGetter("uuid")
    public static String getUuid(TaskDefinition instance) {
        return instance.getUuid();
    }

    @PropertyGetter("display")
    public static String getDisplay(TaskDefinition instance) {
        return instance.getName();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
     *      It will return only the scheduled tasks, if query contains string as scheduled otherwise
     *      it will return the registered tasks
     */
    @Override
    protected PageableResult doSearch(RequestContext context) {
        String query = context.getParameter("q");
        List<TaskDefinition> taskDefinitions = null;
        if (query == "registered") {
            taskDefinitions = (ArrayList<TaskDefinition>) taskServiceWrapper.getRegisteredTasks();
        } else {
            taskDefinitions = (ArrayList<TaskDefinition>) taskServiceWrapper.getScheduledTasks();
        }
        return new NeedsPaging<TaskDefinition>(taskDefinitions, context);
    }
}
