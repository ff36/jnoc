/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.entity.csm;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 *
 * @version Build 2.0.0
 * @since Aug 2, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Entity
@NoSql(dataFormat = DataFormatType.MAPPED)
public class DmsComment implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Id
    @GeneratedValue
    private String id;
    @Basic
    private Long created;
    @Basic
    private String commenter;
    @Basic
    private String comment;
    @Basic
    private String nexus;

    // Constructors-------------------------------------------------------------
    public DmsComment() {
    }

    public DmsComment(Long created, String commenter, String comment) {
        this.created = created;
        this.commenter = commenter;
        this.comment = comment;
    }

    // Getters------------------------------------------------------------------
    public Long getCreated() {
        return created;
    }

    public String getCommenter() {
        return commenter;
    }

    public String getComment() {
        return comment;
    }

    public String getNexus() {
        return nexus;
    }

    public String getId() {
        return id;
    }

    // Setters------------------------------------------------------------------
    public void setCreated(Long created) {
        this.created = created;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setNexus(String nexus) {
        this.nexus = nexus;
    }
 
    public void setId(String id) {
        this.id = id;
    }
 
    // Methods------------------------------------------------------------------

   

}
