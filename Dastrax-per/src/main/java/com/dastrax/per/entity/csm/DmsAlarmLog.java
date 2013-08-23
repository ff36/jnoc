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
 * @since Aug 9, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Entity
@NoSql(dataFormat = DataFormatType.MAPPED)
public class DmsAlarmLog implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Id
    @GeneratedValue
    private String id;
    @Basic
    private Long updateEpoch;
    @Basic
    private Long startEpoch;
    @Basic
    private Long stopEpoch;

    // Constructors-------------------------------------------------------------
    public DmsAlarmLog() {
    }
    
    // Getters------------------------------------------------------------------
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getUpdateEpoch() {
        return updateEpoch;
    }

    public Long getStartEpoch() {
        return startEpoch;
    }

    public Long getStopEpoch() {
        return stopEpoch;
    }

    public String getId() {
        return id;
    }

    // Setters------------------------------------------------------------------

    public void setUpdateEpoch(Long updateEpoch) {
        this.updateEpoch = updateEpoch;
    }

    public void setStartEpoch(Long startEpoch) {
        this.startEpoch = startEpoch;
    }

    public void setStopEpoch(Long stopEpoch) {
        this.stopEpoch = stopEpoch;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    // Methods------------------------------------------------------------------

    

}
