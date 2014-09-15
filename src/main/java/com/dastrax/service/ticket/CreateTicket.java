/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.ticket;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.per.entity.Ticket;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Create Ticket CDI bean. Provides methods for creating Ticket
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class CreateTicket implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(CreateTicket.class.getName());
    private static final long serialVersionUID = 1L;

    private Ticket ticket;
    private final Map<String, List<String>> parameters;
    private boolean render;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public CreateTicket() {
        this.ticket = new Ticket();
        this.parameters = JsfUtil.getRequestParameters();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of ticket
     *
     * @return the value of ticket
     */
    public Ticket getTicket() {
        return ticket;
    }

    /**
     * Get the value of render
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }

    
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of ticket.
     *
     * @param ticket new value of ticket
     */
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    //</editor-fold>
    
    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     */
    public void init() {
        ticket.initCreator(parameters);
        this.render = true;
    }
}
