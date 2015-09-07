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
package com.jnoc.service.ticket;

import com.jnoc.app.misc.JsfUtil;
import com.jnoc.app.model.DataTable;
import com.jnoc.app.model.ModelQuery;
import com.jnoc.app.model.TicketModelQuery;
import com.jnoc.app.service.internal.DefaultAttributeFilter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
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
    private final ModelQuery model;
    private final Map<String, List<String>> parameters;
    private int browserHeight;
    private int browserWidth;
    private int numberOfRows;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Tickets() {
        this.model = new TicketModelQuery();
        this.parameters = JsfUtil.getRequestParameters();
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

    public int getBrowserHeight() {
        return browserHeight;
    }

    public void setBrowserHeight(int browserHeight) {
        this.browserHeight = browserHeight;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public int getBrowserWidth() {
        return browserWidth;
    }

    public void setBrowserWidth(int browserWidth) {
        this.browserWidth = browserWidth;
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
        // Get the browser height
        Map<String, String> requestParameterMap = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
        browserHeight = Integer.valueOf(requestParameterMap.get("hidden_field_browser_height"));
        browserWidth = Integer.valueOf(requestParameterMap.get("hidden_field_browser_width"));
        if (browserWidth >= 2100) {
           numberOfRows = browserHeight / 34; 
        } 
        if (browserWidth > 1900 && browserWidth < 2100) {
            numberOfRows = browserHeight / 40;
        }
        if (browserWidth < 1900) {
            numberOfRows = browserHeight / 50;
        }

        dataTable = new DataTable(model);
        dataTable.initTable(
                parameters, 
                new DefaultAttributeFilter().authorizedTickets());
    }

}
