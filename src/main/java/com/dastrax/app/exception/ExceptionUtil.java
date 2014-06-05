/*
 * Created Jul 14, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.exception;

import com.dastrax.app.email.Email;
import com.dastrax.app.email.Emailer;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Template;
import com.dastrax.per.project.DTX;
import com.dastrax.per.project.DTX.EmailVariableKey;
import com.dastrax.per.project.DTX.ProjectStage;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Inject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;

/**
 * When a runtime exception is encountered in the PRO environment we can
 * accelerate production response by notifying the system developer by email.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 14, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 * 
 * TODO: This could probably do with an interface and split out the development
 * stages
 */
public class ExceptionUtil {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private final String stage = ResourceBundle.getBundle("config").getString("ProjectStage");
    private final String recipent = ResourceBundle.getBundle("config").getString("WebMasterEmailAddress");
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CDI">
    @Inject
    private Emailer emailer;
//</editor-fold>

    /**
     * Generates an email with the captured exception and sends it to the system
     * developer
     *
     * @param exception
     */
    public void report(Exception exception) {
        
        if (stage.equals(ProjectStage.PRO.toString())) {

            // Build an new email
            Email email = new Email();

            // Set the recipient
            email.setRecipientEmail(recipent);

            // Get the current user if one exists
            String user = "Unavailable";
            try {
                user = SecurityUtils
                        .getSubject()
                        .getPrincipals()
                        .asList()
                        .get(0)
                        .toString();
            } catch (Exception ex) {
                // Do nothing as this means we have no security manager
            }

            // Set the email variables
            Map<EmailVariableKey, String> vars = new HashMap<>();
            vars.put(EmailVariableKey.USER_EMAIL, user);
            vars.put(EmailVariableKey.STAGE, stage);
            vars.put(EmailVariableKey.EXCEPTION_NAME,
                    exception.getClass().getName());
            vars.put(EmailVariableKey.EXCEPTION_MESSAGE,
                    exception.getMessage());
            vars.put(EmailVariableKey.EXCEPTION_STACK,
                    ExceptionUtils.getStackTrace(exception));
            email.setVariables(vars);

            // Retreive the email template from the database.
            Template et = 
                    (Template)dap.find(
                            Template.class, DTX.EmailTemplate.EXCEPTION
                    );
            email.setTemplate(et);

            // Send the email
            emailer.send(email);
        }
    }
    
}
