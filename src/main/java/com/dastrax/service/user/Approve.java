/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dastrax.service.user;

import com.dastrax.app.email.Email;
import com.dastrax.app.security.Password;
import com.dastrax.per.entity.Token;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
import com.dastrax.app.misc.JsfUtil;
import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.Pattern;

/**
 *
 * @author tarka
 */
@Named
@ViewScoped
public class Approve implements Serializable {
    
//    @Pattern(regexp = DTX.EMAIL_REGEX, message = "Invalid Email")
//    private String email;
//    private String token;
//    private Integer pinCode;
//    private boolean legal;
//    
//    //TODO - Sort out these properties into a new bean
//    private String token;
//    private String email;
//    private boolean renderPublicForm;
//    private Token emailParam;
//    
//    private Password password = new Password();
    
    /**
     * Called by viewParam after attributes have been set but before the page is
     * rendered. This should only be called from the public password reset page
     * and the email change confirm page.
     */
//    public void init() {
//        emailParam = emailParamDAO.findParamByToken(token);
//        if (emailParam != null) {
//            renderPublicForm = true;
//            email = emailParam.getEmail();
//        } else {
//            JsfUtil.addWarningMessage("Please use the link in the email.");
//        }
//    }
    
    /**
     * Called by the account confirmation page
     */
//    public void init() {
//        Token ep = emailParamDAO.findParamByToken(token);
//        if (ep != null) {
//            pageInit = true;
//        } else {
//            JsfUtil.addWarningMessage("Please use the link in your email to confirm your account.");
//        }
//    }
    
    /**
     * Confirm change of email
     */
//    public void confirm() {
//        // Retrieve the param
//        Token ep = emailParamDAO.findParamByToken(token);
//        if (ep != null) {
//            // Check to make sure the email is available
//            if (subjectDAO.findSubjectByEmail(ep.getEmail()) == null) {
//                // Get the subject
//                subject = subjectDAO.findSubjectByEmail(ep.getParamA());
//                // Check the password
//                if (passwordUtil.passwordCorrect(currentPassword, subject.getPassword())) {
//                    subjectDAO.updateEmail(
//                            subject.getUid(),
//                            ep.getEmail());
//                    emailParamDAO.delete(ep.getToken());
//
//                    SecurityUtils.getSubject().logout();
//                    JsfUtil.addSuccessMessage("New email updated. For security you have been signed out of your account and will need to sign back in using your new email.");
//                } else {
//                    // Wrong password
//                    JsfUtil.addWarningMessage("Wrong password");
//                }
//            } else {
//                // Already registered
//                JsfUtil.addWarningMessage(ep.getEmail() + " is already registered to another account.");
//            }
//        } else {
//            // ep was null
//            JsfUtil.addWarningMessage("This account doesn't appear to have requested a change of email.");
//        }
//    }
    
    /**
     * When a password is reset from the public domain
     */
//    public void savePublicPsw() {
//        subject = subjectDAO.findSubjectByEmail(emailParam.getParamA());
//        String encryptedPassword = passwordUtil.validate(newPasswords, subject.getEmail());
//        if (encryptedPassword != null) {
//            // If the account is not yet confirmed we can use this to confirm it
//            if (!subject.getAccount().isConfirmed()) {
//                subjectDAO.confirm(
//                        subject.getUid(), encryptedPassword);
//            } else {
//                // Update the password
//                subjectDAO.updatePassword(
//                        subject.getUid(), encryptedPassword);
//            }
//            emailParamDAO.delete(token);
//            JsfUtil.addSuccessMessage("Password Updated");
//        } else {
//            // Password failed
//        }
//    }
    
    /**
     * Process change of password request
     */
//    public void changePswRequest() {
//        // Check the account is registered
//        User s = subjectDAO.findSubjectByEmail(email);
//        if (s != null) {
//            Token ep = sendEmail(s, DTX.EmailTemplate.CHANGE_PASSWORD, new Email());
//            emailParamDAO.create(ep);
//
//            JsfUtil.addSuccessMessage("An email containing instructions on how to complete the process has been sent to " + s.getEmail());
//        } else {
//            JsfUtil.addWarningMessage(email + " is not a registered account.");
//        }
//    }
    
    /**
     * Confirmation response from the account request email. This method must be
     * executed in order to activate an account.
     */
//    public void confirm() {
//
//        if (legal == true) {
//
//            // Retrieve the param
//            Token ep = emailParamDAO.findParamByToken(token);
//
//            // Check the PIN is correct
//            if (ep.getParamA().equals(pinCode.toString())) {
//                Password encryptedPassword = passwordUtil.validate(password);
//
//                // Check if the password meets the requirements 
//                if (encryptedPassword != null) {
//                    User s = subjectDAO.findSubjectByEmail(ep.getEmail());
//                    if (s != null) {
//                        subjectDAO.confirm(s.getUid(), password.getEncrypted());
//                        emailParamDAO.delete(ep.getToken());
//
//                        // Add JSF message
//                        JsfUtil.addSuccessMessage("Congratulations! You're account has been confirmed and you can now sign into your account");
//
//                        // Create an audit log of the event
//                        auditDAO.create("Account confirmed: " + s.getContact().buildFullName());
//                    } else {
//                        JsfUtil.addErrorMessage("We could not find any records of this account.");
//                    }
//                } else {
//                    // Add any messages that exist in the response
//                }
//            }
//        } else {
//            JsfUtil.addWarningMessage("Please accept all the legal terms and conditions.");
//        }
//    }
    
}
