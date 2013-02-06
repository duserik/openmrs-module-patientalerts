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
package org.openmrs.module.patientalerts.db.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientalerts.Constants;
import org.openmrs.module.patientalerts.db.PatientAlertsDAO;
import org.openmrs.module.patientalerts.service.ContextProvider;


/**
 *
 */
public class PatientAlertsDAOImpl implements PatientAlertsDAO {
Log log = LogFactory.getLog(getClass());
	
	private SessionFactory sessionFactory;
	
	
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private Boolean canViewPatientNames() {
		return Context.getAuthenticatedUser().hasPrivilege("View Patient Names");
	}
	
	/**
	 * Returns the sum of all returned maps sizes that will be considered as the total number of all
	 * existing alerts for all patients.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#getNumberOfAlerts()
	 * @return numberOfAlerts the total sum of all sizes of all returned maps
	 */
	public int getNumberOfAlerts(Boolean isIndiv) {
		
		int numberOfAlerts = 0;
		Map<String, Integer> categoryNumber = new HashMap<String, Integer>();
		
		categoryNumber.put("lowCd4NoMeds", patientsWithLowCD4NoMedication(isIndiv).size());
		categoryNumber.put("noEnclast3Mon", patientsWithNoEncounterLast3Months(isIndiv).size());
		categoryNumber.put("malePMTCTPat", malePatientInPMTCT(isIndiv).size());
		categoryNumber.put("moreThan9MonInPMTCT", patientInPMTCTLonger9Months(isIndiv).size());
		categoryNumber.put("hivPatWithNoCd4", hivPatientWithNoCd4(isIndiv).size());
		categoryNumber.put("patWithNoNextVisitDate", patientsWithNoNextVisitDate(isIndiv).size());
		categoryNumber.put("patWithNoCD4TestLast6Mon", patientWithNoCD4TestLast6Months(isIndiv).size());
		categoryNumber.put("tbPatWithNoTreat", tbPatientWithNoTreatment(isIndiv).size());
		categoryNumber.put("hivPosWomChildWithNoTestIn18Mon", hivPosWomenChildrenWithNoTestIn18Months(isIndiv).size());
		categoryNumber.put("patWithNoPartnerInfo", patientsWithNoPartnerInfo(isIndiv).size());
		categoryNumber.put("coupleWithNoFamPlan", couplesWithNoFamilyPlanning(isIndiv).size());
		categoryNumber.put("sTIPattWithNoTreat", sTIPatientWithNoTreatment(isIndiv).size());
		categoryNumber.put("patWithDeclCD4", patientWithDecliningCD4(isIndiv).size());
		categoryNumber.put("stopDrugsWithNoReason", patientsWhoStoppedDrugsWithNoReason(isIndiv).size());
		categoryNumber.put("outOfRangePatLabRes", outOfRangePatientLabResult(isIndiv).size());
				
		for(String ind : categoryNumber.keySet()) {
			numberOfAlerts += categoryNumber.get(ind);
		}
		
		return numberOfAlerts;
	}
	
