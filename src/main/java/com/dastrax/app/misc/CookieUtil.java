/*
 * Created Mar 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.misc;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Manages system interaction with client cookies.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Mar 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class CookieUtil {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(CookieUtil.class.getName());
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public CookieUtil() {
    }
//</editor-fold>

    /**
     * Generates a HTTP Servlet Request that can retrieve cookies that exist in
     * the clients browser relating to the application domain. Once the Cookie[]
     * is retrieved you can inspect the individual cookies. No guarantee is 
     * provided as to the order that the cookies will be returned.
     *
     * @return a Cookie[]
     */
    public static  Cookie[] readCookie() {
        HttpServletRequest request = (HttpServletRequest) 
                FacesContext.getCurrentInstance()
                        .getExternalContext().getRequest();

        try {
           Cookie[] cookies = request.getCookies();
           return cookies;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error retreiving cookies", e);
        }

        return new Cookie[] {};
    }

    /**
     * Creates an HTTP Servlet Response to save a single cookie to the clients 
     * browser.
     * 
     * @param cookie
     * @return a boolean value expressing the success of the operation.
     */
    public static boolean writeCookie(Cookie cookie) {

        boolean result = false;

        try {
            HttpServletResponse response = (HttpServletResponse) 
                    FacesContext.getCurrentInstance()
                            .getExternalContext().getResponse();

            response.addCookie(cookie);
            result = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error writing cookie", e);
        }
        return result;
    }
}
