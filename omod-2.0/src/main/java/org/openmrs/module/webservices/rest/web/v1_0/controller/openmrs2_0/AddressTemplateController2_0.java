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

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.layout.address.AddressTemplate;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.AddressTemplateXml;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/addresstemplate")
public class AddressTemplateController2_0 extends BaseRestController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody()
	public Object getCurrentConfiguration() {
		Context.requirePrivilege(PrivilegeConstants.MANAGE_ADDRESS_TEMPLATES);

		String addressTemplateXml = Context.getLocationService().getAddressTemplate();
		AddressTemplateXml wrapper = new AddressTemplateXml(addressTemplateXml);

		return ConversionUtil.convertToRepresentation(wrapper, Representation.FULL);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void updateCurrentConfiguration(@RequestBody AddressTemplateXml addressTemplate) {
		Context.requirePrivilege(PrivilegeConstants.CONFIGURE_VISITS);

		MessageSourceService messageSourceService = Context.getMessageSourceService();

		String xml = addressTemplate.getAddressTemplateXml();

		if (!StringUtils.hasText(xml)) {
			throw new IllegalPropertyException(messageSourceService.getMessage("AddressTemplate.error.empty"));
		}

		try {
			// To test whether this xml can be converted to address template
			AddressTemplate test = Context.getSerializationService().getDefaultSerializer()
					.deserialize(xml, AddressTemplate.class);

			List<String> requiredElements = test.getRequiredElements();
			if (requiredElements != null) {
				for (String fieldName : requiredElements) {
					try {
						PropertyUtils.getProperty(new PersonAddress(), fieldName);
					}
					catch (Exception e) {
						// wrong field declared in template
						throw new IllegalPropertyException(messageSourceService
								.getMessage("AddressTemplate.error.fieldNotDeclaredInTemplate", new Object[] { fieldName },
										Context.getLocale()));
					}
				}
			}

			Context.getLocationService().saveAddressTemplate(xml);
		}
		catch (SerializationException e) {
			String causeMessage = e.getCause().toString();

			if (causeMessage.contains("must be terminated by the matching")) {
				String causeMessage2 = e.getCause().getCause().toString();

				throw new IllegalPropertyException(messageSourceService.getMessage("AddressTemplate.error.elementInvalid",
						new Object[] { causeMessage.split("\"")[1], causeMessage2.split(";")[1].split(":")[1] },
						Context.getLocale()));
			} else if (causeMessage.split("\n")[0].endsWith("null")) {
				for (String part : causeMessage.split("\n")) {
					if (part.startsWith("path")) {
						throw new IllegalPropertyException(messageSourceService
								.getMessage("AddressTemplate.error.nameOrValueInvalid", new Object[] { part.split(":")[1] },
										Context.getLocale()));
					}
				}
			} else if (causeMessage.contains("UnknownFieldException")) {
				for (String part : causeMessage.split("\n")) {
					if (part.startsWith("path")) {
						throw new IllegalPropertyException(
								messageSourceService.getMessage("AddressTemplate.error.wrongFieldName",
										new Object[] { part.split("/")[part.split("/").length - 1] }, Context.getLocale()));
					}
				}
			} else {
				throw new IllegalRequestException(messageSourceService.getMessage("AddressTemplate.error"));
			}
		}
	}
}
