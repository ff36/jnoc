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
    public String epochDateFmt(Long event) {
        if (event != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
            return simpleDateFormat.format(cal.getTime());
        } else {
            return "Not Available";
        }
    }

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

    public String epochDateTimeFmt(Long event) {
        if (event != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");
            return simpleDateFormat.format(cal.getTime());
        } else {
            return "Not Available";
        }
    }

    public Date epochToDate(Long event) {
        if (event != null) {
            return new Date(event);
        }
        return null;
    }

    public Long dateToEpoch(Date event) {
        return event.getTime();
    }
    
    public Calendar epochToCal(Long event) {
        if (event != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return cal;
        }
        return null;
    }

    public Long calToEpoch(Calendar event) {
        return event.getTimeInMillis();
    }
}
