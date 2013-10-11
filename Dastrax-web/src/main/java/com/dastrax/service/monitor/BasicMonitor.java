/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.monitor;

import com.dastrax.app.security.PasswordSvcs;
import com.dastrax.app.util.FilterUtil;
import com.dastrax.cnx.monitor.DevicePoll;
import com.dastrax.cnx.snmp.SnmpUtil;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Site;
import com.dastrax.service.site.SiteModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @version Build 2.0.0
 * @since Sep 9, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class BasicMonitor implements Serializable {
    
    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(BasicMonitor.class.getName());

    // Variables----------------------------------------------------------------
    private List<Site> sites = new ArrayList<>();
    private Site[] selectedSites;
    private SiteModel siteModel;
    private List<Site> filtered;
    private String principalPassword;
    private String filter;

    // EJB----------------------------------------------------------------------
    @EJB
    SiteDAO siteDAO;
    @EJB
    PasswordSvcs passwordSvcs;
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    FilterUtil filterUtil;
    @EJB
    DevicePoll devicePole;
    @EJB
    SnmpUtil snmpUtil;
    
    // Constructors-------------------------------------------------------------
    public void init() {
        Map<String, List<String>> metierFilter = filterUtil.authorizedCompanies();
        Map<String, List<String>> optFilter = filterUtil.optionalFilter(filter);
        siteModel = new SiteModel(siteDAO, metierFilter, optFilter);
    }

    // Getters------------------------------------------------------------------
    public List<Site> getSites() {
        return sites;
    }

    public Site[] getSelectedSites() {
        return selectedSites;
    }

    public SiteModel getSiteModel() {
        return siteModel;
    }

    public List<Site> getFiltered() {
        return filtered;
    }

    public String getPrincipalPassword() {
        return principalPassword;
    }

    public int getSelectedLength() {
        if (selectedSites != null) {
            return selectedSites.length;
        } else {
            return 0;
        }
    }

    public String getFilter() {
        return filter;
    }

    // Setters------------------------------------------------------------------
    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public void setSelectedSites(Site[] selectedSites) {
        this.selectedSites = selectedSites;
    }

    public void setSiteModel(SiteModel siteModel) {
        this.siteModel = siteModel;
    }

    public void setFiltered(List<Site> filtered) {
        this.filtered = filtered;
    }

    public void setPrincipalPassword(String principalPassword) {
        this.principalPassword = principalPassword;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    // Methods------------------------------------------------------------------
    public int deviceStatus(String siteId) {
        return devicePole.cachedSiteStatus(siteId); 
    }
    
    public int dmsStatus(String siteId) {
        return snmpUtil.cachedDmsStatus(siteId);
    }
    
}
