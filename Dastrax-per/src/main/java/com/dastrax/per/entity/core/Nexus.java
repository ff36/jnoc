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
import javax.persistence.JoinColumn;
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
    @NamedQuery(name = "Nexus.findByPK", query = "SELECT e FROM Nexus e WHERE e.id = :id"),
})
@Entity
public class Nexus implements Serializable {
    
    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @TableGenerator(name = "Nexus_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Id
    private String id;
    private String name;
    @ManyToOne
    private Subject creator;
    @OneToMany
    private List<Metier> contMetiers = new ArrayList<>();
    private boolean excludeSubjects;
    @OneToMany
    private List<Subject> subjects = new ArrayList<>();
    @OneToMany
    @JoinColumn(name="NEXUS_ID")
    private List<Nexus> contNexus = new ArrayList<>();
    @ManyToOne
    private Metier rootAcl;
    
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
    /**
     * Metiers that are contained in the nexus.
     * @return 
     */
    public List<Metier> getContMetiers() {
        return contMetiers;
    }

    /**
     * If a metier is present we can opt to exclude selected subjects. This
     * option is not available if no metier is present.
     * @return 
     */
    public boolean isExcludeSubjects() {
        return excludeSubjects;
    }

    /**
     * Subjects can individually be added to nexus. Depending on the contMetiers
     * and the excludeSubjects settings this either includes or excludes them
     * from the nexus.
     * @return 
     */
    public List<Subject> getSubjects() {
        return subjects;
    }

    public List<Nexus> getContNexus() {
        return contNexus;
    }

    /**
     * This is set to control the three root nexus that must exist with the
     * system. Thats to say the nexus that permits all AdminSj, all VarSj
     * and all ClientSj. This shouldn't be set for other types of nexus.
     * @return 
     */
    public Metier getRootAcl() {
        return rootAcl;
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

    /**
     * Metiers that are contained in the nexus.
     * @param contMetiers 
     */
    public void setContMetiers(List<Metier> contMetiers) {
        this.contMetiers = contMetiers;
    }

    /**
     * If a metier is present we can opt to exclude selected subjects. This
     * option is not available if no metier is present.
     * @param excludeSubjects 
     */
    public void setExcludeSubjects(boolean excludeSubjects) {
        this.excludeSubjects = excludeSubjects;
    }

    /**
     * Subjects can individually be added to nexus. Depending on the contMetiers
     * and the excludeSubjects settings this either includes or excludes them
     * from the nexus.
     * @param subjects 
     */
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    /**
     * A nexus can contain other nexus to create a complete group
     * @param contNexus 
     */
    public void setContNexus(List<Nexus> contNexus) {
        this.contNexus = contNexus;
    }

    /**
     * This is set to control the three root nexus that must exist with the
     * system. Thats to say the nexus that permits all AdminSj, all VarSj
     * and all ClientSj. This shouldn't be set for other types of nexus.
     * @param rootAcl 
     */
    public void setRootAcl(Metier rootAcl) {
        this.rootAcl = rootAcl;
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
