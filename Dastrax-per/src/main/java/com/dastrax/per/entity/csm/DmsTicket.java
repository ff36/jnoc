/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.entity.csm;

import com.dastrax.per.entity.core.Subject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 *
 * @version Build 2.0.0
 * @since Aug 2, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "DmsTicket.findAll", query = "SELECT e FROM DmsTicket e"),
    @NamedQuery(name = "DmsTicket.findByPK", query = "SELECT e FROM DmsTicket e WHERE e.id = :id"),
    @NamedQuery(name = "DmsTicket.findReverse", query = "SELECT e FROM DmsTicket e ORDER BY e.openEpoch DESC"),
    @NamedQuery(name = "DmsTicket.findByCause", query = "SELECT e FROM DmsTicket e WHERE e.cause = :cause"),
})
@Entity
@NoSql(dataFormat = DataFormatType.MAPPED)
public class DmsTicket implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Id
    @GeneratedValue
    private String id;
    @Basic
    private String cause;
    @Basic
    private String status;
    @Basic
    private String priority;
    @Basic
    private String title;
    @Basic
    private String assignee;
    @Basic
    private Long openEpoch;
    @Basic
    private Long closeEpoch;
    @Basic
    private String site;
    @Basic
    private String closer;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<DmsAlarm> alarms = new ArrayList<>();
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<DmsComment> comments = new ArrayList<>();
    @Transient
    private Subject assigneeTrans;

    // Constructors-------------------------------------------------------------
    public DmsTicket() {
    }

    // Getters------------------------------------------------------------------
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public String getCause() {
        return cause;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String getAssignee() {
        return assignee;
    }

    public Long getOpenEpoch() {
        return openEpoch;
    }

    public Long getCloseEpoch() {
        return closeEpoch;
    }

    public String getSite() {
        return site;
    }

    public String getCloser() {
        return closer;
    }

    public List<DmsAlarm> getAlarms() {
        return alarms;
    }

    public List<DmsComment> getComments() {
        return comments;
    }

    public Subject getAssigneeTrans() {
        return assigneeTrans;
    }
    
    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public void setOpenEpoch(Long openEpoch) {
        this.openEpoch = openEpoch;
    }

    public void setCloseEpoch(Long closeEpoch) {
        this.closeEpoch = closeEpoch;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setCloser(String closer) {
        this.closer = closer;
    }

    public void setAlarms(List<DmsAlarm> alarms) {
        this.alarms = alarms;
    }

    public void setComments(List<DmsComment> comments) {
        this.comments = comments;
    }

    public void setAssigneeTrans(Subject assigneeTrans) {
        this.assigneeTrans = assigneeTrans;
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
        if (!(object instanceof DmsTicket)) {
            return false;
        }
        DmsTicket other = (DmsTicket) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.per.entity.csm.DmsTicket[ id=" + id + " ]";
    }

}
