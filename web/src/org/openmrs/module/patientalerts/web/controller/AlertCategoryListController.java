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
import org.openmrs.module.patientalerts.service.ContextProvider;
import org.openmrs.module.patientalerts.service.PatientAlertsService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 *
 */
public class AlertCategoryListController extends ParameterizableViewController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(AlertsViewController.class);
	
	/**
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Auto-generated method stub
		
		Map<String, Map<Integer, String>> categoryList = new HashMap<String, Map<Integer, String>>();
		
		ModelAndView mav = new ModelAndView();
		PatientAlertsService service = Context.getService(PatientAlertsService.class);
		
		if (service.patientsWithLowCD4NoMedication(true) != null && service.patientsWithLowCD4NoMedication(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertLowCD4NoMedicationCategory"), service
			        .patientsWithLowCD4NoMedication(true));
		}
		
		if (service.malePatientInPMTCT(true) != null && service.malePatientInPMTCT(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertMaleInPMTCTCategory"), service
			        .malePatientInPMTCT(true));
		}
		
		if (service.patientInPMTCTLonger9Months(true) != null && service.patientInPMTCTLonger9Months(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertInPMTCTLonger9MonthsCategory"), service
			        .patientInPMTCTLonger9Months(true));
		}
		
		if (service.patientsWithNoEncounterLast3Months(true) != null && service.patientsWithNoEncounterLast3Months(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertWithNoEncounterLast3MonthsCategory"), service
			        .patientsWithNoEncounterLast3Months(true));
		}
		
		if (service.hivPatientWithNoCd4(true) != null && service.hivPatientWithNoCd4(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertHivPatientWithNoCd4Category"), service
			        .hivPatientWithNoCd4(true));
		}
		
		if (service.patientsWithNoNextVisitDate(true) != null && service.patientsWithNoNextVisitDate(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertWithNoNextVisitDateCategory"), service
			        .patientsWithNoNextVisitDate(true));
		}
		
		if (service.patientsWithNoPartnerInfo(true) != null && service.patientsWithNoPartnerInfo(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertWithNoPartnerInfoCategory"), service
			        .patientsWithNoPartnerInfo(true));
		}
		
		if (service.patientWithDecliningCD4(true) != null && service.patientWithDecliningCD4(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertWithDecliningCD4Category"), service
			        .patientWithDecliningCD4(true));
		}
		
		if (service.patientWithNoCD4TestLast6Months(true) != null && service.patientWithNoCD4TestLast6Months(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertWithNoCD4TestLast6MonthsCategory"), service
			        .patientWithNoCD4TestLast6Months(true));
		}
		
		if (service.couplesWithNoFamilyPlanning(true) != null && service.couplesWithNoFamilyPlanning(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertWithNoFamilyPlanningCategory"), service
			        .couplesWithNoFamilyPlanning(true));
		}
		
		if (service.patientsWhoStoppedDrugsWithNoReason(true) != null
		        && service.patientsWhoStoppedDrugsWithNoReason(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertWithStoppedDrugsWithNoReasonCategory"), service
			        .patientsWhoStoppedDrugsWithNoReason(true));
		}
		
		if (service.tbPatientWithNoTreatment(true) != null && service.tbPatientWithNoTreatment(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertTbWithNoTreatmentCategory"), service
			        .tbPatientWithNoTreatment(true));
		}
		
		if (service.sTIPatientWithNoTreatment(true) != null && service.sTIPatientWithNoTreatment(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertWithSTIPatientWithNoTreatmentCategory"), service
			        .sTIPatientWithNoTreatment(true));
		}
		
		if (service.hivPosWomenChildrenWithNoTestIn18Months(true) != null
		        && service.hivPosWomenChildrenWithNoTestIn18Months(true).size() > 0) {
			categoryList.put(ContextProvider
			        .getMessage("patientalerts.alerthivPosWomenChildrensWithNoTestIn18MonthsCategory"), service
			        .hivPosWomenChildrenWithNoTestIn18Months(true));
		}
		
		if (service.outOfRangePatientLabResult(true) != null && service.outOfRangePatientLabResult(true).size() > 0) {
			categoryList.put(ContextProvider.getMessage("patientalerts.alertWithOutOfRangeLabResultCategory"), service
			        .outOfRangePatientLabResult(true));
		}
		
		mav.setViewName(getViewName());
		mav.addObject("alertsCategoryList", categoryList);
				
		return mav;
	}
}
