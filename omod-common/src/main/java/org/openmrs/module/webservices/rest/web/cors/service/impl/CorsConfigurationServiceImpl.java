package org.openmrs.module.webservices.rest.web.cors.service.impl;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.cors.CORSConfiguration;
import org.openmrs.module.webservices.rest.web.cors.CORSConfigurationException;
import org.openmrs.module.webservices.rest.web.cors.CORSFilter;
import org.openmrs.module.webservices.rest.web.cors.service.CorsConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Properties;

public class CorsConfigurationServiceImpl implements CorsConfigurationService {
	
	@Autowired
	private CORSFilter corsFilter;
	
	@Override
	public void updateConfiguration(Properties properties) {
		if (properties == null) {
			throw new InvalidParameterException("Parameter 'properties' cannot be null.");
		}
		
		try {
			CORSConfiguration corsConfiguration = new CORSConfiguration(properties);
			corsFilter.setConfiguration(corsConfiguration);
		}
		catch (CORSConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public CORSConfiguration loadCorsConfigurationFromDatabase() {
		try {
			Properties properties = loadConfigurationFromGlobalProperties();
			return new CORSConfiguration(properties);
		}
		catch (CORSConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Properties loadConfigurationFromGlobalProperties() {
		Properties properties = new Properties();
		AdministrationService administrationService = Context.getAdministrationService();
		List<GlobalProperty> globalProperties = administrationService.getGlobalPropertiesByPrefix(String.format("%s.%s",
		    RestConstants.MODULE_ID, RestConstants.CORS_PREFIX));
		
		for (GlobalProperty globalProperty : globalProperties) {
			properties.put(globalProperty.getProperty(), globalProperty.getPropertyValue());
		}
		
		return properties;
	}
}
