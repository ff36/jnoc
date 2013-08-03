/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */
package com.dastrax.per.entity.csm;

import com.dastrax.per.entity.core.Subject;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;

/**
 * Joining entity that maps subjects to tickets via the type of associated 
 * assignment that the subject has to the ticket. 
 * ie. Is the subject the CREATOR or the REQUESTER of the ticket.
 * 
 * @version Build 2.0.0
 * @since Jul 12, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "TicketHap.findAll", query = "SELECT e FROM TicketHap e"),
    @NamedQuery(name = "TicketHap.findByPK", query = "SELECT e FROM TicketHap e WHERE e.id = :id"),
})
@Entity
public class TicketHap implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @TableGenerator(name = "TicketHap_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Id
    private String id;
    @ManyToOne
    private Ticket ticket;
    @ManyToOne
    private Subject subject;
    private String assignment;

    // Constructors-------------------------------------------------------------
    public TicketHap() {
    }

    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getAssignment() {
        return assignment;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Assignment options are in DastraxCst.TicketAssignment Enum
     *
     * @param assignment
     */
    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    // Methods------------------------------------------------------------------
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TicketHap)) {
            return false;
        }
        TicketHap other = (TicketHap) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.app.entity.TicketHap[ id=" + id + " ]";
    }

}
