/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */
package com.dastrax.per.entity.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    @NamedQuery(name = "Nexus.findAll", query = "SELECT e FROM Nexus e"),
    @NamedQuery(name = "Nexus.findByPK", query = "SELECT e FROM Nexus e WHERE e.id = :id"),})
@Entity
public class Nexus implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @TableGenerator(name = "Nexus_Gen", table = "SEQ_ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", initialValue = 10)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Nexus_Gen")
    @Id
    private String id;
    private String name;
    @ManyToOne
    private Subject creator;
    @OneToMany
    private List<Subject> membersEXP = new ArrayList<>();
    private String membersIMP;

    // Constructors-------------------------------------------------------------
    public Nexus() {
    }

    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Subject getCreator() {
        return creator;
    }

    public List<Subject> getMembersEXP() {
        return membersEXP;
    }

    public String getMembersIMP() {
        return membersIMP;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreator(Subject creator) {
        this.creator = creator;
    }

    public void setMembersEXP(List<Subject> membersEXP) {
        this.membersEXP = membersEXP;
    }

    public void setMembersIMP(String membersIMP) {
        this.membersIMP = membersIMP;
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
        if (!(object instanceof Nexus)) {
            return false;
        }
        Nexus other = (Nexus) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.app.entity.Group[ id=" + id + " ]";
    }

    
}
