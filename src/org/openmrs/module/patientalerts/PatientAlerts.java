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
package org.openmrs.module.patientalerts;

import org.openmrs.Patient;

/**
 *
 */
public class PatientAlerts {
	
	private Patient patient;
	
	private String messageProperties;
	
	/**
     * 
     */
	public PatientAlerts() {
		// TODO Auto-generated constructor stub
	}
	
	private int identifier;
	
	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}
	
	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	/**
	 * @return the messageProperties
	 */
	public String getMessageProperties() {
		return messageProperties;
	}
	
	/**
	 * @param messageProperties the messageProperties to set
	 */
	public void setMessageProperties(String messageProperties) {
		this.messageProperties = messageProperties;
	}
	
	/**
	 * @return the identifier
	 */
	public int getIdentifier() {
		return identifier;
	}
	
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
	
}
