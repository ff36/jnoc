/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/


/*
 * Created May 16, 2014.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 

 */
package com.jnoc.app.security;

import com.jnoc.per.entity.User;
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
