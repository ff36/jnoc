/*
 * Created May 16, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.security;

import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.apache.shiro.SecurityUtils;

/**
 * Information pertaining to the current session authenticated User.
 *
 * @version 3.0.0
 * @since Build 3.0-SNAPSHOT (May 16, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
public class SessionUser {

    /**
     * Obtains the current session User Agent Information.
     * <b>IMPORTANT!</b>
     * I don't know how this will react if there is no User Agent! There 
     * probably needs to be some exception handling here.
     *
     * @return the ReadableUserAgent associated with the current calling session.
     */
    public static ReadableUserAgent getUserAgent() {
        HttpServletRequest request = 
                (HttpServletRequest) FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getRequest();
        UserAgentStringParser parser = 
                UADetectorServiceFactory.getResourceModuleParser();
        return parser.parse(request.getHeader("User-Agent"));
    }
    
    /**
     * Obtains the current session User IP address.
     * <b>IMPORTANT!</b>
     * I don't know how this will react if there is no User Agent! There 
     * probably needs to be some exception handling here.
     *
     * @return the IP associated with the current calling session.
     */
    public static String getIP() {
        HttpServletRequest request = 
                (HttpServletRequest) FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getRequest();
        return request.getRemoteAddr();
    }
    
    /**
     * Obtains the current session authenticated User.
     * <b>IMPORTANT!</b>
     * This is a convenience method and should <b>NOT</b> be relied on for up to
     * date User information. The User object returned by this method is created
     * at authentication time and is <b>NOT</b> updated until the subjects
     * re-authenticates.
     *
     * @return the User associated with the current calling session.
     */
    public static User getCurrentUser() {
        return (User) SecurityUtils
                .getSubject()
                .getPrincipals()
                .asList()
                .get(0);
    }

    /**
     * Convenience method to determine if the current user has a metier of
     * type Administrator.
     * @return True if the current user has a metier of Administrator.
     * Otherwise false.
     */
    public static boolean isAdministrator() {
        return getCurrentUser()
                .getMetier().getName()
                .equals(DTX.Metier.ADMIN.toString());
    }

    /**
     * Convenience method to determine if the current user has a metier of
     * type VAR.
     * @return True if the current user has a metier of type VAR.
     * Otherwise false.
     */
    public static boolean isVAR() {
        return getCurrentUser()
                .getMetier().getName()
                .equals(DTX.Metier.VAR.toString());
    }

    /**
     * Convenience method to determine if the current user has a metier of
     * type Client.
     * @return True if the current user has a metier of type Client.
     * Otherwise false.
     */
    public static boolean isClient() {
        return getCurrentUser()
                .getMetier().getName()
                .equals(DTX.Metier.CLIENT.toString());
    }

}
