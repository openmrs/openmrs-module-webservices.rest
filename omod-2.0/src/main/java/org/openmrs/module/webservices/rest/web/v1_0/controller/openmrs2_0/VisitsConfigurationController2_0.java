/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.GlobalProperty;
import org.openmrs.VisitType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.handler.EncounterVisitHandler;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.GenericRestException;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.VisitsConfiguration;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/visitsconfiguration")
public class VisitsConfigurationController2_0 extends BaseRestController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Object getCurrentConfiguration() {
		Context.requirePrivilege(PrivilegeConstants.CONFIGURE_VISITS);
		AdministrationService administrationService = Context.getAdministrationService();
		EncounterService encounterService = Context.getEncounterService();
		VisitService visitService = Context.getVisitService();
		SchedulerService schedulerService = Context.getSchedulerService();

		VisitsConfiguration visitsConfiguration = new VisitsConfiguration();
		visitsConfiguration.setEnableVisits(getEnableVisitsValue(administrationService));
		visitsConfiguration.setVisitEncounterHandler(getVisitEncounterHandlerValue(administrationService, encounterService));
		visitsConfiguration.setAutoCloseVisitsTaskStarted(getAutoCloseVisitsTaskStartedValue(schedulerService));
		visitsConfiguration.setVisitTypesToAutoClose(getVisitTypesToAutoCloseValue(administrationService, visitService));

		return ConversionUtil.convertToRepresentation(visitsConfiguration, Representation.FULL);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void updateCurrentConfiguration(@RequestBody VisitsConfiguration newConfiguration) {
		Context.requirePrivilege(PrivilegeConstants.CONFIGURE_VISITS);
		AdministrationService administrationService = Context.getAdministrationService();
		VisitService visitService = Context.getVisitService();
		SchedulerService schedulerService = Context.getSchedulerService();

		// validate
		if (newConfiguration.getEnableVisits() && StringUtils.isEmpty(newConfiguration.getVisitEncounterHandler())) {
			throw new IllegalRequestException("Visit encounter handler cannot be empty");
		}

		updateEnableVisitsValue(administrationService, newConfiguration.getEnableVisits());

		if (newConfiguration.getEnableVisits()) {
			updateVisitEncounterHandlerValue(administrationService,
					newConfiguration.getVisitEncounterHandler());
		}
		updateGetAutoCloseVisitsTaskStartedValue(schedulerService, newConfiguration.getAutoCloseVisitsTaskStarted());
		updateVisitTypesToAutoCloseValue(administrationService, visitService, newConfiguration.getVisitTypesToAutoClose());
	}

	private Boolean getEnableVisitsValue(AdministrationService administrationService) {
		String enableVisits = administrationService
				.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ENABLE_VISITS, Boolean.FALSE.toString());
		return Boolean.parseBoolean(enableVisits);
	}

	private void updateEnableVisitsValue(AdministrationService administrationService, Boolean value) {
		GlobalProperty enableVisitsGp = new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ENABLE_VISITS,
				Boolean.toString(value));
		administrationService.saveGlobalProperty(enableVisitsGp);
	}

	private String getVisitEncounterHandlerValue(AdministrationService administrationService,
			EncounterService encounterService) {
		String visitEncounterHandler = administrationService.getGlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER);
		for (EncounterVisitHandler visitHandler : getEncounterVisitHandlers(encounterService)) {
			if (visitHandler.getClass().getName().equals(visitEncounterHandler)) {
				return visitHandler.getClass().getName();
			}
		}
		return null;
	}

	private Collection<EncounterVisitHandler> getEncounterVisitHandlers(EncounterService encounterService) {
		return encounterService.getEncounterVisitHandlers();
	}

	private void updateVisitEncounterHandlerValue(AdministrationService administrationService, String value) {
		GlobalProperty visitEncounterHandlerGp = new GlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER, value);
		administrationService.saveGlobalProperty(visitEncounterHandlerGp);
	}

	private Boolean getAutoCloseVisitsTaskStartedValue(SchedulerService schedulerService) {
		TaskDefinition autoCloseVisitsTaskStarted = schedulerService
				.getTaskByName(OpenmrsConstants.AUTO_CLOSE_VISITS_TASK_NAME);

		if (autoCloseVisitsTaskStarted != null) {
			return autoCloseVisitsTaskStarted.getStarted();
		}

		return null;
	}

	private void updateGetAutoCloseVisitsTaskStartedValue(SchedulerService schedulerService,
			Boolean autoCloseVisitsTaskStarted) {
		TaskDefinition closeVisitsTask = schedulerService.getTaskByName(OpenmrsConstants.AUTO_CLOSE_VISITS_TASK_NAME);
		if (closeVisitsTask != null) {
			try {
				if (autoCloseVisitsTaskStarted && !closeVisitsTask.getStarted()) {
					schedulerService.scheduleTask(closeVisitsTask);
				} else if (!autoCloseVisitsTaskStarted && closeVisitsTask.getStarted()) {
					schedulerService.shutdownTask(closeVisitsTask);
				}
			}
			catch (SchedulerException e) {
				throw new GenericRestException(e);
			}
		}
	}

	private List<VisitType> getVisitTypesToAutoCloseValue(AdministrationService administrationService,
			VisitService visitService) {
		String gpValue = administrationService.getGlobalProperty(OpenmrsConstants.GP_VISIT_TYPES_TO_AUTO_CLOSE);
		if (StringUtils.isNotBlank(gpValue)) {
			List<VisitType> visitTypes = new ArrayList<>();
			String[] visitTypeNames = StringUtils.split(gpValue.trim(), ",");
			for (int i = 0; i < visitTypeNames.length; i++) {
				String currName = visitTypeNames[i];
				visitTypeNames[i] = currName.trim().toLowerCase();
			}

			List<VisitType> allVisitTypes = visitService.getAllVisitTypes();
			for (VisitType visitType : allVisitTypes) {
				if (ArrayUtils.contains(visitTypeNames, visitType.getName().toLowerCase())) {
					visitTypes.add(visitType);
				}
			}
			return visitTypes;
		}
		return Collections.emptyList();
	}

	private void updateVisitTypesToAutoCloseValue(AdministrationService administrationService, VisitService visitService,
			List<VisitType> visitTypesToAutoClose) {
		// visitTypesToAutoClose contains only uuids, map to full visit types
		List<String> visitTypesToAutoCloseUuids = visitTypesToAutoClose.stream().map(BaseOpenmrsObject::getUuid)
				.collect(Collectors.toList());
		List<VisitType> visitTypesToAutoCloseFull = getVisitTypesByUuids(visitTypesToAutoCloseUuids, visitService);

		String visitTypeNames = visitTypesToAutoCloseFull.stream().map(BaseOpenmrsMetadata::getName)
				.collect(Collectors.joining(","));

		GlobalProperty visitTypesToAutoCloseGp = new GlobalProperty(OpenmrsConstants.GP_VISIT_TYPES_TO_AUTO_CLOSE,
				visitTypeNames);
		administrationService.saveGlobalProperty(visitTypesToAutoCloseGp);
	}

	private List<VisitType> getVisitTypesByUuids(List<String> uuids, VisitService visitService) {
		return uuids.stream().map(visitService::getVisitTypeByUuid).collect(Collectors.toList());
	}
}
