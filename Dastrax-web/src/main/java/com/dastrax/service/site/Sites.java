/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.site;

import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Site;
import com.dastrax.app.security.PasswordSvcs;
import com.dastrax.app.util.FilterUtil;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.service.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
    /**
     * This method provides the the access point to delete selected accounts.
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
     * Save name changes
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
     * Save package changes
     */
    public void savePackageType() {
        siteDAO.updatePackageType(
                selectedSites[0].getId(),
                selectedSites[0].getPackageType());
        JsfUtil.addSuccessMessage("Support package saved");
    }
    
    /**
     * Save response changes
     */
    public void savePackageResponse() {
        siteDAO.updatePackageResponse(
                selectedSites[0].getId(),
                selectedSites[0].getResponseHrs());
        JsfUtil.addSuccessMessage("Response time saved");
    }

    /**
     * This needs to be converted into a data list on the presentation layer.
     *
     * @param filterId
     * @throws IOException
     */
    public void tempFilterApply(int filterId) throws IOException {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        String url = null;
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/a/sites/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/a/sites/list.jsf?filter=" + filterId;
            }
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/b/sites/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/b/sites/list.jsf?filter=" + filterId;
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

}
