/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_4;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.helper.TaskAction;
import org.openmrs.module.webservices.rest.web.MockTaskServiceWrapper2_4;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.TaskActionResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_4.TaskDefinitionResource2_4;
import org.openmrs.scheduler.Task;
import org.openmrs.scheduler.TaskDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

public class TaskActionController2_4Test extends MainResourceControllerTest {

	@Autowired
	RestService restService;

	static int count = 1;

	public static final String POST_TASK_DEFINITION_UUID = "9a34d170-5f63-4f36-996c-24674107ae73";

	private final IdentifiedTask IDENTIFIED_TASK = new IdentifiedTask();

	private final TaskDefinition TASK_ONE = IDENTIFIED_TASK.getTaskDefinition();

	private final TaskDefinition TASK_TWO = new TaskDefinition(34, "TaskTwo", "TaskTwoDescription",
			"org.openmrs.scheduler.tasks.TestTask");
	
	private final MockTaskServiceWrapper2_4 MOCK_TASK_SERVICE_WRAPPER = new MockTaskServiceWrapper2_4();

	/**
	 * @return the URI of the resource
	 */
	@Override
	public String getURI() {
		return "taskaction";
	}
	
	/**
	 * @return the uuid of an existing object
	 */
	@Override
	public String getUuid() {
		return null;
	}
	
	/**
	 * @return the count of all not retired/voided objects
	 */
	@Override
	public long getAllCount() {
		return 0;
	}
	
	@Before
	public void setUp() throws Exception {
		MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks().addAll(Arrays.asList(TASK_ONE, TASK_TWO));
		TaskActionResource1_8 taskActionResource = (TaskActionResource1_8) restService
				.getResourceBySupportedClass(TaskAction.class);
		taskActionResource.setTaskServiceWrapper(MOCK_TASK_SERVICE_WRAPPER);
		TaskDefinitionResource2_4 taskResource = (TaskDefinitionResource2_4) restService
				.getResourceBySupportedClass(TaskDefinition.class);
		taskResource.setTaskServiceWrapper(MOCK_TASK_SERVICE_WRAPPER);
	}

	@Test
	public void shouldScheduleTask() throws Exception {
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks(), not(hasItem(TASK_ONE)));
		deserialize(handle(newPostRequest(getURI(),
				"{\"action\": \"scheduletask\", \"tasks\":[\"" + POST_TASK_DEFINITION_UUID + "\"]}")));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks(), hasItem(TASK_ONE));
	}

	@Test
	public void scheduleTask_shouldDoNothingIfTaskIsAlreadyScheduled() throws Exception {
		MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks().add(TASK_ONE);
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks(), hasItem(TASK_ONE));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks(), hasItem(TASK_ONE));
		deserialize(handle(newPostRequest(getURI(),
				"{\"action\": \"scheduletask\", \"tasks\":[\"" + POST_TASK_DEFINITION_UUID + "\"]}")));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks(), hasItem(TASK_ONE));
	}

	@Test
	public void shouldShutdownTask() throws Exception {
		MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks().add(TASK_ONE);
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks(), hasItem(TASK_ONE));
		deserialize(handle(newPostRequest(getURI(),
				"{\"action\": \"shutdowntask\", \"tasks\":[\"" + POST_TASK_DEFINITION_UUID + "\"]}")));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks(), not(hasItem(TASK_ONE)));
	}

	@Test
	public void shutdownTask_shouldDoNothingIfTaskAlreadyShutdown() throws Exception {
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks(), not(hasItem(TASK_ONE)));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks(), hasItem(TASK_ONE));
		deserialize(handle(newPostRequest(getURI(),
				"{\"action\": \"shutdowntask\", \"tasks\":[\"" + POST_TASK_DEFINITION_UUID + "\"]}")));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks(), not(hasItem(TASK_ONE)));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks(), hasItem(TASK_ONE));
	}
	
	@Test
	public void shouldRunTask() throws Exception {
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks(), hasItem(TASK_ONE));
		assertEquals(2, MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks().size());
		int countBefore = count;
		deserialize(handle(newPostRequest(getURI(),
				"{\"action\": \"runtask\", \"tasks\":[\"" + POST_TASK_DEFINITION_UUID + "\"]}")));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks(), hasItem(TASK_ONE));
		assertEquals(2, MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks().size());
		int countAfter = count;
		assertEquals(++countBefore, countAfter);
	}

	@Test
	public void shouldRescheduleTask() throws Exception {
		MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks().add(TASK_ONE);
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getScheduledTasks(), hasItem(TASK_ONE));
		deserialize(handle(newPostRequest(getURI(),
				"{\"action\": \"rescheduletask\", \"tasks\":[\"" + POST_TASK_DEFINITION_UUID + "\"]}")));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks(), hasItem(TASK_ONE));
	}

	@Test
	public void shouldDeleteTask() throws Exception {
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks(), hasItem(TASK_ONE));
		deserialize(handle(newPostRequest(getURI(),
				"{\"action\": \"delete\", \"tasks\":[\"" + POST_TASK_DEFINITION_UUID + "\"]}")));
		assertThat(MOCK_TASK_SERVICE_WRAPPER.getRegisteredTasks(), not(hasItem(TASK_ONE)));
	}

	@Override
	@Test(expected = Exception.class)
	public void shouldGetDefaultByUuid() throws Exception {
		super.shouldGetDefaultByUuid();
	}

	@Override
	@Test(expected = Exception.class)
	public void shouldGetRefByUuid() throws Exception {
		super.shouldGetRefByUuid();
	}

	@Override
	@Test(expected = Exception.class)
	public void shouldGetFullByUuid() throws Exception {
		super.shouldGetFullByUuid();
	}

	@Override
	@Test(expected = Exception.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}

	public static class IdentifiedTask implements Task {
		@Override
		public void execute() {
			count = count + 1;
		}

		@Override
		public void initialize(TaskDefinition taskDefinition) {}
		
		public TaskDefinition getTaskDefinition() {
			TaskDefinition taskDefinition = new TaskDefinition();
			taskDefinition.setId(31);
			taskDefinition.setUuid(POST_TASK_DEFINITION_UUID);
			taskDefinition.setName("Identified Task");
			taskDefinition.setTaskClass(IdentifiedTask.class.getName());
			taskDefinition.setStartOnStartup(false);
			taskDefinition.setStartTime(new Date());
			return taskDefinition;
		}

		@Override
		public boolean isExecuting() {
			return false;
		}

		@Override
		public void shutdown() {}
	}
}
