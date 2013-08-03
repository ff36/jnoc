/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.entity.csm;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Embeddable;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 *
 * @version Build 2.0.0
 * @since Aug 2, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Embeddable
@NoSql(dataFormat = DataFormatType.MAPPED)
public class DmsComment implements Serializable {
    
    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @Basic
    private Long created;
    @Basic
    private String commenterId;
    @Basic
    private String comment;

    // Constructors-------------------------------------------------------------
    public DmsComment() {
    }
    
    public DmsComment(Long created, String commenterId, String comment) {
        this.created = created;
        this.commenterId = commenterId;
        this.comment = comment;
    }
    
    // Getters------------------------------------------------------------------
    public Long getCreated() {
        return created;
    }

    public String getCommenterId() {
        return commenterId;
    }

    public String getComment() {
        return comment;
    }
    
    // Setters------------------------------------------------------------------
    public void setCreated(Long created) {
        this.created = created;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
