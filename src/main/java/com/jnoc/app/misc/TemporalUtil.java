/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/


/*
 * Created Jul 10, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 

 */
package com.jnoc.app.misc;

import com.jnoc.per.project.JNOC;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Temporal conversion utility to handle transitions from one temporal format to
 * another.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere

 */
public class TemporalUtil {
	private static final Logger LOG = Logger.getLogger(TemporalUtil.class.getName());
    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a string format.
     *
     * @param event
     * @return string representation of the date using the system format. Or an 
     * empty string if the event is null.
     */
    public static String epochToStringDate(Long event) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return new SimpleDateFormat(
                    JNOC.TemporalFormat.DATE_FORMAT.getValue())
                    .format(cal.getTime());
        } catch (NullPointerException npe) {
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            // Event is null
            return "";
        }
    }

    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a string format.
     *
     * @param event
     * @return string representation of the date using the system format. Or an 
     * empty string if the event is null.
     */
    public static String epochToStringTime(Long event) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return new SimpleDateFormat(
                    JNOC.TemporalFormat.TIME_FORMAT.getValue())
                    .format(cal.getTime());
        } catch (NullPointerException npe) {
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            // Event is null
            return "";
        }
    }

    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a string format.
     *
     * @param event
     * @return string representation of the date using the system format. Or an 
     * empty string if the event is null.
     */
    public static String epochToStringDateTime(Long event) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return new SimpleDateFormat(
                    JNOC.TemporalFormat.DATE_TIME_FORMAT.getValue())
                    .format(cal.getTime());
        } catch (NullPointerException npe) {
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            // Event is null
            return "";
        }
    }

    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a Calendar.
     *
     * @param event
     * @return Calendar representation of the long. Or an null if the event is 
     * null.
     */
    public static Calendar epochToCalendar(Long event) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event);
            return cal;
        } catch (NullPointerException npe) {
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            // Event is null
            return null;
        }
    }

    /**
     * Converts Calendar to Unix Epoch (UTC milliseconds from the epoch).
     *
     * @param event
     * @return UTC milliseconds from the epoch. Or an null if the event is 
     * null.
     */
    public static Long calendarToEpoch(Calendar event) {
        try {
            return event.getTimeInMillis();
        } catch (NullPointerException npe) {
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            // Event is null
            return null;
        }
    }

    /**
     * Converts Unix Epoch (UTC milliseconds from the epoch) to a Date.
     *
     * @param event
     * @return Date representation of the long. Or an null if the event is 
     * null.
     */
    public static Date epochToDate(Long event) {
        try {
            return new Date(event);
        } catch (NullPointerException npe) {
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            // Event is null
            return null;
        }
    }

    /**
     * Converts Date to Unix Epoch (UTC milliseconds from the epoch).
     *
     * @param event
     * @return UTC milliseconds from the epoch. Or an null if the event is 
     * null.
     */
    public static Long dateToEpoch(Date event) {
        try {
            return event.getTime();
        } catch (NullPointerException npe) {
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            // Event is null
            return null;
        }
    }

}
