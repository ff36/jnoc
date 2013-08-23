/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.mesh.email;

import com.dastrax.per.entity.core.EmailParam;
import com.dastrax.per.entity.core.EmailTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class Email {

    // Variables----------------------------------------------------------------
    private String recipientEmail;
    private EmailTemplate template = new EmailTemplate();
    /**
     * These are the values that are persisted to the database to perform the
     * return authentication by the subject.
     */
    private EmailParam param = new EmailParam();
    /**
     * These are the variables that can be dynamically injected into the email
     * template.
     */
    private Map<String, String> variables = new HashMap<>();

    // Constructors-------------------------------------------------------------
    public Email() {
    }

    // Getters------------------------------------------------------------------
    public String getRecipientEmail() {
        return recipientEmail;
    }

    public EmailTemplate getTemplate() {
        return template;
    }

    public EmailParam getParam() {
        return param;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    // Setters------------------------------------------------------------------
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public void setTemplate(EmailTemplate template) {
        this.template = template;
    }

    public void setParam(EmailParam param) {
        this.param = param;
    }

    /**
     * Currently the following keys are available for the map;
     * String : link;
     * String : pin;
     * String : date;
     * String : time;
     * String : ticket_id;
     * String : ticket_title;
     * String : name;
     * String : exception;
     * 
     * @param variables 
     */
    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
    
}
