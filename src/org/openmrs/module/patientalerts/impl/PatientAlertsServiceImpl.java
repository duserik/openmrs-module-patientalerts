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
package org.openmrs.module.patientalerts.impl;

import java.util.List;
import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.module.patientalerts.db.PatientAlertsDAO;
import org.openmrs.module.patientalerts.service.PatientAlertsService;

/**
 * Default implementation of the Patient Alerts Service
 * 
 * @author Kamonyo & Dusabe
 * @see org.openmrs.module.patientalerts.service.PatientAlertsService
 */

public class PatientAlertsServiceImpl implements PatientAlertsService {
	
	private PatientAlertsDAO patientAlertsDAO;

	
    /**
     * @return the drugOrderDAO
     */
    public PatientAlertsDAO getPatientAlertsDAO() {
    	return patientAlertsDAO;
    }

	
    /**
     * @param drugOrderDAO the drugOrderDAO to set
     */
    public void setPatientAlertsDAO(PatientAlertsDAO patientAlertsDAO) {
    	this.patientAlertsDAO = patientAlertsDAO;
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#couplesWithNoFamilyPlanning(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> couplesWithNoFamilyPlanning(Boolean isIndiv) {
	    return patientAlertsDAO.couplesWithNoFamilyPlanning(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#getAlertsForPatient(org.openmrs.Patient, java.lang.Boolean)
     */
    @Override
    public List<String> getAlertsForPatient(Patient patient, Boolean isIndiv) {
	    return patientAlertsDAO.getAlertsForPatient(patient, isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#getNumberOfAlerts(java.lang.Boolean)
     */
    @Override
    public int getNumberOfAlerts(Boolean isIndiv) {
	    return patientAlertsDAO.getNumberOfAlerts(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#hivPatientWithNoCd4(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> hivPatientWithNoCd4(Boolean isIndiv) {
	    return patientAlertsDAO.hivPatientWithNoCd4(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#hivPosWomenChildrenWithNoTestIn18Months(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> hivPosWomenChildrenWithNoTestIn18Months(Boolean isIndiv) {
	    return patientAlertsDAO.hivPosWomenChildrenWithNoTestIn18Months(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#malePatientInPMTCT(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> malePatientInPMTCT(Boolean isIndiv) {
	    return patientAlertsDAO.malePatientInPMTCT(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#outOfRangePatientLabResult(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> outOfRangePatientLabResult(Boolean isIndiv) {
	    return patientAlertsDAO.outOfRangePatientLabResult(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientInPMTCTLonger9Months(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> patientInPMTCTLonger9Months(Boolean isIndiv) {
	    return patientAlertsDAO.patientInPMTCTLonger9Months(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientWithDecliningCD4(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> patientWithDecliningCD4(Boolean isIndiv) {
	    return patientAlertsDAO.patientWithDecliningCD4(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientWithNoCD4TestLast6Months(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> patientWithNoCD4TestLast6Months(Boolean isIndiv) {
	    return patientAlertsDAO.patientWithNoCD4TestLast6Months(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientsWhoStoppedDrugsWithNoReason(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> patientsWhoStoppedDrugsWithNoReason(Boolean isIndiv) {
    	return patientAlertsDAO.patientsWhoStoppedDrugsWithNoReason(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientsWithLowCD4NoMedication(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> patientsWithLowCD4NoMedication(Boolean isIndiv) {
	    return patientAlertsDAO.patientsWithLowCD4NoMedication(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientsWithNoEncounterLast3Months(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> patientsWithNoEncounterLast3Months(Boolean isIndiv) {
	    return patientAlertsDAO.patientsWithNoEncounterLast3Months(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientsWithNoNextVisitDate(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> patientsWithNoNextVisitDate(Boolean isIndiv) {
	    return patientAlertsDAO.patientsWithNoNextVisitDate(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientsWithNoPartnerInfo(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> patientsWithNoPartnerInfo(Boolean isIndiv) {
	    return patientAlertsDAO.patientsWithNoPartnerInfo(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#sTIPatientWithNoTreatment(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> sTIPatientWithNoTreatment(Boolean isIndiv) {
	    return patientAlertsDAO.sTIPatientWithNoTreatment(isIndiv);
    }


	/**
     * @see org.openmrs.module.patientalerts.service.PatientAlertsService#tbPatientWithNoTreatment(java.lang.Boolean)
     */
    @Override
    public Map<Integer, String> tbPatientWithNoTreatment(Boolean isIndiv) {
	    return patientAlertsDAO.tbPatientWithNoTreatment(isIndiv);
    }
	
}
