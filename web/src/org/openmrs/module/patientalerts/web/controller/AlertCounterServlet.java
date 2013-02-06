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

import javax.servlet.http.HttpServlet;

import org.openmrs.module.patientalerts.AlertModuleActivator;

/**
 * A dummy servlet class to call the module activator's load method
 */
public class AlertCounterServlet extends HttpServlet {

	private static final long serialVersionUID = 2990051975359480523L;

	/**
	 * A shameless hack to get around the lack of an activator event
	 * called after the application context refresh
	 */
	public AlertCounterServlet() {
		AlertModuleActivator.getInstance().load();
	}
}
