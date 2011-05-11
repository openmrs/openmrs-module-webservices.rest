package org.openmrs.module.webservices.rest.web.resource.api;

import org.openmrs.module.webservices.rest.web.representation.Representation;

/**
 * Can convert from String -> T.
 * An instance of this class can serve as a delegating resource wrapper around a T, if a new instance is
 * instantiated and had {@link #setDelegate(Object)} called on it.
 */
public interface DelegateConverter<T> {
	
	/**
	 * @param string
	 * @return the result of converting the String input to a T
	 */
	T fromString(String string);

	/**
	 * @param rep
	 * @return a convertible-to-json object for this resource in the given representation
	 * @throws Exception 
	 */
	Object asRepresentation(Representation rep) throws Exception;

	/**
	 * Configures an instance of the implementing class to act as a Resource representation of T. 
	 * @param o
	 */
	void setDelegate(T delegate);

}
