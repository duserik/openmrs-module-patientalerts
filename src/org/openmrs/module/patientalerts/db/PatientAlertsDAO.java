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
package org.openmrs.module.patientalerts.db;

import java.util.List;
import java.util.Map;

import org.openmrs.Patient;


/**
 *
 */
public interface PatientAlertsDAO {
		
		/**
		 * Returns the alerts map for all HIV infected patients with low CD4 count without any
		 * medication. This must retrieve the latest non voided CD4 count values through encounters so
		 * that it can check if it is less than 350 cells/mm3 (Because 350 is already critical). If a
		 * patient has CD4 count < 350, the system must alert the clinician so that he/she can take an
		 * immediate attention for this patient.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> patientsWithLowCD4NoMedication(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all HIV infected patients without any encounter for last 3 months.
		 * These patients are known by comparing their latest encounter with the today's date. If the
		 * difference is greater than 3 then the clinician can see the alert in order to take immediate
		 * attention to that patient.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> patientsWithNoEncounterLast3Months(Boolean isIndiv);
		
		/**
		 * Returns alerts map for all male patients in PMTCT program. This is only valid for male
		 * patients who are older than 18 months. This is due to the fact that a male child who is not
		 * more than 18 months old can be in PMTCT Program. So, it will only alert for male patients who
		 * are older than 18 months old. The clinician will have to remove those patients from the PMTCT
		 * Program. This is calculated by taking the patient's gender and birth date, and see whether he
		 * is a male and is more than 18 months and he has been enrolled in PMTCT Program.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> malePatientInPMTCT(Boolean isIndiv);
		
		/**
		 * Returns alerts map for all adults patients who have been in PMTCT Program longer than 9
		 * months. This is due to the fact that a patient cannot be pregnant for more than 9 months. The
		 * clinician must be alerted when this happens so that he/she can take an immediate attention.
		 * The probable solution to this is to remove the patient from the PMTCT Program. This is
		 * calculated by taking the patient's date of enrollement (<code>getDateEnrolled()</code>) in
		 * PMTCT Program and compare it to today's date.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> patientInPMTCTLonger9Months(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all HIV Positive Women whose Children didn't have any HIV test in
		 * last 18 months. Children born to women HIV positive who have not made HIV test after 18
		 * months, this is normally a case that needs an immediate attention. This is calculated by
		 * checking the relationship between child patient and parent patient, and see whether the
		 * mother is enrolled in PMTCT Program, then check if this child patient did not have no HIV
		 * test for last 18 months.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> hivPosWomenChildrenWithNoTestIn18Months(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all HIV infected patients with no CD4 count. This is a critical
		 * case, because every patient in HIV Program must have CD4 count values known in order to
		 * evaluate them. This is brought to the clinician who must take an immediate attention to the
		 * patient. This is calculated by checking whether the patient is enrolled in HIV Program and
		 * see if his/her CD4 Count values are known.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> hivPatientWithNoCd4(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all TB infected patients with no TB treatment. This method first
		 * checks whether the patient is enrolled in TB Program or HIV Program, and if he/she is TB
		 * infected (Simple Tuberculosis Test is Positive). Then
		 * <code>hasTBTreatement(Patient patient)</code> method check through the list of all existing
		 * TB treatments and see if any of those is not being taken by this patient.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> tbPatientWithNoTreatment(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all patients with no next visit date. This method checks if the
		 * patient is enrolled in HIV Program and see if he/she has Next Scheduled Visit Date. Otherwise
		 * it alerts the clinician that a Next Visit Date should be assigned to the patient.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> patientsWithNoNextVisitDate(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all HIV infected patients who do not practice any family planning
		 * method. This method first checks if the patient is married or lives with a permanent partner
		 * and whether he/she is enrolled in HIV or PMTCT Program, then goes through the list of various
		 * used and known methods of Family Planning and checks whether the patient has been assigned
		 * any of them. Otherwise, the system alerts the clinician that the patient does not follow any
		 * family planning method.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> couplesWithNoFamilyPlanning(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all HIV infected patients whose partners do not have any
		 * information about HIV test result. This method first checks if the patient is married or
		 * lives with a permanent partner and whether he/she is enrolled in HIV or PMTCT Program, then
		 * checks if any HIV Test Result information about the partner is known. If not, the clinician
		 * is alerted.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> patientsWithNoPartnerInfo(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all HIV infected patients with a declining CD4 count. This method
		 * checks whether the patient is enrolled in HIV Program and compares the latest CD4 count
		 * values (by patient's encounter) with the previous one. If the previous is greater than the
		 * latest, this means the CD4 count for this patient is declining. The alert is shown.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> patientWithDecliningCD4(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all HIV infected patients without any CD4 test for last 6 months.
		 * This method first checks whether the patient is enrolled in HIV Program, then checks the
		 * latest CD4 count test date and compares it with today's date (using
		 * <code>computeMonths(Date then)</code>). If the number returned is greater than 6 months, the
		 * clinician is alerted about that.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> patientWithNoCD4TestLast6Months(Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all STI infected patients with no treatment.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> sTIPatientWithNoTreatment(Boolean isIndiv);
		
		/**
		 * Returns a list of all alerts related to a given patient. Every alert matching the given
		 * patient will be added to the array list of all alerts related to this patient.
		 * 
		 * @param patient the patient to be matched.
		 * @return messages the list of all existing alerts matching the given patient.
		 */
		public List<String> getAlertsForPatient(Patient patient, Boolean isIndiv);
		
		/**
		 * Returns the alerts map for all patients who stopped drugs without any reason. this method
		 * checks whether the patient DrugOrder has been descontinued (
		 * <code>getDiscontinued()==true</code>) and if discontinued reason (
		 * <code>getDiscontinuesReason()</code>) is known. If not, the clinician is alerted.
		 * 
		 * @return messages the alerts map matching the conditions.
		 */
		public Map<Integer, String> patientsWhoStoppedDrugsWithNoReason(Boolean isIndiv);
		
		/**
		 * Auto generated method comment
		 * 
		 * @return
		 */
		public Map<Integer, String> outOfRangePatientLabResult(Boolean isIndiv);
		
		/**
		 * Helps in counting the total number of all existing alerts. This by calculating the sum of
		 * sizes of differents kind of alerts' maps returned by all methods called in this method : -
		 * <code>patientsWithLowCD4NoMedication()</code> -
		 * <code>patientsWithNoEncounterLast3Months()</code> - <code>malePatientInPMTCT()</code> -
		 * <code>patientInPMTCTLonger9Months()</code> - <code>hivPatientWithNoCd4()</code> -
		 * <code>patientsWithNoNextVisitDate()</code> - <code>patientWithNoCD4TestLast6Months()</code> -
		 * <code>tbPatientWithNoTreatment()</code> -
		 * <code>hivPosWomenChildrenWithNoTestIn18Months()</code> -
		 * <code>patientsWithNoPartnerInfo()</code> - <code>couplesWithNoFamilyPlanning()</code> -
		 * <code>sTIPatientWithNoTreatment()</code> - <code>patientWithDecliningCD4()</code> -
		 * <code>patientsWhoStoppedDrugsWithNoReason()</code>
		 * 
		 * @return the total sum of all maps sizes returned by all these methods above.
		 */
		public int getNumberOfAlerts(Boolean isIndiv);	
}
