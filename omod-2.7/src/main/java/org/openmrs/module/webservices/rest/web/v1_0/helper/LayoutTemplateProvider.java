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

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.layout.LayoutSupport;
import org.openmrs.layout.LayoutTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class LayoutTemplateProvider<T extends LayoutTemplate> {
	
	private static final Logger log = LoggerFactory.getLogger(LayoutTemplateProvider.class);
	
	private final LayoutSupport<T> source;
	
	private final String layoutDefaultsProperty;
	
	public LayoutTemplateProvider(LayoutSupport<T> layoutSupport, String layoutDefaultsProperty) {
		this.source = layoutSupport;
		this.layoutDefaultsProperty = layoutDefaultsProperty;
	}
	
	public abstract T createInstance();
	
	public T getDefaultLayoutTemplate() {
		T template = source.getDefaultLayoutTemplate();
		return createPopulatedLayoutTemplate(template);
	}
	
	public T getLayoutTemplateByName(String codename) {
		T template = source.getLayoutTemplateByName(codename);
		return createPopulatedLayoutTemplate(template);
	}
	
	public List<T> getAllLayoutTemplates() {
		List<T> templates = source.getLayoutTemplates();
		List<T> populated = new ArrayList<>(templates.size());
		for (T template : templates) {
			populated.add(createPopulatedLayoutTemplate(template));
		}
		return populated;
	}
	
	private T createPopulatedLayoutTemplate(T template) {
		T shallowCopy = null;
		if (template != null) {
			shallowCopy = cloneLayoutTemplate(template);
		}
		if (shallowCopy != null) {
			Map<String, String> translatedNameMappings = getTranslatedNameMappings(template);
			Map<String, String> populatedTemplateDefaults = getPopulatedTemplateDefaults(template);
			shallowCopy.setNameMappings(translatedNameMappings);
			shallowCopy.setElementDefaults(populatedTemplateDefaults);
		}
		return shallowCopy;
	}
	
	private Map<String, String> getTranslatedNameMappings(T template) {
		return translateValues(template.getNameMappings());
	}
	
	private Map<String, String> getPopulatedTemplateDefaults(T template) {
		String customDefaults = Context.getAdministrationService().getGlobalProperty(layoutDefaultsProperty);
		Map<String, String> populated = populateElementDefaults(template.getElementDefaults(),
				layoutDefaultsProperty, customDefaults);
		return translateValues(populated);
	}
	
	/**
	 * Create of copy of the given element defaults map, having default values/overrides from the given global property.
	 *
	 * @param elementDefaults The element defaults map to be copied and populated.
	 * @param propertyName The name of the global property that supplies custom default values; for logging purposes.
	 * @param customDefaults The global defaults/overrides as a string in the form of "n=v,n1=v1,..."
	 * @return A fully populated copy of the given element defaults map,
	 *         or null if the given element defaults map was null and no defaults/overrides were specified.
	 */
	private static Map<String, String> populateElementDefaults(Map<String, String> elementDefaults, String propertyName, String customDefaults) {
		Map<String, String> merged = null;
		if (elementDefaults != null) {
			merged = new HashMap<>(elementDefaults);
		}
		
		if (!StringUtils.isBlank(customDefaults)) {
			if (merged == null) {
				merged = new HashMap<>();
			}
			// Check global properties for defaults/overrides in the form of n=v,n1=v1, etc
			Map<String, String> elementDefaultOverrides = parseElementDefaultOverrides(propertyName, customDefaults);
			for (String key : elementDefaultOverrides.keySet()) {
				merged.put(key, elementDefaultOverrides.get(key));
			}
		}
		return merged;
	}
	
	private static Map<String, String> parseElementDefaultOverrides(String propertyName, String customDefaults) {
		Map<String, String> parsedElementDefaults = new HashMap<>();
		String[] tokens = customDefaults.split(",");
		for (String token : tokens) {
			String[] pair = token.split("=");
			if (pair.length == 2) {
				String name = pair[0];
				String val = pair[1];
				parsedElementDefaults.put(name, val);
			} else {
				log.warn("Found invalid token while parsing GlobalProperty " + propertyName + " : " + token);
			}
		}
		return parsedElementDefaults;
	}
	
	/**
	 * Create a copy of a map having all message values translated according to the current locale.
	 *
	 * @param map The map to be copied and translated.
	 * @return A copy of the given map having translated values.
	 */
	private static Map<String, String> translateValues(Map<String, String> map) {
		MessageSource messageSource = Context.getMessageSourceService();
		Locale locale = Context.getLocale();
		if (map == null || messageSource == null || locale == null) {
			return map;
		}
		
		Map<String, String> translatedMap = new HashMap<>(map.size());
		for (String key : map.keySet()) {
			String value = map.get(key);
			try {
				String translated = messageSource.getMessage(value, null, locale);
				translatedMap.put(key, translated);
			}
			catch (NoSuchMessageException e) {
				translatedMap.put(key, value);
			}
		}
		return translatedMap;
	}
	
	/**
	 * Create a shallow copy of a LayoutTemplate
	 *
	 * @param layoutTemplate the LayoutTemplate instance to be copied
	 * @return a shallow copy of the given LayoutTemplate instance.
	 */
	private T cloneLayoutTemplate(T layoutTemplate) {
		T clone = createInstance();
		clone.setDisplayName(layoutTemplate.getDisplayName());
		clone.setCodeName(layoutTemplate.getCodeName());
		clone.setCountry(layoutTemplate.getCountry());
		clone.setNameMappings(layoutTemplate.getNameMappings());
		clone.setSizeMappings(layoutTemplate.getSizeMappings());
		clone.setElementDefaults(layoutTemplate.getElementDefaults());
		clone.setElementRegex(layoutTemplate.getElementRegex());
		clone.setElementRegexFormats(layoutTemplate.getElementRegexFormats());
		clone.setLineByLineFormat(layoutTemplate.getLineByLineFormat());
		clone.setRequiredElements(layoutTemplate.getRequiredElements());
		return clone;
	}
}
