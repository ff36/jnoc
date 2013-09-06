/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.util;

import com.dastrax.app.util.DnsUtil;
import com.dastrax.per.dao.core.SubjectDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIInput;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @version Build 2.0.0
 * @since Sep 5, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class AjaxValidUtil {

    // Variables----------------------------------------------------------------
    private List<String> emails;
    private List<String> subdomains;
    private boolean emailFree = false;
    private boolean subdomainFree = false;
    private final String emailRegex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    private final String subdomainRegex = "[a-z0-9]{2,20}";
    private final String context = ResourceBundle.getBundle("Config").getString("BaseUrl");

    
    // EJB----------------------------------------------------------------------
    @EJB
    DnsUtil dnsUtil;
    @EJB
    SubjectDAO subjectDAO;

    // Getters------------------------------------------------------------------
    public boolean isEmailFree() {
        return emailFree;
    }

    public boolean isSubdomainFree() {
        return subdomainFree;
    }

    // Methods------------------------------------------------------------------
    @PostConstruct
    public void postConstruct() {
        emails = subjectDAO.findAllSubjectEmails();
        subdomains = dnsUtil.allSubdomains();
    }

    /**
     * Determines whether the query string is both a valid email address and if
     * the email is already registered of if its still available. 
     * @param event
     */
    public void emailAvailable(UIInput event) {

        String query = (String)event.getValue();
        emailFree = false;
        List<String> collection = new ArrayList<>();
        // Make sure its a valid email
        if (query != null & query.matches(emailRegex)) {
            // Check query against existing list
            for (String email : emails) {
                if (email.toLowerCase().startsWith(query.toLowerCase())) {
                    collection.add(email);
                }
                // Exit the loop if we have a match
                if (!collection.isEmpty()) {
                    break;
                }
            }
            emailFree = collection.isEmpty();
        } else {
            emailFree = false;
        }
    }
    
    /**
     * Determines whether the query string is both a valid sub-domain and if
     * the sub-domain is already registered of if its still available. 
     * @param event
     */
    public void subdomainAvailable(UIInput event) {

        String query = (String)event.getValue();
        subdomainFree = false;
        List<String> collection = new ArrayList<>();
        // Make sure its a valid email
        if (query != null & query.matches(subdomainRegex)) {
            // Check query against existing list
            for (String subdomain : subdomains) {
                String q = query + "." + context;
                if (subdomain.toLowerCase().equals(q.toLowerCase())) {
                    collection.add(subdomain);
                }
                // Exit the loop if we have a match
                if (!collection.isEmpty()) {
                    break;
                }
            }
            subdomainFree = collection.isEmpty();
        } else {
            subdomainFree = false;
        }
    }
}
