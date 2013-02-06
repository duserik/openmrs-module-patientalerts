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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientalerts.service.PatientAlertsService;
import org.openmrs.web.controller.PortletController;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

/**
 *
 */
public class PatientAlertDashboardController extends PortletController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(AlertsViewController.class);
	
	/**
	 * Retrieves the Patient, and loads all his/her alerts.
	 * 
	 * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.HttpServletRequest,
	 *      java.util.Map)
	 */
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		PatientService patService = Context.getPatientService();
		PatientAlertsService patAlertService = Context.getService(PatientAlertsService.class);
		try {
			int patientId = ServletRequestUtils.getRequiredIntParameter(request, "patientId");
			List<String> alertsMessagesForPatient = new ArrayList<String>();
			Map<Integer, String> alerts = new HashMap<Integer, String>();
			
			alertsMessagesForPatient.addAll(patAlertService.getAlertsForPatient(
			    patService.getPatient(patientId), false));
			
			int i = 0;
			for (String alert : alertsMessagesForPatient) {
				i += 1;
				alerts.put(i, alert);
			}
			model.put("alertsForPatient", alerts);
		}
		catch (ServletRequestBindingException e) {
			log.error("Error generated", e);
		}
		
		super.populateModel(request, model);
	}
}
