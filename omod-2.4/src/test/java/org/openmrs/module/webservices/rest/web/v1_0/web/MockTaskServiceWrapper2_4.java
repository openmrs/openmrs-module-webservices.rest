/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.web;

import org.openmrs.module.webservices.helper.TaskServiceWrapper2_4;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;

import java.util.ArrayList;
import java.util.List;

public class MockTaskServiceWrapper2_4 extends TaskServiceWrapper2_4 {

    public List<TaskDefinition> registeredTasks = new ArrayList<TaskDefinition>();

    public List<TaskDefinition> scheduledTasks = new ArrayList<TaskDefinition>();

    /**
     * It returns a task based on id provided
     *
     * @param id - used to find the task definition from the registered tasks
     * @return - return the task definition if it is found, else return  null
     */
    @Override
    public TaskDefinition getTaskById(Integer id) {
        TaskDefinition taskFound = null;
        for (TaskDefinition taskDef : registeredTasks) {
            if (id == taskDef.getId()) {
                taskFound = taskDef;
                break;
            }
        }
        return taskFound;
    }

    /**
     * It returns a task based on uuid provided
     *
     * @param uuid - used to find the task definition from the registered tasks
     * @return - return the task definition if it is found, else return  null
     */

    @Override
    public TaskDefinition getTaskByUuid(String uuid) {
        TaskDefinition taskFound = null;
        for (TaskDefinition taskDef : registeredTasks) {
            if (uuid == taskDef.getUuid()) {
                taskFound = taskDef;
                break;
            }
        }
        return taskFound;
    }

    /**
     * It returns a task based on name provided
     *
     * @param taskName - used to find the task definition from the registered tasks
     * @return - return the task definition if it is found, else return  null
     */
    @Override
    public TaskDefinition getTaskByName(String taskName) {
        TaskDefinition taskFound = null;
        for (TaskDefinition taskDef : registeredTasks) {
            if (taskName.equals(taskDef.getName())) {
                taskFound = taskDef;
                break;
            }
        }
        return taskFound;
    }

    @Override
    public List<TaskDefinition> getRegisteredTasks() {
        return registeredTasks;
    }

    @Override
    public List<TaskDefinition> getScheduledTasks() {
        return scheduledTasks;
    }

    /**
     * Mock Function : It will add the task definition to the registered tasks list
     *
     * @param task will contain the task definition to be saved
     */
    @Override
    public void saveTaskDefinition(TaskDefinition task) {
        if (!registeredTasks.contains(task)) {
            registeredTasks.add(task);
        }
    }

    /**
     * Mock Function : It will remove the task definition from the registered tasks list and scheduled
     * list
     *
     * @param task will contain the task definition to be deleted
     * @throws SchedulerException
     */
    @Override
    public void deleteTask(TaskDefinition task) throws SchedulerException {
        if (scheduledTasks.contains(task)) {
            scheduledTasks.remove(task);
        }
    }

    /**
     * Mock Function : It will add the task definition to the  scheduled tasks List
     *
     * @param task contains the task definition to be scheduled
     * @throws SchedulerException
     */
    @Override
    public void scheduleTask(TaskDefinition task) throws SchedulerException {
        if (!registeredTasks.contains(task)) {
            scheduledTasks.add(task);
        }
    }

    /**
     * Mock Function : It will remove the task definition from the scheduled tasks List
     *
     * @param task contains the task definition to be shutdown
     * @throws SchedulerException
     */
    @Override
    public void shutDownTask(TaskDefinition task) throws SchedulerException {
        if (scheduledTasks.contains(task)) {
            scheduledTasks.remove(task);
        }

    }

    /**
     * Mock Function : it will remove the task definition from the scheduled tasks and add it again
     *
     * @param task contains the task definition to be re-scheduled
     * @throws SchedulerException
     */
    @Override
    public void reScheduleTask(TaskDefinition task) throws SchedulerException {
        if (scheduledTasks.contains(task)) {
            scheduledTasks.remove(task);
        }
        scheduledTasks.add(task);
    }

}
