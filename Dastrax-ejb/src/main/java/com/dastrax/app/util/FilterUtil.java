/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.app.util;

import com.dastrax.per.dao.core.FilterDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Filter;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.project.DastraxCst;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import org.apache.shiro.SecurityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @version Build 2.0.0
 * @since Aug 5, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class FilterUtil {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(FilterUtil.class.getName());

    // EJB----------------------------------------------------------------------
    @EJB
    FilterDAO filterDAO;
    @EJB
    ExceptionUtil exu;
    @EJB
    SubjectDAO subjectDAO;

    // Methods------------------------------------------------------------------
    /**
     * Table optional filters are generated in this method. The JSON filter is
     * retrieved from the db and converted into a MAP
     * @param filterId
     * @return 
     */
    public Map<String, List<String>> optionalFilter(String filterId) {
        Map<String, List<String>> jsonExp = new HashMap<>();
        // Get the filter from the DB
        Filter filter = filterDAO.findFilterById(filterId);
        if (filter != null) {
            try {
                // Convert uid values
                String expression = convertHolders(filter.getExpression());
                // Get a new JSON converter from Jackson
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
                jsonExp = mapper.readValue(expression, Map.class);

            } catch (IOException ioe) {
                exu.report(ioe);
                LOG.log(Level.SEVERE, "SQS IOException", ioe);
            }
        }
        return jsonExp;
    }

    /**
     * If the JSON contains the holder 'UID' it should be converted to the 
     * current subjects UID
     * @param expression
     * @return 
     */
    private String convertHolders(String expression) {
        return expression.replaceAll(
                "UID", 
                SecurityUtils.getSubject().getPrincipals()
                .asList().get(1).toString());
    }

    /**
     * @return a complete Map of companies that the current subject is 
     * authorized to access
     */
    public Map<String, List<String>> authorizedCompanies() {
        Map<String, List<String>> filters = new HashMap<>();
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            List<String> companies = new ArrayList<>();
            // Add the VAR company ID
            companies.add(s.getCompany().getId());
            // Add the Client companies
            for (Company client: s.getCompany().getClients()) {
                companies.add(client.getId());
            }
            filters.put("company", companies);
        } 
        // CLIENT access
        else if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            List<String> companies = new ArrayList<>();
            // Add the Client company ID
            companies.add(s.getCompany().getId());
            filters.put("company", companies);
        }
        return filters;
    }
    
    /**
     * Performs the same as the authorizedCompanies() method but returns a
     * list of authorized companies instead of a MAP
     * @return 
     */
    public List<String> authorizedCompaniesList() {
        List<String> companies = new ArrayList<>();
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            // Add the VAR company ID
            companies.add(s.getCompany().getId());
            // Add the Client companies
            for (Company client: s.getCompany().getClients()) {
                companies.add(client.getId());
            }
        } else if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            // Add the Client company ID
            companies.add(s.getCompany().getId());
        }
        return companies;
    }
    
    /**
     * 
     * @return a complete Map of companies that the current subject is 
     * authorized to access EXCLUDING their own company
     */
    public Map<String, List<String>> authorizedCompaniesNonInclusive() {
        Map<String, List<String>> filters = new HashMap<>();
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            List<String> companies = new ArrayList<>();
            // Add the Client companies
            for (Company client: s.getCompany().getClients()) {
                companies.add(client.getId());
            }
            filters.put("company", companies);
        }
        return filters;
    }
    
    /**
     * 
     * @return a complete Map of sites that the current subject is 
     * authorized to access
     */
    public Map<String, List<String>> authorizedSites() {
        Map<String, List<String>> filters = new HashMap<>();
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            List<String> sites = new ArrayList<>();
            // Add the VAR sites
            for (Site site: s.getCompany().getVarSites()) {
                sites.add(site.getId());
            }
            filters.put("site", sites);
        }
        // client access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            List<String> sites = new ArrayList<>();
            // Add the Client sites
            for (Site site: s.getCompany().getClientSites()) {
                sites.add(site.getId());
            }
            filters.put("site", sites);
        }
        return filters;
    }

}