	/**
	 * Returns the alerts map for all HIV infected patients with low CD4 count without any
	 * medication.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#lowCD4NoMedication()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> patientsWithLowCD4NoMedication(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Session session = getSessionFactory().getCurrentSession();
		
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		
		String queryStr = " SELECT distinct person_id, obs_datetime "
							+ " FROM obs as ut "
							+ " WHERE obs_datetime = (SELECT max(obs_datetime) from obs as ut2 where ut2.person_id = ut.person_id) "
							+ " and value_numeric is not null "
							+ " and concept_id = " + Constants.CD4_COUNT
							+ " and person_id not in (select distinct patient_id from orders) "
							+ " and value_numeric < " + Constants.CRITICAL_LOW_CD4_COUNT
							+ " ORDER BY obs_datetime DESC ";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Object[]> records = query.list();
		for (Object[] object : records) {
			Patient patient = patService.getPatient((Integer) object[0]);
			if (!patient.getVoided()) {
				if(isIndiv && canViewPatientNames())
					obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				else
					obj[0] = ", ";
				
				messages.put((Integer) object[0], ContextProvider.getMessage("patientalerts.alertLowCD4NoMedication", obj));		
			}
		}		
		return messages;
	}
	
	/**
	 * Returns the alerts map for all HIV infected patients without any encounter for last 3 months.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#noEncounterLast3Months()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> patientsWithNoEncounterLast3Months(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		
		Session session = getSessionFactory().getCurrentSession();
		
		String queryStr = "SELECT pp.patient_id FROM patient_program pp INNER JOIN encounter e ON pp.patient_id=e.patient_id LEFT JOIN obs o on pp.patient_id = o.person_id WHERE e.patient_id NOT IN (SELECT patient_id FROM encounter WHERE datediff(curdate(), encounter_datetime) < 90) AND pp.patient_id NOT IN (select person_id from obs where concept_id = " + Constants.REASON_FOR_EXITING_CARE + ") GROUP BY pp.patient_id";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> records = query.list();
		for (Integer patientId : records) {
			Patient patient = patService.getPatient(patientId);
			if (!patient.getVoided()) {
				if(isIndiv && canViewPatientNames())
					obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				else
					obj[0] = ", ";
				
				messages.put(patientId, ContextProvider.getMessage("patientalerts.alertWithNoEncounterLast3Months", obj));				
			}
		}		
		return messages;
	}
	
	/**
	 * Returns alerts map for all male patients in PMTCT program
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#malePatientInPMTCT()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> malePatientInPMTCT(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Object[] obj = new Object[2];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		String queryStr = "SELECT p.patient_id FROM patient_program p INNER JOIN program pr ON p.program_id=pr.program_id AND pr.concept_id=" + Constants.PMTCT_PROGRAM + " AND p.voided = 0 LEFT JOIN obs o on p.patient_id = o.person_id INNER JOIN person pe ON p.patient_id=pe.person_id AND pe.gender='M' AND p.patient_id NOT IN (SELECT person_id from obs where concept_id = " + Constants.REASON_FOR_EXITING_CARE + ") AND datediff(curdate(), pe.birthdate) > 540 GROUP BY p.patient_id";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> patientIds = query.list();
		for (Integer patientId : patientIds) {
			Patient patient = patService.getPatient(patientId);
			if (!patient.getVoided()) {
				if(isIndiv && canViewPatientNames())
					obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				else
					obj[0] = ", ";
				
				messages.put(patientId, ContextProvider.getMessage("patientalerts.alertMaleInPMTCT", obj));
			}
		}		
		return messages;
	}
	
	/**
	 * Returns alerts map for all adults patients who have been in PMTCT Program longer than 9
	 * months.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientInPMTCTLonger3Months()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings( { "unchecked" })
	public Map<Integer, String> patientInPMTCTLonger9Months(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		String queryStr = "SELECT pp.patient_id FROM patient_program pp LEFT JOIN obs o ON pp.patient_id = o.person_id AND pp.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id = " + Constants.REASON_FOR_EXITING_CARE + ") INNER JOIN person pe ON pp.patient_id=pe.person_id AND date_completed IS NULL AND datediff(curdate(), pe.birthdate) > 540 AND pe.gender='F' INNER JOIN program p ON pp.program_id=p.program_id AND p.concept_id= " + Constants.REASON_FOR_EXITING_CARE + " AND datediff(curdate(), pp.date_enrolled) > 270 GROUP BY pp.patient_id";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> patientIds = query.list();
		for (Integer patientId : patientIds) {
			Patient patient = patService.getPatient(patientId);
			if (!patient.getVoided()) {
				if(isIndiv && canViewPatientNames())
					obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				else
					obj[0] = ", ";
				
				messages.put(patientId, ContextProvider.getMessage("patientalerts.alertInPMTCTLonger9Months", obj));
			}
		}		
		return messages;
	}
	
	/**
	 * Returns the alerts map for all TB infected patients with no TB treatment.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#TbPatientWithNoTreatment()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> tbPatientWithNoTreatment(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		
		String queryStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.SIMPLE_TB_TEST_RESULT + " AND o.value_coded= "+ Constants.POSITIVE +" AND p.patient_id NOT IN (SELECT person_id from obs WHERE concept_id = " + Constants.REASON_FOR_EXITING_CARE + ") WHERE p.patient_id NOT IN (SELECT patient_id FROM orders) AND p.voided=0 GROUP BY p.patient_id";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> patientIds = query.list();
		for (Integer patientId : patientIds) {
			Patient patient = patService.getPatient(patientId);
			if(isIndiv && canViewPatientNames())
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
			else
				obj[0] = ", ";
			
			messages.put(patientId, ContextProvider.getMessage("patientalerts.alertTbWithNoTreatment", obj));
		}		
		return messages;
	}
	
	/**
	 * Returns the alerts map for all HIV infected patients with no CD4 count.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#hivPatientWithNoCd4()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> hivPatientWithNoCd4(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		
		String queryStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.RESULT_OF_HIV_TEST + " AND o.value_coded= " + Constants.POSITIVE + " WHERE p.voided=0 AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.CD4_COUNT + " OR concept_id=1811) GROUP BY p.patient_id";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> patientIds = query.list();
		for (Integer patientId : patientIds) {
			Patient patient = patService.getPatient(patientId);
			if(isIndiv && canViewPatientNames())
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
			else
				obj[0] = ", ";
			messages.put(patientId, ContextProvider.getMessage("patientalerts.alertHivPatientWithNoCd4", obj));
		}
		return messages;
	}
	
	/**
	 * Returns the alerts map for all HIV Positive Women whose Children didn't have any HIV test in
	 * last 18 months.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#hivPosWomenChildrenWithNoTestIn18Months()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> hivPosWomenChildrenWithNoTestIn18Months(Boolean isIndiv) {
		PersonService perService = Context.getPersonService();
		PatientService patService = Context.getPatientService();
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		
		String queryStr = "SELECT r.person_b FROM obs o INNER JOIN relationship r ON o.person_id=r.person_a AND r.person_b NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.RESULT_OF_HIV_TEST + " AND datediff(curdate(), obs_datetime) < 540) WHERE o.concept_id= " + Constants.RESULT_OF_HIV_TEST + " AND o.value_coded= " + Constants.POSITIVE + " AND r.person_b NOT IN (SELECT person_id FROM obs WHERE concept_id =  " + Constants.REASON_FOR_EXITING_CARE + ") GROUP BY r.person_b";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> personIds = query.list();
		for (Integer personId : personIds) {
			Person person = perService.getPerson(personId);
			if (person.isPatient()) {
				Patient patient = patService.getPatient(personId);
				if (!patient.getVoided()) {
					if(isIndiv && canViewPatientNames())
						obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
					else
						obj[0] = ", ";
					
					messages.put(personId, ContextProvider.getMessage(
						    "patientalerts.alerthivPosWomenChildrensWithNoTestIn18Months", obj));
				}
			}
		}
		return messages;
	}
	
	/**
	 * Returns the alerts map for all patients with no next visit date.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientsWithNoNextVisitDate()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> patientsWithNoNextVisitDate(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		
		String queryStr = "SELECT patient_id FROM patient WHERE patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.RETURN_VISIT_DATE + " OR concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND voided=0 GROUP BY patient_id";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> patientIds = query.list();
		for (Integer patientId : patientIds) {
			Patient patient = patService.getPatient(patientId);
			if (!patient.getVoided()) {
				if(isIndiv && canViewPatientNames())
					obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				else
					obj[0] = ", ";
				
				messages.put(patientId, ContextProvider.getMessage("patientalerts.alertWithNoNextVisitDate", obj));
			}
		}
		return messages;
	}
	
	/**
	 * Returns the map of alerts for all HIV infected patients whose partners do not have any
	 * information about HIV test result.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientsWithNoPartnerInfo()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> patientsWithNoPartnerInfo(Boolean isIndiv) {
		PersonService perService = Context.getPersonService();
		PatientService patService = Context.getPatientService();
		List<Integer> personIds = null;
		Object[] obj = new Object[2];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		String queryStr = "SELECT pa.person_id FROM person_attribute pa LEFT JOIN obs o ON pa.person_id=o.person_id WHERE pa.person_id NOT IN (SELECT person_id FROM obs WHERE concept_id=3082 OR concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND person_attribute_type_id=5 AND (value= " + Constants.MARRIED + " OR value= " + Constants.LIVING_WITH_PARTNER + ") GROUP BY pa.person_id";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		personIds = query.list();
		
		for (Integer personId : personIds) {
			Person person = perService.getPerson(personId);
			
			if (person.isPatient()) {
				Patient patient = patService.getPatient(personId);
				try {
					if (!patient.getVoided()) {
						if(isIndiv && canViewPatientNames()) {
							obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name!")
							        + "</b>";
							obj[1] = (patient.getGender().equals("F") ? "her" : "his");
						} else {
							obj[0] = ", ";
							obj[1] = (patient.getGender().equals("F") ? "her" : "his");
						}
						
						messages.put(personId, ContextProvider.getMessage("patientalerts.alertWithNoPartnerInfo", obj));
					}
				}
				catch (Exception e) {}
				
			}
		}
		return messages;
	}
	
	/**
	 * Returns the map of alerts for all HIV infected patients who do not practice any family
	 * planning method.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#couplesWithNoFamilyPlanning()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> couplesWithNoFamilyPlanning(Boolean isIndiv) {
		PersonService persService = Context.getPersonService();
		
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		
		String queryStr = "SELECT pa.person_id FROM person_attribute pa WHERE pa.person_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.METHOD_OF_FAMILY_PLANNING + " OR concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND person_attribute_type_id= " + Constants.PERSON_ATTRIBUTE_TYPE_ID + " AND (value= " + Constants.MARRIED + " OR value= " + Constants.LIVING_WITH_PARTNER + ") GROUP BY pa.person_id";
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> personIds = query.list();
		for (Integer personId : personIds) {
			
			Person person = persService.getPerson(personId);
			if (person.isPatient()) {
				Patient patient = (Patient) person;
				try {
					if (!patient.getVoided()) {
						if(isIndiv && canViewPatientNames())
							obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
						else
							obj[0] = ", ";
						
						messages.put(personId, ContextProvider.getMessage("patientalerts.alertWithNoFamilyPlanning", obj));
					}
				}
				catch (Exception e) {
				}
			}
		}
		return messages;
	}
	
	/**
	 * Returns the alerts map for all STI infected patients with no treatment.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#sTIPatientWithNoTreatment()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> sTIPatientWithNoTreatment(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		Object[] obj = new Object[1];
		
		String queryStr = "SELECT p.patient_id FROM patient p LEFT JOIN obs o ON p.patient_id=o.person_id WHERE p.patient_id NOT IN (SELECT patient_id FROM orders) AND o.concept_id= " + Constants.OUT_PATIENT_DIAGNOSIS + " AND o.value_coded= " + Constants.STI + " AND p.voided=0 AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") GROUP BY p.patient_id";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> patientIds = query.list();
		for (Integer patientId : patientIds) {
			Patient patient = patService.getPatient(patientId);
			if(isIndiv && canViewPatientNames())
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
			else
				obj[0] = ", ";
			
			messages.put(patientId, ContextProvider.getMessage("patientalerts.alertWithSTIPatientWithNoTreatment", obj));
		}
		return messages;
	}
	
	/**
	 * Returns the alerts map for all patients who stopped drugs without any reason.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientsWhoStoppedDrugs()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings( { "unchecked" })
	public Map<Integer, String> patientsWhoStoppedDrugsWithNoReason(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Object[] obj = new Object[1];
		Session session = getSessionFactory().getCurrentSession();
		Map<Integer, String> messages = new HashMap<Integer, String>();
		
		String queryStr = "SELECT DISTINCT patient_id FROM orders WHERE discontinued=1 AND patient_id not in (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND discontinued_reason IS NULL";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Integer> patientIds = query.list();
		for (Integer patientId : patientIds) {
			Patient patient = patService.getPatient(patientId);
			if (!patient.getVoided()) {
				if(isIndiv && canViewPatientNames())
					obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				else
					obj[0] = ", ";
				
				messages.put(patientId, ContextProvider.getMessage("patientalerts.alertWithStoppedDrugsWithNoReason", obj));
			}
		}
		return messages;
	}
	
	/**
	 * Returns the alerts map for all HIV infected patients with a declining CD4 count.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientWithDecliningCD4()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> patientWithDecliningCD4(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		Session session = getSessionFactory().getCurrentSession();
		
		String queryStr = "SELECT * FROM ( SELECT p.patient_id, (SELECT value_numeric FROM obs WHERE concept_id = " + Constants.CD4_COUNT + " AND person_id = p.patient_id ORDER BY obs_datetime DESC LIMIT 1) as count1, (SELECT value_numeric FROM obs WHERE concept_id = " + Constants.CD4_COUNT + " AND person_id = p.patient_id ORDER BY obs_datetime DESC LIMIT 1,1) as count2 FROM patient p WHERE p.patient_id NOT IN (SELECT person_id from obs where concept_id= " + Constants.REASON_FOR_EXITING_CARE + ")) cd4 WHERE cd4.count1 < cd4.count2";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Object[]> records = query.list();
		for (Object[] object : records) {
			Patient patient = patService.getPatient((Integer) object[0]);
			if (!patient.getVoided()) {
				if(isIndiv && canViewPatientNames())
					obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				else
					obj[0] = ", ";
				
				messages.put((Integer) object[0], ContextProvider.getMessage("patientalerts.alertWithDecliningCD4", obj));
			}
		}
		return messages;
	}
	
	/**
	 * Returns the alerts map for all HIV infected patients without any CD4 test for last 6 months.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#patientWithNoCD4TestLast6Months()
	 * @return messages the alerts map matching the conditions.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> patientWithNoCD4TestLast6Months(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		Session session = getSessionFactory().getCurrentSession();
		Object[] obj = new Object[1];
		Map<Integer, String> messages = new HashMap<Integer, String>();
		
		String queryStr = "SELECT p.patient_id, MAX(o.obs_datetime) FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.obs_datetime IS NOT NULL WHERE p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + " OR (datediff(CURDATE(), obs_datetime) < 180) AND concept_id = " + Constants.CD4_COUNT + ") AND p.voided=0 GROUP BY p.patient_id";
		
		SQLQuery query = session.createSQLQuery(queryStr);
		List<Object[]> records = query.list();
		
		for (Object[] object : records) {
			Patient patient = patService.getPatient((Integer) object[0]);
			
			if(isIndiv && canViewPatientNames())
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
			else
				obj[0] = ", ";
			
			messages
				        .put((Integer) object[0], ContextProvider.getMessage("patientalerts.alertWithNoCD4TestLast6Months", obj));
		}
		return messages;
	}
	
	/**
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#outOfRangePatientLabResult()
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> outOfRangePatientLabResult(Boolean isIndiv) {
		PatientService patService = Context.getPatientService();
		int count = 0;
		Object[] obj = new Object[2];
		Session session = getSessionFactory().getCurrentSession();
		Map<Integer, String> messages = new HashMap<Integer, String>();
		String hbFStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.HEMOGLOBIN + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 123 AND 157 AND p.patient_id NOT IN (SELECT person_id FROM obs where concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") INNER JOIN person per ON p.patient_id=per.person_id AND per.gender='F' WHERE p.voided=0 GROUP BY p.patient_id";
		String hbMStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.HEMOGLOBIN + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 140 AND 174 AND p.patient_id NOT IN (SELECT person_id FROM obs where concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") INNER JOIN person per ON p.patient_id=per.person_id AND per.gender='M' WHERE p.voided=0 GROUP BY p.patient_id";
		String inrStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.INR + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 0.9 AND 1.2 AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND p.voided=0 GROUP BY p.patient_id";
		String hctFStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.HEMATOCRIT + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 0.370 AND 0.460 AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") INNER JOIN person per ON p.patient_id=per.person_id AND per.gender='F' AND p.voided=0 GROUP BY p.patient_id";
		String hctMStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.HEMATOCRIT + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 0.420 AND 0.5200 AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") INNER JOIN person per ON p.patient_id=per.person_id AND per.gender='M' WHERE p.voided=0 GROUP BY p.patient_id";
		String pltStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.PLATELETS + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 130 AND (400*1000000000) AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND p.voided=0 GROUP BY p.patient_id";
		String astStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.ALKALINE_PHOSPHATASE + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 0 AND 35 AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND p.voided=0 GROUP BY p.patient_id";
		String altStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o on p.patient_id=o.person_id AND o.concept_id= " + Constants.ALT + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 3 AND 36 AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND p.voided=0 GROUP BY p.patient_id";
		String lymphoStr = "SELECT p.patient_id FROM obs o INNER JOIN patient p ON o.person_id=p.patient_id AND  o.concept_id= " + Constants.LYMPHOCYTES + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 1.5 AND (3.4*1000000000) AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND p.voided=0 GROUP BY p.patient_id";
		String ckStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.CREATINE_KINASE + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 5 AND 130 AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND p.voided=0 GROUP BY p.patient_id";
		String gluStr = "SELECT p.patient_id FROM patient p INNER JOIN obs o ON p.patient_id=o.person_id AND o.concept_id= " + Constants.SERUM_GLUCOSE + " AND o.voided=0 AND o.value_numeric NOT BETWEEN 2 AND 4 AND p.patient_id NOT IN (SELECT person_id FROM obs WHERE concept_id= " + Constants.REASON_FOR_EXITING_CARE + ") AND p.voided=0 GROUP BY p.patient_id";
		
		//Patients with Glucose out of range lab result
		SQLQuery gluquery = session.createSQLQuery(gluStr);
		List<Integer> gluRecords = gluquery.list();
		for (Integer patId : gluRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Glucose";
			} else {
				obj[0] = ", ";
				obj[1] = "Glucose";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		
		//Patients with Creatine out of range lab result
		SQLQuery ckquery = session.createSQLQuery(ckStr);
		List<Integer> ckRecords = ckquery.list();
		
		for (Integer patId : ckRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Creatine Kinase";
			} else {
				obj[0] = ", ";
				obj[1] = "Creatine Kinase";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));			
		}
		
		//Patients with Alanine(SGPT, ALT) out of range lab result
		SQLQuery altquery = session.createSQLQuery(altStr);
		List<Integer> altRecords = altquery.list();
		
		for (Integer patId : altRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Alanine";
			} else {
				obj[0] = ", ";
				obj[1] = "Alanine";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		
		//Patients with Aspartate(SGOT, AST) out of range lab result
		SQLQuery astquery = session.createSQLQuery(astStr);
		List<Integer> astRecords = astquery.list();
		
		for (Integer patId : astRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Aspartate";
			} else {
				obj[0] = ", ";
				obj[1] = "Aspartate";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		
		//Patients with Platelet Count out of range lab result
		SQLQuery pltquery = session.createSQLQuery(pltStr);
		List<Integer> pltRecords = pltquery.list();
		
		for (Integer patId : pltRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Platelet Count";
			} else {
				obj[0] = ", ";
				obj[1] = "Platelet Count";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		
		//Patients with Lympho out of range lab result
		SQLQuery lymphoquery = session.createSQLQuery(lymphoStr);
		List<Integer> lymphoRecords = lymphoquery.list();
		
		for (Integer patId : lymphoRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Lympho";
			} else {
				obj[0] = ", ";
				obj[1] = "Lympho";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		
		//Male patients with hemoglobin out of range lab result
		SQLQuery hbMquery = session.createSQLQuery(hbMStr);
		List<Integer> hbMRecords = hbMquery.list();
		
		for (Integer patId : hbMRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Hemoglobin";
			} else {
				obj[0] = ", ";
				obj[1] = "Hemoglobin";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		
		//Female patients with hemoglobin out of range lab result
		SQLQuery hbFquery = session.createSQLQuery(hbFStr);
		List<Integer> hbFRecords = hbFquery.list();
		
		for (Integer patId : hbFRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Hemoglobin";
			} else {
				obj[0] = ", ";
				obj[1] = "Hemoglobin";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		
		//Patients with International Normalized Ratio (INR) out of range lab results
		SQLQuery inrquery = session.createSQLQuery(inrStr);
		List<Integer> inrRecords = inrquery.list();
		
		for (Integer patId : inrRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "INR";
			} else {
				obj[0] = ", ";
				obj[1] = "INR";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		
		//Female patients with Hematrocrit (Hct) out of range lab results
		SQLQuery hctFquery = session.createSQLQuery(hctFStr);
		List<Integer> hctFRecords = hctFquery.list();
		
		for (Integer patId : hctFRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Hematrocrit";
			} else {
				obj[0] = ", ";
				obj[1] = "Hematrocrit";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		
		//Male patients with Hematrocrit (Hct) out of range lab results
		SQLQuery hctMquery = session.createSQLQuery(hctMStr);
		List<Integer> hctMRecords = hctMquery.list();
		
		for (Integer patId : hctMRecords) {
			count++;
			Patient patient = patService.getPatient(patId);
			if(isIndiv && canViewPatientNames()) {
				obj[0] = "<b>" + (patient.getPersonName() != null ? patient.getPersonName().toString() : "No name") + "</b>";
				obj[1] = "Hematrocrit";
			} else {
				obj[0] = ", ";
				obj[1] = "Hematrocrit";
			}
			messages.put(patId, ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResult", obj));
		}
		return messages;
	}
	
	/**
	 * Returns a list of all alerts related to a given patient.
	 * 
	 * @see org.openmrs.module.patientalerts.service.PatientAlertsService#getAlertsForPatient(org.openmrs.Patient)
	 * @param patient the patient to be matched.
	 * @return messages the list of all existing alerts matching the given patient.
	 */
	public List<String> getAlertsForPatient(Patient patient, Boolean isIndiv) {
		
		List<String> messages = new ArrayList<String>();
		Map<Integer, String> patientsWithLowCD4NoMedication = patientsWithLowCD4NoMedication(isIndiv);
		Map<Integer, String> patientsWithNoEncounterLast3Months = patientsWithNoEncounterLast3Months(isIndiv);
		Map<Integer, String> malePatientInPMTCT = malePatientInPMTCT(isIndiv);
		Map<Integer, String> patientInPMTCTLonger9Months = patientInPMTCTLonger9Months(isIndiv);
		Map<Integer, String> hivPatientWithNoCd4 = hivPatientWithNoCd4(isIndiv);
		Map<Integer, String> patientsWithNoNextVisitDate = patientsWithNoNextVisitDate(isIndiv);
		Map<Integer, String> patientsWithNoPartnerInfo = patientsWithNoPartnerInfo(isIndiv);
		Map<Integer, String> patientWithDecliningCD4 = patientWithDecliningCD4(isIndiv);
		Map<Integer, String> patientWithNoCD4TestLast6Months = patientWithNoCD4TestLast6Months(isIndiv);
		Map<Integer, String> couplesWithNoFamilyPlanning = couplesWithNoFamilyPlanning(isIndiv);
		Map<Integer, String> patientsWhoStoppedDrugsWithNoReason = patientsWhoStoppedDrugsWithNoReason(isIndiv);
		Map<Integer, String> tbPatientWithNoTreatment = tbPatientWithNoTreatment(isIndiv);
		Map<Integer, String> sTIPatientWithNoTreatment = sTIPatientWithNoTreatment(isIndiv);
		Map<Integer, String> hivPosWomenChildrenWithNoTestIn18Months = hivPosWomenChildrenWithNoTestIn18Months(isIndiv);
		Map<Integer, String> outOfRangePatientLabResult = outOfRangePatientLabResult(isIndiv);
		
		if (patientsWithLowCD4NoMedication != null)
			for (Integer patientId : patientsWithLowCD4NoMedication.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(patientsWithLowCD4NoMedication.get(patientId));
					break;
				}
		
		if (couplesWithNoFamilyPlanning != null)
			for (Integer patientId : couplesWithNoFamilyPlanning.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(couplesWithNoFamilyPlanning.get(patientId));
					break;
				}
		
		if (hivPosWomenChildrenWithNoTestIn18Months != null)
			for (Integer patientId : hivPosWomenChildrenWithNoTestIn18Months.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(hivPosWomenChildrenWithNoTestIn18Months.get(patientId));
					break;
				}
		
		if (tbPatientWithNoTreatment != null)
			for (Integer patientId : tbPatientWithNoTreatment.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(tbPatientWithNoTreatment.get(patientId));
					break;
				}
		
		if (patientsWhoStoppedDrugsWithNoReason != null)
			for (Integer patientId : patientsWhoStoppedDrugsWithNoReason.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(patientsWhoStoppedDrugsWithNoReason.get(patientId));
					break;
				}
		
		if (patientWithNoCD4TestLast6Months != null)
			for (Integer patientId : patientWithNoCD4TestLast6Months.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(patientWithNoCD4TestLast6Months.get(patientId));
					break;
				}
		
		if (patientsWithNoEncounterLast3Months != null) {
			for (Integer patientId : patientsWithNoEncounterLast3Months.keySet()) {
				if (patientId.equals(patient.getPatientId())) {
					messages.add(patientsWithNoEncounterLast3Months.get(patientId).toString());
					break;
				}
			}
		}
		
		if (malePatientInPMTCT != null)
			for (Integer patientId : malePatientInPMTCT.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(malePatientInPMTCT.get(patientId));
					break;
				}
		
		if (patientInPMTCTLonger9Months != null)
			for (Integer patientId : patientInPMTCTLonger9Months.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(patientInPMTCTLonger9Months.get(patientId));
					break;
				}
		
		if (hivPatientWithNoCd4 != null)
			for (Integer patientId : hivPatientWithNoCd4.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(hivPatientWithNoCd4.get(patientId));
					break;
				}
		
		if (patientsWithNoNextVisitDate != null)
			for (Integer patientId : patientsWithNoNextVisitDate.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(patientsWithNoNextVisitDate.get(patientId));
					break;
				}
		
		if (patientsWithNoPartnerInfo != null)
			for (Integer patientId : patientsWithNoPartnerInfo.keySet()) {
				Person person = Context.getPersonService().getPerson(patientId);
				if (person.isPatient()) {
					if (patientId.equals(patient.getPatientId())) {
						messages.add(patientsWithNoPartnerInfo.get(patientId));
						break;
					}
				}
			}
		
		if (patientWithDecliningCD4 != null)
			for (Integer patientId : patientWithDecliningCD4.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(patientWithDecliningCD4.get(patientId));
					break;
				}
		
		if (sTIPatientWithNoTreatment != null)
			for (Integer patientId : sTIPatientWithNoTreatment.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(sTIPatientWithNoTreatment.get(patientId));
					break;
				}
		
		if (outOfRangePatientLabResult != null)
			for (Integer patientId : outOfRangePatientLabResult.keySet())
				if (patientId.equals(patient.getPatientId())) {
					messages.add(outOfRangePatientLabResult.get(patientId));
					break;
				}
		
		return messages;
	}

}
