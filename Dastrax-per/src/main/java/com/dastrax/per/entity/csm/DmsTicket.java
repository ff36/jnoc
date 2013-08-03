/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.entity.csm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
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
})
@Entity
@NoSql(dataFormat = DataFormatType.MAPPED)
public class DmsTicket implements Serializable {
// Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @Version
    private long version;
    @Id
    @GeneratedValue
    @Field(name="_id")
    private String id;
    @Basic
    private String siteId;
    @Basic
    private String ticketStatus;
    @Basic
    private String causeId;
    @Basic
    private String alarmId;
    @Basic
    private String displayId;
    @Basic
    private Long openEpoch;
    @Basic
    private Long closeEpoch;
    @Basic
    private String assigneeId;
    @Basic
    private String closerId;
    @Basic
    private String alarmName;
    @Basic
    private String squealer;
    @Basic
    private Long alarmStartEpoch;
    @Basic
    private Long alarmStopEpoch;
    @ElementCollection
    private List<DmsComment> comments = new ArrayList<>();

    // Constructors-------------------------------------------------------------
    public DmsTicket() {
    }
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public String getCauseId() {
        return causeId;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public String getDisplayId() {
        return displayId;
    }

    public Long getOpenEpoch() {
        return openEpoch;
    }

    public Long getCloseEpoch() {
        return closeEpoch;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public String getCloserId() {
        return closerId;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public String getSquealer() {
        return squealer;
    }

    public Long getAlarmStartEpoch() {
        return alarmStartEpoch;
    }

    public Long getAlarmStopEpoch() {
        return alarmStopEpoch;
    }

    public List<DmsComment> getComments() {
        return comments;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }
    
    public void setVersion(long version) {
        this.version = version;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public void setCauseId(String causeId) {
        this.causeId = causeId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public void setOpenEpoch(Long openEpoch) {
        this.openEpoch = openEpoch;
    }

    public void setCloseEpoch(Long closeEpoch) {
        this.closeEpoch = closeEpoch;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public void setCloserId(String closerId) {
        this.closerId = closerId;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public void setSquealer(String squealer) {
        this.squealer = squealer;
    }

    public void setAlarmStartEpoch(Long alarmStartEpoch) {
        this.alarmStartEpoch = alarmStartEpoch;
    }

    public void setAlarmStopEpoch(Long alarmStopEpoch) {
        this.alarmStopEpoch = alarmStopEpoch;
    }

    public void setComments(List<DmsComment> comments) {
        this.comments = comments;
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
        return "com.dastrax.app.entity.nosql.AlarmTicket[ id=" + id + " ]";
    }

}
