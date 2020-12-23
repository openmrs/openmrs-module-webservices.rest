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

import org.openmrs.api.APIException;
import org.openmrs.module.webservices.helper.TaskAction;
import org.openmrs.module.webservices.helper.TaskServiceWrapper2_4;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.Creatable;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.TaskActionResource1_8;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Collection;


@Resource(name = RestConstants.VERSION_1 + "/taskaction", order = 2, supportedClass = TaskAction.class, supportedOpenmrsVersions = {
        "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*" })
public class TaskActionResource2_4 extends TaskActionResource1_8 implements Creatable {

    private TaskServiceWrapper2_4 taskServiceWrapper = new TaskServiceWrapper2_4();

    public void setTaskServiceWrapper(TaskServiceWrapper2_4 taskServiceWrapper) {
        this.taskServiceWrapper = taskServiceWrapper;
    }

    private void scheduleTasks(Collection<TaskDefinition> taskDefs) {
        for (TaskDefinition taskDef : taskDefs) {
            try {
                taskServiceWrapper.scheduleTask(taskDef);
            }
            catch (SchedulerException e) {
                throw new APIException("Errors occurred while scheduling task", e);
            }
        }
    }

    private void shutDownTasks(Collection<TaskDefinition> taskDefs) {
        for (TaskDefinition taskDef : taskDefs) {
            try {
                taskServiceWrapper.shutDownTask(taskDef);
            }
            catch (SchedulerException e) {
                throw new APIException("Errors occurred while shutdowning task", e);
            }
        }
    }

    // Stop and start a set of scheduled tasks.
    private void reScheduleTasks(Collection<TaskDefinition> taskDefs) {
        for (TaskDefinition taskDef : taskDefs) {
            try {
                taskServiceWrapper.reScheduleTask(taskDef);
            }
            catch (SchedulerException e) {
                throw new APIException("Errors occurred while rescheduling task", e);
            }
        }
    }

    // Stop and start all the tasks.
    private void reScheduleAllTasks() {
        try {
            taskServiceWrapper.reScheduleAllTasks();
        }
        catch (SchedulerException e) {
            throw new APIException("Errors occurred while rescheduling all tasks", e);
        }

    }

    private void deleteTasks(Collection<TaskDefinition> taskDefs) {
        for (TaskDefinition taskDef : taskDefs) {
            try {
                taskServiceWrapper.deleteTask(taskDef);
            }
            catch (SchedulerException e) {
                throw new APIException("Errors occurred while deleting task", e);
            }
        }
    }

    private void runTasks(Collection<TaskDefinition> taskDefs) {
        for (TaskDefinition taskDef : taskDefs) {
            try {
                taskServiceWrapper.runTask(taskDef);
            }
            catch (SchedulerException e) {
                throw new APIException("Errors occurred while running task", e);
            }
        }
    }

}
