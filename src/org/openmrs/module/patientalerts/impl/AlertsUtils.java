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
package org.openmrs.module.patientalerts.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientalerts.UsageFilter;

public class AlertsUtils {
	
	public static final String LOW_CD4_NO_MEDICATION = "patientsWithLowCD4NoMedication";
	public static final String NO_ENCOUNTER_LAST_3MONTHS = "patientsWithNoEncounterLast3Months";
	public static final String MALE_PMTCT = "malePatientInPMTCT";
	public static final String IN_PMTCT_9MONTHS = "patientInPMTCTLonger9Months";
	public static final String NO_CD4 = "hivPatientWithNoCd4";
	public static final String NO_NEXT_VISITE_DATE = "patientsWithNoNextVisitDate";
	public static final String NO_CD4_LAST_6MONTHS = "patientWithNoCD4TestLast6Months";
	public static final String TB_NO_TREATMENT = "tbPatientWithNoTreatment";
	public static final String POS_WOMEN_NO_TEST_IN18MONTHS = "hivPosWomenChildrenWithNoTestIn18Months";
	public static final String NO_PARTNER_INFO = "patientsWithNoPartnerInfo";
	public static final String NO_FAM_PLANNING = "couplesWithNoFamilyPlanning";
	public static final String STI_NO_TREATMENT = "sTIPatientWithNoTreatment";
	public static final String DECLINE_CD4 = "patientWithDecliningCD4";
	public static final String STOPPED_DRUG_NO_REASON = "patientsWhoStoppedDrugsWithNoReason";
	public static final String UBNORMAL_LAB_RESULT = "outOfRangePatientLabResult";
	
	protected static final Log log = LogFactory.getLog(AlertsUtils.class);
	
	/**
	 * Utility method to get a parsed usage filter parameter
	 * @param request the HTTP request object
	 * @param name the name of the usage filter parameter
	 * @param def the default value if parameter doesn't exist or is invalid
	 * @return the filter value
	 */
	public static UsageFilter getUsageFilterParameter(HttpServletRequest request, String name, UsageFilter def) {
		String str = request.getParameter(name);
		if (str != null) {
			try {
				int i = Integer.parseInt(str);
				return UsageFilter.values()[i];
			} catch (Exception ex) {
				log.warn("Invalid usage filter value: " + str);
			}
		}
		return def;
	}
	
	/**
	 * Utility method to get a parsed date parameter
	 * @param request the HTTP request object
	 * @param name the name of the date parameter
	 * @param def the default value if parameter doesn't exist or is invalid
	 * @return the date
	 */
	public static Date getDateParameter(HttpServletRequest request, String name, Date def) {
		String str = request.getParameter(name);
		if (str != null) {
			try {
				return Context.getDateFormat().parse(str);
			} catch (Exception ex) {
				log.warn("Invalid date format: " + str);
			}
		}
		return def;
	}
	
	/**
	 * Utility method to add days to an existing date
	 * @param date (may be null to use today's date)
	 * @param days the number of days to add (negative to subtract days)
	 * @return the new date
	 */
	public static Date addDaysToDate(Date date, int days) {
		// Initialize with date if it was specified
		Calendar cal = new GregorianCalendar();
		if (date != null)
			cal.setTime(date);
		
		cal.add(Calendar.DAY_OF_WEEK, days);
		return cal.getTime();
	}
	
	/**
	 * Utility method to add seconds to an existing date
	 * @param date (may be null to use today's date)
	 * @param seconds the number of seconds to add (negative to subtract seconds)
	 * @return the new date
	 */
	public static Date addSecondsToDate(Date date, int seconds) {
		// Initialize with date if it was specified
		Calendar cal = new GregorianCalendar();
		if (date != null)
			cal.setTime(date);
		
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}
	
	/**
     * Calculates the time of the last midnight before an existing date
     * @param date (may be null to use today's date)
     * @return the new date
     */
    public static Date getPreviousMidnight(Date date) {
    	// Initialize with date if it was specified
    	Calendar cal = new GregorianCalendar();
		if (date != null)
			cal.setTime(date);
		  
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
    }
}
