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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientalerts.service.PatientAlertsService;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants;


/**
 *
 */
public class PatientAlertTask extends AbstractTask {
	
private static Log log = LogFactory.getLog(PatientAlertTask.class);
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		execute(true);
	}
	
	/**
	 * Executes this data aggregation task
	 * @param newSession true to create a new OpenMRS session
	 */
	public void execute(boolean newSession) {
		if (!isExecuting) {
            isExecuting = true;
            
            if (newSession)
            	Context.openSession();
            
			try {
				if (Context.isAuthenticated() == false)
					authenticate();
				
				doAlertCount();
				
			} catch (NumberFormatException ex) {
				log.error("Invalid value for global property " + Constants.ABSTINENCE);
				throw ex;
			} catch (APIException ex) {
				log.error("Error processing usage statistics", ex);
				throw ex;
			} finally {
				if (newSession)
					Context.closeSession();
				isExecuting = false;
			}
		}
	}

	/**
	 * Does the actual alert count
	 */
	private void doAlertCount() {
		Context.addProxyPrivilege("View Patient Alerts");
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTERS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_GLOBAL_PROPERTIES);
		
		PatientAlertsService patientSrv = Context.getService(PatientAlertsService.class);
		int numAlerts = patientSrv.getNumberOfAlerts(true);
		
		AdministrationService adminSvc = Context.getAdministrationService();
		
		GlobalProperty prop = adminSvc.getGlobalPropertyObject("patientalerts.alertcount");	
		if (prop == null) 
			prop = new GlobalProperty("patientalerts.alertcount");
		
		prop.setPropertyValue(numAlerts + "");
		
		adminSvc.saveGlobalProperty(prop);
		
		Context.removeProxyPrivilege("View Patient Alerts");
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTERS);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_GLOBAL_PROPERTIES);

		log.info("Found " + numAlerts + " alerts");
	}
}
