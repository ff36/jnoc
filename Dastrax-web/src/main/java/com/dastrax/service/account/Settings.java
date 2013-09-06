/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.account;

import com.dastrax.per.dao.core.EmailParamDAO;
import com.dastrax.per.dao.core.EmailTemplateDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.EmailParam;
import com.dastrax.per.entity.core.EmailTemplate;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.app.pojo.Response;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.app.security.PasswordSvcs;
import com.dastrax.app.util.S3KeyUtil;
import com.dastrax.app.util.S3Util;
import com.dastrax.mesh.email.Email;
import com.dastrax.mesh.email.EmailUtil;
import com.dastrax.per.entity.core.Address;
import com.dastrax.per.entity.core.Telephone;
import com.dastrax.service.util.JsfUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.Pattern;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Aug 1, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class Settings implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(Settings.class.getName());

    // Variables----------------------------------------------------------------
    private Subject subject;
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
    PasswordSvcs passwordSvcs;
    @EJB
    S3Util s3Util;
    @EJB
    S3KeyUtil s3KeyUtil;

    // Constructors-------------------------------------------------------------
    @PostConstruct
    private void postConstruct() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            subject = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString()
                    );
        }

    }

    /**
     * Called by viewParam after attributes have been set but before the page is
     * rendered. This should only be called from the public password reset page
     * and the email change confirm page.
     */
    public void init() {
        helper.emailParam = emailParamDAO.findParamByToken(helper.token);
        if (helper.emailParam != null) {
            helper.setRenderPublicForm(true);
            helper.email = helper.emailParam.getEmail();
        } else {
            JsfUtil.addWarningMessage("Please use the link in the email.");
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
     * Save profile details
     */
    public void saveContact() {
        subjectDAO.updateContact(
                subject.getUid(),
                subject.getContact());
        JsfUtil.addSuccessMessage("Profile Updated");
    }

    /**
     * Process change of password when it is accessed from the authenticated domain
     */
    public void savePassword() {

        String[] psws = {helper.tempPswA, helper.tempPswB};
        Response response = passwordSvcs.validateExt(
                psws,
                subject.getEmail(),
                helper.principalPsw,
                subject.getPassword());
        if (response.getObject() != null) {
            subjectDAO.updatePassword(
                    subject.getUid(),
                    (String) response.getObject());
            JsfUtil.addSuccessMessage("Password Updated");
        } else {
            // Password failed
            response.renderJsfMsgs();
        }
    }

    /**
     * Process change of email request
     */
    public void changeEmailRequest() {

        // Check to make sure the email is available
        if (subjectDAO.findSubjectByEmail(helper.newEmail) == null) {
            // Persist the email params
            EmailParam ep = sendEmail(subject, DastraxCst.EmailTemplate.CHANGE_EMAIL.toString());
            emailParamDAO.create(ep);

            JsfUtil.addSuccessMessage("An email containing instructions on how to complete the process has been sent to " + ep.getEmail());
        } else {
            JsfUtil.addWarningMessage(helper.newEmail + " is already registered to another account.");
        }
    }

    /**
     * Process change of password request
     */
    public void changePswRequest() {
        // Check the account is registered
        Subject s = subjectDAO.findSubjectByEmail(helper.email);
        if (s != null) {
            emailParamDAO.create(
                    sendEmail(s, DastraxCst.EmailTemplate.CHANGE_PASSWORD.toString())
                    );
            JsfUtil.addSuccessMessage("An email containing instructions on how to complete the process has been sent to " + s.getEmail());
        } else {
            JsfUtil.addWarningMessage(helper.email + " is not a registered account.");
        }
    }

    /**
     * When a password is reset from the public domain
     */
    public void savePublicPsw() {
        subject = subjectDAO.findSubjectByEmail(helper.emailParam.getParamA());
        String[] psws = {helper.tempPswA, helper.tempPswB};
        Response response = passwordSvcs.validate(psws, subject.getEmail());
        if (response.getObject() != null) {
            // If the account is not yet confirmed we can use this to confirm it
            if (!subject.getAccount().isConfirmed()) {
                subjectDAO.confirm(
                        subject.getUid(),
                        (String) response.getObject());
            } else {
                // Update the password
                subjectDAO.updatePassword(
                        subject.getUid(),
                        (String) response.getObject());
            }
            emailParamDAO.delete(helper.token);
            helper = new Helper();
            JsfUtil.addSuccessMessage("Password Updated");
        } else {
            // Password failed
            response.renderJsfMsgs();
        }
    }

    /**
     * Construct an email to the newly requested address and persist the
     * EmailParam to confirm the authentication at a later date.
     *
     * @param subject
     * @return The populated EmailParam object
     * @throws Exception
     * @throws AmazonClientException
     */
    private EmailParam sendEmail(Subject subject, String template) {

        // Build an new email
        Email email = new Email();

        // Set the recipient
        email.setRecipientEmail(subject.getEmail().toLowerCase());
        // Set the persistable params
        email.getParam().setEmail(helper.newEmail);
        email.getParam().setUid(subject.getUid());
        email.getParam().setToken(UUID.randomUUID().toString());
        email.getParam().setCreationEpoch(Calendar.getInstance().getTimeInMillis());
        email.getParam().setParamA(subject.getEmail().toLowerCase());

        // Set the variables
        Map<String, String> vars = new HashMap<>();
        vars.put("name", subject.getContact().buildFullName());
        email.setVariables(vars);

        // Retreive the email template from the database.
        EmailTemplate et = emailTemplateDAO.findTemplateById(template);
        email.setTemplate(et);

        // Send the email
        emailUtil.build(email);

        return email.getParam();
    }

    /**
     * Confirm change of email
     */
    public void confirm() {
        // Retrieve the param
        EmailParam ep = emailParamDAO.findParamByToken(helper.token);
        if (ep != null) {
            // Check to make sure the email is available
            if (subjectDAO.findSubjectByEmail(ep.getEmail()) == null) {
                // Get the subject
                subject = subjectDAO.findSubjectByEmail(ep.getParamA());
                // Check the password
                if (passwordSvcs.passwordCorrect(helper.principalPsw, subject.getPassword())) {
                    subjectDAO.updateEmail(
                            subject.getUid(),
                            ep.getEmail());
                    emailParamDAO.delete(ep.getToken());

                    SecurityUtils.getSubject().logout();
                    helper = new Helper();
                    JsfUtil.addSuccessMessage("New email updated. For security you have been signed out of your account and will need to sign back in using your new email.");
                } else {
                    // Wrong password
                    JsfUtil.addWarningMessage("Wrong password");
                }
            } else {
                // Already registered
                JsfUtil.addWarningMessage(ep.getEmail() + " is already registered to another account.");
            }
        } else {
            // ep was null
            JsfUtil.addWarningMessage("This account doesn't appear to have requested a change of email.");
        }
    }

    /**
     * Closing your own account
     *
     * @return
     */
    public String closeAccount() {
        String result = null;

        // Check password
        if (passwordSvcs.passwordCorrect(helper.principalPsw, subject.getPassword())) {
            // Delete the S3 directory
            s3Util.deleteDirectory(s3KeyUtil.subjectDir(subject.getUid()));
            // Delete the subject from the db
            subjectDAO.delete(subject.getUid());
            // Add success message
            JsfUtil.addSuccessMessage("Your account has been closed");
            // Carry the message over to the page redirect
            FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .getFlash()
                    .setKeepMessages(true);
            SecurityUtils.getSubject().logout();
            result = "logout";
        } else {
            // Wrong password
            JsfUtil.addErrorMessage("Invalid Password");
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
        private String newEmail;
        private String token;
        private String view = "PROFILE";
        private String principalPsw;
        private String tempPswA;
        private String tempPswB;
        private String email;
        private boolean renderPublicForm;
        private EmailParam emailParam;
        private Address address = new Address();
        private Telephone telephone = new Telephone();
        

        // Constructors-------------------------------------------------------------
        public Helper() {
        }

        // Getters------------------------------------------------------------------
        public String getNewEmail() {
            return newEmail;
        }

        public String getToken() {
            return token;
        }

        public String getView() {
            return view;
        }

        public String getPrincipalPsw() {
            return principalPsw;
        }

        public String getTempPswA() {
            return tempPswA;
        }

        public String getTempPswB() {
            return tempPswB;
        }

        public String getEmail() {
            return email;
        }

        public boolean isRenderPublicForm() {
            return renderPublicForm;
        }

        public Address getAddress() {
            return address;
        }

        public Telephone getTelephone() {
            return telephone;
        }

        // Setters------------------------------------------------------------------
        public void setNewEmail(String newEmail) {
            this.newEmail = newEmail;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setView(String view) {
            this.view = view;
        }

        public void setPrincipalPsw(String principalPsw) {
            this.principalPsw = principalPsw;
        }

        public void setTempPswA(String tempPswA) {
            this.tempPswA = tempPswA;
        }

        public void setTempPswB(String tempPswB) {
            this.tempPswB = tempPswB;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setRenderPublicForm(boolean renderPublicForm) {
            this.renderPublicForm = renderPublicForm;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public void setTelephone(Telephone telephone) {
            this.telephone = telephone;
        }

        // Methods------------------------------------------------------------------
        /**
         * The address list component used to manage collections requires this 
         * method to reset the address object each time a new one is created.
         */
        public void addAddress() {
            subject.getContact().getAddresses().add(address);
            address = new Address();
        }
        
        /**
         * The telephone list component used to manage collections requires this 
         * method to reset the telephone object each time a new one is created.
         */
        public void addTelephone() {
            subject.getContact().getTelephones().add(telephone);
            telephone = new Telephone();
        }
        
    }
}
