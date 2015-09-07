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
 * Created Jul 10, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.service.ticket;

import com.jnoc.app.misc.JsfUtil;
import com.jnoc.per.entity.Ticket;
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
