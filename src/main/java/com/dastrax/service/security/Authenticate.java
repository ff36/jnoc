/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.security;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.app.service.internal.DefaultDNSManager;
import com.dastrax.app.service.internal.DefaultURI;
import com.dastrax.app.services.DNSManager;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Audit;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
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

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Authenticate.class.getName());
    private final String stage = ResourceBundle.getBundle("config").getString("ProjectStage");

    @Pattern(regexp = DTX.EMAIL_REGEX, message = "Invalid Email")
    private String email;
    private String password;
    private boolean renderLogin;
    private boolean renderUniversal;
    private Company var;
    private String logoPath;
    private String subdomain;
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

    /**
     * Get the value of renderLogin
     *
     * @return the value of renderLogin
     */
    public boolean isRenderLogin() {
        return renderLogin;
    }

    /**
     * Get the value of renderUniversal
     *
     * @return the value of renderUniversal
     */
    public boolean isRenderUniversal() {
        return renderUniversal;
    }

    /**
     * Get the value of logoPath
     *
     * @return the value of logoPath
     */
    public String getLogoPath() {
        return logoPath;
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
     * properties and data before the page has loaded. Specifically the graphics
     * and subdomain are checked to make sure the authenticating user is on the
     * correct login page.
     */
    public void init() {
        /*
         * Obtain the current URL so we can populate the page based on the
         * sub-domain.
         */
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
        String contextURL = request.getRequestURL().toString();

        DNSManager dns = new DefaultDNSManager();
        subdomain = dns.extract(contextURL);

        // Set the email
        email = JsfUtil.getRequestParameter("email");
        
        // Are we in DEV mode. If so bypass all this
        if (!stage.equals(DTX.ProjectStage.DEV.toString())) {

            // Is the user trying to access the login page via /login.jsf
            if (subdomain != null) {

                // Is the user trying to access the universal page
                if (!subdomain.equals(ResourceBundle.getBundle("config")
                        .getString("UniversalSubdomain"))) {

                    // Is the user trying to access the admin page
                    if (!subdomain.equals(ResourceBundle.getBundle("config")
                            .getString("AdminSubdomain"))) {

                        var = (Company) dap.findWithNamedQuery(
                                "Company.findBySubdomain",
                                QueryParameter.with("subdomain", subdomain)
                                .parameters())
                                .get(0);

                        if (var != null) {
                            logoPath = new DefaultURI.Builder(
                                    DTX.URIType.COMPANY_LOGO)
                                    .withCompany(var)
                                    .create()
                                    .generate();

                            renderLogin = true;

                        } else {
                            JsfUtil.addWarningMessage(
                                    "Sorry, this subdomain is not registered.");
                            renderLogin = false;
                        }

                    } else {

                        // Is accessing admin subdomain
                        logoPath = new DefaultURI.Builder(
                                DTX.URIType.LOGO)
                                .withFile("dastrax_logo_v1.png")
                                .create()
                                .generate();
                        renderLogin = true;
                    }
                } else {

                    // Page is been accessed from the universal domain
                    logoPath = new DefaultURI.Builder(
                            DTX.URIType.LOGO)
                            .withFile("dastrax_logo_v1.png")
                            .create()
                            .generate();
                    renderLogin = false;
                    renderUniversal = true;
                }
            } else {

                // Subdomain is null so redirect to universal login page
                String url
                        = ResourceBundle.getBundle("config")
                        .getString("AccessProtocol")
                        + ResourceBundle.getBundle("config")
                        .getString("UniversalSubdomain")
                        + "."
                        + ResourceBundle.getBundle("config")
                        .getString("BaseUrl");
                try {
                    FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .redirect(url);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Login redirect failed due to Faces"
                            + " External Context", ex);
                }
                renderLogin = false;
            }
        } else {

            // In development mode
            logoPath = new DefaultURI.Builder(
                    DTX.URIType.LOGO)
                    .withFile("dastrax_logo_v1.png")
                    .create()
                    .generate();
            renderLogin = true;
        }
    }

    /**
     * The universal login page uses this method to redirect users to their
     * specific sub-domain login page.
     *
     * @throws IOException
     */
    public void redirect() throws IOException {
        User user = (User) dap.findWithNamedQuery(
                "User.findByEmail",
                QueryParameter.with("email", email.toLowerCase())
                .parameters())
                .get(0);

        if (user != null) {

            String url = null;

            // Email matches ADMIN
            if (user.isAdministrator()) {
                url = ResourceBundle.getBundle("config").getString("AccessProtocol")
                        + ResourceBundle.getBundle("config").getString("AdminSubdomain")
                        + "."
                        + ResourceBundle.getBundle("config").getString("BaseUrl")
                        + "/login.jsf?email="
                        + email.toLowerCase();
            }

            // Email matches VAR
            if (user.isVAR()) {
                url = ResourceBundle.getBundle("config").getString("AccessProtocol")
                        + user.getCompany().getSubdomain()
                        + "."
                        + ResourceBundle.getBundle("config").getString("BaseUrl")
                        + "/login.jsf?email="
                        + email.toLowerCase();
            }

            // Email matches CLIENT
            if (user.isClient()) {
                url = ResourceBundle.getBundle("config").getString("AccessProtocol")
                        + user.getCompany().parent().getSubdomain()
                        + "."
                        + ResourceBundle.getBundle("config").getString("BaseUrl")
                        + "/login.jsf?email="
                        + email.toLowerCase();
            }

            // Redirect the subject
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);

            // Add success message
            JsfUtil.addSuccessMessage("In the futur you can access you login "
                    + "page directly by going to " + url);
            // Carry the message over to the page redirect
            FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .getFlash()
                    .setKeepMessages(true);
        } else {
            // Email not registered in the system
            JsfUtil.addWarningMessage(ResourceBundle.getBundle("messages")
                    .getString("error.login.noemail"));
        }
    }

    /**
     * Collects the login credentials from the view layer and attempt to
     * authenticate the requesting user.
     */
    public void authenticate() {

        // Is the user already authenticated
        if (!SecurityUtils.getSubject().isAuthenticated()) {

            // Make sure the email is registered
            User user = (User) dap.findWithNamedQuery(
                    "User.findByEmail",
                    QueryParameter.with("email", email.toLowerCase())
                    .parameters())
                    .get(0);

            if (user != null) {

                // Check user has access to this subdomain
                boolean isAuthorised = false;

                if (user.isVAR()
                        && var != null
                        && user.getCompany().equals(var)) {
                    isAuthorised = true;
                }

                if (user.isClient()
                        && var != null
                        && user.getCompany().parent().equals(var)) {
                    isAuthorised = true;
                }

                if (isAuthorised
                        | ResourceBundle.getBundle("config")
                                .getString("AdminSubdomain").equals(subdomain)
                        | stage.equals(DTX.ProjectStage.DEV.toString())) {

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
//                                new Audit().log("Signed in from "
//                                        + request.getRemoteAddr());

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

                } else {
                    /*
                     Not a member of the company under which they are trying 
                     to sign in
                     */
                    JsfUtil.addWarningMessage(
                            ResourceBundle
                            .getBundle("messages")
                            .getString("error.login.wrongdomain"));
                }

            } else {
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
