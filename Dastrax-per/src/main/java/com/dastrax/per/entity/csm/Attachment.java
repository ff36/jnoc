/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.entity.csm;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;

/**
 *
 * @version Build 2.0.0
 * @since Jul 12, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Attachment.findAll", query = "SELECT e FROM Attachment e"),
    @NamedQuery(name = "Attachment.findByPK", query = "SELECT e FROM Attachment e WHERE e.id = :id"),
})
@Entity
public class Attachment implements Serializable {
    
    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @TableGenerator(name = "Attachment_Gen", table = "SEQ_ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Attachment_Gen")
    @Id
    private String id;
    private String link;

    // Constructors-------------------------------------------------------------
    public Attachment() {
    }
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setLink(String link) {
        this.link = link;
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
        if (!(object instanceof Attachment)) {
            return false;
        }
        Attachment other = (Attachment) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.app.entity.Attachment[ id=" + id + " ]";
    }

}
