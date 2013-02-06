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
package org.openmrs.module.patientalerts.extension.html;

import org.openmrs.module.web.extension.PatientDashboardTabExt;


/**
 *
 */
public class PatientDashboardForAlerts extends PatientDashboardTabExt {
	
	/**
	 * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getPortletUrl()
	 */
	@Override
	public String getPortletUrl() {
		return "alertsByPatientView";
	}
	
	/**
	 * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getRequiredPrivilege()
	 */
	@Override
	public String getRequiredPrivilege() {
		return "View Alerts";
	}
	
	/**
	 * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getTabId()
	 */
	@Override
	public String getTabId() {
		return "Dus&KamModule";
	}
	
	/**
	 * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getTabName()
	 */
	@Override
	public String getTabName() {
		return "patientalerts.alertGutterTitle";
	}
	
}
