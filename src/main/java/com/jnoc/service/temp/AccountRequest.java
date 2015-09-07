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


package com.jnoc.service.temp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.jnoc.app.email.DefaultEmailer;
import com.jnoc.app.email.Email;
import com.jnoc.app.misc.JsfUtil;
import com.jnoc.per.dap.CrudService;
import com.jnoc.per.entity.Template;
import com.jnoc.per.entity.Token;
import com.jnoc.per.entity.User;
import com.jnoc.per.project.JNOC;
import com.jnoc.per.project.JNOC.EmailVariableKey;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class AccountRequest implements Serializable {
    
    // Logger------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(AccountRequest.class.getName());
    
    // Variables---------------------------------------------------------------
    private static final String recipient = "register@solid.com";
//    private static final String recipient = "tomxming@gmail.com";
    //private static final String recipient = "test1@dev.tarka.tv";
    private String name;
    private String company;
    private String email;
    private String phone;
    
    // EJB----------------------------------------------------------------------
    @EJB
    private CrudService dap;
    
    // Getters------------------------------------------------------------------
    public String getName() {
        return name; 
    }

    public String getCompany() {
        return company;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
    
    // Setters------------------------------------------------------------------
    public void setName(String name) {
        this.name = name;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    // Constructors------------------------------------------------------------
    public AccountRequest() {
    }
    
    // Methods------------------------------------------------------------------
    public void submit(ActionEvent event) { 

    	Map<String, List<String>> ps = JsfUtil.getRequestParameters();
    	
        // Build an new email
        Email e = new Email();

        // Set the recipient
        e.setRecipientEmail(recipient);

        // Set the variables
        Map<EmailVariableKey, String> vars = new HashMap<>();
        vars.put(EmailVariableKey.NAME, name);
        vars.put(EmailVariableKey.COMPANY, company);
        vars.put(EmailVariableKey.USER_EMAIL, email);
        vars.put(EmailVariableKey.TELEPHONE, phone);
        
        e.setVariables(vars);
        
        // Retreive the email template from the database.
        Template emailTemplate = (Template) dap.find(Template.class, JNOC.EmailTemplate.ACCOUNT_REQUEST.getValue());

        e.setTemplate(emailTemplate);
        
        // Send the email
        new DefaultEmailer().send(e);
        
        JsfUtil.addSuccessMessage("Thankyou! We have received your account request and one of our agents is currently reviewing your details and will be in touch shortly. (Accounts are normally processed within 2 working hours.)");
    }
}
