package org.openmrs.module.webservices.rest.web.resource.api;

import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Can convert from String -> T.
 * Can convert from T -> json-friendly version of a given Representation 
 */
public interface DelegateConverter<T> {
	
	/**
	 * @param string
	 * @return the result of converting the String input to a T
	 */
	T getByUniqueId(String string);

	/**
	 * @param instance
	 * @param rep
	 * @return a convertible-to-json object for instance in the given representation
	 * @throws Exception 
	 */
	Object asRepresentation(T instance, Representation rep) throws ConversionException;

}
