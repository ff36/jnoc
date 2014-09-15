/*
 * Created May 16, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.security;

import com.dastrax.per.entity.User;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import net.sf.uadetector.ReadableUserAgent;

/**
 * Information pertaining to the current session authenticated User to be
 * accessed from the view layer.
 *
 * @version 3.0.0
 * @since Build 3.0-SNAPSHOT (May 16, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@SessionScoped
public class JsfSessionUser implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    
    private final User currentUser;
    private final ReadableUserAgent userAgent;
    private final String ip;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public JsfSessionUser() {
        this.currentUser = SessionUser.getCurrentUser();
        this.userAgent = SessionUser.getUserAgent();
        this.ip = SessionUser.getIP();
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of ip
     *
     * @return the value of ip
     */
    public String getIp() {
        return ip;
    }
    
    /**
     * Get the value of userAgent
     *
     * @return the value of userAgent
     */
    public ReadableUserAgent getUserAgent() {
        return userAgent;
    }
    
    /**
     * Get the value of currentUser
     *
     * @return the value of currentUser
     */
    public User getCurrentUser() {
        return currentUser;
    }

//</editor-fold>
    
}
