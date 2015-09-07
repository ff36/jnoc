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
 * Created May 8, 2014.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.service.feedback;

import com.jnoc.app.email.Email;
import com.jnoc.app.email.DefaultEmailer;
import com.jnoc.app.security.SessionUser;
import com.jnoc.per.entity.Template;
import com.jnoc.per.project.JNOC;
import com.jnoc.per.dap.CrudService;
import com.jnoc.app.misc.JsfUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import net.sf.uadetector.ReadableUserAgent;

/**
 * Feedback CDI bean. Permits Users to send feedback relating to the site.
 *
 *
 * @version 3.0.0
 * @since Build 3.0-SNAPSHOT (May 8, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@RequestScoped
public class Feedback implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    private String feedback;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Feedback() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of feedback.
     *
     * @return the value of feedback
     */
    public String getFeedback() {
        return feedback;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of feedback.
     *
     * @param feedback new value of feedback
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
//</editor-fold>

    /**
     * Sends the feedback to the system web master.
     */
    public void send() {
        if (!feedback.isEmpty()) {

            Email email = new Email();

            // Set the recipient
            email.setRecipientEmail(System.getenv("JNOC_WEB_MASTER_EMAIL"));

            // Get the user agent details
            ReadableUserAgent userAgent = SessionUser.getUserAgent();
            
            // Set the variables
            Map<JNOC.EmailVariableKey, String> vars = new HashMap<>();
            vars.put(JNOC.EmailVariableKey.USER_EMAIL,
                    SessionUser.getCurrentUser().getEmail());
            vars.put(JNOC.EmailVariableKey.FEEDBACK_MESSAGE, feedback);
            vars.put(JNOC.EmailVariableKey.AGENT_DEVICE_CATEGORY,
                    userAgent.getDeviceCategory().toString());
            vars.put(JNOC.EmailVariableKey.AGENT_FAMILY,
                    userAgent.getFamily().toString());
            vars.put(JNOC.EmailVariableKey.AGENT_OPERATING_SYSTEM,
                    userAgent.getOperatingSystem().toString());
            vars.put(JNOC.EmailVariableKey.AGENT_TYPE,
                    userAgent.getType().toString());
            vars.put(JNOC.EmailVariableKey.AGENT_VERSION_NUMBER,
                    userAgent.getVersionNumber().toString());
            email.setVariables(vars);

            // Retreive the email template from the database.
            Template template = (Template) dap.find(Template.class,
                    JNOC.EmailTemplate.USER_FEEDBACK.getValue());
            email.setTemplate(template);

            // Send the email
            new DefaultEmailer().send(email);

            JsfUtil.addSuccessMessage("Thank you, we appreciate you taking the" 
                    + " time to provide us with feedback.");
        } else {
            JsfUtil.addWarningMessage("Please enter some feedback.");
        }

    }

}
