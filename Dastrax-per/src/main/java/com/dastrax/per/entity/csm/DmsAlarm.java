/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.entity.csm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
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
public class DmsAlarm implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Id
    @GeneratedValue
    private String id;
    @Basic
    private String alarmId;
    @Basic
    private String alarmName;
    @Basic
    private String squealer;
    @Basic
    private Long startEpoch;
    @Basic
    private Long stopEpoch;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<DmsAlarmLog> logs = new ArrayList<>();
    @Transient
    private boolean active;

    // Constructors-------------------------------------------------------------
    public DmsAlarm() {
    }

    // Getters------------------------------------------------------------------
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public String getSquealer() {
        return squealer;
    }

    public Long getStartEpoch() {
        return startEpoch;
    }

    public Long getStopEpoch() {
        return stopEpoch;
    }

    public boolean isActive() {
        return active;
    }

    public List<DmsAlarmLog> getLogs() {
        return logs;
    }

    public String getId() {
        return id;
    }

    // Setters------------------------------------------------------------------
    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public void setSquealer(String squealer) {
        this.squealer = squealer;
    }

    public void setStartEpoch(Long startEpoch) {
        this.startEpoch = startEpoch;
    }

    public void setStopEpoch(Long stopEpoch) {
        this.stopEpoch = stopEpoch;
    }

    public void setLogs(List<DmsAlarmLog> logs) {
        this.logs = logs;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Methods------------------------------------------------------------------
    /**
     * Calculates the total duration of the alarm including all log versions
     * @return String representation of the time in format 'X mins Y secs'
     */
    public String totalDuration() {
        if (stopEpoch > 0) {
            long totalDuration;
            // calculate the current duration
            totalDuration = stopEpoch - startEpoch;
            // calculate the cumulative log duration
            for (DmsAlarmLog dal : logs) {
                if (dal.getStopEpoch() > 0) {
                    // alarm duration in millis
                    long logDuration = dal.getStopEpoch() - dal.getStartEpoch();
                    totalDuration = totalDuration + logDuration;
                }
            }
            // convert duration into mins and secs
            return String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(totalDuration),
                        TimeUnit.MILLISECONDS.toSeconds(totalDuration)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDuration)));
        }
        return null;
    }

}
