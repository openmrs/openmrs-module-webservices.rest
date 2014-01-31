package org.openmrs.module.webservices.rest.web.cors;

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Validated resource request origin, as defined in The Web Origin Concept (RFC 6454). Supported
 * schemes are {@code http} and {@code https}.
 * 
 * @author Vladimir Dzhuvinov
 * @author Luis Sala
 * @author Jared Ottley
 * @author Edraí Brosa
 */
public class ValidatedOrigin extends Origin {
	
	/**
	 * The origin scheme.
	 */
	private String scheme;
	
	/**
	 * The origin host.
	 */
	private String host;
	
	/**
	 * The parsed origin port, -1 for default port.
	 */
	private int port = -1;
	
	/**
	 * Creates a new validated origin from the specified URI string.
	 * 
	 * @param value The URI string for the origin. Must not be {@code null}.
	 * @throws OriginException If the value doesn't represent a valid and supported origin string.
	 */
	public ValidatedOrigin(final String value) throws OriginException {
		
		super(value);
		
		// Parse URI value
		
		URI uri = null;
		
		try {
			uri = new URI(value);
			
		}
		catch (URISyntaxException e) {
			
			throw new OriginException("Bad origin URI: " + e.getMessage());
		}
		
		scheme = uri.getScheme();
		host = uri.getHost();
		port = uri.getPort();
		
		if (scheme == null)
			throw new OriginException("Bad origin URI: Missing scheme, such as http or https");
		
		// Canonicalise scheme and host
		scheme = scheme.toLowerCase();
		
		// Apply the IDNA toASCII algorithm [RFC3490] to /host/
		host = IDN.toASCII(host, IDN.ALLOW_UNASSIGNED | IDN.USE_STD3_ASCII_RULES);
		
		// Finally, convert to lower case
		host = host.toLowerCase();
	}
	
	/**
	 * Returns the scheme.
	 * 
	 * @return The scheme.
	 */
	public String getScheme() {
		
		return scheme;
	}
	
	/**
	 * Returns the host (name or IP address).
	 * 
	 * @return The host name or IP address.
	 */
	public String getHost() {
		
		return host;
	}
	
	/**
	 * Returns the port number.
	 * 
	 * @return The port number, -1 for default port.
	 */
	public int getPort() {
		
		return port;
	}
	
	/**
	 * Returns the suffix which is made up of the host name / IP address and port (if a non-default
	 * port is specified).
	 * <p/>
	 * Example:
	 * <p/>
	 * 
	 * <pre>
	 * http://example.com => example.com
	 * http://example.com:8080 => example.com:8080
	 * </pre>
	 * 
	 * @return The suffix.
	 */
	public String getSuffix() {
		
		String s = host;
		
		if (port != -1)
			s = s + ":" + port;
		
		return s;
	}
}
