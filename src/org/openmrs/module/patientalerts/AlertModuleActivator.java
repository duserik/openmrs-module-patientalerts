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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.patientalerts.impl.AlertsUtils;
import org.openmrs.scheduler.TaskDefinition;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class AlertModuleActivator implements Activator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting Alert Module");
		
	}
	
	/**
	 * Not called automatically by the OpenMRS framework, so needs
	 * to be manually called from one of the following places:
	 *   1. An advice point class constructor
	 *   2. A servlet class constructor
	 *  as these will be invoked after the context refresh
	 *   
	 *  In this module its called from TimeChartServlet
	 *  @see org.openmrs.module.AlertCounterServlet.web.servlet.StubServlet#StubServlet()
	 */
	public void load() {
		log.info("Alert module loaded");
		
		registerAlertCountTask();
	}
	
	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down Alert Module");
		
		unregisterAlertCountTask();
	}
	
	/**
	 * Provides a convenient way of calling load() from another
	 * class, e.g. 
	 * <code>UsageStatsActivator.getInstance().load();</code>
	 * @return the instance of this activator created by OpenMRS
	 */
	public static AlertModuleActivator getInstance() {
		return (AlertModuleActivator)ModuleFactory.getModuleById("patientalerts").getActivator();
	}

	/**
	 * Registers the aggregation task if it isn't already registered
	 */
	public boolean registerAlertCountTask() {
		try {
			Context.addProxyPrivilege("Manage Scheduler");

			TaskDefinition task = Context.getSchedulerService().getTaskByName("Alert Counter Scheduler");
			if (task == null) {
				Date start = AlertsUtils.getPreviousMidnight(null);

				task = new TaskDefinition();
				task.setTaskClass(PatientAlertTask.class.getCanonicalName());
				task.setRepeatInterval(60 * 10l); // Hourly
				task.setStartOnStartup(true);
				task.setStartTime(start);
				task.setName("Alert Counter Scheduler");
				task.setDescription("Deletes or aggregates old alert data");
				Context.getSchedulerService().scheduleTask(task);

				log.info("Registered alert count task with scheduler");
			} else {
				log.info("Alert count task already registered with scheduler");
				return false;
			}

		} catch (Exception ex) {
			log.warn("Unable to register alert count task with scheduler", ex);
			return false;
		} finally {
			Context.removeProxyPrivilege("Manage Scheduler");
		}
		return true;
	}

	/**
	 * Unregisters the aggregation task if it exists
	 */
	private void unregisterAlertCountTask() {
		TaskDefinition task = Context.getSchedulerService().getTaskByName("Alert Counter Scheduler");

		if (task != null)
			Context.getSchedulerService().deleteTask(task.getId());
	}

	
}
