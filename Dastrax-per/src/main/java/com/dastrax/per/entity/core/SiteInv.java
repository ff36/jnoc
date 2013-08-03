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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

/**
 *
 * @version Build 2.0.0
 * @since Jul 11, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "SiteInv.findAll", query = "SELECT e FROM SiteInv e"),
    @NamedQuery(name = "SiteInv.findByPK", query = "SELECT e FROM SiteInv e WHERE e.id = :id")
})
@Entity
public class SiteInv implements Serializable {
    
    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Version
    private int version;
    @TableGenerator(name = "SiteInv_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Id
    private String id;
    private boolean online;
    private String unit;
    private String band;
    private String name;
    private String firmware;
    private String serial;
    private String note;
    private int nodeLevel;
    @OneToMany
    private List<SiteInv> children = new ArrayList<>();
    
    // Constructors-------------------------------------------------------------
    public SiteInv() {
    }
    
    // Getters------------------------------------------------------------------
    public int getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }
    
    public boolean isOnline() {
        return online;
    }

    public String getUnit() {
        return unit;
    }

    public String getBand() {
        return band;
    }

    public String getName() {
        return name;
    }

    public String getFirmware() {
        return firmware;
    }

    public String getSerial() {
        return serial;
    }

    public String getNote() {
        return note;
    }

    public int getNodeLevel() {
        return nodeLevel;
    }

    public List<SiteInv> getChildren() {
        return children;
    }
    
    // Setters------------------------------------------------------------------
    public void setVersion(int version) {
        this.version = version;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setNodeLevel(int nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    public void setChildren(List<SiteInv> children) {
        this.children = children;
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
        if (!(object instanceof SiteInv)) {
            return false;
        }
        SiteInv other = (SiteInv) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.app.entity.SiteInv[ id=" + id + " ]";
    }

}
