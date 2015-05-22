/*
 * Created May 13, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.service.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.dastrax.app.security.SessionUser;
import com.dastrax.app.services.AttributeFilter;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.DAS;
import com.dastrax.per.entity.User;

/**
 * This class contains executable filter methods. In contrast to using SHIRO or
 * Servlet filters that map to URLs, this class provides data filters that can
 * be called on demand.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (Aug 5, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DefaultAttributeFilter implements AttributeFilter {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(DefaultAttributeFilter.class.getName());
    private CrudService dap;

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DefaultAttributeFilter() {
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
            LOG.log(Level.CONFIG, null, ex);
        }
    }
//</editor-fold>

    /**
     * Determines all the companies that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @param includeOwnCompany If true the users own company is included in the
     * list of authorized companies.
     * @return a complete Map of companies that the current user is authorized
     * to access. The map key is "company" and the value is a list of company
     * ID's
     */
    @Override
    public Map<String, List<Long>> authorizedCompanies(
            boolean includeOwnCompany) {

        Map<String, List<Long>> filters = new HashMap<>();
        User user = (User) dap.find(User.class,
                SessionUser.getCurrentUser().getId());

        if (SessionUser.getCurrentUser().isVAR()) {
            List<Long> companies = new ArrayList<>();
            // Add the VAR company ID
            if (includeOwnCompany) {
                companies.add(user.getCompany().getId());
            }
            // Add the Client companies
            for (Company client : user.getCompany().getClients()) {
                companies.add(client.getId());
            }
            filters.put("company", companies);
        }

        if (SessionUser.getCurrentUser().isClient()) {
            List<Long> companies = new ArrayList<>();
            // Add the Client company ID
            companies.add(user.getCompany().getId());
            filters.put("company", companies);
        }

        return filters;
    }

    /**
     * Determines all the DAS that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @return a complete Map of DAS that the current user is authorized to
     * access. The map key is "das" and the value is a list of DAS ID's
     */
    @Override
    public Map<String, List<Long>> authorizedDAS() {

        Map<String, List<Long>> filters = new HashMap<>();
        User user = (User) dap.find(User.class, SessionUser.getCurrentUser().getId());

        List<Long> das = new ArrayList<>();

        try {
            // Add the VAR DAS
            for (DAS d : user.getCompany().getDas()) {
                das.add(d.getId());
            }
            filters.put("das", das);
        } catch (NullPointerException npe) {
            // Do nothing. The User company is null
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
        }

        return filters;
    }

    /**
     * Determines all the audit users that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @return a complete Map of users that the current user is authorized to
     * access. The map key is "author" and the value is a list of user ID's
     */
    @Override
    public Map<String, List<Long>> authorizedAuthors() {
        Map<String, List<Long>> filters = new HashMap<>();
        List<Long> authors = new ArrayList<>();
        authors.add(SessionUser.getCurrentUser().getId());
        filters.put("author", authors);
        return filters;
    }

    /**
     * Determines all the tickets that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @return a complete Map of users that the current user is authorized to
     * access. The map key is "author" and the value is a list of user ID's
     */
    @Override
    public Map<String, List<Long>> authorizedTickets() {
        Map<String, List<Long>> filters = new HashMap<>();
        // Get the current users company if its a VAR or client
        if (SessionUser.getCurrentUser().isVAR()) {
            List<Long> companies = new ArrayList<>();
            companies.add(SessionUser.getCurrentUser().getCompany().getId());
            for (Company client : SessionUser.getCurrentUser().getCompany().getClients()) {
                companies.add(client.getId());
            }
            filters.put("company", companies);
        }
        if (SessionUser.getCurrentUser().isClient()) {
            List<Long> companies = new ArrayList<>();
            companies.add(SessionUser.getCurrentUser().getCompany().getId());
            filters.put("company", companies);
        }
        return filters;
    }

    /**
     * Determines all the incidents that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @return a complete Map of users that the current user is authorized to
     * access. The map key is "author" and the value is a list of user ID's
     */
    @Override
    public Map<String, List<Long>> authorizedIncidents() {
        Map<String, List<Long>> filters = new HashMap<>();
        // TODO
        return filters;
    }

    /**
     * Determines all the users that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @return a complete Map of users that the current user is authorized to
     * access. The map key is "author" and the value is a list of user ID's
     */
    @Override
    public Map<String, List<Long>> authorizedUsers() {
        Map<String, List<Long>> filters = new HashMap<>();
        // TODO
        return filters;
    }

}
