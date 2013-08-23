/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.entity.csm;

import com.dastrax.per.entity.core.Nexus;
import com.dastrax.per.entity.core.Subject;
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
import javax.persistence.TableGenerator;

/**
 *
 * @version Build 2.0.0
 * @since Jul 11, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Comment.findAll", query = "SELECT e FROM Comment e"),
    @NamedQuery(name = "Comment.findByPK", query = "SELECT e FROM Comment e WHERE e.id = :id"),
})
@Entity
public class Comment implements Serializable {
    
    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @TableGenerator(name = "Comment_Gen", table = "SEQ_ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Comment_Gen")
    @Id
    private String id;
    private Long created;
    @ManyToOne
    private Subject commenter;
    @Column(length = 8000)
    private String comment;
    @ManyToOne
    private Nexus acl;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Attachment> attachements = new ArrayList<>();
    
    // Constructors-------------------------------------------------------------
    public Comment() {
    }
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public Long getCreated() {
        return created;
    }

    public String getComment() {
        return comment;
    }

    public Nexus getAcl() {
        return acl;
    }

    public List<Attachment> getAttachements() {
        return attachements;
    }

    public Subject getCommenter() {
        return commenter;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public void setComment(String coment) {
        this.comment = coment;
    }

    public void setAcl(Nexus acl) {
        this.acl = acl;
    }

    public void setAttachements(List<Attachment> attachements) {
        this.attachements = attachements;
    }

    public void setCommenter(Subject commenter) {
        this.commenter = commenter;
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
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.dastrax.app.entity.Comment[ id=" + id + " ]";
    }

}
