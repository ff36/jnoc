/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.security;

import com.dastrax.app.util.DnsUtil;
import com.dastrax.app.util.ExceptionUtil;
import com.dastrax.app.util.UriUtil;
import com.dastrax.per.dao.core.CompanyDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.service.util.JsfUtil;
import com.dastrax.service.util.PathUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
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
 *
 * @version Build 2.0.0
 * @since Jul 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@RequestScoped
public class Authenticate implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(Authenticate.class.getName());

    // Project Stage------------------------------------------------------------
    private final String stage = ResourceBundle.getBundle("Config").getString("ProjectStage");

    // Variables----------------------------------------------------------------
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid Email")
    private String email;
    private String password;
    private boolean renderLogin;
    private boolean renderUniversal;
    private Company var;
    private String logoPath;
    private String subdomain;

    // EJB----------------------------------------------------------------------
    @EJB
    DnsUtil dnsUtil;
    @EJB
    CompanyDAO companyDAO;
    @EJB
    UriUtil uriUtil;
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    ExceptionUtil exu;

    // Inject------------------------------------------------------------------
    @Inject
    PathUtil pathUtil;

    // Getters------------------------------------------------------------------
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRenderLogin() {
        return renderLogin;
    }

    public boolean isRenderUniversal() {
        return renderUniversal;
    }

    public String getLogoPath() {
        return logoPath;
    }

    // Setters------------------------------------------------------------------
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Post Construct-----------------------------------------------------------
    @PostConstruct
    private void postConstruct() {
        /*
         * Obtain the current URL so we can populate the page based on the
         * sub-domain.
         */
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
        String contextURL = request.getRequestURL().toString();

        subdomain = dnsUtil.extract(contextURL);

        // Are we in DEV mode. If so bypass all this
        if (!stage.equals(DastraxCst.ProjectStage.DEV.toString())) {
            // Is the subject trying to access the login page via /login.jsf
            if (subdomain != null) {
                // Is the subject trying to access the universal page
                if (!subdomain.equals(ResourceBundle.getBundle("Config").getString("UniversalSubdomain"))) {
                    // Is the subject trying to access the admin page
                    if (!subdomain.equals(ResourceBundle.getBundle("Config").getString("AdminSubdomain"))) {

                        var = companyDAO.findCompanyBySubdomain(subdomain);
                        if (var != null) {
                            logoPath = uriUtil.companyLogo(var);
                            renderLogin = true;
                        } else {
                            JsfUtil.addWarningMessage("Sorry, this subdomain is not registered.");
                            renderLogin = false;
                        }

                    } else {
                        // Is accessing admin subdomain
                        logoPath = pathUtil.s3Logo("dastrax_logo_v1.png");
                        renderLogin = true;
                    }
                } else {
                    // This means the page is been accessed from the universal domain
                    logoPath = pathUtil.s3Logo("dastrax_logo_v1.png");
                    renderLogin = false;
                    renderUniversal = true;
                }
            } else {
                // Subdomain is null
                String url = ResourceBundle.getBundle("Config").getString("AccessProtocol")
                        + ResourceBundle.getBundle("Config").getString("UniversalSubdomain")
                        + "."
                        + ResourceBundle.getBundle("Config").getString("BaseUrl");
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(url);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Login redirect failed due to Faces External Context", ex);
                }
                renderLogin = false;
            }
        } else {
            // In development mode
            logoPath = pathUtil.s3Logo("dastrax_logo_v1.png");
            renderLogin = true;
        }
    }

    public void init() {
    }

    // Methods------------------------------------------------------------------
    /**
     * The universal login page uses this method to redirect subjects to their
     * specific sub-domain login based on their email.
     * @throws IOException 
     */
    public void redirect() throws IOException {
        Subject s = subjectDAO.findSubjectByEmail(email);
        if (s != null) {
            ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
            String url = null;
            // Email matches ADMIN
            if (s.getMetier().getName().equals(DastraxCst.Metier.ADMIN.toString())) {
                url = ResourceBundle.getBundle("Config").getString("AccessProtocol")
                        + ResourceBundle.getBundle("Config").getString("AdminSubdomain")
                        + "."
                        + ResourceBundle.getBundle("Config").getString("BaseUrl")
                        + "/login.jsf?email="
                        + email;
            }
            // Email matches VAR
            if (s.getMetier().getName().equals(DastraxCst.Metier.VAR.toString())) {
                url = ResourceBundle.getBundle("Config").getString("AccessProtocol")
                        + s.getCompany().getSubdomain()
                        + "."
                        + ResourceBundle.getBundle("Config").getString("BaseUrl")
                        + "/login.jsf?email="
                        + email;
            }
            // Email matches CLIENT
            if (s.getMetier().getName().equals(DastraxCst.Metier.CLIENT.toString())) {
                url = ResourceBundle.getBundle("Config").getString("AccessProtocol")
                        + s.getCompany().getParentVAR().getSubdomain()
                        + "."
                        + ResourceBundle.getBundle("Config").getString("BaseUrl")
                        + "/login.jsf?email="
                        + email;
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);

            // Add success message
            JsfUtil.addSuccessMessage("In the futur you can access you login page directly by going to " + url);
            // Carry the message over to the page redirect
            FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .getFlash()
                    .setKeepMessages(true);
        } else {
            // Email not registered in the system
            JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.noemail"));
        }
    }

    /**
     * Collects the login credentials from the view layer and passes the data to
     * the logic layer
     */
    public void authenticate() {
        // Is the subject already authenticated
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            // Make sure the email is registered
            Subject s = subjectDAO.findSubjectByEmail(email.toLowerCase());

            if (s != null) {
                // Check subject has access to this subdomain
                boolean isAuthorised = false;

                if (s.getMetier().getName().equals(
                        DastraxCst.Metier.VAR.toString())
                        && var != null
                        && s.getCompany().equals(var)) {
                    isAuthorised = true;
                }

                if (s.getMetier().getName().equals(
                        DastraxCst.Metier.CLIENT.toString())
                        && var != null
                        && s.getCompany().getParentVAR().equals(var)) {
                    isAuthorised = true;
                }

                if (isAuthorised
                        | subdomain.equals(ResourceBundle.getBundle("Config").getString("AdminSubdomain"))
                        | stage.equals(DastraxCst.ProjectStage.DEV.toString())) {

                    if (s.getAccount().isConfirmed()) {

                        if (!s.getAccount().isLocked()) {

                            try {

                                // Submit credentials to shiro for authentication
                                UsernamePasswordToken subjectToken = new UsernamePasswordToken(email.toLowerCase(), password);
                                SecurityUtils.getSubject().login(subjectToken);

                                // Set a new session date and move the previouse session
                                subjectDAO.updateSessionDetails(s.getUid());

                                // Obtain request and responses to redirect the user to their original requested page
                                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                                String fallbackUrl = "./role-redirect.jsf";

                                WebUtils.redirectToSavedRequest(request, response, fallbackUrl);

                            } catch (UnknownAccountException uae) {
                                JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.unknownaccount"));
                                exu.report(uae);
                            } catch (IncorrectCredentialsException ice) {
                                JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.incorrect"));
                            } catch (LockedAccountException lae) {
                                JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.locked"));
                            } catch (ExcessiveAttemptsException eae) {
                                // TODO Set timeout
                                JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.excessive"));
                            } catch (AuthenticationException ae) {
                                JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.auth"));
                            } catch (IOException ex) {
                                JsfUtil.addErrorMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.io"));
                                exu.report(ex);
                            }

                        } else {
                            // Account is locked
                            JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.locked"));
                        }

                    } else {
                        // Account is not yet confirmed
                        JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.unconfirmed"));
                    }

                } else {
                    // Not a member of the company under which they are trying to sign in
                    JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.wrongdomain"));
                }

            } else {
                // Email not registered in the system
                JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.noemail"));
            }

        } else {
            // Subject is already signed in
            JsfUtil.addWarningMessage(ResourceBundle.getBundle("WebBundle").getString("error.login.signedin"));
        }
    }

    /**
     * Ends a subjects session. Returns a navigation String.
     *
     * @return String
     */
    public String logout() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            SecurityUtils.getSubject().logout();
        }
        return "logout";
    }

}
