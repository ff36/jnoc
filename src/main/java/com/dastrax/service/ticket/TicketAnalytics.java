/*
 * Created Nov 4, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.ticket;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Comment;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.project.DTX;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * {description}
 *
 * @version 3.1.0
 * @since Build 141104.092550
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class TicketAnalytics implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(EditTicket.class.getName());
    private static final long serialVersionUID = 1L;

    private String data;
    private boolean render;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public TicketAnalytics() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of render.
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }

    public String getData() {
        return data;
    }
//</editor-fold>

    /**
     * Initialize the page by loading the specified ticket from the persistence
     * layer.
     */
    public void init() {

        // Get the ticket from the persistence layer
        List<Ticket> tickets = dap.findWithNamedQuery("Ticket.findAll");

        List<TicketDigest> digests = new ArrayList<>();
        for (Ticket ticket : tickets) {
            // Only process tickets that belong to legitimate users
            if (!"UNDEFINED".equals(ticket.getRequester().getMetier().getName())) {
                TicketDigest ticketDigest = new TicketDigest();
                ticketDigest.create(ticket);
                digests.add(ticketDigest);
            }
        }

        try {
            data = new ObjectMapper().writeValueAsString(digests);
        } catch (JsonProcessingException ex) {
            LOG.log(Level.SEVERE, "JSON SERIALIZATION ERROR", ex);
        }

        render = true;
    }

    /**
     * {description}
     *
     * @version 3.1.0
     * @since Build 141104.092550
     * @author Tarka L'Herpiniere
     * @author <tarka@solid.com>
     */
    protected class TicketDigest {

        //<editor-fold defaultstate="collapsed" desc="Properties">
        private Long id;
        private DTX.TicketStatus status;
        private DTX.TicketTopic topic;
        private DTX.TicketSeverity severity;
        private String creatorName;
        private String requesterName;
        private String requesterCompany;
        private String closerName;
        private String assigneeName;
        private String title;
        private Long openEpoch;
        private Long closeEpoch;
        private DTX.TicketSatisfaction satisfied;
        private String das;
        private int commentQty;
        private long resolutionDuration;
        private long averageResponseTime;
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public TicketDigest() {
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Getters & Setters">
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public DTX.TicketStatus getStatus() {
            return status;
        }

        public void setStatus(DTX.TicketStatus status) {
            this.status = status;
        }

        public DTX.TicketTopic getTopic() {
            return topic;
        }

        public void setTopic(DTX.TicketTopic topic) {
            this.topic = topic;
        }

        public DTX.TicketSeverity getSeverity() {
            return severity;
        }

        public void setSeverity(DTX.TicketSeverity severity) {
            this.severity = severity;
        }

        public String getCreatorName() {
            return creatorName;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public String getRequesterName() {
            return requesterName;
        }

        public void setRequesterName(String requesterName) {
            this.requesterName = requesterName;
        }

        public String getRequesterCompany() {
            return requesterCompany;
        }

        public void setRequesterCompany(String requesterCompany) {
            this.requesterCompany = requesterCompany;
        }

        public String getCloserName() {
            return closerName;
        }

        public void setCloserName(String closerName) {
            this.closerName = closerName;
        }

        public String getAssigneeName() {
            return assigneeName;
        }

        public void setAssigneeName(String assigneeName) {
            this.assigneeName = assigneeName;
        }

        public Long getOpenEpoch() {
            return openEpoch;
        }

        public void setOpenEpoch(Long openEpoch) {
            this.openEpoch = openEpoch;
        }

        public Long getCloseEpoch() {
            return closeEpoch;
        }

        public void setCloseEpoch(Long closeEpoch) {
            this.closeEpoch = closeEpoch;
        }

        public DTX.TicketSatisfaction getSatisfied() {
            return satisfied;
        }

        public void setSatisfied(DTX.TicketSatisfaction satisfied) {
            this.satisfied = satisfied;
        }

        public String getDas() {
            return das;
        }

        public void setDas(String das) {
            this.das = das;
        }

        public int getCommentQty() {
            return commentQty;
        }

        public void setCommentQty(int commentQty) {
            this.commentQty = commentQty;
        }

        public long getResolutionDuration() {
            return resolutionDuration;
        }

        public void setResolutionDuration(long resolutionDuration) {
            this.resolutionDuration = resolutionDuration;
        }

        public long getAverageResponseTime() {
            return averageResponseTime;
        }

        public void setAverageResponseTime(long averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
//</editor-fold>

        protected void create(Ticket ticket) {

            // Organise the comments
            ticket.sortCommentsOldestFirst();

            // Convert the data
            this.id = ticket.getId();
            this.status = ticket.getStatus();
            this.topic = ticket.getTopic();
            this.severity = ticket.getSeverity();
            this.openEpoch = ticket.getOpenEpoch();
            this.closeEpoch = ticket.getCloseEpoch();
            this.satisfied = ticket.getSatisfied();
            this.commentQty = ticket.getComments().size();
            this.title = ticket.getTitle();
            if (ticket.getCreator().getContact() != null) {
                this.creatorName = ticket.getCreator().getContact().buildFullName();
            }
            if (ticket.getRequester().getContact() != null) {
                this.requesterName = ticket.getRequester().getContact().buildFullName();
            }
            if (ticket.getRequester().getCompany() != null) {
                this.requesterCompany = ticket.getRequester().getCompany().getName();
            }
            // Only add assignee if one exists
            if (ticket.getAssignee() != null) {
                this.assigneeName = ticket.getAssignee().getContact().buildFullName();
            }
            // Only add DAS if one exists
            if (ticket.getDas() != null) {
                this.das = ticket.getDas().getName();
            }
            // Only calculate the close duration if the ticket is closed
            if (ticket.getCloseEpoch() != null) {
                this.resolutionDuration = resolutionDuration(ticket.getComments());
            }

            // Calculate the average response time for a ticket
            this.averageResponseTime = avgResponseTime(ticket.getComments());
        }

        /**
         * Calculate the average response time for the comments on this ticket.
         * @param comments
         * @return 
         */
        private long avgResponseTime(List<Comment> comments) {
            // The first epoch to subtract
            Comment previousComment = null;
            long totalDuration = 0;
            int numberOfComments = comments.size();

            for (Comment comment : comments) {
                if (previousComment != null) {
                    long duration = comment.getCreateEpoch() - previousComment.getCreateEpoch();
                    totalDuration = totalDuration + duration;
                }
                // Set this as the previous comment for the next iteration
                previousComment = comment;
            }
            // Calculate the average
            return totalDuration / numberOfComments;

        }

        private long resolutionDuration(List<Comment> comments) {
            long first = comments.get(0).getCreateEpoch();
            long last = comments.get(comments.size() - 1).getCreateEpoch();
            return last - first;
        }

    }

}
