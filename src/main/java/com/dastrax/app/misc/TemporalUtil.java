/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.misc;

import com.dastrax.per.project.DTX;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Temporal conversion utility to handle transitions from one temporal format to 
 * another.
 * 
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class TemporalUtil {

    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a string format.
     * 
     * @param event
     * @return string representation of the date using the system format
     */
    public static String epochToStringDate(Long event) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return new SimpleDateFormat(
                    DTX.TemporalFormat.DATE_FORMAT.getValue())
                    .format(cal.getTime());
    }

    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a string format.
     * 
     * @param event
     * @return string representation of the date using the system format
     */
    public static String epochToStringTime(Long event) {
        Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return new SimpleDateFormat(
                    DTX.TemporalFormat.TIME_FORMAT.getValue())
                    .format(cal.getTime());
    }

    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a string format.
     * 
     * @param event
     * @return string representation of the date using the system format
     */
    public static String epochToStringDateTime(Long event) {
        Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return new SimpleDateFormat(
                    DTX.TemporalFormat.DATE_TIME_FORMAT.getValue())
                    .format(cal.getTime());
    }

    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a Calendar.
     *
     * @param event
     * @return Calendar representation of the long
     */
    public static Calendar epochToCalendar(Long event) {
        if (event != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return cal;
        }
        return null;
    }

    /**
     * Converts Calendar to Unix Epoch (UTC milliseconds from the epoch).
     *
     * @param event
     * @return UTC milliseconds from the epoch
     */
    public static Long calendarToEpoch(Calendar event) {
        return event.getTimeInMillis();
    }

    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a Date.
     *
     * @param event
     * @return Date representation of the long
     */
    public static Date epochToDate(Long event) {
        return new Date(event);
    }

    /**
     * Converts Date to Unix Epoch (UTC milliseconds from the epoch).
     *
     * @param event
     * @return UTC milliseconds from the epoch
     */
    public static Long dateToEpoch(Date event) {
        return event.getTime();
    }

}
