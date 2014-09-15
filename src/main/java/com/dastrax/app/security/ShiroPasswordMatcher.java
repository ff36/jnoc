/*
 * Created Jul 15, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.credential.PasswordMatcher;

/**
 * This class extends SHIRO PasswordMatcher extending the SHIRO API to enable 
 * authentication against encrypted passwords.
 *
 * @version 1.0.0
 * @since Build 1.0.0 (Jul 15, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class ShiroPasswordMatcher extends PasswordMatcher {

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    /**
     * Converts the stored encrypted user password into a char[] so SHIRO 
     * can match it with new authentication requests.
     * 
     * @param storedAccountInfo
     * @return 
     */
    @Override
    protected Object getStoredPassword(AuthenticationInfo storedAccountInfo) {
        Object stored = super.getStoredPassword(storedAccountInfo);
        
        if (stored instanceof char[]) {
            stored = String.valueOf((char[]) stored);
        }
        
        return stored;
    }
//</editor-fold>
    
}
