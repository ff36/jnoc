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
 * Created Aug 8, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.service.audit;

import com.jnoc.app.misc.JsfUtil;
import com.jnoc.app.model.AuditModelQuery;
import com.jnoc.app.model.DataTable;
import com.jnoc.app.model.ModelQuery;
import com.jnoc.app.service.internal.DefaultAttributeFilter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Audit table CDI bean. Provides advanced data table capability for displaying
 * and querying Audit logs.
 * 
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class Audits implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    private DataTable dataTable;
    private final ModelQuery model;
    private boolean render;
    private final Map<String, List<String>> parameters;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Audits() {
        this.model = new AuditModelQuery();
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
                new DefaultAttributeFilter().authorizedAuthors());
        render = true;
    }

}
