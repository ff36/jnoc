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
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Account.findAll", query = "SELECT e FROM Account e"),
    @NamedQuery(name = "Account.findByPK", query = "SELECT e FROM Account e WHERE e.id = :id"),
    @NamedQuery(name = "Account.findBySubjectPK", query = "SELECT e FROM Account e JOIN e.subject s WHERE s.uid = :uid"),
    @NamedQuery(name = "Account.findBySubjectEmail", query = "SELECT e FROM Account e JOIN e.subject s WHERE s.email = :email")
})
@Entity
public class Account implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Version
    private int version;
    @TableGenerator(name = "Account_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Id
    private String id;
    @OneToOne(mappedBy = "account")
    private Subject subject;
    private String s3id;
    private boolean confirmed;
    private boolean locked;
    private Long creationEpoch;
    private Long lastSessionEpoch;
    private Long currentSessionEpoch;
    private Long lastPswResetEpoch;

    // Constructors-------------------------------------------------------------
    public Account() {
    }

    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getS3id() {
        return s3id;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isLocked() {
        return locked;
    }

    public Long getCreationEpoch() {
        return creationEpoch;
    }

    public Long getLastSessionEpoch() {
        return lastSessionEpoch;
    }

    public Long getCurrentSessionEpoch() {
        return currentSessionEpoch;
    }

    public Long getLastPswResetEpoch() {
        return lastPswResetEpoch;
    }

    public Subject getSubject() {
        return subject;
    }

    public int getVersion() {
        return version;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setS3id(String s3id) {
        this.s3id = s3id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setCreationEpoch(Long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }

    public void setLastSessionEpoch(Long lastSessionEpoch) {
        this.lastSessionEpoch = lastSessionEpoch;
    }

    public void setCurrentSessionEpoch(Long currentSessionEpoch) {
        this.currentSessionEpoch = currentSessionEpoch;
    }

    public void setLastPswResetEpoch(Long lastPswResetEpoch) {
        this.lastPswResetEpoch = lastPswResetEpoch;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
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
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.iconsult.application.entity.AccountInfo[ id=" + id + " ]";
    }

}
