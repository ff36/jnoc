/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.ticket.dms;

import com.dastrax.app.util.FilterUtil;
import com.dastrax.app.util.TemporalUtil;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.dao.csm.DmsTicketDAO;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.csm.DmsAlarm;
import com.dastrax.per.entity.csm.DmsComment;
import com.dastrax.per.entity.csm.DmsTicket;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.service.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @version Build 2.0.0
 * @since Aug 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class DmsTickets implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(DmsTickets.class.getName());

    // Variables----------------------------------------------------------------
    private List<DmsTicket> tickets = new ArrayList<>();
    private DmsTicket[] selectedTickets;
    private DmsTicketModel ticketModel;
    private List<DmsTicket> filtered;
    private String filter;
    private Helper helper = new Helper();

    // EJB----------------------------------------------------------------------
    @EJB
    DmsTicketDAO dmsTicketDAO;
    @EJB
    TemporalUtil temporalUtil;
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    FilterUtil filterUtil;

    // Constructors-------------------------------------------------------------
    @PostConstruct
    public void DmsTickets() {
        helper.subjects = subjectDAO.findAllSubjectsByMetier(DastraxCst.Metier.ADMIN.toString());
    }

    public void init() {
        Map<String, List<String>> metierFilter = filterUtil.authorizedSites();
        Map<String, List<String>> optFilter = filterUtil.optionalFilter(filter);
        ticketModel = new DmsTicketModel(dmsTicketDAO, metierFilter, optFilter);
    }

    // Getters------------------------------------------------------------------
    public List<DmsTicket> getTickets() {
        return tickets;
    }

    public DmsTicket[] getSelectedTickets() {
        return selectedTickets;
    }

    public DmsTicketModel getTicketModel() {
        return ticketModel;
    }

    public List<DmsTicket> getFiltered() {
        return filtered;
    }

    public TemporalUtil getTemporalUtil() {
        return temporalUtil;
    }

    public Helper getHelper() {
        return helper;
    }

    public int getSelectedLength() {
        if (selectedTickets != null) {
            return selectedTickets.length;
        } else {
            return 0;
        }
    }

    public String getFilter() {
        return filter;
    }

    // Setters------------------------------------------------------------------
    public void setTickets(List<DmsTicket> tickets) {
        this.tickets = tickets;
    }

    public void setSelectedTickets(DmsTicket[] selectedTickets) {
        this.selectedTickets = selectedTickets;
    }

    public void setTicketModel(DmsTicketModel ticketModel) {
        this.ticketModel = ticketModel;
    }

    public void setFiltered(List<DmsTicket> filtered) {
        this.filtered = filtered;
    }

    public void setTemporalUtil(TemporalUtil temporalUtil) {
        this.temporalUtil = temporalUtil;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    // Methods------------------------------------------------------------------
    /**
     * Method to convert a DMS Ticket status to 'SOLVED'
     */
    public void statusSolved() {

        // complete the comment
        helper.completeComment();

        for (DmsTicket ticket : selectedTickets) {

            // tickets already marked as solved should be ignored
            if (!ticket.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())) {
                // tickets with active alarms cannot be closed
                boolean hasActiveAlarm = false;
                for (DmsAlarm dmsA : ticket.getAlarms()) {
                    if (dmsA.getStopEpoch() == 0) {
                        hasActiveAlarm = true;
                    }
                }
                if (!hasActiveAlarm) {
                    // change the status
                    dmsTicketDAO.updateComment(ticket.getId(), helper.closingComment);
                    dmsTicketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.SOLVED.toString());
                    JsfUtil.addSuccessMessage("Ticket DMST-" + ticket.getCause() + " status updated to " + DastraxCst.TicketStatus.SOLVED.toString());
                } else {
                    // alarm status cannot be changed
                    JsfUtil.addWarningMessage("Ticket DMST-" + ticket.getCause() + " cannot be marked as SOLVED as it still has an active alarm ");
                }
            } else {
                JsfUtil.addWarningMessage("Ticket DMST-" + ticket.getCause() + " status already " + DastraxCst.TicketStatus.SOLVED.toString());
            }
        }
        // Set the selected list to null
        selectedTickets = null;
        // Reset the comment
        helper.closingComment = new DmsComment();
    }

    /**
     * Method to convert a DMS Ticket status to all statuses except 'SOLVED'
     *
     * @param status (the desired value to convert the status to)
     */
    public void saveStatus(String status) {
        if (status.equals(DastraxCst.TicketStatus.OPEN.toString())) {
            for (DmsTicket ticket : selectedTickets) {
                if (!ticket.getStatus().equals(DastraxCst.TicketStatus.OPEN.toString())) {
                    dmsTicketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.OPEN.toString());
                    JsfUtil.addSuccessMessage("Ticket DMST-" + ticket.getCause() + " status updated to " + DastraxCst.TicketStatus.OPEN.toString());
                } else {
                    JsfUtil.addWarningMessage("Ticket DMST-" + ticket.getCause() + " status already " + DastraxCst.TicketStatus.OPEN.toString());
                }
            }
        } else if (status.equals(DastraxCst.TicketStatus.ARCHIVED.toString())) {
            for (DmsTicket ticket : selectedTickets) {
                if (ticket.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())) {
                    if (!ticket.getStatus().equals(DastraxCst.TicketStatus.ARCHIVED.toString())) {
                        dmsTicketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.ARCHIVED.toString());
                        JsfUtil.addSuccessMessage("Ticket DMST-" + ticket.getCause() + " status updated to " + DastraxCst.TicketStatus.ARCHIVED.toString());
                    } else {
                        JsfUtil.addWarningMessage("Ticket DMST-" + ticket.getCause() + " status already " + DastraxCst.TicketStatus.ARCHIVED.toString());
                    }
                    dmsTicketDAO.updateStatus(ticket.getCause(), DastraxCst.TicketStatus.ARCHIVED.toString());
                } else {
                    JsfUtil.addWarningMessage("Only " + DastraxCst.TicketStatus.SOLVED.toString() + " tickets can be archived");
                }
            }
        }
        // Set the selected list to null
        selectedTickets = null;

    }

    /**
     * Deletes the selected DMS Tickets
     */
    public void delete() {
        for (DmsTicket t : selectedTickets) {
            dmsTicketDAO.delete(t.getId());
            JsfUtil.addSuccessMessage("Ticket DMST-" + t.getCause() + " successfully deleted");
        }
        // In view scoped bean we just want to clear variables
        selectedTickets = null;
    }

    /**
     * This needs to be converted into a data list on the presentation layer.
     *
     * @param filterId
     * @throws IOException
     */
    public void tempFilterApply(int filterId) throws IOException {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        String url = null;
        // Admin
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/a/tickets/dms/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/a/tickets/dms/list.jsf?filter=" + filterId;
            }
        }
        // VAR
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/b/tickets/dms/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/b/tickets/dms/list.jsf?filter=" + filterId;
            }
        }
        // Client
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/c/tickets/dms/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/c/tickets/dms/list.jsf?filter=" + filterId;
            }
        }

        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    /**
     * When a row is edited in the JSF table the 'SAVE' event triggers this
     * method to save any changes made to the DMS ticket.
     *
     * @param event (PF specified ajax event)
     */
    public void onRowEdit(RowEditEvent event) {
        DmsTicket dmsT = (DmsTicket) event.getObject();
        dmsT.setAssignee(dmsT.getAssigneeTrans().getUid());
        dmsTicketDAO.update(dmsT);
        JsfUtil.addSuccessMessage("Ticket DMST-" + dmsT.getCause() + " successfully updated");
    }

    /**
     * This is a simple class designed to tidy up the outer-class by
     * encapsulating some variables that are needed to complete some of the out
     * methods.
     */
    public class Helper {

        // Variables----------------------------------------------------------------
        private DmsComment closingComment = new DmsComment();
        private String commentACL = "public";
        private List<Subject> subjects = new ArrayList<>();

        // Constructors-------------------------------------------------------------
        public Helper() {
        }

        // Getters------------------------------------------------------------------
        public DmsComment getClosingComment() {
            return closingComment;
        }

        public String getCommentACL() {
            return commentACL;
        }

        public List<Subject> getSubjects() {
            return subjects;
        }

        // Setters------------------------------------------------------------------
        public void setClosingComment(DmsComment closingComment) {
            this.closingComment = closingComment;
        }

        public void setCommentACL(String commentACL) {
            this.commentACL = commentACL;
        }

        public void setSubjects(List<Subject> subjects) {
            this.subjects = subjects;
        }

        // Methods------------------------------------------------------------------
        /**
         * DMS Tickets must have a comment added when the status is changed to
         * 'SOLVED'. This method provides boiler plate functions to finish off
         * the comment by adding required values to its attributes.
         */
        private void completeComment() {
            closingComment.setCommenter(subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString()).getUid());
            closingComment.setCreated(Calendar.getInstance().getTimeInMillis());

            if (commentACL.equals("internal")) {
                closingComment.setNexus(DastraxCst.ROOT_NEXUS_ADMIN);
            }
        }

        /**
         * The auto complete function allows the input field to dynamically
         * filter down options based on currently entered values. This method is
         * called whenever a 'keyup' event is detected in the field and passes
         * in the current value of the field to filter down the possible correct
         * matches from the list of subjects. Values are converted to lower case
         * to make sure that case sensitivity is not required for a match.
         *
         * @param query
         * @return The filtered list of subjects that match the current value of
         * the field.
         */
        public List<Subject> completeSubject(String query) {
            List<Subject> suggestions = new ArrayList<>();

            for (Subject s : subjects) {
                String name = s.getContact().buildFullName();
                if (name.toLowerCase().startsWith(query.toLowerCase())) {
                    suggestions.add(s);
                }
            }

            return suggestions;
        }

        /**
         * The NoSQL DB only stored subjects as UID String references. When the
         * subject needs to be displayed we need to convert them back into a
         * Subject
         *
         * @param uid
         * @return The subject represented by the specified ID
         */
        public Subject convertStringToSubject(String uid) {
            return subjectDAO.findSubjectByUid(uid);
        }

    }

}
