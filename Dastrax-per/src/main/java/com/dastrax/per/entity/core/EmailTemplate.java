/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.entity.core;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "EmailTemplate.findAll", query = "SELECT e FROM EmailTemplate e"),
    @NamedQuery(name = "EmailTemplate.findByPK", query = "SELECT e FROM EmailTemplate e WHERE e.id = :id")
})
@Entity
public class EmailTemplate implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @Id
    private String id;
    private String title;
    private String description;
    private String subject;
    @Lob
    private char[] html;
    @Lob
    private char[] plainText;
    private String linkPath;

    // Constructors-------------------------------------------------------------
    public EmailTemplate() {
    }
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSubject() {
        return subject;
    }

    public char[] getHtml() {
        return html;
    }

    public char[] getPlainText() {
        return plainText;
    }

    public String getLinkPath() {
        return linkPath;
    }
    
    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setHtml(char[] html) {
        this.html = html;
    }

    public void setPlainText(char[] plainText) {
        this.plainText = plainText;
    }

    public void setLinkPath(String linkPath) {
        this.linkPath = linkPath;
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
        if (!(object instanceof EmailTemplate)) {
            return false;
        }
        EmailTemplate other = (EmailTemplate) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.iconsult.application.entity.EmailTemplate[ id=" + id + " ]";
    }
}
