/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/


/*
 * Created Jul 10, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.per.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Permission holds information pertaining to user rights.
 * 
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Permission.findAll", query = "SELECT e FROM Permission e"),
    @NamedQuery(name = "Permission.findByID", query = "SELECT e FROM Permission e WHERE e.id = :id")
})
@Entity
public class Permission implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    
    @Version
    private int version;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String expression;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Permission() {
    }
    
    public Permission(String expression) {
        this.expression = expression;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of id. Unique storage key
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Get the value of version. Unique storage version
     *
     * @return the value of version
     */
    public int getVersion() {
        return version;
    }
    
    /**
     * Get the value of expression.
     *
     * @return the value of expression
     */
    public String getExpression() {
        return expression;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id. Unique storage key
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Set the value of version. Unique storage version
     *
     * @param version new value of version
     */
    public void setVersion(int version) {
        this.version = version;
    }
    
    /**
     * Set the value of expression.
     *
     * @param expression new value of expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Permission other = (Permission) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.expression, other.expression)) {
            return false;
        }
        return true;
    }
//</editor-fold>

}
