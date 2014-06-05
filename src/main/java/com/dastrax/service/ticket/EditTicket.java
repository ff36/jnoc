/*
 * Created Aug 21, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.ticket;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Ticket;
import com.dastrax.service.navigation.Navigator;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Edit Ticket CDI bean. Permits Users to edit support tickets.
 * 
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 21, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class EditTicket implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(EditTicket.class.getName());
    private static final long serialVersionUID = 1L;
    
    private Ticket ticket;
    private String viewParamTicketID;
    private boolean renderWYSIWYG;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public EditTicket() {
        this.ticket = new Ticket();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CDI">
    @Inject
    private Navigator navigator;

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of ticket.
     *
     * @return the value of ticket
     */
    public Ticket getTicket() {
        return ticket;
    }
    
    /**
     * Get the value of viewParamTicketID.
     *
     * @return the value of viewParamTicketID
     */
    public String getViewParamTicketID() {
        return viewParamTicketID;
    }
    
    /**
     * Get the value of renderWYSIWYG.
     *
     * @return the value of renderWYSIWYG
     */
    public boolean isRenderWYSIWYG() {
        return renderWYSIWYG;
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
    
    /**
     * Set the value of viewParamTicketID.
     *
     * @param viewParamTicketID new value of viewParamTicketID
     */
    public void setViewParamTicketID(String viewParamTicketID) {
        this.viewParamTicketID = viewParamTicketID;
    }
    
    /**
     * Set the value of renderWYSIWYG.
     *
     * @param renderWYSIWYG new value of renderWYSIWYG
     */
    public void setRenderWYSIWYG(boolean renderWYSIWYG) {
        this.renderWYSIWYG = renderWYSIWYG;
    }
//</editor-fold>
    
    /**
     * Initialize the page by loading the specified ticket from the persistence
     * layer.
     */
    public void init() {

        try {
            // Get the ticket from the persistence layer
            ticket = (Ticket) dap.find(Ticket.class, viewParamTicketID);

            // Redirect if the user is not allowed access to the ticket
            if (!ticket.initEditor()) {
                navigator.navigate("LIST_TICKETS");
            }

        } catch (NullPointerException npe) {
            // Do Nothing! The ticket was not found in the persistence
        }
    }

    public void changeWYSIWYG() {
        renderWYSIWYG = true;
    }

    
}
