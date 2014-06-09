/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.user;

import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.User;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * User Settings CDI bean.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class Settings implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Settings.class.getName());
    private static final long serialVersionUID = 1L;
    
    private User user;
    private boolean render;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Settings() {
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of version. Unique storage version
     *
     * @return the value of version
     */
    public User getUser() {
        return user;
    }

    /**
     * Get the value of render.
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }
    
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of user.
     *
     * @param user new value of user
     */
    public void setUser(User user) {
        this.user = user;
    }
//</editor-fold>

    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     */
    public void init() {
        user = (User) dap.find(User.class, SessionUser.getCurrentUser().getId());
        render = true;      
    }

}
