/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.per.entity.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Subject.findAll", query = "SELECT e FROM Subject e"),
    @NamedQuery(name = "Subject.findByPK", query = "SELECT e FROM Subject e WHERE e.uid = :uid"),
    @NamedQuery(name = "Subject.findByEmail", query = "SELECT e FROM Subject e WHERE e.email = :email"),
    @NamedQuery(name = "Subject.findByMetier", query = "SELECT e FROM Subject e WHERE e.metier.name = :name"),
    @NamedQuery(name = "Subject.findPWByPK", query = "SELECT e.password FROM Subject e WHERE e.uid = :uid"),
    @NamedQuery(name = "Subject.findByNullCompany", query = "SELECT e FROM Subject e WHERE e.company IS NULL"),
})
@Entity
public class Subject implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Version
    private int version;
    @TableGenerator(name = "Subject_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private String uid;
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid Email")
    @Column(unique = true)
    private String email;
    private String password;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Contact contact;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Account account;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Permission> permissions = new ArrayList<>();
    @OneToMany(mappedBy = "creator")
    private List<Nexus> nexus = new ArrayList<>();
    @ManyToOne
    private Metier metier;
    @ManyToOne(cascade = {CascadeType.MERGE})
    private Company company;

    // Getters------------------------------------------------------------------
    public int getVersion() {
        return version;
    }

    public String getUid() {
        return uid;
    }

    public Contact getContact() {
        return contact;
    }

    public Account getAccount() {
        return account;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public List<Nexus> getNexus() {
        return nexus;
    }

    public Metier getMetier() {
        return metier;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Company getCompany() {
        return company;
    }

    // Setters------------------------------------------------------------------
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void setNexus(List<Nexus> nexus) {
        this.nexus = nexus;
    }

    public void setMetier(Metier metier) {
        this.metier = metier;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    // Methods------------------------------------------------------------------
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Subject)) {
            return false;
        }
        Subject other = (Subject) object;
        return (this.uid != null || other.uid == null) && (this.uid == null || this.uid.equals(other.uid));
    }

    @Override
    public String toString() {
        return "com.iconsult.application.entity.Subject[ uid=" + uid + " ]";
    }

}