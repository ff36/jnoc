/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.per.entity.csm;

import com.dastrax.per.entity.core.Comment;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Tag;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

/**
 *
 * @version Build 2.0.0
 * @since Jul 11, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Ticket.findAll", query = "SELECT e FROM Ticket e"),
    @NamedQuery(name = "Ticket.findByPK", query = "SELECT e FROM Ticket e WHERE e.id = :id"),
    @NamedQuery(name = "Ticket.findAllByStatus", query = "SELECT e FROM Ticket e WHERE e.status = :status"),
    @NamedQuery(name = "Ticket.findAllByType", query = "SELECT e FROM Ticket e WHERE e.type = :type"),
    @NamedQuery(name = "Ticket.findAllBySubject", query = "SELECT e FROM Ticket e JOIN e.hap h JOIN h.subject s WHERE s.uid = :uid"),
    @NamedQuery(name = "Ticket.findBySubjectAndAssignment", query = "SELECT e FROM Ticket e JOIN e.hap h JOIN h.subject s WHERE s.uid = :uid AND h.assignment = :assignment"),
})
@Entity
public class Ticket implements Serializable {
    
    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @TableGenerator(name = "Ticket_Gen", table = "TICKET_ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Id
    private String id;
    private String status;
    private String type;
    private String priority;
    private Long openEpoch;
    private Long closeEpoch;
    private boolean satisfied;
    @ManyToOne
    private Site site;
    @OneToMany(mappedBy = "ticket", cascade = {CascadeType.MERGE})
    private List<TicketHap> hap = new ArrayList<>();
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Comment> comments = new ArrayList<>();
    @OneToMany
    private List<Tag> tags = new ArrayList<>();
    
    // Constructors-------------------------------------------------------------
    public Ticket() {
    }
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public Site getSite() {
        return site;
    }

    public List<TicketHap> getHap() {
        return hap;
    }

    public Long getOpenEpoch() {
        return openEpoch;
    }

    public Long getCloseEpoch() {
        return closeEpoch;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getType() {
        return type;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void setHap(List<TicketHap> hap) {
        this.hap = hap;
    }

    public void setOpenEpoch(Long openEpoch) {
        this.openEpoch = openEpoch;
    }

    public void setCloseEpoch(Long closeEpoch) {
        this.closeEpoch = closeEpoch;
    }

    /**
     * Can be PENDING, OPEN, SOLVED, ARCHIVED
     * @param status 
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Types are derived from DastraxCst.TicketType
     * @param type 
     */
    public void setType(String type) {
        this.type = type;
    }
    
    // Constructors-------------------------------------------------------------
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ticket)) {
            return false;
        }
        Ticket other = (Ticket) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.app.entity.Ticket[ id=" + id + " ]";
    }

}
