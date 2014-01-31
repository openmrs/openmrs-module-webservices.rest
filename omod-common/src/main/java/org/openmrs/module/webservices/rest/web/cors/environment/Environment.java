package org.openmrs.module.webservices.rest.web.cors.environment;

import java.util.Properties;

/**
 * Interface for accessing environment properties (variables).
 * 
 * @author David Bellem
 */
public interface Environment {
	
	/**
	 * Gets an environment property with the specified name.
	 * 
	 * @param name The name of the environment property (variable). Must not be {@code null}.
	 * @return The value, {@code null} if not found.
	 */
	public abstract String getProperty(final String name);
	
	/**
	 * Gets the environment properties.
	 * 
	 * @return The environment properties.
	 */
	public abstract Properties getProperties();
}
