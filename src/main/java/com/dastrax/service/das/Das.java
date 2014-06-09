/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.das;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.app.model.DasModelQuery;
import com.dastrax.app.model.DataTable;
import com.dastrax.app.model.ModelQuery;
import com.dastrax.app.security.Password;
import com.dastrax.service.navigation.Navigator;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * DAS table CDI bean. Provides advanced data table capability for
 * displaying and querying.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class Das implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Das.class.getName());
    
    private static final long serialVersionUID = 1L;
    private DataTable dataTable;
    private final ModelQuery model;
    private Password password;
    private final Map<String, List<String>> parameters;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CDI">
    @Inject
    private Navigator navigator;

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Das() {
        this.model = new DasModelQuery();
        parameters = JsfUtil.getRequestParameters();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of dataTable.
     *
     * @return the value of dataTable
     */
    public DataTable getDataTable() {
        return dataTable;
    }

    /**
     * Get the value of password.
     *
     * @return the value of password
     */
    public Password getPassword() {
        return password;
    }
    
    /**
     * Get the value of navigator.
     *
     * @return the value of navigator
     */
    public Navigator getNavigator() {
        return navigator;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of dataTable.
     *
     * @param dataTable new value of dataTable
     */
    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }
    
    /**
     * Set the value of password.
     *
     * @param password new value of password
     */
    public void setPassword(Password password) {
        this.password = password;
    }
//</editor-fold>

    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     */
    public void init() {
        dataTable = new DataTable(model);
        dataTable.initTable(parameters);
        password = new Password();
    }

}