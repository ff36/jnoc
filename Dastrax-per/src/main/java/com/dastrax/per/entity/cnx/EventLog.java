/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.entity.cnx;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 *
 * @version Build 2.0.0
 * @since Sep 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "EventLog.findAll", query = "SELECT e FROM EventLog e"),
    @NamedQuery(name = "EventLog.findByPK", query = "SELECT e FROM EventLog e WHERE e.id = :id"),
    @NamedQuery(name = "EventLog.findBySite", query = "SELECT e FROM EventLog e WHERE e.site = :site"),
    @NamedQuery(name = "EventLog.findLastEntryBySite", query = "SELECT e FROM EventLog e WHERE e.site = :site ORDER BY e.externalId DESC"),
})
@Entity
@NoSql(dataFormat = DataFormatType.MAPPED)
public class EventLog implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Id
    @GeneratedValue
    private String id;
    @Basic
    private String site;
    @Basic
    private int externalId;
    @Basic
    private String address;
    @Basic
    private String dbcolumn;
    @Basic
    private long receivedTime;
    @Basic
    private long clearedTime;
    @Basic
    private long downTime;
    @Basic
    private String frequency;
    @Basic
    private String carrier;
    @Basic
    private long hysteresis;

    // Constructors-------------------------------------------------------------
    public EventLog() {
    }
    
    // Getters------------------------------------------------------------------
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public String getSite() {
        return site;
    }

    public int getExternalId() {
        return externalId;
    }

    public String getAddress() {
        return address;
    }

    public String getDbcolumn() {
        return dbcolumn;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public long getClearedTime() {
        return clearedTime;
    }

    public long getDownTime() {
        return downTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getCarrier() {
        return carrier;
    }

    public long getHysteresis() {
        return hysteresis;
    }
    
    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setExternalId(int externalId) {
        this.externalId = externalId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDbcolumn(String dbcolumn) {
        this.dbcolumn = dbcolumn;
    }

    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public void setClearedTime(long clearedTime) {
        this.clearedTime = clearedTime;
    }

    public void setDownTime(long downTime) {
        this.downTime = downTime;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setHysteresis(long hysteresis) {
        this.hysteresis = hysteresis;
    }
    
    
}
