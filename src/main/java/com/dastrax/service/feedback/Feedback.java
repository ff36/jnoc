/*
 * Created May 8, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.feedback;

import com.dastrax.app.email.Email;
import com.dastrax.app.email.DefaultEmailer;
import com.dastrax.app.security.SessionUser;
import com.dastrax.per.entity.Template;
import com.dastrax.per.project.DTX;
import com.dastrax.per.dap.CrudService;
import com.dastrax.app.misc.JsfUtil;
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
    CrudService dap;
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
            email.setRecipientEmail(
                    ResourceBundle
                    .getBundle("config")
                    .getString("WebMasterEmailAddress"));

            // Get the user agent details
            ReadableUserAgent userAgent = SessionUser.getUserAgent();
            
            // Set the variables
            Map<DTX.EmailVariableKey, String> vars = new HashMap<>();
            vars.put(DTX.EmailVariableKey.USER_EMAIL,
                    SessionUser.getCurrentUser().getEmail());
            vars.put(DTX.EmailVariableKey.FEEDBACK_MESSAGE, feedback);
            vars.put(DTX.EmailVariableKey.AGENT_DEVICE_CATEGORY,
                    userAgent.getDeviceCategory().toString());
            vars.put(DTX.EmailVariableKey.AGENT_FAMILY,
                    userAgent.getFamily().toString());
            vars.put(DTX.EmailVariableKey.AGENT_OPERATING_SYSTEM,
                    userAgent.getOperatingSystem().toString());
            vars.put(DTX.EmailVariableKey.AGENT_TYPE,
                    userAgent.getType().toString());
            vars.put(DTX.EmailVariableKey.AGENT_VERSION_NUMBER,
                    userAgent.getVersionNumber().toString());
            email.setVariables(vars);

            // Retreive the email template from the database.
            Template template = (Template) dap.find(Template.class,
                    DTX.EmailTemplate.FEEDBACK);
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
