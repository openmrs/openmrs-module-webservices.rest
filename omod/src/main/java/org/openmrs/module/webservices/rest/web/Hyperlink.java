package org.openmrs.module.webservices.rest.web;


/**
 * A link to another resource
 */
public class Hyperlink {
	
	private String rel;
	
	private String uri;
	
	public Hyperlink(String rel, String uri) {
		this.rel = rel;
		this.uri = uri;
	}
	
	/**
	 * @return the rel
	 */
	public String getRel() {
		return rel;
	}
	
	/**
	 * @param rel the rel to set
	 */
	public void setRel(String rel) {
		this.rel = rel;
	}
	
	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
}