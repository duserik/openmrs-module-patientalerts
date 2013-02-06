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

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientalerts.Constants;
import org.openmrs.module.web.extension.LinkExt;

/**
 * Adds the Alerts (X) link to the gutter
 */
public class GutterLinkForAlerts extends LinkExt {
	
	/**
	 * @see org.openmrs.module.web.extension.LinkExt#getLabel()
	 */
	@Override
	public String getLabel() {	
		AdministrationService adminSvc = Context.getAdministrationService();
		String propVal = adminSvc.getGlobalProperty(Constants.PROP_ALERTCOUNT);
		Integer count = null;
		
		if (propVal != null) {
			try {
				count = Integer.parseInt(propVal);
			}
			catch (NumberFormatException ex) {}
		}
		
		if (count == null || count == 0)
			return "Alerts";
		else
			return "Alerts (" + count + ")";
	}
	
	/**
	 * @see org.openmrs.module.web.extension.LinkExt#getRequiredPrivilege()
	 */
	@Override
	public String getRequiredPrivilege() {
		return Constants.PRIV_VIEWPATIENTALERTS;
	}
	
	/**
	 * @see org.openmrs.module.web.extension.LinkExt#getUrl()
	 */
	@Override
	public String getUrl() {
		return "module/patientalerts/alertCategory.list";
	}
	
}
