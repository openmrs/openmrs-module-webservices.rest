package org.openmrs.module.webservices.rest.web.cors;

import java.util.Iterator;
import java.util.Set;

/**
 * Header utilities.
 * 
 * @author Vladimir Dzhuvinov
 */
public class HeaderUtils {
	
	/**
	 * Serialises the items of a set into a string. Each item must have a meaningful
	 * {@code toString()} method.
	 * 
	 * @param set The set to serialise. Must not be {@code null}.
	 * @param sep The string separator to apply. Should not be {@code null}.
	 * @return The serialised set as string.
	 */
	public static String serialize(final Set set, final String sep) {
		
		Iterator it = set.iterator();
		
		StringBuilder sb = new StringBuilder();
		
		while (it.hasNext()) {
			
			sb.append(it.next().toString());
			
			if (it.hasNext())
				sb.append(sep);
		}
		
		return sb.toString();
	}
	
	/**
	 * Parses a header value consisting of zero or more space / comma / space + comma separated
	 * strings. The input string is trimmed before splitting.
	 * 
	 * @param headerValue The header value, may be {@code null}.
	 * @return A string array of the parsed string items, empty if none were found or the input was
	 *         {@code null}.
	 */
	public static String[] parseMultipleHeaderValues(final String headerValue) {
		
		if (headerValue == null)
			return new String[0]; // empty array
			
		String trimmedHeaderValue = headerValue.trim();
		
		if (trimmedHeaderValue.isEmpty())
			return new String[0];
		
		return trimmedHeaderValue.split("\\s*,\\s*|\\s+");
	}
}
