/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.layout.LayoutSupport;
import org.openmrs.layout.LayoutTemplate;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

public class LayoutTemplateProvider<T extends LayoutTemplate> {
	
	private static final Logger log = LoggerFactory.getLogger(LayoutTemplateProvider.class);
	
	private final LayoutSupport<T> source;
	
	private final String layoutDefaultsProperty;
	
	public LayoutTemplateProvider(LayoutSupport<T> layoutSupport, String layoutDefaultsProperty) {
		this.source = layoutSupport;
		this.layoutDefaultsProperty = layoutDefaultsProperty;
	}
	
	public T getDefaultLayoutTemplate() {
		T template = source.getDefaultLayoutTemplate();
		return populateTemplateDefaults(template);
	}
	
	public T getLayoutTemplateByName(String codename) {
		T template = source.getLayoutTemplateByName(codename);
		return populateTemplateDefaults(template);
	}
	
	public List<T> getAllLayoutTemplates() {
		List<T> templates = source.getLayoutTemplates();
		List<T> populated = new ArrayList<>(templates.size());
		for (T template: templates) {
			populated.add(populateTemplateDefaults(template));
		}
		return populated;
	}
	
	public SimpleObject asRepresentation(T instance) throws ConversionException {
		List<List<Map<String, String>>> lines = getLines(instance);
		SimpleObject obj = new SimpleObject();
		obj.add("displayName", instance.getDisplayName());
		obj.add("codeName", instance.getCodeName());
		obj.add("country", instance.getCountry());
		obj.add("lines", lines);
		obj.add("lineByLineFormat", instance.getLineByLineFormat());
		obj.add("nameMappings", instance.getNameMappings());
		obj.add("sizeMappings", instance.getSizeMappings());
		obj.add("elementDefaults", instance.getElementDefaults());
		obj.add("elementRegex", instance.getElementRegex());
		obj.add("elementRegexFormats", instance.getElementRegexFormats());
		obj.add("requiredElements", instance.getRequiredElements());
		return obj;
	}
	
	private T populateTemplateDefaults(T template) {
		String customDefaults = Context.getAdministrationService().getGlobalProperty(layoutDefaultsProperty);
		if (template != null && customDefaults != null) {
			applyElementDefaults(template, layoutDefaultsProperty, customDefaults);
		}
		return template;
	}
	
	/**
	 * Update the element defaults property of the given LayoutTemplate with
	 * default values/overrides from the given global property.
	 *
	 * @param template The layout template to update.
	 * @param propertyName The name of the global property that supplies custom default values; for logging purposes.
	 * @param customDefaults The global defaults/overrides as a string in the form of "n=v,n1=v1,..."
	 */
	private static void applyElementDefaults(LayoutTemplate template, String propertyName, String customDefaults) {
		// Check global properties for defaults/overrides in the form of n=v,n1=v1, etc
		Map<String, String> parsedElementDefaults = new HashMap<>();
		String[] tokens = customDefaults.split(",");
		for (String token : tokens) {
			String[] pair = token.split("=");
			if (pair.length == 2) {
				String name = pair[0];
				String val = pair[1];
				parsedElementDefaults.put(name, val);
			} else {
				log.debug("Found invalid token while parsing GlobalProperty " + propertyName + " : " + token);
			}
		}
		mergeElementDefaults(template, parsedElementDefaults);
	}
	
	private static void mergeElementDefaults(LayoutTemplate template, Map<String, String> elementDefaultOverrides) {
		Map<String, String> elementDefaults = template.getElementDefaults();
		if (elementDefaults == null) {
			elementDefaults = new HashMap<>();
		}
		for (String key : elementDefaultOverrides.keySet()) {
			elementDefaults.put(key, elementDefaultOverrides.get(key));
		}
		template.setElementDefaults(elementDefaults);
	}
	
	private static List<List<Map<String, String>>> getLines(LayoutTemplate template) {
		MessageSource messageSource = Context.getMessageSourceService();
		Map<String, String> nameMappings = template.getNameMappings();
		List<List<Map<String, String>>> lines = template.getLines();
		for (List<Map<String, String>> line : lines) {
			for (Map<String, String> elements : line) {
				if (elements.containsKey("displayText")) {
					String displayCode = elements.get("displayText");
					if (StringUtils.isNotBlank(displayCode)) {
						String displayText;
						try {
							displayText = messageSource.getMessage(displayCode, null, Context.getLocale());
						}
						catch (NoSuchMessageException e) {
							displayText = displayCode;
						}
						elements.put("displayText", displayText);
						
						String codeName = elements.get("codeName");
						if (codeName != null && nameMappings.containsKey(codeName)) {
							nameMappings.put(codeName, displayText);
						}
					}
				}
			}
		}
		return lines;
	}
}
