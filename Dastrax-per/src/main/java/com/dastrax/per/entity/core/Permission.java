/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.per.entity.core;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Permission.findAll", query = "SELECT e FROM Permission e"),
    @NamedQuery(name = "Permission.findByPK", query = "SELECT e FROM Permission e WHERE e.id = :id")
})
@Entity
public class Permission implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @Version
    private int version;
    @TableGenerator(name = "Permission_Gen", table = "SEQ_ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", initialValue = 50)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Permission_Gen")
    @Id
    private String id;
    private String name;

    // Constructors-------------------------------------------------------------
    public Permission() {
    }

    public Permission(String name) {
        this.name = name;
    }

    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof Permission)) {
            return false;
        }
        Permission other = (Permission) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.iconsult.application.entity.Permission[ id=" + id + " ]";
    }

}
