package org.openmrs.module.webservices.rest.web.cors;

/**
 * Resource request origin (not validated), as defined in The Web Origin Concept (RFC 6454).
 * <p/>
 * Use {@link #validate} to check the origin syntax or if you want to query its scheme, host and
 * port.
 * <p/>
 * Origin examples:
 * <p/>
 * 
 * <pre>
 * http://www.example.com
 * https://sso.example.com:8080
 * http://192.168.0.1
 * file:///data/file.html
 * null
 * </pre>
 * 
 * @author Vladimir Dzhuvinov
 * @author Luis Sala
 * @author Jared Ottley
 */
public class Origin {
	
	/**
	 * The original origin value, used in hash code generation and equality checking.
	 */
	private final String value;
	
	/**
	 * Creates a new origin from the specified URI string. Note that the syntax is not validated.
	 * 
	 * @param value The URI string for the origin. Must not be {@code null}.
	 */
	public Origin(final String value) {
		
		if (value == null)
			throw new IllegalArgumentException("The origin value must not be null");
		
		this.value = value;
	}
	
	/**
	 * Returns a validated instance of this origin.
	 * 
	 * @throws OriginException If the value doesn't represent a valid and supported origin string.
	 */
	public ValidatedOrigin validate() throws OriginException {
		
		return new ValidatedOrigin(value);
	}
	
	/**
	 * Returns the original string value of this origin.
	 * 
	 * @return The origin as a URI string.
	 */
	@Override
	public String toString() {
		
		return value;
	}
	
	/**
	 * Overrides {@code Object.hashCode}.
	 * 
	 * @return The object hash code.
	 */
	@Override
	public int hashCode() {
		
		return value.hashCode();
	}
	
	/**
	 * Overrides {@code Object.equals()}.
	 * 
	 * @param object The object to compare to.
	 * @return {@code true} if the objects are both origins with the same value, else {@code false}.
	 */
	@Override
	public boolean equals(Object object) {
		
		return object != null && object instanceof Origin && this.toString().equals(object.toString());
	}
}
