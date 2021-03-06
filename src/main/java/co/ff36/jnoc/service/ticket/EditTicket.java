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

package co.ff36.jnoc.service.ticket;

import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.entity.Comment;
import co.ff36.jnoc.per.entity.Ticket;
import co.ff36.jnoc.service.navigation.Navigator;

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

            ticket.setComment(new Comment());
            
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
