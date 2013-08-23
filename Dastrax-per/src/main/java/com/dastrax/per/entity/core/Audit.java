/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.entity.core;

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
 * @since Aug 8, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Audit.findAll", query = "SELECT e FROM Audit e"),
    @NamedQuery(name = "Audit.findByPK", query = "SELECT e FROM Audit e WHERE e.id = :id"),
    @NamedQuery(name = "Audit.findSince", query = "SELECT e FROM Audit e WHERE e.created > :time"),
    @NamedQuery(name = "Audit.findReverse", query = "SELECT e FROM Audit e ORDER BY e.created DESC"),
})
@Entity
@NoSql(dataFormat = DataFormatType.MAPPED)
public class Audit implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Id
    @GeneratedValue
    private String id;
    @Basic
    private long created;
    @Basic
    private String event;
    @Basic
    private String author;
    
    // Constructors-------------------------------------------------------------
    public Audit() {
    }

    public Audit(long created, String event, String author) {
        this.created = created;
        this.event = event;
        this.author = author;
    }

    public Audit(String id, long created, String event, String author) {
        this.id = id;
        this.created = created;
        this.event = event;
        this.author = author;
    }
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public long getCreated() {
        return created;
    }

    public String getEvent() {
        return event;
    }

    public String getAuthor() {
        return author;
    }
    
    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setAuthor(String author) {
        this.author = author;
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
        if (!(object instanceof Audit)) {
            return false;
        }
        Audit other = (Audit) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.per.entity.core.Audit[ id=" + id + " ]";
    }
    
}
