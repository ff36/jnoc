/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.util;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Aug 23, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@RequestScoped
public class PermissionUtil {

    // Methods------------------------------------------------------------------
    public boolean hasPermission(String permission) {
        return SecurityUtils.getSubject().isPermitted(permission);
    }
    
}
