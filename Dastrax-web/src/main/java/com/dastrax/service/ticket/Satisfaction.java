/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.ticket;

import com.dastrax.app.util.UriUtil;
import com.dastrax.per.dao.core.EmailParamDAO;
import com.dastrax.per.dao.csm.TicketDAO;
import com.dastrax.per.entity.core.EmailParam;
import com.dastrax.per.entity.csm.Ticket;
import com.dastrax.service.util.JsfUtil;
import com.dastrax.service.util.PathUtil;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @version Build 2.0.0
 * @since Aug 21, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class Satisfaction implements Serializable {
    
    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(Satisfaction.class.getName());

    // Variables----------------------------------------------------------------
    private Ticket ticket;
    private String token;
    private String satisfied;
    private String feedback;
    
    // EJB----------------------------------------------------------------------
    @EJB
    TicketDAO ticketDAO;
    @EJB
    EmailParamDAO emailParamDAO;
    @EJB
    UriUtil uriUtil;
    
    // Inject------------------------------------------------------------------
    @Inject
    PathUtil pathUtil;
    
    // Getters------------------------------------------------------------------
    public String getToken() {
        return token;
    }

    public String getSatisfied() {
        return satisfied;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public String getFeedback() {
        return feedback;
    }
    
    // Setters------------------------------------------------------------------
    public void setToken(String token) {
        this.token = token;
    }

    public void setSatisfied(String satisfied) {
        this.satisfied = satisfied;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    // Constructors-------------------------------------------------------------
    /**
     * Initialize the page by retrieving the ticket via the email parameter
     */
    public void init() {
        EmailParam ep = emailParamDAO.findParamByToken(token);
        if (ep != null) {
           ticket = ticketDAO.findTicketById(ep.getParamA());
        } else {
            // The token does not yeald a ticket
            JsfUtil.addWarningMessage("Sorry this ticket has either already been given feedback or is not yet available for feedback.");
        }
    }
    
    // Methods------------------------------------------------------------------
    /**
     * Save the satisfaction rating and feedback
     */
    public void saveSatisfaction() {
        ticketDAO.updateSatisfaction(token, satisfied, feedback);
        emailParamDAO.delete(token);
        JsfUtil.addSuccessMessage("Thankyou for your feedback.");
    }
    
}
