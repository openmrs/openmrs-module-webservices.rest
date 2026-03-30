/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.webservices.helper;

import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.Task;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsClassLoader;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Wrapping the required task information between related resource and SchedulerService
 */
public class TaskServiceWrapper {
	
	public TaskDefinition getTaskById(Integer id) {
		return Context.getSchedulerService().getTask(id);
	}
	
	public TaskDefinition getTaskByName(String taskName) {
		return Context.getSchedulerService().getTaskByName(taskName);
	}
	
	public Collection<TaskDefinition> getScheduledTasks() {
		return Context.getSchedulerService().getScheduledTasks();
	}
	
	public Collection<TaskDefinition> getRegisteredTasks() {
		return Context.getSchedulerService().getRegisteredTasks();
	}
	
	/**
	 * Save the new task in the service
	 * 
	 * @param task will contain the taskDefinition to be saved
	 */
	public void saveTaskDefinition(TaskDefinition task) {
		Context.getSchedulerService().saveTaskDefinition(task);
	}
	
	/**
	 * It will delete the task from the service
	 * 
	 * @param task will contain the taskDefinition to be deleted
	 * @throws SchedulerException - It will throw in case of any SchedulerService exceptions
	 */
	public void deleteTask(TaskDefinition task) throws SchedulerException {
		Context.getSchedulerService().deleteTask(task.getId());
	}
	
	/**
	 * It will schedule a task which is registered in the service
	 * 
	 * @param task contains the taskDefinition to be scheduled
	 * @throws SchedulerException - It will throw in case of any SchedulerService exceptions
	 */
	public void scheduleTask(TaskDefinition task) throws SchedulerException {
		Context.getSchedulerService().scheduleTask(task);
		
	}
	
	/**
	 * It will shutdown a task which is scheduled in the service
	 * 
	 * @param task contains the taskDefinition to be shutdown
	 * @throws SchedulerException - It will throw in case of any SchedulerService exceptions
	 */
	public void shutDownTask(TaskDefinition task) throws SchedulerException {
		Context.getSchedulerService().shutdownTask(task);
	}
	
	/**
	 * It will re-schedule a task which is registered in the service
	 * 
	 * @param task contains the taskDefinition to be re-scheduled
	 * @throws SchedulerException - It will throw in case of any SchedulerService exceptions
	 */
	public void reScheduleTask(TaskDefinition task) throws SchedulerException {
		Context.getSchedulerService().rescheduleTask(task);
	}
	
	/**
	 * It will re-schedule all the tasks which are registered in the service
	 * 
	 * @throws SchedulerException - It will throw in case of any SchedulerService exceptions
	 */
	public void reScheduleAllTasks() throws SchedulerException {
		Context.getSchedulerService().rescheduleAllTasks();
	}
	
	/**
	 * It will run the task from the service
	 * 
	 * @param task will contain the taskDefinition to be run
	 * @throws SchedulerException - It will throw in case of any SchedulerService exceptions
	 */
	public void runTask(TaskDefinition taskDefinition) throws SchedulerException {
		try {
			Class<?> taskClass = OpenmrsClassLoader.getInstance().loadClass(taskDefinition.getTaskClass());
			Object instance = taskClass.getDeclaredConstructor().newInstance();
			if (!(instance instanceof Task)) {
				throw new SchedulerException("Task class " + taskDefinition.getTaskClass() + " must implement Task");
			}
			Task task = (Task) instance;
			task.initialize(taskDefinition);
			task.execute();
		}
		catch (ClassNotFoundException e) {
			throw new SchedulerException("Task class " + taskDefinition.getTaskClass() + " not found", e);
		}
		catch (ReflectiveOperationException e) {
			throw new SchedulerException("Unable to instantiate task class " + taskDefinition.getTaskClass(), e);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SchedulerException("Task execution interrupted", e);
		}
		catch (ExecutionException e) {
			throw new SchedulerException("Task execution failed", e);
		}
	}
}
