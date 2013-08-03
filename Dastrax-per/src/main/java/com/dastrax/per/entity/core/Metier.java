/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.per.entity.core;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @version Build 2.0.0
 * @since Jul 12, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Metier.findAll", query = "SELECT e FROM Metier e"),
    @NamedQuery(name = "Metier.findByPK", query = "SELECT e FROM Metier e WHERE e.id = :id"),
    @NamedQuery(name = "Metier.findByName", query = "SELECT e FROM Metier e WHERE e.name = :name"),
})
@Entity
public class Metier implements Serializable {
    
    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Id
    private String id;
    @Column(unique = true)
    private String name;
    
    // Constructors-------------------------------------------------------------
    public Metier() {
    }

    public Metier(String id) {
        this.id = id;
    }
    
    // Getters------------------------------------------------------------------
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    // Setters------------------------------------------------------------------
    public void setName(String name) {
        this.name = name;
    }
    
    public void setId(String id) {
        this.id = id;
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
        if (!(object instanceof Metier)) {
            return false;
        }
        Metier other = (Metier) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.app.entity.Role[ id=" + id + " ]";
    }

}
