/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.company;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.dastrax.per.dao.core.CompanyDAO;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.per.project.DastraxCst.S3ContentType;
import com.dastrax.app.security.PasswordSvcs;
import com.dastrax.app.util.DnsUtil;
import com.dastrax.app.util.FilterUtil;
import com.dastrax.app.util.S3KeyUtil;
import com.dastrax.app.util.S3Util;
import com.dastrax.service.util.JsfUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @version Build 2.0.0
 * @since Jul 26, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class Companies implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(Companies.class.getName());

    // Project Stage------------------------------------------------------------
    private final String stage = ResourceBundle.getBundle("Config").getString("ProjectStage");

    // Variables----------------------------------------------------------------
    private List<Company> companies = new ArrayList<>();
    private Company[] selectedCompanies;
    private CompanyModel companyModel;
    private List<Company> filtered;
    private String principalPassword;
    private String filter;
    private Helper helper = new Helper();

    // EJB----------------------------------------------------------------------
    @EJB
    CompanyDAO companyDAO;
    @EJB
    PasswordSvcs passwordSvcs;
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    DnsUtil dnsUtil;
    @EJB
    S3Util s3Util;
    @EJB
    S3KeyUtil s3KeyUtil;
    @EJB
    SiteDAO siteDAO;
    @EJB
    FilterUtil filterUtil;

    // Constructors-------------------------------------------------------------
    public void init() {
        Map<String, List<String>> metierFilter = filterUtil.authorizedCompaniesNonInclusive();
        Map<String, List<String>> optFilter = filterUtil.optionalFilter(filter);
        companyModel = new CompanyModel(companyDAO, metierFilter, optFilter);
    }

    // Getters------------------------------------------------------------------
    public List<Company> getCompanies() {
        return companies;
    }

    public Company[] getSelectedCompanies() {
        return selectedCompanies;
    }

    public CompanyModel getCompanyModel() {
        return companyModel;
    }

    public List<Company> getFiltered() {
        return filtered;
    }

    public String getPrincipalPassword() {
        return principalPassword;
    }

    public int getSelectedLength() {
        if (selectedCompanies != null) {
            return selectedCompanies.length;
        } else {
            return 0;
        }
    }

    public String getFilter() {
        return filter;
    }

    public Helper getHelper() {
        return helper;
    }

    // Setters------------------------------------------------------------------
    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public void setSelectedCompanies(Company[] selectedCompanies) {
        this.selectedCompanies = selectedCompanies;
    }

    public void setCompanyModel(CompanyModel companyModel) {
        this.companyModel = companyModel;
    }

    public void setFiltered(List<Company> filtered) {
        this.filtered = filtered;
    }

    public void setPrincipalPassword(String principalPassword) {
        this.principalPassword = principalPassword;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    // Methods------------------------------------------------------------------
    /**
     * This method provides the the access point to delete selected accounts.
     */
    public void deleteCompanies() {

        String dbPsw = subjectDAO.findSubjectPasswordByUid(
                SecurityUtils.getSubject().getPrincipals()
                .asList().get(1).toString());

        // Check password
        if (passwordSvcs.passwordCorrect(principalPassword, dbPsw)) {

            for (Company s : selectedCompanies) {

                // If its a VAR delete the subdomain
                if (!stage.equals(DastraxCst.ProjectStage.DEV.toString())) {
                    if (s.getType().equals(DastraxCst.CompanyType.VAR.toString())) {
                        dnsUtil.deleteSubdomain(s.getSubdomain());
                    }
                }
                // Delete the S3 directory
                s3Util.deleteDirectory(s3KeyUtil.subjectDir(selectedCompanies[0].getId()));
                // Delete the actual company from the db
                companyDAO.delete(s.getId());

                JsfUtil.addSuccessMessage("Company " + s.getName() + " successfully deleted");
            }
            // In view scoped bean we just want to clear variables
            selectedCompanies = null;

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
        companyDAO.updateName(
                selectedCompanies[0].getId(),
                selectedCompanies[0].getName());
        JsfUtil.addSuccessMessage("New name saved");
    }

    /**
     * Save contact changes
     */
    public void saveContacts() {
        companyDAO.updateContacts(
                selectedCompanies[0].getId(),
                selectedCompanies[0].getContacts());

        JsfUtil.addSuccessMessage("Contact Updated");
    }

    /**
     * Save VAR Site changes
     */
    public void saveVarSites() {
        selectedCompanies[0].setVarSites(helper.varSites.getTarget());
        companyDAO.updateVarSites(
                selectedCompanies[0].getId(),
                selectedCompanies[0].getVarSites());

        JsfUtil.addSuccessMessage("Sites Updated");
    }

    /**
     * Save CLIENT Site changes
     */
    public void saveClientSites() {
        selectedCompanies[0].setClientSites(helper.clientSites.getTarget());
        companyDAO.updateClientSites(
                selectedCompanies[0].getId(),
                selectedCompanies[0].getClientSites());

        JsfUtil.addSuccessMessage("Sites Updated");
    }

    /**
     * Update VAR Clients
     */
    public void saveVarClients() {
        selectedCompanies[0].setClients(helper.clients.getTarget());
        companyDAO.updateVarClients(
                selectedCompanies[0].getId(),
                selectedCompanies[0].getClients());

        JsfUtil.addSuccessMessage("Clients Updated");
    }

    /**
     * TODO: File upload! this does not work yet! PF bug that needs to be fixed
     *
     * @param event
     */
    public void logoUpload(FileUploadEvent event) {
        BufferedImage bufferedImage;

        try {
            String test = event.getFile().getContentType();

            // Convert the event input stream into a buffered image
            bufferedImage = ImageIO.read(event.getFile().getInputstream());

            // Generate an s3Key
            String[] s3Key = s3KeyUtil.companyLogo(selectedCompanies[0].getId());

            // Save the image to S3
            s3Util.imageToS3(
                    bufferedImage,
                    s3Key[1],
                    285,
                    S3ContentType.JPEG,
                    CannedAccessControlList.PublicRead);

            bufferedImage.flush();
            JsfUtil.addSuccessMessage("New Logo Uploaded.");

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error converting uploaded file to BufferedImage", ex);
            JsfUtil.addErrorMessage("There was an error saving your image. Please contact support.");
        } catch (Exception e) {
            JsfUtil.addErrorMessage("There was an error saving your image. Please contact support.");
        }
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
                url = ectx.getRequestContextPath() + "/a/companies/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/a/companies/list.jsf?filter=" + filterId;
            }
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/b/companies/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/b/companies/list.jsf?filter=" + filterId;
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    /**
     * This is a simple class designed to tidy up the outer-class by
     * encapsulating some variables that are needed to complete some of the out
     * methods.
     */
    public class Helper {

        // Variables----------------------------------------------------------------
        private DualListModel<Site> clientSites;
        private DualListModel<Site> varSites;
        private DualListModel<Company> clients;
        private List<Company> vars = new ArrayList<>();

        // Constructors-------------------------------------------------------------
        public Helper() {
            varSites = new DualListModel<>(new ArrayList<Site>(), new ArrayList<Site>());
        }

        // Getters------------------------------------------------------------------
        public DualListModel<Site> getClientSites() {
            return clientSites;
        }

        public DualListModel<Site> getVarSites() {
            return varSites;
        }

        public DualListModel<Company> getClients() {
            return clients;
        }

        public List<Company> getVars() {
            return vars;
        }

        // Setters------------------------------------------------------------------
        public void setClientSites(DualListModel<Site> clientSites) {
            this.clientSites = clientSites;
        }

        public void setVarSites(DualListModel<Site> varSites) {
            this.varSites = varSites;
        }

        public void setVars(List<Company> vars) {
            this.vars = vars;
        }

        public void setClients(DualListModel<Company> clients) {
            this.clients = clients;
        }

        // Methods------------------------------------------------------------------
        /**
         * Filters the available sites based on the subjects metier and the type
         * of company they are trying to access.
         */
        public void filterSites() {
            // VAR access
            if (selectedCompanies[0].getType().equals(DastraxCst.CompanyType.VAR.toString())) {
                helper.varSites = new DualListModel<>();
                varSites.setSource(siteDAO.findSitesByNullVar());
                varSites.setTarget(selectedCompanies[0].getVarSites());
            }
            // CLIENT access
            if (selectedCompanies[0].getType().equals(DastraxCst.CompanyType.CLIENT.toString())) {
                helper.clientSites = new DualListModel<>();
                if (selectedCompanies[0].getParentVAR() != null) {
                    clientSites.setSource(siteDAO.findSitesByNullClient(selectedCompanies[0].getParentVAR().getId()));
                    clientSites.setTarget(selectedCompanies[0].getClientSites());
                } else {
                    clientSites.setSource(new ArrayList<Site>());
                    clientSites.setTarget(selectedCompanies[0].getClientSites());
                }
            }
        }

        /**
         * Filters the available companies based on the subject.
         */
        public void filterClients() {
            // Get all the clients without parent VARs
            List<Company> cs = companyDAO.findByNullParentVar(DastraxCst.CompanyType.CLIENT.toString());
            helper.clients = new DualListModel<>(cs, selectedCompanies[0].getClients());
        }
    }

}
