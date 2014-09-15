/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Template holds information pertaining to email templates.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Template.findAll", query = "SELECT e FROM Template e"),
    @NamedQuery(name = "Template.findByPK", query = "SELECT e FROM Template e WHERE e.id = :id")
})
@Entity
public class Template implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String title;
    private String description;
    private String subject;
    @Lob
    private char[] html;
    @Lob
    private char[] plainText;
    private String linkPath;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Template() {
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Get the value of description
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the value of subject
     *
     * @return the value of subject
     */
    public String getSubject() {
        return subject;
    }
    
    /**
     * Get the value of html
     *
     * @return the value of html
     */
    public char[] getHtml() {
        return html;
    }
    
    /**
     * Get the value of plainText
     *
     * @return the value of plainText
     */
    public char[] getPlainText() {
        return plainText;
    }
    
    /**
     * Get the value of linkPath
     *
     * @return the value of linkPath
     */
    public String getLinkPath() {
        return linkPath;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id.
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Set the value of title.
     *
     * @param title new value of title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Set the value of description.
     *
     * @param description new value of description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Set the value of subject.
     *
     * @param subject new value of subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * Set the value of html.
     *
     * @param html new value of html
     */
    public void setHtml(char[] html) {
        this.html = html;
    }
    
    /**
     * Set the value of plainText.
     *
     * @param plainText new value of plainText
     */
    public void setPlainText(char[] plainText) {
        this.plainText = plainText;
    }
    
    /**
     * Set the value of linkPath.
     *
     * @param linkPath new value of linkPath
     */
    public void setLinkPath(String linkPath) {
        this.linkPath = linkPath;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    /**
     * Warning - this method won't work in the case the id fields are not set.
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Template)) {
            return false;
        }
        Template other = (Template) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}
