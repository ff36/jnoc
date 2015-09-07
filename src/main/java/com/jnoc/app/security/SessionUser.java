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
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.app.security;

import com.jnoc.per.entity.User;
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
     * @return the User associated with the current calling session. If no user
     * is associated with the session a new empty User is returned.
     */
    public static User getCurrentUser() {
        try {
            return (User) SecurityUtils
                .getSubject()
                .getPrincipals()
                .asList()
                .get(0);
        } catch (NullPointerException npe) {
            return new User();
        }
    }

}
