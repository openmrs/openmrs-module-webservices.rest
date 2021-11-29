/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web;

import org.openmrs.module.webservices.helper.TaskServiceWrapper2_4;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The Mock TaskServiceWrapper2_4.
 */
public class MockTaskServiceWrapper2_4 extends TaskServiceWrapper2_4 {
	
	private static final Logger log = LoggerFactory.getLogger(MockTaskServiceWrapper2_4.class);

	/**
	 * The Registered tasks.
	 */
	public List<TaskDefinition> registeredTasks = new ArrayList<>();

	/**
	 * The Scheduled tasks.
	 */
	public List<TaskDefinition> scheduledTasks = new ArrayList<>();

	/**
	 * Gets task by uuid.
	 * @param uuid the uuid
	 * @return the task by uuid
	 */
	@Override
	public TaskDefinition getTaskByUuid(String uuid) {
		TaskDefinition taskDefinition = null;
		for (TaskDefinition taskDef : registeredTasks) {
			if (uuid.equals(taskDef.getUuid())) {
				taskDefinition = taskDef;
				break;
			}
		}
		return taskDefinition;
	}

	/**
	 * Gets task by id
	 * @param id the id
	 * @return task
	 */
	@Override
	public TaskDefinition getTaskById(Integer id) {
		TaskDefinition taskDefinition = null;
		for (TaskDefinition taskDef : registeredTasks) {
			if (id.equals(taskDef.getId())) {
				taskDefinition = taskDef;
				break;
			}
		}
		return taskDefinition;
	}

	/**
	 * Get the task by name
	 * @param taskName the taskName
	 * @return the task
	 */
	@Override
	public TaskDefinition getTaskByName(String taskName) {
		TaskDefinition taskDefinition = null;
		for (TaskDefinition taskDef : registeredTasks) {
			if (taskName.equals(taskDef.getName())) {
				taskDefinition = taskDef;
				break;
			}
		}
		return taskDefinition;
	}

	/**
	 * Get scheduled tasks
	 * @return collection of scheduled tasks
	 */
	@Override
	public Collection<TaskDefinition> getScheduledTasks() {
		return scheduledTasks;
	}

	/**
	 * Get registered tasks
	 * @return collection of registered tasks
	 */
	@Override
	public Collection<TaskDefinition> getRegisteredTasks() {
		return registeredTasks;
	}

	/**
	 * Save the new task in the service
	 * @param task will contain the taskDefinition to be saved
	 */
	@Override
	public void saveTaskDefinition(TaskDefinition task) {
		if (!registeredTasks.contains(task)) {
			registeredTasks.add(task);
		} 
	}

	/**
	 * It will delete the task from the service
	 * @param task will contain the taskDefinition to be deleted
	 * @throws SchedulerException - thrown in case of any SchedulerService exceptions
	 */
	@Override
	public void deleteTask(TaskDefinition task) throws SchedulerException {
		if (registeredTasks.contains(task)) {
			registeredTasks.remove(task);
		} 
		else if (scheduledTasks.contains(task)) {
			scheduledTasks.remove(task);
		} else {
			throw new SchedulerException("Could not delete task");
		}
	}

	/**
	 * It will schedule a task which is registered in the service
	 * @param task contains the taskDefinition to be scheduled
	 * @throws SchedulerException - thrown in case of any SchedulerService exceptions
	 */
	@Override
	public void scheduleTask(TaskDefinition task) throws SchedulerException {
		if (!scheduledTasks.contains(task)) {
			scheduledTasks.add(task);
		}
		else if (scheduledTasks.contains(task)) {
			log.info("Nothing to do, the task is already scheduled");
		}
		else {
			throw new SchedulerException("Could not schedule task");
		}
	}

	/**
	 * It will shut down a task which is scheduled in the service
	 * @param task contains the taskDefinition to be shutdown
	 * @throws SchedulerException - thrown in case of any SchedulerService exceptions
	 */
	@Override
	public void shutDownTask(TaskDefinition task) throws SchedulerException {
		if (scheduledTasks.contains(task)) {
			scheduledTasks.remove(task);
		}
		else if (!scheduledTasks.contains(task)) {
			log.info("Nothing to do, the task is not scheduled");
		}
		else {
			throw new SchedulerException("Could not shut down task");
		}
	}

	/**
	 * It will re-schedule a task which is registered in the service
	 * @param task contains the taskDefinition to be re-scheduled
	 * @throws SchedulerException - thrown in case of any SchedulerService exceptions
	 */
	@Override
	public void reScheduleTask(TaskDefinition task) throws SchedulerException {
		if (scheduledTasks.contains(task)) {
			scheduledTasks.remove(task);
		} else {
			throw new SchedulerException("Could not reschedule task");
		}
		scheduledTasks.add(task);
	}

	/**
	 * It will re-schedule all the tasks which are registered in the service
	 * @throws SchedulerException - thrown in case of any SchedulerService exceptions
	 */
	@Override
	public void reScheduleAllTasks() throws SchedulerException {
		super.reScheduleAllTasks();
	}

	/**
	 * It will run the task from the service
	 *
	 * @param task will contain the taskDefinition to be run
	 * @throws SchedulerException - thrown in case of any SchedulerService exceptions
	 */
	@Override
	public void runTask(TaskDefinition task) throws SchedulerException {
		super.runTask(task);
	}
}
