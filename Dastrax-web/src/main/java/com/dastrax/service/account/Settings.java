/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.account;

import com.dastrax.per.dao.EmailParamDAO;
import com.dastrax.per.dao.EmailTemplateDAO;
import com.dastrax.per.dao.SubjectDAO;
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
    private void init() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            subject = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString()
                    );
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
    public void saveContact() {
        subjectDAO.updateContact(
                subject.getUid(),
                subject.getContact());
        JsfUtil.addSuccessMessage("Profile Updated");
    }

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

    public void changeEmailRequest() {

        // Check to make sure the email is available
        if (subjectDAO.findSubjectByEmail(helper.newEmail) == null) {
            // Persist the email params
            EmailParam ep = sendEmail(subject);
            emailParamDAO.create(ep);

            JsfUtil.addSuccessMessage("An email containing instructions on how to complete the process has been sent to " + ep.getEmail());
        } else {
            JsfUtil.addWarningMessage(helper.newEmail + " is already registered to another account.");
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
    private EmailParam sendEmail(Subject subject) {

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
        email.setVariables(vars);

        // Retreive the email template from the database.
        EmailTemplate et = emailTemplateDAO.findTemplateById(
                DastraxCst.EmailTemplate.CHANGE_EMAIL.toString());
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
        @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid Email")
        private String newEmail;
        private String token;
        private String view = "PROFILE";
        private String principalPsw;
        private String tempPswA;
        private String tempPswB;

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

    }
}
