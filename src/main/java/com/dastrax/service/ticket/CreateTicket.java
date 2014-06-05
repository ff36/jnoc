/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.ticket;

import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.entity.User;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
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
    private String viewParamRequesterEmail;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public CreateTicket() {
        this.ticket = new Ticket();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
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
     * Get the value of viewParamRequesterEmail. An email can be specified in
     * the query parameter of the url to pre-allocate specific user to be
     * requester.
     *
     * @return the value of viewParamRequesterEmail
     */
    public String getViewParamRequesterEmail() {
        return viewParamRequesterEmail;
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
     * Set the value of viewParamRequesterEmail. An email can be specified in
     * the query parameter of the url to pre-allocate specific user to be
     * requester.
     *
     * @param viewParamRequesterEmail new value of viewParamRequesterEmail
     */
    public void setViewParamRequesterEmail(String viewParamRequesterEmail) {
        this.viewParamRequesterEmail = viewParamRequesterEmail;
    }

    //</editor-fold>
    
    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     */
    public void init() {

        ticket.initAvailableAssignees();
        ticket.initAvailableRequesters();
        ticket.initAvailableDAS();
        ticket.initAvailableTags();

        // Set the requester if they have been passed as a view parameter
        try {
            List<User> users = dap.findWithNamedQuery(
                    "User.findByEmail",
                    QueryParameter
                    .with("email", viewParamRequesterEmail)
                    .parameters());

            // Check the user email is registered and can be set as requester
            if (!users.isEmpty()) {

                // If the user exists admins can set them as requesters.
                if (SessionUser.getCurrentUser().isAdministrator()) {
                    ticket.setRequester(users.get(0));
                }

                // Can only set other people in their company and clients
                if (SessionUser.getCurrentUser().isVAR()) {
                    // Get all the client companies
                    for (Company client : users.get(0).getCompany().getClients()) {
                        // Get all the users of that client company
                        List<User> clients = dap.findWithNamedQuery(
                                "User.findByCompany",
                                QueryParameter
                                .with("id", client.getId())
                                .parameters());
                        // Check if they match
                        if (clients.contains(users.get(0))) {
                            ticket.setRequester(users.get(0));
                        }
                    }
                    // Can only set other people in their company
                    if (users.get(0).getCompany().equals(
                            SessionUser.getCurrentUser().getCompany())) {
                        ticket.setRequester(users.get(0));
                    }
                }

                // Can only set other people in their company
                if (SessionUser.getCurrentUser().isClient()) {
                    if (users.get(0).getCompany().equals(
                            SessionUser.getCurrentUser().getCompany())) {
                        ticket.setRequester(users.get(0));
                    }
                }
            }

        } catch (NullPointerException npe) {
            // Do Nothing! The View parameter is null
        }
    }
}
