/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.app.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.credential.PasswordMatcher;

/**
 *
 * @version Build 2.0.0
 * @since Jul 15, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class ShiroPswMatcher extends PasswordMatcher {

    @Override
    protected Object getStoredPassword(AuthenticationInfo storedAccountInfo) {
        Object stored = super.getStoredPassword(storedAccountInfo);

        if (stored instanceof char[]) {
            stored = String.valueOf((char[]) stored);
        }

        return stored;
    }
}
