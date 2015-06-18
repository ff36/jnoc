/*
 * Created Aug 21, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.ticket;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Comment;
import com.dastrax.per.entity.Ticket;
import com.dastrax.service.navigation.Navigator;
import java.io.Serializable;
import java.util.ArrayList;
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
    private final String viewParamTicketID;
    private boolean renderEditor;
    private boolean render;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public EditTicket() {
        this.renderEditor = true;
        this.ticket = new Ticket();
        this.viewParamTicketID = JsfUtil.getRequestParameter("ticket");
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
     * Get the value of render.
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * Get the value of renderEditor.
     *
     * @return the value of renderEditor
     */
    public boolean isRenderEditor() {
        return renderEditor;
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
     * Set the value of renderEditor.
     *
     * @param renderEditor new value of renderEditor
     */
    public void setRenderEditor(boolean renderEditor) {
        this.renderEditor = renderEditor;
    }
//</editor-fold>
    
    /**
     * Initialize the page by loading the specified ticket from the persistence
     * layer.
     */
    public void init() {

        try {
            // Get the ticket from the persistence layer
            ticket = (Ticket) dap.find(Ticket.class, Long.valueOf(viewParamTicketID));

            // Redirect if the user is not allowed access to the ticket
            if (!ticket.initEditor()) {
                navigator.navigate("LIST_TICKETS");
            }

            ticket.setCcEmailRecipients(new ArrayList<String>());
            ticket.setComment(new Comment());
        } catch (NullPointerException | NumberFormatException e) {
            // The ticket was not found in the persistence
            navigator.navigate("LIST_TICKETS");
        }
        render = true;
    }

    /**
     * Inverts the state of renderEditor
     */
    public void changeEditor() {
        if (renderEditor) {
            //renderEditor = false;
        } else {
            this.ticket.setComment(new Comment());
            renderEditor = true;
        }
        
    }

    /**
     * When a push notification comes in we want to update the ticket but without
     * loosing what the administrator was working on.
     */
    public void updateComments() {
        Ticket newTicket = (Ticket) dap.find(Ticket.class, Long.valueOf(viewParamTicketID));
        newTicket.setComment(ticket.getComment());
        ticket = newTicket;
    }
    
}
