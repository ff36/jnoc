/*
 * Created Jul 11, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.project.DTX;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Comment holds information pertaining to support comments.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 11, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Comment.findAll", query = "SELECT e FROM Comment e"),
    @NamedQuery(name = "Comment.findByID", query = "SELECT e FROM Comment e WHERE e.id = :id"),})
@Entity
public class Comment implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Comment.class.getName());
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long createEpoch;
    @ManyToOne
    private User commenter;
    @Column(length = 8000)
    private String comment;
    @ManyToOne
    private Nexus nexus;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Comment() {
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
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
     * Get the value of createEpoch
     *
     * @return the value of createEpoch
     */
    public Long getCreateEpoch() {
        return createEpoch;
    }

    /**
     * Get the value of comment
     *
     * @return the value of comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Get the value of nexus
     *
     * @return the value of nexus
     */
    public Nexus getNexus() {
        return nexus;
    }

    /**
     * Get the value of commenter
     *
     * @return the value of commenter
     */
    public User getCommenter() {
        return commenter;
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
     * Set the value of createEpoch.
     *
     * @param createEpoch new value of createEpoch
     */
    public void setCreateEpoch(Long createEpoch) {
        this.createEpoch = createEpoch;
    }

    /**
     * Set the value of comment.
     *
     * @param comment new value of comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Set the value of nexus.
     *
     * @param nexus new value of nexus
     */
    public void setNexus(Nexus nexus) {
        this.nexus = nexus;
    }

    /**
     * Set the value of commenter.
     *
     * @param commenter new value of commenter
     */
    public void setCommenter(User commenter) {
        this.commenter = commenter;
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
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

    /**
     * Comments can have their nexus modified
     *
     * @param commentACL
     */
    public void changeNexus(String commentACL) {

        if (SessionUser.getCurrentUser().isAdministrator()) {
            if (commentACL.equals("internal")) {
                nexus = (Nexus) dap.find(Nexus.class, DTX.RootNexus.ADMIN);
            }
            if (commentACL.equals("public")) {
                nexus = null;
            }
        }
        if (SessionUser.getCurrentUser().isVAR()) {
            if (commentACL.equals("internal")) {
                nexus = (Nexus) dap.find(Nexus.class, DTX.RootNexus.ADMIN_VAR);
            }
            if (commentACL.equals("public")) {
                nexus = null;
            }
        }
    }
    
    /**
     * Update the persistence layer with a new version of the comment.
     */
    public void update() {
        dap.update(this);
    }

}
