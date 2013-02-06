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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 *
 */
public class AlertsCategoryViewController extends ParameterizableViewController {
	
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
		String alertCategory = ServletRequestUtils.getRequiredStringParameter(request, "alertCategory");
		Map<Integer, String> alerts = new HashMap<Integer, String>();
		
		if (alertCategory.equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertLowCD4NoMedicationCategory")))
			alerts.putAll(service.patientsWithLowCD4NoMedication(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertMaleInPMTCTCategory")))
			alerts.putAll(service.malePatientInPMTCT(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertInPMTCTLonger9MonthsCategory")))
			alerts.putAll(service.patientInPMTCTLonger9Months(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider
		        .getMessage("patientalerts.alertWithNoEncounterLast3MonthsCategory")))
			alerts.putAll(service.patientsWithNoEncounterLast3Months(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertHivPatientWithNoCd4Category")))
			alerts.putAll(service.hivPatientWithNoCd4(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertWithNoNextVisitDateCategory")))
			alerts.putAll(service.patientsWithNoNextVisitDate(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertWithNoPartnerInfoCategory")))
			alerts.putAll(service.patientsWithNoPartnerInfo(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertWithDecliningCD4Category")))
			alerts.putAll(service.patientWithDecliningCD4(true));
		
		if (alertCategory
		        .equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertWithNoCD4TestLast6MonthsCategory")))
			alerts.putAll(service.patientWithNoCD4TestLast6Months(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertWithNoFamilyPlanningCategory")))
			alerts.putAll(service.couplesWithNoFamilyPlanning(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider
		        .getMessage("patientalerts.alertWithStoppedDrugsWithNoReasonCategory")))
			alerts.putAll(service.patientsWhoStoppedDrugsWithNoReason(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider.getMessage("patientalerts.alertTbWithNoTreatmentCategory")))
			alerts.putAll(service.tbPatientWithNoTreatment(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider
		        .getMessage("patientalerts.alertWithSTIPatientWithNoTreatmentCategory")))
			alerts.putAll(service.sTIPatientWithNoTreatment(true));
		
		if (alertCategory.equalsIgnoreCase(ContextProvider
	        .getMessage("patientalerts.alerthivPosWomenChildrensWithNoTestIn18MonthsCategory")))
		alerts.putAll(service.hivPosWomenChildrenWithNoTestIn18Months(true));

		if (alertCategory.equalsIgnoreCase(ContextProvider
	        .getMessage("patientalerts.alertWithOutOfRangeLabResultCategory")))
		alerts.putAll(service.outOfRangePatientLabResult(true));

		mav.setViewName(getViewName());
		mav.addObject("alertsByCategory", alerts);
		mav.addObject ("alertCategory", alertCategory);
		
				
		return mav;
	}
	
}
