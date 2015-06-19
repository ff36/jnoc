/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.security;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.util.WebUtils;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Audit;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;

/**
 * User authentication related services. Including subdomain authentication
 * management.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@RequestScoped
public class Authenticate implements Serializable {

	private static final long serialVersionUID = 1L;
	//<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Authenticate.class.getName());

    @Pattern(regexp = DTX.EMAIL_REGEX, message = "Invalid Email")
    private String email;
    private String password;
    //private Company var;
    //private String logoPath;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of email
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of email.
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the value of password.
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }
//</editor-fold>

    /**
     * Called after the object has been created to initialize the page
     * properties and data before the page has loaded.
     */
    public void init() {
        // Set the email
        email = JsfUtil.getRequestParameter("email");
    }

    /**
     * Collects the login credentials from the view layer and attempt to
     * authenticate the requesting user.
     */
    public void authenticate() {

        // Is the user already authenticated
        if (!SecurityUtils.getSubject().isAuthenticated()) {

            // Make sure the email is registered
            try {
                User user = (User) dap.findWithNamedQuery(
                        "User.findByEmail",
                        QueryParameter.with("email", email.toLowerCase())
                        .parameters())
                        .get(0);

                    if (user.getAccount().isConfirmed()) {

                        if (!user.getAccount().isLocked()) {

                            try {

                                // Submit credentials to shiro for authentication
                                UsernamePasswordToken subjectToken
                                        = new UsernamePasswordToken(
                                                email.toLowerCase(), password);
                                SecurityUtils.getSubject().login(subjectToken);
                                
                                /*
                                 Obtain request and responses to redirect the 
                                 user to their original requested page
                                 */
                                HttpServletRequest request
                                        = (HttpServletRequest) FacesContext
                                        .getCurrentInstance()
                                        .getExternalContext()
                                        .getRequest();
                                HttpServletResponse response
                                        = (HttpServletResponse) FacesContext
                                        .getCurrentInstance()
                                        .getExternalContext()
                                        .getResponse();
                                String fallbackUrl = "./role-redirect.jsf";

                                // Set a new session date
                                user.getAccount().setLastSessionEpoch(
                                        user.getAccount().getLastSessionEpoch());
                                user.getAccount().setCurrentSessionEpoch(
                                        new Date().getTime());
                                dap.update(user.getAccount());

                                // Audit the authentication
                                new Audit().log("Signed in from "
                                        + request.getRemoteAddr());

                                /*
                                 If the original request was for a protected 
                                 resource complete the request
                                 */
                                WebUtils.redirectToSavedRequest(
                                        request, response, fallbackUrl);

                            } catch (UnknownAccountException uae) {
                                JsfUtil.addWarningMessage(
                                        ResourceBundle
                                        .getBundle("messages")
                                        .getString(
                                                "error.login.unknownaccount"));
                            } catch (IncorrectCredentialsException ice) {
                                JsfUtil.addWarningMessage(
                                        ResourceBundle
                                        .getBundle("messages")
                                        .getString(
                                                "error.login.incorrect"));
                            } catch (LockedAccountException lae) {
                                JsfUtil.addWarningMessage(
                                        ResourceBundle
                                        .getBundle("messages")
                                        .getString(
                                                "error.login.locked"));
                            } catch (ExcessiveAttemptsException eae) {
                                // TODO Set a timeout for excessive attempts
                                JsfUtil.addWarningMessage(
                                        ResourceBundle
                                        .getBundle("messages")
                                        .getString(
                                                "error.login.excessive"));
                            } catch (AuthenticationException ae) {
                                JsfUtil.addWarningMessage(
                                        ResourceBundle
                                        .getBundle("messages")
                                        .getString("error.login.auth"));
                            } catch (IOException ex) {
                                JsfUtil.addErrorMessage(
                                        ResourceBundle
                                        .getBundle("messages")
                                        .getString("error.login.io"));
                            }

                        } else {
                            // Account is locked
                            JsfUtil.addWarningMessage(
                                    ResourceBundle
                                    .getBundle("messages")
                                    .getString("error.login.locked"));
                        }

                    } else {
                        // Account is not yet confirmed
                        JsfUtil.addWarningMessage(
                                ResourceBundle
                                .getBundle("messages")
                                .getString("error.login.unconfirmed"));
                    }

            } catch (ArrayIndexOutOfBoundsException e) {
                // Email not registered in the system
                JsfUtil.addWarningMessage(
                        ResourceBundle
                        .getBundle("messages")
                        .getString("error.login.noemail"));
            }

        } else {
            // User is already signed in
            JsfUtil.addWarningMessage(
                    ResourceBundle
                    .getBundle("messages")
                    .getString("error.login.signedin"));
        }
    }

    /**
     * Ends a user SHIRO session.
     */
    public void logout() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            // End the users shiro session
            SecurityUtils.getSubject().logout();
            try {
                // Redirect the user to the login page
                ExternalContext ectx
                        = FacesContext.getCurrentInstance().getExternalContext();
                String url = ectx.getRequestContextPath() + "/login.jsf";
                ectx.redirect(url);
            } catch (IOException ex) {
                LOG.log(Level.WARNING,
                        "Could not redirect subject after logout", ex);
            }
        }

    }

}
