/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */
package com.dastrax.per.entity.csm;

import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.core.Tag;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
    @NamedQuery(name = "Ticket.findAllExceptStatus", query = "SELECT e FROM Ticket e WHERE e.status <> :status"),
    @NamedQuery(name = "Ticket.findAllExceptMultiStatus", query = "SELECT e FROM Ticket e WHERE e.status <> :status1 AND e.status <> :status2"),
    @NamedQuery(name = "Ticket.findAllByType", query = "SELECT e FROM Ticket e WHERE e.type = :type"),
    @NamedQuery(name = "Ticket.findAllByCreator", query = "SELECT e FROM Ticket e JOIN e.creator a WHERE a.uid = :uid"),
    @NamedQuery(name = "Ticket.findAllByRequester", query = "SELECT e FROM Ticket e JOIN e.requester a WHERE a.uid = :uid"),
    @NamedQuery(name = "Ticket.findAllByAssignee", query = "SELECT e FROM Ticket e JOIN e.assignee a WHERE a.uid = :uid"),
    @NamedQuery(name = "Ticket.findAllByCloser", query = "SELECT e FROM Ticket e JOIN e.closer a WHERE a.uid = :uid"),})
@Entity
public class Ticket implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @TableGenerator(name = "Ticket_Gen", table = "SEQ_ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Ticket_Gen")
    @Id
    private String id;
    private String status;
    private String type;
    private String priority;
    private String title;
    @ManyToOne
    private Subject creator;
    @ManyToOne
    private Subject requester;
    @ManyToOne
    private Subject closer;
    @ManyToOne
    private Subject assignee;
    private Long openEpoch;
    private Long closeEpoch;
    private String satisfied;
    @Column(length = 8000)
    private String feedback;
    @ManyToOne
    private Site site;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Tag> tags = new ArrayList<>();

    // Constructors-------------------------------------------------------------
    public Ticket() {
    }

    /**
     * Sort comments into chronological order
     */
    public void sortComments() {
        Collections.sort(comments,
                new Comparator<Comment>() {
                    @Override
                    public int compare(Comment c1, Comment c2) {
                        if (c1.getCreated() == c2.getCreated()) {
                            return 0;
                        } else if (c1.getCreated() > c2.getCreated()) {
                            return -1;
                        }
                        return 1;
                    }
                }
                );
    }

    /**
     * Retrieves the last comment added to the ticket
     * @return 
     */
    public Comment lastComment() {
        Comment last = new Comment();
        last.setCreated(0L);
        for (Comment comment : comments) {
            if (comment.getCreated() > last.getCreated()) {
                last = comment;
            }
        }
        return last;
    }
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public Site getSite() {
        return site;
    }

    public String getTitle() {
        return title;
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

    public String isSatisfied() {
        return satisfied;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getType() {
        return type;
    }

    public Subject getCreator() {
        return creator;
    }

    public Subject getRequester() {
        return requester;
    }

    public Subject getCloser() {
        return closer;
    }

    public Subject getAssignee() {
        return assignee;
    }

    public String getFeedback() {
        return feedback;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOpenEpoch(Long openEpoch) {
        this.openEpoch = openEpoch;
    }

    public void setCloseEpoch(Long closeEpoch) {
        this.closeEpoch = closeEpoch;
    }

    /**
     * Can be PENDING, OPEN, SOLVED, ARCHIVED
     *
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

    public void setSatisfied(String satisfied) {
        this.satisfied = satisfied;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Types are: General, Equipment Failure, Connection Failure, Signal Source
     * Failure, Informational, Total System Failure
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    public void setCreator(Subject creator) {
        this.creator = creator;
    }

    public void setRequester(Subject requester) {
        this.requester = requester;
    }

    public void setCloser(Subject closer) {
        this.closer = closer;
    }

    public void setAssignee(Subject assignee) {
        this.assignee = assignee;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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
