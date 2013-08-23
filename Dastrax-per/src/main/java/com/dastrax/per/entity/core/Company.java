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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    @NamedQuery(name = "Company.findAll", query = "SELECT e FROM Company e"),
    @NamedQuery(name = "Company.findByPK", query = "SELECT e FROM Company e WHERE e.id = :id"),
    @NamedQuery(name = "Company.findBySubdomain", query = "SELECT e FROM Company e WHERE e.subdomain = :subdomain"),
    @NamedQuery(name = "Company.findByType", query = "SELECT e FROM Company e WHERE e.type = :type"),
    @NamedQuery(name = "Company.findByNullParentVar", query = "SELECT e FROM Company e WHERE e.parentVAR IS NULL AND e.type = :type"),
})
@Entity
public class Company implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Version
    private int version;
    @TableGenerator(name = "Company_Gen", table = "SEQ_ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Company_Gen")
    @Id
    private String id;
    private String s3id;
    private String name;
    private String type;
    private String subdomain; // VARs have subdomains
    @OneToMany(cascade = {CascadeType.ALL})
    private List<Contact> contacts = new ArrayList<>();
    @OneToMany(mappedBy = "var", cascade = {CascadeType.MERGE})
    private List<Site> varSites = new ArrayList<>(); // Sites that belong to a VAR
    @OneToMany(mappedBy = "client", cascade = {CascadeType.MERGE})
    private List<Site> clientSites = new ArrayList<>(); // Sites that belong to a Client
    @OneToMany(mappedBy = "company", cascade = {CascadeType.MERGE})
    private List<Subject> members = new ArrayList<>(); // Company members
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name="VAR_ID")
    private Company parentVAR; // Clients have a parent VAR
    @OneToMany(mappedBy = "parentVAR", cascade = {CascadeType.MERGE})
    private List<Company> clients = new ArrayList<>(); // Vars have children clients

    // Constructors-------------------------------------------------------------
    public Company() {
    }

    // Getters------------------------------------------------------------------
    public int getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    public String getS3id() {
        return s3id;
    }

    public String getName() {
        return name;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public List<Site> getVarSites() {
        return varSites;
    }

    public List<Site> getClientSites() {
        return clientSites;
    }

    public List<Subject> getMembers() {
        return members;
    }

    public String getType() {
        return type;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public Company getParentVAR() {
        return parentVAR;
    }

    public List<Company> getClients() {
        return clients;
    }

    // Setters------------------------------------------------------------------
    public void setVersion(int version) {
        this.version = version;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setS3id(String s3id) {
        this.s3id = s3id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setVarSites(List<Site> varSites) {
        this.varSites = varSites;
    }

    public void setClientSites(List<Site> clientSites) {
        this.clientSites = clientSites;
    }

    public void setMembers(List<Subject> members) {
        this.members = members;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public void setParentVAR(Company parentVAR) {
        this.parentVAR = parentVAR;
    }

    public void setClients(List<Company> clients) {
        this.clients = clients;
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
        if (!(object instanceof Company)) {
            return false;
        }
        Company other = (Company) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.iconsult.application.entity.Company[ id=" + id + " ]";
    }

}
