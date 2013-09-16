/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.per.entity.core;

import java.io.Serializable;
import javax.persistence.Column;
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
    @NamedQuery(name = "Tag.findAll", query = "SELECT e FROM Tag e"),
    @NamedQuery(name = "Tag.findByPK", query = "SELECT e FROM Tag e WHERE e.id = :id"),
    @NamedQuery(name = "Tag.findByName", query = "SELECT e FROM Tag e WHERE e.name = :name"),
})
@Entity
public class Tag implements Serializable {
    
    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @TableGenerator(name = "Tag_Gen", table = "SEQ_ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Tag_Gen")
    @Id
    private String id;
    @Column(unique = true)
    private String name;
    
    // Constructors-------------------------------------------------------------
    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }
    
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    // Methods------------------------------------------------------------------
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.app.entity.Tag[ id=" + id + " ]";
    }

}
