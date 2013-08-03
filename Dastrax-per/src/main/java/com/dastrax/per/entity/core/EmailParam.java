/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.per.entity.core;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "EmailParam.findAll", query = "SELECT e FROM EmailParam e"),
    @NamedQuery(name = "EmailParam.findByPK", query = "SELECT e FROM EmailParam e WHERE e.token = :token"),
    @NamedQuery(name = "EmailParam.findByEmail", query = "SELECT e FROM EmailParam e WHERE e.email = :email"),
})
@Entity
public class EmailParam implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @Id
    private String token;
    private String email;
    private String uid;
    private String paramA;
    private String paramB;
    private String paramC;
    private Long creationEpoch;

    // Constructors-------------------------------------------------------------
    public EmailParam() {
    }
    
    // Getters------------------------------------------------------------------
    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getParamA() {
        return paramA;
    }

    public String getParamB() {
        return paramB;
    }

    public String getParamC() {
        return paramC;
    }

    public Long getCreationEpoch() {
        return creationEpoch;
    }

    // Setters------------------------------------------------------------------
    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setParamA(String paramA) {
        this.paramA = paramA;
    }

    public void setParamB(String paramB) {
        this.paramB = paramB;
    }

    public void setParamC(String paramC) {
        this.paramC = paramC;
    }

    public void setCreationEpoch(Long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }

    // Methods------------------------------------------------------------------
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (token != null ? token.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the token fields are not set
        if (!(object instanceof EmailParam)) {
            return false;
        }
        EmailParam other = (EmailParam) object;
        return (this.token != null || other.token == null) && (this.token == null || this.token.equals(other.token));
    }

    @Override
    public String toString() {
        return "com.iconsult.application.entity.EmailParam[ token=" + token + " ]";
    }
}
