/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.project.DTX;
import com.dastrax.per.project.DTX.IncidentStatus;
import com.dastrax.per.project.DTX.TicketSeverity;
import com.dastrax.app.misc.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Incident holds information pertaining to groups of IncidentEvents that are
 * automatically triggered by a DAS.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Incident.findAll", query = "SELECT e FROM Incident e"),
    @NamedQuery(name = "Incident.findByID", query = "SELECT e FROM Incident e WHERE e.id = :id"),
    @NamedQuery(name = "Incident.findReverse", query = "SELECT e FROM Incident e ORDER BY e.openEpoch DESC"),
    @NamedQuery(name = "Incident.findByCause", query = "SELECT e FROM Incident e WHERE e.cause = :cause"),})
@Entity
public class Incident implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Incident.class.getName());
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String cause;
    @Enumerated(EnumType.STRING)
    private IncidentStatus status;
    @Enumerated(EnumType.STRING)
    private TicketSeverity severity;
    private String title;
    @ManyToOne
    private User assignee;
    private Long openEpoch;
    private Long closeEpoch;
    @ManyToOne
    private DAS das;
    @ManyToOne
    private User closer;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<IncidentEvent> events;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Comment> comments;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
    @Transient
    private User assigneeTrans;
    @Transient
    private Comment closingComment;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Incident() {
        this.comments = new ArrayList<>();
        this.events = new ArrayList<>();
        this.closingComment = new Comment();
        
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
//            LOG.log(Level.SEVERE, null, ex);
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of id. Auto incrementing storage id
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the value of cause
     *
     * @return the value of cause
     */
    public String getCause() {
        return cause;
    }

    /**
     * Get the value of status
     *
     * @return the value of status
     */
    public IncidentStatus getStatus() {
        return status;
    }

    /**
     * Get the value of severity
     *
     * @return the value of severity
     */
    public TicketSeverity getSeverity() {
        return severity;
    }

    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the value of assignee
     *
     * @return the value of assignee
     */
    public User getAssignee() {
        return assignee;
    }

    /**
     * Get the value of openEpoch
     *
     * @return the value of openEpoch
     */
    public Long getOpenEpoch() {
        return openEpoch;
    }

    /**
     * Get the value of closeEpoch
     *
     * @return the value of closeEpoch
     */
    public Long getCloseEpoch() {
        return closeEpoch;
    }

    /**
     * Get the value of das
     *
     * @return the value of das
     */
    public DAS getDas() {
        return das;
    }

    /**
     * Get the value of closer
     *
     * @return the value of closer
     */
    public User getCloser() {
        return closer;
    }

    /**
     * Get the value of events
     *
     * @return the value of events
     */
    public List<IncidentEvent> getEvents() {
        return events;
    }

    /**
     * Get the value of comments
     *
     * @return the value of comments
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * Get the value of assigneeTrans
     *
     * @return the value of assigneeTrans
     */
    public User getAssigneeTrans() {
        return assigneeTrans;
    }

    /**
     * Get the value of closingComment
     *
     * @return the value of closingComment
     */
    public Comment getClosingComment() {
        return closingComment;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id.
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the value of cause.
     *
     * @param cause new value of cause
     */
    public void setCause(String cause) {
        this.cause = cause;
    }

    /**
     * Set the value of status.
     *
     * @param status new value of status
     */
    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    /**
     * Set the value of severity.
     *
     * @param severity new value of severity
     */
    public void setSeverity(TicketSeverity severity) {
        this.severity = severity;
    }

    /**
     * Set the value of title.
     *
     * @param title new value of title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the value of assignee.
     *
     * @param assignee new value of assignee
     */
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    /**
     * Set the value of openEpoch.
     *
     * @param openEpoch new value of openEpoch
     */
    public void setOpenEpoch(Long openEpoch) {
        this.openEpoch = openEpoch;
    }

    /**
     * Set the value of closeEpoch.
     *
     * @param closeEpoch new value of closeEpoch
     */
    public void setCloseEpoch(Long closeEpoch) {
        this.closeEpoch = closeEpoch;
    }

    /**
     * Set the value of das.
     *
     * @param das new value of das
     */
    public void setDas(DAS das) {
        this.das = das;
    }

    /**
     * Set the value of closer.
     *
     * @param closer new value of closer
     */
    public void setCloser(User closer) {
        this.closer = closer;
    }

    /**
     * Set the value of events. CascadeType.PERSIST, CascadeType.MERGE,
     * CascadeType.REMOVE
     *
     * @param events new value of events
     */
    public void setEvents(List<IncidentEvent> events) {
        this.events = events;
    }

    /**
     * Set the value of comments. CascadeType.PERSIST, CascadeType.MERGE,
     * CascadeType.REMOVE
     *
     * @param comments new value of comments
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Set the value of assigneeTrans.
     *
     * @param assigneeTrans new value of assigneeTrans
     */
    public void setAssigneeTrans(User assigneeTrans) {
        this.assigneeTrans = assigneeTrans;
    }

    /**
     * Set the value of closingComment.
     *
     * @param closingComment new value of closingComment
     */
    public void setClosingComment(Comment closingComment) {
        this.closingComment = closingComment;
    }

//</editor-fold>
    
    /**
     * Removes the incident from the persistence layer and any associated
     * resources linked to the incident.
     */
    public void delete() {
        dap.delete(Incident.class, id);
        JsfUtil.addSuccessMessage(
                "Incident " + id + " deleted.");
    }

    /**
     * Update the persistence layer with a new version of the incident.
     */
    public void update() {
        dap.update(this);
    }

    /**
     * Changes the incident status
     *
     * @param status (the desired value to convert the status to)
     */
    public void changeStatus(DTX.IncidentStatus status) {

        switch (status) {
            case OPEN:
                this.status = status;
                update();
                break;
            case CLOSED:
                if (activeEvents()) {
                    JsfUtil.addWarningMessage("Incident "
                            + id
                            + " cannot be SOLVED as it still has active events");
                } else if (!solved()) {
                    JsfUtil.addWarningMessage("Incident "
                            + id
                            + " is already SOLVED.");
                } else {
                    this.status = status;
                    update();
                    JsfUtil.addWarningMessage("Incident "
                            + id
                            + " is SOLVED.");
                }
                break;
            case ARCHIVED:
                if (activeEvents()) {
                    JsfUtil.addWarningMessage("Incident "
                            + id
                            + " cannot be ARCHIVED as it still has active events");
                } else if (!this.status.equals(DTX.IncidentStatus.CLOSED)) {
                    JsfUtil.addWarningMessage("Incident "
                            + id
                            + " needs to be SOLVED before it can be ARCHIVED.");
                } else {
                    this.status = status;
                    update();
                    JsfUtil.addWarningMessage("Incident "
                            + id
                            + " is ARCHIVED.");
                }
                break;
        }

    }

    /**
     * If an incident passes the requirements to have its status changed to 
     * SOLVED this method completes the external closing comment dependencies.
     * 
     * @return true if the incident has its closing comment dependencies set.
     */
    private boolean solved() {
        // Incident is already marked as solved should be ignored
        if (!this.status.equals(DTX.IncidentStatus.CLOSED)) {
            closingComment.setCommenter(closer);
            closingComment.setCreateEpoch(closeEpoch);
            comments.add(closingComment);
            return true;
        }
        return false;
    }

    /**
     * Determines if any of the associated incident events are still active.
     * 
     * @return True if any associated incident events are stile active. 
     */
    private boolean activeEvents() {
        for (IncidentEvent incidentEvent : events) {
            if (incidentEvent.getClearEpoch() == 0) {
                return true;
            }
        }
        return false;
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Warning - this method won't work in the case the id fields are not set.
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Incident)) {
            return false;
        }
        Incident other = (Incident) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}
