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
import java.util.Locale;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.layout.LayoutSupport;
import org.openmrs.layout.LayoutTemplate;
import org.openmrs.messagesource.MessageSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		return populateLayoutTemplate(template);
	}
	
	public T getLayoutTemplateByName(String codename) {
		T template = source.getLayoutTemplateByName(codename);
		return populateLayoutTemplate(template);
	}
	
	public List<T> getAllLayoutTemplates() {
		List<T> templates = source.getLayoutTemplates();
		List<T> populated = new ArrayList<>(templates.size());
		for (T template : templates) {
			populated.add(populateLayoutTemplate(template));
		}
		return populated;
	}
	
	private T populateLayoutTemplate(T template) {
		if (template == null) {
			return null;
		}
		Locale locale = Context.getLocale();
		T translated = translateNameMappings(template, locale);
		return populateTemplateDefaults(translated, locale);
	}
	
	private T translateNameMappings(T template, Locale locale) {
		MessageSourceService messageSourceService = Context.getMessageSourceService();
		Map<String, String> translatedNameMappings = translateValues(template.getNameMappings(),
				messageSourceService, locale);
		template.setNameMappings(translatedNameMappings);
		return template;
	}
	
	private T populateTemplateDefaults(T template, Locale locale) {
		String customDefaults = Context.getAdministrationService().getGlobalProperty(layoutDefaultsProperty);
		if (customDefaults != null) {
			applyElementDefaults(template, layoutDefaultsProperty, customDefaults);
		}
		
		MessageSourceService messageSourceService = Context.getMessageSourceService();
		Map<String, String> translatedDefaults = translateValues(template.getElementDefaults(),
				messageSourceService, locale);
		template.setElementDefaults(translatedDefaults);
		
		return template;
	}
	
	/**
	 * Create a copy of a map having all message values translated according to the given locale.
	 *
	 * @param map The map to be copied and translated
	 * @param messageService The message translation service
	 * @param locale The locale for which all messages should be translated
	 * @return A copy of the given map but with translated values
	 */
	private static Map<String, String> translateValues(Map<String, String> map,
			MessageSourceService messageService, Locale locale) {
		if (map == null || messageService == null || locale == null) {
			return map;
		}
		
		Map<String, String> translatedMap = new HashMap<>(map.size());
		for (String key : map.keySet()) {
			String value = map.get(key);
			try {
				String translated = messageService.getMessage(value, null, locale);
				translatedMap.put(key, translated);
			}
			catch (NoSuchMessageException e) {
				translatedMap.put(key, value);
			}
		}
		return translatedMap;
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
}
