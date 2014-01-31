package org.openmrs.module.webservices.rest.web.cors.service;

import org.openmrs.module.webservices.rest.web.cors.CORSConfiguration;

import java.util.Properties;

public interface CorsConfigurationService {
	
	void updateConfiguration(Properties properties);
	
	CORSConfiguration loadCorsConfigurationFromDatabase();
}
