/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.account;

import com.dastrax.per.dao.core.CompanyDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.app.security.PasswordSvcs;
import com.dastrax.app.util.FilterUtil;
import com.dastrax.app.util.S3KeyUtil;
import com.dastrax.app.util.S3Util;
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
 * @since Jul 22, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class Subjects implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(Subjects.class.getName());

    // Variables----------------------------------------------------------------
    private List<Subject> subjects = new ArrayList<>();
    private Subject[] selectedSubjects;
    private SubjectModel subjectModel;
    private List<Subject> filtered;
    private String principalPassword;
    private String filter;
    private Helper helper = new Helper();

    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    PasswordSvcs passwordSvcs;
    @EJB
    CompanyDAO companyDAO;
    @EJB
    S3Util s3Util;
    @EJB
    S3KeyUtil s3KeyUtil;
    @EJB
    FilterUtil filterUtil;

    // Constructors-------------------------------------------------------------
    public void init() {
        Map<String, List<String>> metierFilter = filterUtil.authorizedCompanies();
        Map<String, List<String>> optFilter = filterUtil.optionalFilter(filter);
        subjectModel = new SubjectModel(subjectDAO, metierFilter, optFilter);

        // Populate a list of available vars and clients based on role
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            helper.vars = companyDAO.findCompaniesByType(DastraxCst.CompanyType.VAR.toString());
            helper.clients = companyDAO.findCompaniesByType(DastraxCst.CompanyType.CLIENT.toString());
        } 
        // VAR access
        else if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            for (Company comp : s.getCompany().getClients()) {
                helper.clients.add(comp);
            }
        }
    }

    // Getters------------------------------------------------------------------
    public List<Subject> getSubjects() {
        return subjects;
    }

    public Subject[] getSelectedSubjects() {
        return selectedSubjects;
    }

    public SubjectModel getSubjectModel() {
        return subjectModel;
    }

    public List<Subject> getFiltered() {
        return filtered;
    }

    public String getPrincipalPassword() {
        return principalPassword;
    }

    public int getSelectedLength() {
        if (selectedSubjects != null) {
            return selectedSubjects.length;
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
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public void setSelectedSubjects(Subject[] selectedSubjects) {
        this.selectedSubjects = selectedSubjects;
    }

    public void setSubjectModel(SubjectModel subjectModel) {
        this.subjectModel = subjectModel;
    }

    public void setFiltered(List<Subject> filtered) {
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
    public void deleteAccounts() {

        String dbPsw = subjectDAO.findSubjectPasswordByUid(
                SecurityUtils.getSubject().getPrincipals()
                .asList().get(1).toString());

        // Check password
        if (passwordSvcs.passwordCorrect(principalPassword, dbPsw)) {

            for (Subject s : selectedSubjects) {
                if (!s.getUid().equals(
                        SecurityUtils.getSubject().getPrincipals()
                        .asList().get(1).toString())) {
                    // Delete the S3 directory
                    s3Util.deleteDirectory(s3KeyUtil.subjectDir(selectedSubjects[0].getUid()));
                    // Delete the account from the database
                    subjectDAO.delete(s.getUid());

                    JsfUtil.addSuccessMessage("Account " + s.getEmail() + " successfully deleted");
                } else {
                    JsfUtil.addWarningMessage("You cannot delete your own account. To close you account please go to your account settings");
                }
            }

            // In view scoped bean we just want to clear variables
            selectedSubjects = null;

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
        subjectDAO.updateName(
                selectedSubjects[0].getUid(),
                selectedSubjects[0].getContact().getFirstName(),
                selectedSubjects[0].getContact().getLastName());
        JsfUtil.addSuccessMessage("Name Updated");
    }

    /**
     * Change account ACL
     */
    public void saveACL() {
        subjectDAO.updateACL(
                selectedSubjects[0].getUid(),
                selectedSubjects[0].getAccount().isLocked());
        if (selectedSubjects[0].getAccount().isLocked()) {
            JsfUtil.addSuccessMessage("Account locked");
        } else {
            JsfUtil.addSuccessMessage("Account unlocked");
        }
    }

    /**
     * Save company changes
     */
    public void saveCompany() {
        // Set the selected company
        selectedSubjects[0].setCompany(companyDAO.findCompanyById(helper.selectedCompany));
        subjectDAO.updateCompany(
                selectedSubjects[0].getUid(),
                selectedSubjects[0].getCompany());
        JsfUtil.addSuccessMessage("Company Updated");
    }

    /**
     * Update address
     */
    public void saveAddress() {
        subjectDAO.updateAddress(
                selectedSubjects[0].getUid(),
                selectedSubjects[0].getContact().getAddresses());

        JsfUtil.addSuccessMessage("Address Updated");
    }

    /**
     * Save telephone numbers
     */
    public void saveTelephone() {
        subjectDAO.updateTelephone(
                selectedSubjects[0].getUid(),
                selectedSubjects[0].getContact().getTelephones());

        JsfUtil.addSuccessMessage("Telephone Updated");
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
                url = ectx.getRequestContextPath() + "/a/accounts/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/a/accounts/list.jsf?filter=" + filterId;
            }
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/b/accounts/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/b/accounts/list.jsf?filter=" + filterId;
            }
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/c/accounts/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/c/accounts/list.jsf?filter=" + filterId;
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
        private String selectedCompany;
        private List<Company> vars = new ArrayList<>();
        private List<Company> clients = new ArrayList<>();

        // Constructors-------------------------------------------------------------
        public Helper() {
        }

        // Getters------------------------------------------------------------------
        public List<Company> getVars() {
            return vars;
        }

        public List<Company> getClients() {
            return clients;
        }

        public String getSelectedCompany() {
            return selectedCompany;
        }

        // Setters------------------------------------------------------------------
        public void setVars(List<Company> vars) {
            this.vars = vars;
        }

        public void setClients(List<Company> clients) {
            this.clients = clients;
        }

        public void setSelectedCompany(String selectedCompany) {
            this.selectedCompany = selectedCompany;
        }

    }

}
