package org.openmrs.module.webservices.docs;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;

public class SearchQueryDoc {
	
	private String description;
	
	private List<String> requiredParameters;
	
	private List<String> optionalParameters;
	
	public SearchQueryDoc(SearchQuery searchQuery) {
		this.description = searchQuery.getDescription();
		this.requiredParameters = new ArrayList<String>(searchQuery.getRequiredParameters());
		this.optionalParameters = new ArrayList<String>(searchQuery.getOptionalParameters());
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the requiredParameters
	 */
	public List<String> getRequiredParameters() {
		return requiredParameters;
	}
	
	/**
	 * @param requiredParameters the requiredParameters to set
	 */
	public void setRequiredParameters(List<String> requiredParameters) {
		this.requiredParameters = requiredParameters;
	}
	
	/**
	 * @return the optionalParameters
	 */
	public List<String> getOptionalParameters() {
		return optionalParameters;
	}
	
	/**
	 * @param optionalParameters the optionalParameters to set
	 */
	public void setOptionalParameters(List<String> optionalParameters) {
		this.optionalParameters = optionalParameters;
	}
	
}
