/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */
package com.dastrax.per.entity.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.validation.constraints.Pattern;

/**
 *
 * @version Build 2.0.0
 * @since Jul 11, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Site.findAll", query = "SELECT e FROM Site e"),
    @NamedQuery(name = "Site.findByPK", query = "SELECT e FROM Site e WHERE e.id = :id"),
    @NamedQuery(name = "Site.findByNullVar", query = "SELECT e FROM Site e WHERE e.var IS NULL"),
    @NamedQuery(name = "Site.findByNullClient", query = "SELECT e FROM Site e WHERE e.client IS NULL AND e.var.id = :varId"),
})
@Entity
public class Site implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Version
    private int version;
    @TableGenerator(name = "Site_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Id
    private String id;
    private String name;
    @ManyToOne
    private Company var;
    @ManyToOne
    private Company client;
    @OneToMany(cascade = {CascadeType.MERGE})
    private List<Contact> contacts = new ArrayList<>();
    @OneToOne(cascade = {CascadeType.MERGE})
    private Address address;
    @ManyToOne
    private Company installer;
    private Long installEpoch;
    private String packageType;
    private int responseHrs;
    @Pattern(regexp = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$", message = "Invalid Address")
    private String dmsIP;
    private String dmsType;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<SiteInv> inventory = new ArrayList<>();
    
    // Constructors-------------------------------------------------------------
    public Site() {
    }

    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public Company getVar() {
        return var;
    }

    public Company getClient() {
        return client;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public Address getAddress() {
        return address;
    }

    public Company getInstaller() {
        return installer;
    }

    public Long getInstallEpoch() {
        return installEpoch;
    }

    public String getPackageType() {
        return packageType;
    }

    public int getResponseHrs() {
        return responseHrs;
    }

    public String getDmsIP() {
        return dmsIP;
    }

    public List<SiteInv> getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    public String getDmsType() {
        return dmsType;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setVar(Company var) {
        this.var = var;
    }

    public void setClient(Company client) {
        this.client = client;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setInstaller(Company installer) {
        this.installer = installer;
    }

    public void setInstallEpoch(Long installEpoch) {
        this.installEpoch = installEpoch;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public void setResponseHrs(int responseHrs) {
        this.responseHrs = responseHrs;
    }

    public void setDmsIP(String dmsIP) {
        this.dmsIP = dmsIP;
    }

    public void setInventory(List<SiteInv> inventory) {
        this.inventory = inventory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDmsType(String dmsType) {
        this.dmsType = dmsType;
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
        if (!(object instanceof Site)) {
            return false;
        }
        Site other = (Site) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.app.entity.Site[ id=" + id + " ]";
    }

}
