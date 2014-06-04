/*
 * Created May 13, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.service.internal;

import com.dastrax.app.services.AttributeFilter;
import com.dastrax.app.exception.ExceptionUtil;
import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.Filter;
import com.dastrax.per.entity.DAS;
import com.dastrax.per.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Inject;

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

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CDI">
    @Inject
    ExceptionUtil exu;
//</editor-fold>

    /**
     * Data tables have the option of implemented optional filters to help
     * reduce the amount of data returned from the original search. The filters
     * are stored in the database in JSON format.
     *
     * Example JSON filter: {"status":["OPEN","SOLVED"],"assignee":["UID"]}
     *
     * Additionally the unique place holder 'UID' in the filters is replaced
     * with the current users UID. (Authenticated via SHIRO)
     *
     * @param filterId The database index of the filter
     * @return An OOR representation of the JSON filter.
     */
    @Override
    public Map<String, List<String>> optionalTableFilter(String filterId) {
        // Get the filter from the DB
        Filter filter = (Filter) dap.find(Filter.class, filterId);
        return filter.getExpression();
    }

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
        User user = (User) dap.find(User.class, SessionUser.getCurrentUser().getId());

        if (SessionUser.isVAR()) {
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

        if (SessionUser.isClient()) {
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
        // Add the VAR DAS
        for (DAS d : user.getCompany().getDas()) {
            das.add(d.getId());
        }
        filters.put("das", das);

        return filters;
    }

    /**
     * Determines all the users that the current user has access to.
     * (Authenticated via SHIRO)
     *
     * @return a complete Map of users that the current user is authorized to
     * access. The map key is "author" and the value is a list of user UID's
     */
    @Override
    public Map<String, List<Long>> authorizedAuthors() {
        Map<String, List<Long>> filters = new HashMap<>();
        List<Long> authors = new ArrayList<>();
        authors.add(SessionUser.getCurrentUser().getId());
        filters.put("author", authors);
        return filters;
    }

}
