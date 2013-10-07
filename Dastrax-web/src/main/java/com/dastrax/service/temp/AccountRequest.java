/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.temp;

import com.dastrax.app.util.ExceptionUtil;
import com.dastrax.mesh.email.Email;
import com.dastrax.mesh.email.EmailUtil;
import com.dastrax.per.dao.core.AuditDAO;
import com.dastrax.per.dao.core.EmailParamDAO;
import com.dastrax.per.dao.core.EmailTemplateDAO;
import com.dastrax.per.entity.core.EmailTemplate;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.service.util.JsfUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class AccountRequest implements Serializable {
    
    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(AccountRequest.class.getName());
    
    // Variables----------------------------------------------------------------
    //private static final String recipient = "register@solid.com";
    private static final String recipient = "test1@dev.tarka.tv";
    private String name;
    private String company;
    private String email;
    private String phone;
    
    // EJB----------------------------------------------------------------------
    @EJB
    EmailTemplateDAO emailTemplateDAO;
    @EJB
    EmailUtil emailUtil;
    @EJB
    EmailParamDAO emailParamDAO;
    @EJB
    ExceptionUtil exu;
    @EJB
    AuditDAO auditDAO;
    
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
    
    // Constructors-------------------------------------------------------------
    public AccountRequest() {
    }
    
    // Methods------------------------------------------------------------------
    public void submit(ActionEvent event) { 

        // Build an new email
        Email e = new Email();

        // Set the recipient
        e.setRecipientEmail(recipient);

        // Set the variables
        Map<String, String> vars = new HashMap<>();
        vars.put("name", name);
        vars.put("company", company);
        vars.put("email", email);
        vars.put("telephone", phone);
        
        e.setVariables(vars);

        // Retreive the email template from the database.
        EmailTemplate et = emailTemplateDAO.findTemplateById(
                DastraxCst.EmailTemplate.ACCOUNT_REQUEST.toString());
        e.setTemplate(et);

        // Send the email
        emailUtil.build(e);
        
        JsfUtil.addSuccessMessage("Thankyou! We have received your account request and one of our agents is currently reviewing your details and will be in touch shortly. (Accounts are normally processed within 2 working hours.)");
    }
}
