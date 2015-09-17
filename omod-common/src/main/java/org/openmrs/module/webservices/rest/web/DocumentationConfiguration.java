package org.openmrs.module.webservices.rest.web;

import java.util.List;

public class DocumentationConfiguration {
	
	private List<String> selectedVersions;
	
	public DocumentationConfiguration() {
		
	}
	
	public DocumentationConfiguration(List<String> selectedVersions) {
		this.selectedVersions = selectedVersions;
	}
	
	/**
	 * @return the selectedVersions
	 */
	public List<String> getSelectedVersions() {
		return selectedVersions;
	}
	
	/**
	 * @param selectedVersions the selectedVersions to set
	 */
	public void setSelectedVersions(List<String> selectedVersions) {
		this.selectedVersions = selectedVersions;
	}
	
}
