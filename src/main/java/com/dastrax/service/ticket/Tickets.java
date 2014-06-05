/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.ticket;

import com.dastrax.app.model.DataTable;
import com.dastrax.app.model.ModelQuery;
import com.dastrax.app.model.ModelQueryQualifier;
import com.dastrax.app.model.ModelQueryType;
import com.dastrax.service.navigation.Navigator;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Ticket table CDI bean. Provides advanced data table capability for
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
public class Tickets implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Tickets.class.getName());
    
    private static final long serialVersionUID = 1L;
    private DataTable dataTable;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CDI">
    @Inject
    @ModelQueryQualifier
    @ModelQueryType(ModelQueryType.ModelQueries.TICKET)
    private ModelQuery model;

    @Inject
    private Navigator navigator;

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
//</editor-fold>

    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     */
    public void init() {
        dataTable = new DataTable(model);
        dataTable.initTable();
    }

}
