/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @version Build 2.0.0
 * @since Jul 14, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@RequestScoped
public class JsfRedirect {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(JsfRedirect.class.getName());

    // Constructors-------------------------------------------------------------
    public JsfRedirect() {
    }

    // Methods------------------------------------------------------------------
    /**
     * This method is responsible for redirecting subjects if they try to access
     * a page that previously required them to select a system resource, but 
     * have not yet done so.
     * 
     * @param path Should be relative to the root context 
     * (ie. 'example.com/a/b/index.jsf' should be supplied as '/a/b/index.jsf'
     */
    public void redirect(String path) {
        try {
            ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
            String url = ectx.getRequestContextPath() + path;
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);  
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "IOException: JSFREDIRECT -> REDIRECT", ioe);
        }
    }
    
}
