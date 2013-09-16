/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.app.util;

import com.dastrax.per.dao.core.EmailTemplateDAO;
import com.dastrax.per.entity.core.EmailTemplate;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.mesh.email.Email;
import com.dastrax.mesh.email.EmailUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Jul 14, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class ExceptionUtil {
    // Logger-------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(ExceptionUtil.class.getName());

    // EJB----------------------------------------------------------------------
    @EJB
    EmailTemplateDAO emailTemplateDAO;
    @EJB
    EmailUtil emailUtil;

    // Methods------------------------------------------------------------------    
    /**
     * Sends the developer an email when an exception occurs. This just saves
     * having to trawl the logs on the server.
     * @param e 
     */
    public void report(Exception e) {
        // Build an new email
        Email email = new Email();

        // Set the recipient
        email.setRecipientEmail(
                ResourceBundle
                .getBundle("Config")
                .getString("WebMasterEmailAddress")
                );

        // Get the current subject
        String user = "Not Available";
        if (SecurityUtils.getSubject().isAuthenticated()) {
            user = SecurityUtils.getSubject().getPrincipals().asList().get(1).toString();
        }
        
        // Set the variables
        Map<String, String> vars = new HashMap<>();
        vars.put("user", user);
        vars.put("stage", ResourceBundle.getBundle("Config").getString("ProjectStage"));
        vars.put("exception_name", e.getClass().getName());
        vars.put("exception_message", e.getMessage());
        vars.put("exception_stack", org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        email.setVariables(vars);

        // Retreive the email template from the database.
        EmailTemplate et = emailTemplateDAO.findTemplateById(
                DastraxCst.EmailTemplate.EXCEPTION.toString());
        email.setTemplate(et);

        // Send the email
        emailUtil.build(email);
    }

}
