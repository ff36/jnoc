/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.company;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.app.model.CompanyModelQuery;
import com.dastrax.app.model.DataTable;
import com.dastrax.app.model.ModelQuery;
import com.dastrax.app.service.internal.DefaultAttributeFilter;
import com.dastrax.app.services.AttributeFilter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Company table CDI bean. Provides advanced data table capability for
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
public class Companies implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    private DataTable dataTable;
    private final ModelQuery model;
    private final Map<String, List<String>> parameters;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Companies() {
        this.model = new CompanyModelQuery();
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
        dataTable.initTable(
                parameters, 
                new DefaultAttributeFilter().authorizedCompanies(true));
    }

}
