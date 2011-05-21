package org.openmrs.module.webservices.rest.web;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;

/**
 * This class is a wrapper for {@link org.openmrs.Cohort} and {@link org.openmrs.Patient} that needs 
 * to be sent for adding/removing members from a cohort. Requires extending BaseOpenmrsMetadata to be 
 * able to interact with {@link MetadataDelegatingCrudResource} and making instance of metadata type
 * 
 * @see org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource
 */
public class CohortMember extends BaseOpenmrsMetadata implements Serializable {
	
	public static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(UserAndPassword.class);
	
	private Patient patient;
	
	private String cohortUuid;
	
	public CohortMember() {
	}
	
	public CohortMember(Patient patient, String cohortUuid) {
		super();
		this.patient = patient;
		this.cohortUuid = cohortUuid;
	}
	
	public void setCohortUuid(String cohortUuid) {
		this.cohortUuid = cohortUuid;
	}
	
	public String getCohortUuid() {
		return cohortUuid;
	}
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return this.patient.getId();
	}
	
	@Override
	public void setId(Integer id) {
		this.patient.setId(id);
		
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
}
