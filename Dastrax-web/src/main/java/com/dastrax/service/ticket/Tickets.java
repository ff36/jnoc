/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.ticket;

import com.dastrax.app.security.PasswordSvcs;
import com.dastrax.app.util.FilterUtil;
import com.dastrax.app.util.TemporalUtil;
import com.dastrax.mesh.email.Email;
import com.dastrax.mesh.email.EmailUtil;
import com.dastrax.per.dao.core.EmailParamDAO;
import com.dastrax.per.dao.core.EmailTemplateDAO;
import com.dastrax.per.dao.core.NexusDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.dao.csm.TicketDAO;
import com.dastrax.per.entity.core.EmailParam;
import com.dastrax.per.entity.core.EmailTemplate;
import com.dastrax.per.entity.core.Nexus;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.csm.Comment;
import com.dastrax.per.entity.csm.Ticket;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.service.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Aug 4, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class Tickets implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(Tickets.class.getName());

    // Variables----------------------------------------------------------------
    private List<Ticket> tickets = new ArrayList<>();
    private Ticket[] selectedTickets;
    private TicketModel ticketModel;
    private List<Ticket> filtered;
    private String principalPassword;
    private String filter;
    private Helper helper = new Helper();

    // EJB----------------------------------------------------------------------
    @EJB
    TicketDAO ticketDAO;
    @EJB
    PasswordSvcs passwordSvcs;
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    FilterUtil filterUtil;
    @EJB
    EmailTemplateDAO emailTemplateDAO;
    @EJB
    EmailUtil emailUtil;
    @EJB
    NexusDAO nexusDAO;
    @EJB
    EmailParamDAO emailParamDAO;
    @EJB
    TemporalUtil temporalUtil;

    // Constructors-------------------------------------------------------------
    public void init() {
        Map<String, List<String>> metierFilter = filterUtil.authorizedCompanies();
        Map<String, List<String>> optFilter = filterUtil.optionalFilter(filter);
        ticketModel = new TicketModel(ticketDAO, metierFilter, optFilter);
    }

    // Getters------------------------------------------------------------------
    public List<Ticket> getTickets() {
        return tickets;
    }

    public Ticket[] getSelectedTickets() {
        return selectedTickets;
    }

    public TicketModel getTicketModel() {
        return ticketModel;
    }

    public List<Ticket> getFiltered() {
        return filtered;
    }

    public String getPrincipalPassword() {
        return principalPassword;
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

    public Helper getHelper() {
        return helper;
    }

    public TemporalUtil getTemporalUtil() {
        return temporalUtil;
    }

    // Setters------------------------------------------------------------------
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void setSelectedTickets(Ticket[] selectedTickets) {
        this.selectedTickets = selectedTickets;
    }

    public void setTicketModel(TicketModel ticketModel) {
        this.ticketModel = ticketModel;
    }

    public void setFiltered(List<Ticket> filtered) {
        this.filtered = filtered;
    }

    public void setPrincipalPassword(String principalPassword) {
        this.principalPassword = principalPassword;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    // Methods------------------------------------------------------------------
    /**
     * This method provides the the access point to delete selected accounts.
     */
    public void delete() {
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            for (Ticket t : selectedTickets) {
                ticketDAO.delete(t.getId());
                JsfUtil.addSuccessMessage("Ticket DTX-" + t.getId() + " successfully deleted");
            }
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            for (Ticket t : selectedTickets) {
                // Can only delete if they created the ticket OR an admin is not yet handling the ticket
                if ((t.getAssignee() != null
                        && !t.getAssignee().getMetier().getName().equals(DastraxCst.Metier.ADMIN.toString()))
                        || (t.getRequester().getUid().equals(
                        SecurityUtils.getSubject().getPrincipals()
                        .asList().get(1).toString()))) {
                    ticketDAO.delete(t.getId());
                    JsfUtil.addSuccessMessage("Ticket DTX-" + t.getId() + " successfully deleted");
                } else {
                    JsfUtil.addWarningMessage("Ticket DTX-" + t.getId() + " cannot be deleted as it is being handled by a Dastrax Administrator");
                }
            }
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            // Can only delete if they created the ticket OR an admin is not yet handling the ticket
            for (Ticket t : selectedTickets) {
                if (t.getAssignee() != null || t.getRequester().getUid().equals(
                        SecurityUtils.getSubject().getPrincipals()
                        .asList().get(1).toString())) {
                    ticketDAO.delete(t.getId());
                    JsfUtil.addSuccessMessage("Ticket DTX-" + t.getId() + " successfully deleted");
                } else {
                    JsfUtil.addWarningMessage("Ticket DTX-" + t.getId() + " has been assigned to "
                            + t.getAssignee().getContact().buildFullName() + " and can only be deleted by "
                            + t.getAssignee().getContact().buildFullName() + " or "
                            + t.getRequester().getContact().buildFullName());
                }
            }
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
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/a/tickets/manual/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/a/tickets/manual/list.jsf?filter=" + filterId;
            }
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/b/tickets/manual/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/b/tickets/manual/list.jsf?filter=" + filterId;
            }
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            if (filterId == 0) {
                url = ectx.getRequestContextPath() + "/c/tickets/manual/list.jsf";
            } else {
                url = ectx.getRequestContextPath() + "/c/tickets/manual/list.jsf?filter=" + filterId;
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    /**
     * Redirects the subject to the ticket edit page and specifies the ticket
     * that needs to edited as a parameter.
     * @param ticketId
     * @throws IOException 
     */
    public void editTicket(String ticketId) throws IOException {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        String url = null;
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            url = ectx.getRequestContextPath() + "/a/tickets/manual/edit.jsf?ticket=" + ticketId;
        }
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            url = ectx.getRequestContextPath() + "/b/tickets/manual/edit.jsf?ticket=" + ticketId;
        }
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            url = ectx.getRequestContextPath() + "/c/tickets/manual/edit.jsf?ticket=" + ticketId;
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    /**
     * Method to convert a Ticket status to all statuses except 'SOLVED'
     * 
     * @param status (the desired value to convert the status to)
     */
    public void saveStatus(String status) {
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            // Convert to OPEN if not already OPEN
            if (status.equals(DastraxCst.TicketStatus.OPEN.toString())) {
                for (Ticket ticket : selectedTickets) {
                    if (!ticket.getStatus().equals(DastraxCst.TicketStatus.OPEN.toString())) {
                        ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.OPEN.toString());
                        JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " status updated to " + DastraxCst.TicketStatus.OPEN.toString());
                    } else {
                        JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " status already " + DastraxCst.TicketStatus.OPEN.toString());
                    }
                }
                // Convert to ARCHIVE if not already ARCHIVE and is SOLVED
            } else if (status.equals(DastraxCst.TicketStatus.ARCHIVED.toString())) {
                for (Ticket ticket : selectedTickets) {
                    if (ticket.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())) {
                        if (!ticket.getStatus().equals(DastraxCst.TicketStatus.ARCHIVED.toString())) {
                            ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.ARCHIVED.toString());
                            JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " status updated to " + DastraxCst.TicketStatus.ARCHIVED.toString());
                        } else {
                            JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " status already " + DastraxCst.TicketStatus.ARCHIVED.toString());
                        }
                        ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.ARCHIVED.toString());
                    } else {
                        JsfUtil.addWarningMessage("Only " + DastraxCst.TicketStatus.SOLVED.toString() + " tickets can be archived");
                    }
                }
            }
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            // Convert to OPEN if not already OPEN
            if (status.equals(DastraxCst.TicketStatus.OPEN.toString())) {
                for (Ticket ticket : selectedTickets) {
                    // Can only change to open if subject is requester or an admin is not yet assigneed
                    if (ticket.getAssignee() != null
                            && !ticket.getAssignee().getMetier().getName().equals(DastraxCst.Metier.ADMIN.toString())) {

                        if (!ticket.getStatus().equals(DastraxCst.TicketStatus.OPEN.toString())) {
                            ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.OPEN.toString());
                            JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " status updated to " + DastraxCst.TicketStatus.OPEN.toString());
                        } else {
                            JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " status already " + DastraxCst.TicketStatus.OPEN.toString());
                        }

                    } else {
                        JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " cannot be edited as it is being handled by a Dastrax Administrator");
                    }
                }
                // Convert to ARCHIVE if not already ARCHIVE and is SOLVED
            } else if (status.equals(DastraxCst.TicketStatus.ARCHIVED.toString())) {
                for (Ticket ticket : selectedTickets) {
                    // Can only change to open if subject is requester or an admin is not yet assigneed
                    if (ticket.getAssignee() != null
                            && !ticket.getAssignee().getMetier().getName().equals(DastraxCst.Metier.ADMIN.toString())) {

                        if (ticket.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())) {
                            if (!ticket.getStatus().equals(DastraxCst.TicketStatus.ARCHIVED.toString())) {
                                ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.ARCHIVED.toString());
                                JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " status updated to " + DastraxCst.TicketStatus.ARCHIVED.toString());
                            } else {
                                JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " status already " + DastraxCst.TicketStatus.ARCHIVED.toString());
                            }
                            ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.ARCHIVED.toString());
                        } else {
                            JsfUtil.addWarningMessage("Only " + DastraxCst.TicketStatus.SOLVED.toString() + " tickets can be archived");
                        }

                    } else {
                        JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " cannot be edited as it is being handled by a Dastrax Administrator");
                    }
                }
            }
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            // Convert to OPEN if not already OPEN
            if (status.equals(DastraxCst.TicketStatus.OPEN.toString())) {
                for (Ticket ticket : selectedTickets) {

                    if (!ticket.getStatus().equals(DastraxCst.TicketStatus.OPEN.toString())) {
                        ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.OPEN.toString());
                        JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " status updated to " + DastraxCst.TicketStatus.OPEN.toString());
                    } else {
                        JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " status already " + DastraxCst.TicketStatus.OPEN.toString());
                    }

                }
                // Convert to ARCHIVE if not already ARCHIVE and is SOLVED
            } else if (status.equals(DastraxCst.TicketStatus.ARCHIVED.toString())) {
                for (Ticket ticket : selectedTickets) {

                    if (ticket.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())) {
                        if (!ticket.getStatus().equals(DastraxCst.TicketStatus.ARCHIVED.toString())) {
                            ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.ARCHIVED.toString());
                            JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " status updated to " + DastraxCst.TicketStatus.ARCHIVED.toString());
                        } else {
                            JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " status already " + DastraxCst.TicketStatus.ARCHIVED.toString());
                        }
                        ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.ARCHIVED.toString());
                    } else {
                        JsfUtil.addWarningMessage("Only " + DastraxCst.TicketStatus.SOLVED.toString() + " tickets can be archived");
                    }

                }
            }
        }

        // Set the selected list to null
        selectedTickets = null;
    }

    /**
     * Method to convert a Ticket status to 'SOLVED'
     */
    public void statusSolved() {
        // complete the comment
        helper.completeComment();
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            for (Ticket ticket : selectedTickets) {
                if (!ticket.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())) {

                    ticketDAO.updateComment(ticket.getId(), helper.closingComment);
                    ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.SOLVED.toString());
                    Subject s = subjectDAO.findSubjectByUid(
                            SecurityUtils.getSubject().getPrincipals()
                            .asList().get(1).toString());
                    ticketDAO.updateCloser(ticket.getId(), s);

                    // Send the email
                    sendEmail(ticket, ticket.getRequester().getEmail());
                    JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " status updated to " + DastraxCst.TicketStatus.SOLVED.toString());
                } else {
                    JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " status already " + DastraxCst.TicketStatus.SOLVED.toString());
                }
            }
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            for (Ticket ticket : selectedTickets) {
                if (ticket.getAssignee() != null
                        && !ticket.getAssignee().getMetier().getName().equals(DastraxCst.Metier.ADMIN.toString())) {
                    if (!ticket.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())) {

                        ticketDAO.updateComment(ticket.getId(), helper.closingComment);
                        ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.SOLVED.toString());
                        Subject s = subjectDAO.findSubjectByUid(
                                SecurityUtils.getSubject().getPrincipals()
                                .asList().get(1).toString());
                        ticketDAO.updateCloser(ticket.getId(), s);

                        // Send the email
                        sendEmail(ticket, ticket.getRequester().getEmail());
                        JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " status updated to " + DastraxCst.TicketStatus.SOLVED.toString());
                    } else {
                        JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " status already " + DastraxCst.TicketStatus.SOLVED.toString());
                    }
                } else {
                    JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " cannot be edited as it is being handled by a Dastrax Administrator");
                }
            }
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            for (Ticket ticket : selectedTickets) {
                if (!ticket.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())) {

                    ticketDAO.updateComment(ticket.getId(), helper.closingComment);
                    ticketDAO.updateStatus(ticket.getId(), DastraxCst.TicketStatus.SOLVED.toString());
                    Subject s = subjectDAO.findSubjectByUid(
                            SecurityUtils.getSubject().getPrincipals()
                            .asList().get(1).toString());
                    ticketDAO.updateCloser(ticket.getId(), s);

                    // Send the email
                    sendEmail(ticket, ticket.getRequester().getEmail());
                    JsfUtil.addSuccessMessage("Ticket DTX-" + ticket.getId() + " status updated to " + DastraxCst.TicketStatus.SOLVED.toString());
                } else {
                    JsfUtil.addWarningMessage("Ticket DTX-" + ticket.getId() + " status already " + DastraxCst.TicketStatus.SOLVED.toString());
                }
            }
        }
        // Set the selected list to null
        selectedTickets = null;
        // Reset the comment
        helper.closingComment = new Comment();
    }

    /**
     * Sends asynchronous email to the specified recipient
     * @param ticket
     * @param recipient
     * @return the email parameter object to be stored in the database
     */
    private EmailParam sendEmail(Ticket ticket, String recipient) {
        // Build an new email
        Email email = new Email();

        // Set the recipient
        email.setRecipientEmail(recipient.toLowerCase());

        // Set the variables
        Map<String, String> vars = new HashMap<>();
        vars.put("ticket_id", "DTX-" + ticket.getId());
        vars.put("ticket_title", ticket.getTitle());
        vars.put("ticket_status", ticket.getStatus());
        email.setVariables(vars);
        
        // Set the email Params
        email.getParam().setToken(UUID.randomUUID().toString());
        email.getParam().setCreationEpoch(Calendar.getInstance().getTimeInMillis());
        email.getParam().setParamA(ticket.getId());
        
        // Persist the params
        emailParamDAO.create(email.getParam());

        // Retreive the email template from the database.
        EmailTemplate et = emailTemplateDAO.findTemplateById(
                DastraxCst.EmailTemplate.TICKET_SOLVED.toString());
        email.setTemplate(et);

        // Send the email
        emailUtil.build(email);

        return email.getParam();
    }

    /**
     * This is a simple class designed to tidy up the outer-class by
     * encapsulating some variables that are needed to complete some of the out
     * methods.
     */
    public class Helper {

        // Variables----------------------------------------------------------------
        private Comment closingComment = new Comment();
        private String commentACL = "public";

        // Constructors-------------------------------------------------------------
        public Helper() {
        }

        // Getters------------------------------------------------------------------
        public Comment getClosingComment() {
            return closingComment;
        }

        public String getCommentACL() {
            return commentACL;
        }

        // Setters------------------------------------------------------------------
        public void setClosingComment(Comment closingComment) {
            this.closingComment = closingComment;
        }

        public void setCommentACL(String commentACL) {
            this.commentACL = commentACL;
        }

        // Methods------------------------------------------------------------------
        /**
         * Tickets must have a comment added when the status is changed to
         * 'SOLVED'. This method provides boiler plate functions to finish off
         * the comment by adding required values to its attributes.
         */
        private void completeComment() {
            closingComment.setCommenter(subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString()));
            closingComment.setCreated(Calendar.getInstance().getTimeInMillis());
            // ADMIN access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
                if (commentACL.equals("internal")) {
                    Nexus nexus = nexusDAO.findNexusById(DastraxCst.ROOT_NEXUS_ADMIN);
                    closingComment.setAcl(nexus);
                }
            }
            // VAR access
            if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
                if (commentACL.equals("internal")) {
                    Nexus nexus = nexusDAO.findNexusById(DastraxCst.ROOT_NEXUS_ADMIN_VAR);
                    closingComment.setAcl(nexus);
                }
            }
        }
        
        public void aclListener() {
            // empty listener to fire when acl is changed
        }

    }

}
