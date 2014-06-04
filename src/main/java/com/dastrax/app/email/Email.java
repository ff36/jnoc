/*
 * Created 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.email;

import com.dastrax.per.entity.Token;
import com.dastrax.per.entity.Template;
import com.dastrax.per.project.DTX.EmailVariableKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Email objects contain all the data required to send an email. 
 * Including recipient, persisted values in the case where the email requires 
 * the intended recipient to take some action and the returned value needs to be 
 * authenticated, a template to construct an HTML email and variables that can
 * be dynamically injected into the email at runtime allowing for email
 * customization (eg. name, date etc.). 
 * 
 * This class does not actually send an email!
 *
 * @version 1.0.0
 * @since Build 1.0-SNAPSHOT (2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class Email {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String recipientEmail;
    private Template template;
    private Token parameters;
    private Map<EmailVariableKey, String> variables;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Email() {
        this.variables = new HashMap<>();
        this.parameters = new Token();
        this.template = new Template();
    }
    
    public Email(String recipientEmail) {
        this.variables = new HashMap<>();
        this.parameters = new Token();
        this.template = new Template();
        this.recipientEmail = recipientEmail;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of recipientEmail
     *
     * @return the value of recipientEmail
     */
    public String getRecipientEmail() {
        return recipientEmail;
    }

    /**
     * Get the value of template
     *
     * @return the value of template
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Get the value of parameters. Persisted values to perform return checks.
     *
     * @return the value of parameters
     */
    public Token getParameters() {
        return parameters;
    }

    /**
     * Get the value of variables. Dynamically injected values into the email 
     * template.
     *
     * @return the value of variables
     */
    public Map<EmailVariableKey, String> getVariables() {
        return variables;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of recipientEmail
     *
     * @param recipientEmail new value of recipientEmail
     */
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    /**
     * Set the value of recipientEmail
     *
     * @param template new value of template
     */
    public void setTemplate(Template template) {
        this.template = template;
    }
    
    /**
     * Set the value of recipientEmail
     *
     * @param parameters new value of parameters
     */
    public void setParam(Token parameters) {
        this.parameters = parameters;
    }
    
    /**
     * Set the value of variables
     *
     * @param variables new value of variables
     */
    public void setVariables(Map<EmailVariableKey, String> variables) {
        this.variables = variables;
    }
//</editor-fold>
    
}
