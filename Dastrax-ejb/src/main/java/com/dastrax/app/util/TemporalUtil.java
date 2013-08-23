/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */
package com.dastrax.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @version Build 2.0.0
 * @since Jul 12, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class TemporalUtil {

    // Constructors-------------------------------------------------------------
    public TemporalUtil() {
    }

    // Methods------------------------------------------------------------------
    /**
     * 
     * @param event
     * @return date in "MM/dd/YYYY" format
     */
    public String epochDateFmt(Long event) {
        if (event != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/YYYY");
            return simpleDateFormat.format(cal.getTime());
        } else {
            return "Not Available";
        }
    }

    /**
     * 
     * @param event
     * @return time in "HH:mm" format
     */
    public String epochTimeFmt(Long event) {
        if (event != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            return simpleDateFormat.format(cal.getTime());
        } else {
            return "Not Available";
        }
    }

    /**
     * 
     * @param event
     * @return date and time in "MM/dd/YYYY HH:mm" format
     */
    public String epochDateTimeFmt(Long event) {
        if (event != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/YYYY HH:mm");
            return simpleDateFormat.format(cal.getTime());
        } else {
            return "Not Available";
        }
    }

    /**
     * Convert Long epoch to Date 
     * @param event
     * @return 
     */
    public Date epochToDate(Long event) {
        if (event != null) {
            return new Date(event);
        }
        return null;
    }

    /**
     * Convert Date to Long epoch
     * @param event
     * @return 
     */
    public Long dateToEpoch(Date event) {
        return event.getTime();
    }
    
    /**
     * Convert Long epoch to Calendar
     * @param event
     * @return 
     */
    public Calendar epochToCal(Long event) {
        if (event != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return cal;
        }
        return null;
    }

    /**
     * Convert Calendar to Long
     * @param event
     * @return 
     */
    public Long calToEpoch(Calendar event) {
        return event.getTimeInMillis();
    }
}
