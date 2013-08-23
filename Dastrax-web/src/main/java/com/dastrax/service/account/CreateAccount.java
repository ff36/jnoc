/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.account;

import com.amazonaws.AmazonClientException;
import com.dastrax.per.dao.core.CompanyDAO;
import com.dastrax.per.dao.core.EmailParamDAO;
import com.dastrax.per.dao.core.EmailTemplateDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Account;
import com.dastrax.per.entity.core.Address;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Contact;
import com.dastrax.per.entity.core.EmailParam;
import com.dastrax.per.entity.core.EmailTemplate;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.core.Telephone;
import com.dastrax.per.exception.DuplicateEmailException;
import com.dastrax.app.pojo.Response;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.app.security.PasswordSvcs;
import com.dastrax.app.util.ExceptionUtil;
import com.dastrax.mesh.email.Email;
import com.dastrax.mesh.email.EmailUtil;
import com.dastrax.per.dao.core.AuditDAO;
import com.dastrax.service.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class CreateAccount implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(CreateAccount.class.getName());

    // Variables----------------------------------------------------------------
    private Subject subject = new Subject();
    private Helper helper = new Helper();

    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    EmailTemplateDAO emailTemplateDAO;
    @EJB
    EmailUtil emailUtil;
    @EJB
    EmailParamDAO emailParamDAO;
    @EJB
    ExceptionUtil exu;
    @EJB
    PasswordSvcs passwordSvcs;
    @EJB
    CompanyDAO companyDAO;
    @EJB
    AuditDAO auditDAO;

    // Constructors-------------------------------------------------------------
    public CreateAccount() {
        subject.setAccount(new Account());
        subject.setContact(new Contact());
        subject.getContact().getAddresses().add(new Address());
        subject.getContact().getTelephones().add(new Telephone());
    }

    @PostConstruct
    private void postConstruct() {
        // Set the companies that the subject has access to
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            helper.vars = companyDAO.findCompaniesByType(DastraxCst.CompanyType.VAR.toString());
            helper.clients = companyDAO.findCompaniesByType(DastraxCst.CompanyType.CLIENT.toString());
        } else if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            for (Company comp : s.getCompany().getClients()) {
                helper.clients.add(comp);
            }
        }
    }
    
    /**
     * Called by the account confirmation page
     */
    public void init() {
        EmailParam ep = emailParamDAO.findParamByToken(helper.getToken());
        if (ep != null) {
            helper.setRenderPublicForm(true);
        } else {
            JsfUtil.addWarningMessage("Please use the link in your email to confirm your account.");
        }
    }

    // Getters------------------------------------------------------------------
    public Subject getSubject() {
        return subject;
    }

    public Helper getHelper() {
        return helper;
    }

    // Setters------------------------------------------------------------------
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    // Methods------------------------------------------------------------------
    /**
     * When a subject requests a new administrator account this method performs
     * the required checks before sending them an email with a confirmation link
     * and a pin code. The account remains unconfirmed until it is either
     * confirmed or the housekeeping protocols delete the unconfirmed account.
     *
     * @return if the request was successfully processed and the account was
     * created a navigation string is returned, otherwise null.
     */
    public String administrator() {

        String result = null;

        try {
            // Persist the administrator
            Subject s = subjectDAO.createAdmin(subject);
            // Send an email
            EmailParam ep = sendRegEmail(s);
            // Persist the email params
            emailParamDAO.create(ep);

            // Add success message
            JsfUtil.addSuccessMessage("New administrator successfully created");

            if (helper.redirect()) {

                // Carry the message over to the page redirect
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getFlash()
                        .setKeepMessages(true);

                // Create an audit log of the event
                //audit.create("Admin Account Requested: (ID: " + s.getUid() + ")");
                // Set the navigation outcome
                result = "access-accounts-list-page";
            }

        } catch (DuplicateEmailException dee) {
            JsfUtil.addWarningMessage("This email address is already registered.");
        } catch (AmazonClientException ace) {
            JsfUtil.addErrorMessage("There was an error sending the confirmation email. Please contact support.");
        }

        return result;
    }

    /**
     * When a subject requests a new VAR account this method performs the
     * required checks before sending them an email with a confirmation link and
     * a pin code. The account remains unconfirmed until it is either confirmed
     * or the housekeeping protocols delete the unconfirmed account.
     *
     * @return if the request was successfully processed and the account was
     * created a navigation string is returned, otherwise null.
     */
    public String var() {

        String result = null;

        try {
            // Set the company
            // ADMIN access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
                subject.setCompany(companyDAO.findCompanyById(helper.selectedCompany));
            }
            // VAR access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
                Subject s = subjectDAO.findSubjectByUid(
                        SecurityUtils.getSubject().getPrincipals()
                        .asList().get(1).toString());
                subject.setCompany(s.getCompany());
            }

            // Persist the var
            Subject s = subjectDAO.createVar(subject);
            // Send an email
            EmailParam ep = sendRegEmail(s);
            // Persist the email params
            emailParamDAO.create(ep);

            // Add success message
            JsfUtil.addSuccessMessage("New VAR successfully created");

            if (helper.redirect()) {
                // Carry the message over to the page redirect
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getFlash()
                        .setKeepMessages(true);

                // Create an audit log of the event
                //audit.create("Admin Account Requested: (ID: " + s.getUid() + ")");
                // Set the navigation outcome
                result = "access-accounts-list-page";
            }

        } catch (DuplicateEmailException dee) {
            JsfUtil.addWarningMessage("This email address is already registered.");
        } catch (AmazonClientException ace) {
            JsfUtil.addErrorMessage("There was an error sending the confirmation email. Please contact support.");
        }

        return result;
    }

    /**
     * When a subject requests a new Client account this method performs the
     * required checks before sending them an email with a confirmation link and
     * a pin code. The account remains unconfirmed until it is either confirmed
     * or the housekeeping protocols delete the unconfirmed account.
     *
     * @return if the request was successfully processed and the account was
     * created a navigation string is returned, otherwise null.
     */
    public String client() {

        String result = null;

        try {
            // Set the company
            // ADMIN access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
                subject.setCompany(companyDAO.findCompanyById(helper.selectedCompany));
            }
            // VAR access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
                subject.setCompany(companyDAO.findCompanyById(helper.selectedCompany));
            }
            // CLIENT access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
                Subject s = subjectDAO.findSubjectByUid(
                        SecurityUtils.getSubject().getPrincipals()
                        .asList().get(1).toString());
                subject.setCompany(s.getCompany());
            }

            // Persist the administrator
            Subject s = subjectDAO.createClient(subject);
            // Send an email
            EmailParam ep = sendRegEmail(s);
            // Persist the email params
            emailParamDAO.create(ep);

            // Add success message
            JsfUtil.addSuccessMessage("New Client successfully created");

            if (helper.redirect()) {
                // Carry the message over to the page redirect
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getFlash()
                        .setKeepMessages(true);

                // Create an audit log of the event
                //audit.create("Admin Account Requested: (ID: " + s.getUid() + ")");
                // Set the navigation outcome
                result = "access-accounts-list-page";
            }

        } catch (DuplicateEmailException dee) {
            JsfUtil.addWarningMessage("This email address is already registered.");
        } catch (AmazonClientException ace) {
            JsfUtil.addErrorMessage("There was an error sending the confirmation email. Please contact support.");
        }

        return result;
    }

    /**
     * Once the registration is successful of the new administrator an email
     * needs to be constructed and sent containing all the required info.
     *
     * @param subject
     * @return The populated EmailParam object
     * @throws Exception
     * @throws AmazonClientException
     */
    private EmailParam sendRegEmail(Subject subject) {

        // Generate a new pincode
        Integer pin = new Random().nextInt(9000) + 1000;

        // Build an new email
        Email email = new Email();

        // Set the recipient
        email.setRecipientEmail(subject.getEmail().toLowerCase());
        // Set the persistable params
        email.getParam().setEmail(subject.getEmail().toLowerCase());
        email.getParam().setUid(subject.getUid());
        email.getParam().setToken(UUID.randomUUID().toString());
        email.getParam().setCreationEpoch(Calendar.getInstance().getTimeInMillis());
        email.getParam().setParamA(pin.toString());

        // Set the variables
        Map<String, String> vars = new HashMap<>();
        vars.put("pin", pin.toString());
        vars.put("name", subject.getContact().buildFullName());
        email.setVariables(vars);

        // Retreive the email template from the database.
        EmailTemplate et = emailTemplateDAO.findTemplateById(
                DastraxCst.EmailTemplate.NEW_ACCOUNT.toString());
        email.setTemplate(et);

        // Send the email
        emailUtil.build(email);

        return email.getParam();
    }

    /**
     * Accessed when a subject needs their account confirmation email resent.
     */
    public void resendRegEmail() {

        Subject s = subjectDAO.findSubjectByEmail(helper.email);

        if (s != null) {
            if (!s.getAccount().isConfirmed()) {
                Email email = new Email();
                // Set the recipient
                email.setRecipientEmail(helper.email);
                // Retrieve the params and set them for the email
                email.setParam(emailParamDAO.findParamByEmail(helper.email));

                Map<String, String> vars = new HashMap<>();
                vars.put("pin", email.getParam().getParamA());
                email.setVariables(vars);

                // Retreive the email template from the database.
                EmailTemplate et = emailTemplateDAO.findTemplateById(
                        DastraxCst.EmailTemplate.NEW_ACCOUNT.toString());
                email.setTemplate(et);

                // Send the email
                emailUtil.build(email);

                // Add success message
                JsfUtil.addSuccessMessage("We have sent an email containing instructions on how to complete the process.");

                // Create an audit log of the event
                //audit.create("Resent registration email to: " + s.getUid() + "(" + helper.email + ")");
            } else {
                // Account already confirmed
                JsfUtil.addWarningMessage("Account already confirmed");
            }
        } else {
            // The database search did not find the email
            JsfUtil.addWarningMessage("Could not find an account linked to that address.");
        }
    }

    /**
     * Confirmation response from the account request email. This method must be
     * executed in order to activate an account.
     */
    public void confirm() {

        if (helper.legal == true) {

            // Retrieve the param
            EmailParam ep = emailParamDAO.findParamByToken(helper.token);

            // Check the PIN is correct
            if (ep.getParamA().equals(helper.pinCode.toString())) {

                // Combine the requested passwords
                String[] tempPsw = {helper.tempPswA, helper.tempPswB};

                Response r1 = passwordSvcs.validate(tempPsw, ep.getEmail());

                // Check if the password meets the requirements 
                if (r1.getObject() != null) {
                    Subject s = subjectDAO.findSubjectByEmail(ep.getEmail());
                    if (s != null) {
                        subjectDAO.confirm(s.getUid(), (String) r1.getObject());
                        emailParamDAO.delete(ep.getToken());

                        // Add JSF message
                        JsfUtil.addSuccessMessage("Congratulations! You're account has been confirmed and you can now sign into your account");

                        // Create an audit log of the event
                        auditDAO.create("Account confirmed: " + s.getContact().buildFullName());
                        helper = new Helper();
                    } else {
                        JsfUtil.addErrorMessage("We could not find any records of this account.");
                    }
                } else {
                    // Add any messages that exist in the response
                    r1.renderJsfMsgs();
                }
            }
        } else {
            JsfUtil.addWarningMessage("Please accept all the legal terms and conditions.");
        }
    }

    /**
     * This is a simple class designed to tidy up the outer-class by
     * encapsulating some variables that are needed to complete some of the out
     * methods.
     */
    public class Helper {

        // Variables----------------------------------------------------------------
        @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid Email")
        private String email;
        private String token;
        private String tempPswA;
        private String tempPswB;
        private Integer pinCode;
        private boolean legal;
        private String subjectPsw;
        private String accountType;
        private String selectedCompany;
        private List<Company> vars = new ArrayList<>();
        private List<Company> clients = new ArrayList<>();
        private boolean renderPublicForm;

        // Constructors-------------------------------------------------------------
        public Helper() {
        }

        // Getters------------------------------------------------------------------
        public String getEmail() {
            return email;
        }

        public String getToken() {
            return token;
        }

        public Integer getPinCode() {
            return pinCode;
        }

        public boolean isLegal() {
            return legal;
        }

        public String getSubjectPsw() {
            return subjectPsw;
        }

        public String getTempPswA() {
            return tempPswA;
        }

        public String getTempPswB() {
            return tempPswB;
        }

        public String getAccountType() {
            return accountType;
        }

        public List<Company> getVars() {
            return vars;
        }

        public List<Company> getClients() {
            return clients;
        }

        public String getSelectedCompany() {
            return selectedCompany;
        }

        public boolean isRenderPublicForm() {
            return renderPublicForm;
        }

        // Setters------------------------------------------------------------------
        public void setEmail(String email) {
            this.email = email;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setPinCode(Integer pinCode) {
            this.pinCode = pinCode;
        }

        public void setLegal(boolean legal) {
            this.legal = legal;
        }

        public void setSubjectPsw(String subjectPsw) {
            this.subjectPsw = subjectPsw;
        }

        public void setTempPswA(String tempPswA) {
            this.tempPswA = tempPswA;
        }

        public void setTempPswB(String tempPswB) {
            this.tempPswB = tempPswB;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public void setVars(List<Company> vars) {
            this.vars = vars;
        }

        public void setClients(List<Company> clients) {
            this.clients = clients;
        }

        public void setSelectedCompany(String selectedCompany) {
            this.selectedCompany = selectedCompany;
        }

        public void setRenderPublicForm(boolean renderPublicForm) {
            this.renderPublicForm = renderPublicForm;
        }

        // Methods------------------------------------------------------------------
        /**
         * Accounts can be created from several places. This method is used to 
         * verify that the request is coming from the main account creation page.
         * @return true if the current URL matches the main account creation URL.
         */
        public boolean redirect() {
            ExternalContext extc = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) extc.getRequest();
            String contextURL = request.getRequestURI();
            // ADMIN access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
                return "/a/accounts/create.jsf".equals(contextURL);
            }
            // VAR access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
                return "/b/accounts/create.jsf".equals(contextURL);
            }
            return false;
        }

    }

}
