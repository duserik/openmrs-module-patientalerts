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
package org.openmrs.module.patientalerts.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientalerts.impl.AlertsUtils;
import org.openmrs.module.patientalerts.service.PatientAlertsService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 *
 */
public class AlertsViewController extends ParameterizableViewController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(AlertsViewController.class);
	
	/**
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
						
		ModelAndView mav = new ModelAndView();
		PatientAlertsService service = Context.getService(PatientAlertsService.class);
		
		Map<String, Map<Integer, String>> allInOne = new HashMap<String, Map<Integer, String>>();
		
		allInOne.put(AlertsUtils.LOW_CD4_NO_MEDICATION, service.patientsWithLowCD4NoMedication(true));
		allInOne.put(AlertsUtils.MALE_PMTCT, service.malePatientInPMTCT(true));
		allInOne.put(AlertsUtils.IN_PMTCT_9MONTHS, service.patientInPMTCTLonger9Months(true));
		allInOne.put(AlertsUtils.NO_ENCOUNTER_LAST_3MONTHS, service.patientsWithNoEncounterLast3Months(true));
		allInOne.put(AlertsUtils.NO_CD4, service.hivPatientWithNoCd4(true));
		allInOne.put(AlertsUtils.NO_NEXT_VISITE_DATE, service.patientsWithNoNextVisitDate(true));
		allInOne.put(AlertsUtils.NO_PARTNER_INFO, service.patientsWithNoPartnerInfo(true));
		allInOne.put(AlertsUtils.DECLINE_CD4, service.patientWithDecliningCD4(true));
		allInOne.put(AlertsUtils.NO_CD4_LAST_6MONTHS, service.patientWithNoCD4TestLast6Months(true));
		allInOne.put(AlertsUtils.NO_FAM_PLANNING, service.couplesWithNoFamilyPlanning(true));
		allInOne.put(AlertsUtils.STOPPED_DRUG_NO_REASON, service.patientsWhoStoppedDrugsWithNoReason(true));
		allInOne.put(AlertsUtils.TB_NO_TREATMENT, service.tbPatientWithNoTreatment(true));
		allInOne.put(AlertsUtils.STI_NO_TREATMENT, service.sTIPatientWithNoTreatment(true));
		allInOne.put(AlertsUtils.POS_WOMEN_NO_TEST_IN18MONTHS, service.hivPosWomenChildrenWithNoTestIn18Months(true));
		allInOne.put(AlertsUtils.UBNORMAL_LAB_RESULT, service.outOfRangePatientLabResult(true));
		
		mav.addObject("allInOne", allInOne);		
		
		mav.setViewName(getViewName());
		
		return mav;
	}
	
}
