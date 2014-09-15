/*
 * Created Sep 5, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.util;

import com.dastrax.app.service.internal.DefaultDNSManager;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;


/**
 * CDI bean to perform real time ajax view value validations.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Sep 5, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class AjaxValidUtil implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    private final List<String> emails;
    private List<String> subdomains;
    private final String emailRegex;
    private final String subdomainRegex;
    private final String context;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public AjaxValidUtil() {
        this.emails = new ArrayList<>();
        this.subdomains = new ArrayList<>();
        this.context = ResourceBundle.getBundle("config").getString("BaseUrl");
        this.subdomainRegex = "[a-z0-9]{2,20}";
        this.emailRegex = DTX.EMAIL_REGEX;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;

//</editor-fold>
    
    @PostConstruct
    public void init() {
        List<User> users = dap.findWithNamedQuery("User.findAll");
        for (User user : users) {
            emails.add(user.getEmail());
        }
        subdomains = new DefaultDNSManager().listRecordSets(0);
    }

    /**
     * Query of type string can be submitted to determine if an email is both
     * available as a username (or already registered) and in the correct
     * format.
     *
     * @param query
     * @return True if the email is both available and in the correct format.
     * Otherwise false.
     */
    public boolean emailAvailable(String query) {

        try {
            List<String> collection = new ArrayList<>();
            // Make sure its a valid email
            if (query.matches(emailRegex)) {
                // Check query against existing list
                for (String email : emails) {
                    if (email.toLowerCase().startsWith(query.toLowerCase())) {
                        collection.add(email);
                    }
                    // Exit the loop if we have a match
                    if (!collection.isEmpty()) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException npe) {
            // Query is null
            return false;
        }
    }

    /**
     * Query of type string can be submitted to determine if a CNAME is both
     * available (or already registered) and in the correct format.
     *
     * @param query
     * @return True if the CNAME is both available and in the correct format.
     * Otherwise false.
     */
    public boolean subdomainAvailable(String query) {

        try {
            List<String> collection = new ArrayList<>();
            // Make sure its a valid email
            if (query.matches(subdomainRegex)) {
                // Check query against existing list
                for (String subdomain : subdomains) {
                    String q = query + "." + context + ".";
                    if (subdomain.toLowerCase().equals(q.toLowerCase())) {
                        collection.add(subdomain);
                    }
                    // Exit the loop if we have a match
                    if (!collection.isEmpty()) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException npe) {
            // Query is null
            return false;
        }
    }
}
