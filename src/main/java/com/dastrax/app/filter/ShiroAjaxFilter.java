/*
 * Created Jul 15, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.filter;

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.web.filter.authc.UserFilter;

/**
 * SHIRO needs this extra filter to correctly handle AJAX responses.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 15, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class ShiroAjaxFilter extends UserFilter {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final String FACES_REDIRECT_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<partial-response><redirect url=\"%s\">"
            + "</redirect></partial-response>";
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    protected void redirectToLogin(
            ServletRequest request,
            ServletResponse response) throws IOException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        if ("partial/ajax".equals(httpRequest.getHeader("Faces-Request"))) {
            response.setContentType("text/xml");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().printf(
                    FACES_REDIRECT_XML,
                    httpRequest.getContextPath() + getLoginUrl());
        }
        else {
            super.redirectToLogin(request, response);
        }
    }
//</editor-fold>

}
