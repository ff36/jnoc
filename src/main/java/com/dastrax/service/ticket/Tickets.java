/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.ticket;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.app.model.DataTable;
import com.dastrax.app.model.ModelQuery;
import com.dastrax.app.model.TicketModelQuery;
import com.dastrax.app.service.internal.DefaultAttributeFilter;
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
