/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.company;

import com.dastrax.per.dao.core.CompanyDAO;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Contact;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.app.pojo.Response;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.app.security.PasswordSvcs;
import com.dastrax.app.util.DnsUtil;
import com.dastrax.app.util.ExceptionUtil;
import com.dastrax.service.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.primefaces.model.DualListModel;

/**
 *
 * @version Build 2.0.0
 * @since Jul 25, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class CreateCompany implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(CreateCompany.class.getName());

    // Variables----------------------------------------------------------------
    private Company company = new Company();
    private Helper helper = new Helper();

    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    CompanyDAO companyDAO;
    @EJB
    SiteDAO siteDAO;
    @EJB
    DnsUtil dnsUtil;
    @EJB
    ExceptionUtil exu;
    @EJB
    PasswordSvcs passwordSvcs;

    // Constructors-------------------------------------------------------------
    public CreateCompany() {
        company.setMembers(new ArrayList<Subject>());
        company.setVarSites(new ArrayList<Site>());
        company.setClientSites(new ArrayList<Site>());
        company.setClients(new ArrayList<Company>());
        company.setContacts(new ArrayList<Contact>());
        company.getContacts().add(new Contact());
    }

    @PostConstruct
    private void init() {
        // Get all the clients without parent VARs
        List<Company> cs = companyDAO.findByNullParentVar(DastraxCst.CompanyType.CLIENT.toString());
        helper.clients = new DualListModel<>(cs, company.getClients());

        // Get all VARs so select from when creating a Client
        helper.vars = companyDAO.findCompaniesByType(DastraxCst.CompanyType.VAR.toString());
    }

    // Getters------------------------------------------------------------------
    public Company getCompany() {
        return company;
    }

    public Helper getHelper() {
        return helper;
    }

    // Setters------------------------------------------------------------------
    public void setCompany(Company company) {
        this.company = company;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    // Methods------------------------------------------------------------------
    /**
     * Create a new VAR company
     * @return navigation String
     */
    public String var() {

        String result = null;

        company.setVarSites(helper.varSites.getTarget());
        company.setClients(helper.clients.getTarget());

        // Write the new subdomain to route 53
        Response response = dnsUtil.createSubdomain(company.getSubdomain());

        if ((Boolean) response.getObject()) {
            // Persist the new var
            Company c = companyDAO.createVar(company);

            // Add success message
            JsfUtil.addSuccessMessage("New VAR successfully created");
            // Carry the message over to the page redirect
            FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .getFlash()
                    .setKeepMessages(true);

            // Create an audit log of the event
            //audit.create("Admin Account Requested: (ID: " + s.getUid() + ")");
            // Set the navigation outcome
            result = "access-list-companies-page";
        } else {
            // The DNS writing was an issue
            response.renderJsfMsgs();
        }
        return result;
    }

    /**
     * Create a new CLIENT company
     * @return navigation String
     */
    public String client() {

        String result;

        // Set the parent VAR
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            company.setParentVAR(companyDAO.findCompanyById(helper.selectedVar));
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            company.setParentVAR(s.getCompany());
        }

        company.setClientSites(helper.clientSites.getTarget());

        // Persist the client
        Company c = companyDAO.createClient(company);

        // Add success message
        JsfUtil.addSuccessMessage("New Client successfully created");
        // Carry the message over to the page redirect
        FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getFlash()
                .setKeepMessages(true);

        // Set the navigation outcome
        result = "access-list-companies-page";

        return result;
    }

    /**
     * Add new contact to the company
     */
    public void addContact() {
        company.getContacts().add(new Contact());
    }

    /**
     * Remove the specified contact from the company.
     * @param contact 
     */
    public void removeContact(Contact contact) {
        Iterator<Contact> i = company.getContacts().iterator();
        while (i.hasNext()) {
            Contact c = i.next();
            if (c == contact) {
                i.remove();
            }
        }
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
        private List<Company> vars;
        private String selectedVar;

        // Constructors-------------------------------------------------------------
        public Helper() {
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

        public String getSelectedVar() {
            return selectedVar;
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

        public void setSelectedVar(String selectedVar) {
            this.selectedVar = selectedVar;
        }

        // Methods------------------------------------------------------------------
        /**
         * Filters the available sites based on the subjects metier and the type
         * of company they are trying to access.
         */
        public void filterSites() {
            // ADMIN access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
                if (company.getType().equals(DastraxCst.CompanyType.VAR.toString())) {
                    helper.varSites = new DualListModel<>();
                    varSites.setSource(siteDAO.findSitesByNullVar());
                    varSites.setTarget(company.getVarSites());
                }
                if (company.getType().equals(DastraxCst.CompanyType.CLIENT.toString())) {
                    helper.clientSites = new DualListModel<>();
                    if (selectedVar != null && !"".equals(selectedVar)) {
                        clientSites.setSource(siteDAO.findSitesByNullClient(selectedVar));
                        clientSites.setTarget(company.getClientSites());
                    } else {
                        clientSites.setSource(new ArrayList<Site>());
                        clientSites.setTarget(company.getClientSites());
                    }
                }
            }
            // VAR access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
                if (company.getType().equals(DastraxCst.CompanyType.CLIENT.toString())) {

                    Subject s = subjectDAO.findSubjectByUid(
                            SecurityUtils.getSubject().getPrincipals()
                            .asList().get(1).toString());
                    
                    helper.clientSites = new DualListModel<>();
                    clientSites.setSource(siteDAO.findSitesByNullClient(s.getCompany().getId()));
                    clientSites.setTarget(s.getCompany().getClientSites());

                }
            }
        }

        /**
         * Filters the available sites based on the subjects metier and the type
         * of company they are trying to access.
         * @param type of company
         */
        public void quickFilterSites(String type) {
            company.setType(type);
            filterSites();
        }
    }

}
