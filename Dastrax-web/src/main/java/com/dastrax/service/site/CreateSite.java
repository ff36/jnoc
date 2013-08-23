/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.site;

import com.dastrax.per.dao.core.CompanyDAO;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.entity.core.Address;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Contact;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Telephone;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.app.util.TemporalUtil;
import com.dastrax.service.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.Pattern;

/**
 *
 * @version Build 2.0.0
 * @since Jul 25, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class CreateSite implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(CreateSite.class.getName());

    // Variables----------------------------------------------------------------
    private Site site = new Site();
    private Helper helper = new Helper();

    // EJB----------------------------------------------------------------------
    @EJB
    SiteDAO siteDAO;
    @EJB
    TemporalUtil temporalUtil;
    @EJB
    CompanyDAO companyDAO;

    // Constructors-------------------------------------------------------------
    public CreateSite() {
        site.setAddress(new Address());
        site.setContacts(new ArrayList<Contact>());
        Contact c = new Contact();
        c.setAddresses(new ArrayList<Address>());
        c.addAddress();
        c.setTelephones(new ArrayList<Telephone>());
        c.addTelephone();
        site.getContacts().add(c);
    }
    
    @PostConstruct
    private void postConstruct() {
        helper.vars = companyDAO.findCompaniesByType(DastraxCst.CompanyType.VAR.toString());
    }

    // Getters------------------------------------------------------------------
    public Site getSite() {
        return site;
    }

    public Helper getHelper() {
        return helper;
    }

    public Date getInstallDate() {
        return temporalUtil.epochToDate(site.getInstallEpoch());
    }

    // Setters------------------------------------------------------------------
    public void setSite(Site site) {
        this.site = site;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    public void setInstallDate(Date date) {
        site.setInstallEpoch(temporalUtil.dateToEpoch(date));
    }
    // Methods------------------------------------------------------------------
    /**
     * Register a new site
     * @return a navigation string
     */
    public String register() {
        String result = null;

        site.setDmsIP(helper.concatIP());
        if (site.getDmsIP() != null) {
            if (site.getDmsType() != null) {
                
                // Set the VAR and the Installer
                site.setVar(companyDAO.findCompanyById(helper.selectedVar));
                site.setInstaller(companyDAO.findCompanyById(helper.selectedInstaller));
                // Persist the site
                siteDAO.create(site);

                // Add success message
                JsfUtil.addSuccessMessage("New site successfully created");
                // Carry the message over to the page redirect
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getFlash()
                        .setKeepMessages(true);

                // Create an audit log of the event
                //audit.create("Admin Account Requested: (ID: " + s.getUid() + ")");
                // Set the navigation outcome
                result = "access-list-sites-page";
            } else {
                JsfUtil.addWarningMessage("Please select a DMS Firmware version");
            }
        } else {
            JsfUtil.addWarningMessage("Invalid IP");
        }
        return result;
    }

    /**
     * This is a simple class designed to tidy up the outer-class by
     * encapsulating some variables that are needed to complete some of the out
     * methods.
     */
    public class Helper {

        // Variables----------------------------------------------------------------
        @Pattern(regexp = "^([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])$", message = "Invalid IP")
        private String ipa;
        @Pattern(regexp = "^([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])$", message = "Invalid IP")
        private String ipb;
        @Pattern(regexp = "^([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])$", message = "Invalid IP")
        private String ipc;
        @Pattern(regexp = "^([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])$", message = "Invalid IP")
        private String ipd;
        private String selectedVar;
        private String selectedInstaller;
        private List<Company> vars = new ArrayList<>();
        
        // Constructors-------------------------------------------------------------
        public Helper() {
        }

        // Getters------------------------------------------------------------------
        public String getIpa() {
            return ipa;
        }

        public String getIpb() {
            return ipb;
        }

        public String getIpc() {
            return ipc;
        }

        public String getIpd() {
            return ipd;
        }

        public String getSelectedVar() {
            return selectedVar;
        }

        public String getSelectedInstaller() {
            return selectedInstaller;
        }

        public List<Company> getVars() {
            return vars;
        }

        // Setters------------------------------------------------------------------
        public void setIpa(String ipa) {
            this.ipa = ipa;
        }

        public void setIpb(String ipb) {
            this.ipb = ipb;
        }

        public void setIpc(String ipc) {
            this.ipc = ipc;
        }

        public void setIpd(String ipd) {
            this.ipd = ipd;
        }

        public void setSelectedVar(String selectedVar) {
            this.selectedVar = selectedVar;
        }

        public void setSelectedInstaller(String selectedInstaller) {
            this.selectedInstaller = selectedInstaller;
        } 

        public void setVars(List<Company> vars) {
            this.vars = vars;
        }

        // Methods------------------------------------------------------------------
        /**
         * Converts the input value to a 3 digit integer with 0 prefix
         */
        public void formatA() {
            try {
                int num = Integer.parseInt(ipa);
                ipa = String.format("%03d", num);
            } catch (NumberFormatException nfe) {
            }
        }

        /**
         * Converts the input value to a 3 digit integer with 0 prefix
         */
        public void formatB() {
            try {
                int num = Integer.parseInt(ipb);
                ipb = String.format("%03d", num);
            } catch (NumberFormatException nfe) {
            }
        }

        /**
         * Converts the input value to a 3 digit integer with 0 prefix
         */
        public void formatC() {
            try {
                int num = Integer.parseInt(ipc);
                ipc = String.format("%03d", num);
            } catch (NumberFormatException nfe) {
            }
        }

        /**
         * Converts the input value to a 3 digit integer with 0 prefix
         */
        public void formatD() {
            try {
                int num = Integer.parseInt(ipd);
                ipd = String.format("%03d", num);
            } catch (NumberFormatException nfe) {
            }
        }

        /**
         * Converts the 4 IP octet into a full 12 digit format IP address
         * @return 
         */
        public String concatIP() {
            if (ipa != null && ipb != null && ipc != null && ipd != null) {
                return ipa + "." + ipb + "." + ipc + "." + ipd;
            } else {
                return null;
            }
        }

    }

}
