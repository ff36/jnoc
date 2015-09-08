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

package com.jnoc.app.email;

import com.jnoc.per.entity.Template;
import com.jnoc.per.entity.Token;
import com.jnoc.per.project.JNOC.EmailVariableKey;
import java.io.File;
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

 */
public class Email {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String recipientEmail;
    private Template template;
    private Token parameters;
    private Map<EmailVariableKey, String> variables;
    private File attachment;
    //</editor-fold>

    public File getAttachment() {
        return attachment;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

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
