/*
 * Created May 26, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.incident;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;

/**
 * Experimental Incident Visualizer. This is to try and display Incidents on
 * a linear time line.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (May 26, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named 
@ViewScoped 
public class Visualizer implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    private TimelineModel model;
    private String locale; // current locale as String, java.util.Locale is possible too.
    private Date start;
    private Date end;

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Visualizer() {
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of model
     *
     * @return the value of model
     */
    public TimelineModel getModel() {
        return model;
    }
    
    /**
     * Get the value of locale
     *
     * @return the value of locale
     */
    public String getLocale() {
        return locale;
    }
    
    /**
     * Get the value of start
     *
     * @return the value of start
     */
    public Date getStart() {
        return start;
    }
    
    /**
     * Get the value of end
     *
     * @return the value of end
     */
    public Date getEnd() {
        return end;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of locale.
     *
     * @param locale new value of locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }
//</editor-fold>
 
    @PostConstruct  
    protected void init() {  
        // set initial start / end dates for the axis of the timeline  
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));  
        Date now = new Date();  
  
        cal.setTimeInMillis(now.getTime() - 4 * 60 * 60 * 1000);  
        start = cal.getTime();  
  
        cal.setTimeInMillis(now.getTime() + 8 * 60 * 60 * 1000);  
        end = cal.getTime();  
  
        // groups  
        String[] NAMES = new String[] {
            "BIU DC Fail", 
            "BIU VHF+UHF Tx Low", 
            "MDBU Tx Low", 
            "OEU High Temperature", 
            "OEU PD Fail", 
            "DOU LD Fail"};  
  
        // create timeline model  
        model = new TimelineModel();  
  
        for (String name : NAMES) {  
            now = new Date();  
            Date e = new Date(now.getTime() - 12 * 60 * 60 * 1000);  
  
            for (int i = 0; i < 5; i++) {  
                Date s = new Date(e.getTime() + Math.round(Math.random() * 5) 
                        * 60 * 60 * 1000);  
                e = new Date(s.getTime() + Math.round(4 + Math.random() * 5) 
                        * 60 * 60 * 1000);  
  
                long r = Math.round(Math.random() * 2);  
                String availability = (
                        r == 0 ? "Critical" : (r == 1 ? "Minor" : "Warning"));  
  
                /*
                create an event with content, start / end dates, editable flag, 
                group name and custom style class 
                */
                TimelineEvent event = new TimelineEvent(
                        availability, 
                        s, 
                        e, 
                        true, 
                        name, 
                        availability.toLowerCase());  
                model.add(event);  
            }  
        }  
    }  

}
