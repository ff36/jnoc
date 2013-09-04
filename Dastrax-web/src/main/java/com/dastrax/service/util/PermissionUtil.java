/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.util;

import java.io.IOException;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
    /**
     * Determine whether the current subject has the implied permission.
     * @param permission
     * @return true if the subject has the implied permission otherwise false
     */
    public boolean hasPermission(String permission) {
        return SecurityUtils.getSubject().isPermitted(permission);
    }
    
    public void hasPagePermission(String permission) throws IOException {
        if(!SecurityUtils.getSubject().isPermitted(permission)) {
            ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
            String url = ectx.getRequestContextPath() + "/login.jsf";
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        }
    }
    
}
