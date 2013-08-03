/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.site;

import com.dastrax.per.dao.SiteDAO;
import com.dastrax.per.dao.SubjectDAO;
import com.dastrax.per.entity.core.Site;
import com.dastrax.app.security.PasswordSvcs;
import com.dastrax.service.model.SiteModel;
import com.dastrax.service.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Jul 25, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class Sites implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(Sites.class.getName());

    // Variables----------------------------------------------------------------
    private List<Site> sites = new ArrayList<>();
    private Site[] selectedSites;
    private SiteModel siteModel;
    private List<Site> filtered;
    private String principalPassword;
    private String filterCondition;

    // EJB----------------------------------------------------------------------
    @EJB
    SiteDAO siteDAO;
    @EJB
    PasswordSvcs passwordSvcs;
    @EJB
    SubjectDAO subjectDAO;

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

    public String getFilterCondition() {
        return filterCondition;
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

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    // Methods------------------------------------------------------------------
    public void init() {
        populateData();
    }

    /**
     * Populate the data table based on role
     */
    public void populateData() {

        if (sites.size() > 0) {
            sites.clear();
        }

        sites = siteDAO.findAllSites();
//        if (filterCondition != null) {
//            Iterator<Site> i = sites.iterator();
//            while (i.hasNext()) {
//                Site s = i.next();
//                if (!s.getCompany().getName().equals(filterCondition)) {
//                    i.remove();
//                }
//            }
//        }

        siteModel = new SiteModel(sites);
    }

    /**
     * This method provides the the access point to delete accounts.
     */
    public void deleteSites() {

        String dbPsw = subjectDAO.findSubjectPasswordByUid(
                SecurityUtils.getSubject().getPrincipals()
                .asList().get(1).toString());

        // Check password
        if (passwordSvcs.passwordCorrect(principalPassword, dbPsw)) {

            for (Site s : selectedSites) {
                siteDAO.delete(s.getId());
                JsfUtil.addSuccessMessage("Site " + s.getName() + " successfully deleted");
            }

            populateData();

            // In view scoped bean we just want to clear variables
            selectedSites = null;

        } else {
            // Wrong password
            JsfUtil.addErrorMessage("Invalid Password");
        }
        // In view scoped bean we just want to clear variables
        principalPassword = null;
    }

    /**
     * Save account changes
     */
    public void saveName() {
        siteDAO.updateName(
                selectedSites[0].getId(),
                selectedSites[0].getName());
        JsfUtil.addSuccessMessage("New name saved");
    }
    
    /**
     * Save address changes
     */
    public void saveAddress() {
        siteDAO.updateAddress(
                selectedSites[0].getId(),
                selectedSites[0].getAddress());
        JsfUtil.addSuccessMessage("Address saved");
    }
    
    /**
     * Save address changes
     */
    public void savePackageType() {
        siteDAO.updatePackageType(
                selectedSites[0].getId(),
                selectedSites[0].getPackageType());
        JsfUtil.addSuccessMessage("Support package saved");
    }
    
    /**
     * Save address changes
     */
    public void savePackageResponse() {
        siteDAO.updatePackageResponse(
                selectedSites[0].getId(),
                selectedSites[0].getResponseHrs());
        JsfUtil.addSuccessMessage("Response time saved");
    }

}
