/*
 * Created Aug 21, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.ticket;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Token;
import com.dastrax.per.entity.Ticket;
import com.dastrax.app.misc.JsfUtil;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Ticket satisfaction CDI bean. Permits Users to give satisfaction feedback
 * on closed tickets.
 * 
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 21, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class Satisfaction implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Satisfaction.class.getName());
    private static final long serialVersionUID = 1L;
    
    private Ticket ticket;
    private String tokenID;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Satisfaction() {
        ticket = new Ticket();
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    CrudService dap;
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
     * Get the value of tokenID.
     *
     * @return the value of tokenID
     */
    public String getTokenID() {
        return tokenID;
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
     * Set the value of tokenID.
     *
     * @param tokenID new value of tokenID
     */
    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }
//</editor-fold>

    /**
     * Initialize the page by loading the specified ticket from the persistence
     * layer.
     */
    public void init() {
        try {
            Token token = (Token) dap.find(Token.class, tokenID);
            
            ticket = (Ticket) dap.find(
                    Ticket.class, 
                    token.getParameters().get("ticket"));
        } catch (NullPointerException npe) {
            // The token was null
            JsfUtil.addWarningMessage("Sorry this ticket has either already" 
                    + " been given feedback or is not yet available for "
                    + "feedback.");
        }

    }
    
}
