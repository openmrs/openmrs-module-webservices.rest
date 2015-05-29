/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;

/**
 * This class is a wrapper for {@link org.openmrs.Cohort} and {@link org.openmrs.Patient} that needs 
 * to be sent for adding/removing members from a cohort.  
 */
public class CohortMember1_8 implements Serializable {
	
	public static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(UserAndPassword1_8.class);
	
	/** Patient, who exists in cohort*/
	private Patient patient;
	
	/** Parent cohort*/
	private Cohort cohort;
	
	/**
	 * Default constructor
	 */
	public CohortMember1_8() {
		this.patient = new Patient();
		this.cohort = new Cohort();
	}
	
	/**
	 * Copier constructor to set fields 
	 * @param patient
	 * @param cohort
	 */
	public CohortMember1_8(Patient patient, Cohort cohort) {
		super();
		this.patient = patient;
		this.cohort = cohort;
	}
	
	/**
	 * @param cohort the cohort to set
	 */
	public void setCohort(Cohort cohort) {
		this.cohort = cohort;
	}
	
	/**
	 * @return cohort as parent object
	 */
	public Cohort getCohort() {
		return cohort;
	}
	
	/**
	 * @param patient the patient's object to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	/**
	 * @return patient entity's object
	 */
	public Patient getPatient() {
		return patient;
	}
}
