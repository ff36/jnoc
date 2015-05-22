/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.project.DTX.IncidentEventType;
import com.dastrax.per.project.DTX.TicketPriority;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * IncidentEvent holds information pertaining to individual events that are
 * automatically triggered by a Site.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "IncidentEvent.findAll", query = "SELECT e FROM IncidentEvent e"),
    @NamedQuery(name = "IncidentEvent.findByID", query = "SELECT e FROM IncidentEvent e WHERE e.id = :id")})
@Entity
public class IncidentEvent implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(IncidentEvent.class.getName());
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String externalId;
    @Enumerated(EnumType.STRING)
    private IncidentEventType type;
    @Enumerated(EnumType.STRING)
    private TicketPriority priority;
    private String name;
    private String device;
    private String message;
    private Long receiveEpoch;
    private Long clearEpoch;
    private Long updateEpoch;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<IncidentEvent> logs;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
    @Transient
    private boolean active;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public IncidentEvent() {
        this.logs = new ArrayList<>();
        
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
     * Get the value of externalId. The supplied id of the event.
     *
     * @return the value of externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public IncidentEventType getType() {
        return type;
    }

    /**
     * Get the value of priority
     *
     * @return the value of priority
     */
    public TicketPriority getPriority() {
        return priority;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of device. The string representation of the device index
     * that triggered the event. eg. Biu1Mdbu1
     *
     * @return the value of device
     */
    public String getDevice() {
        return device;
    }

    /**
     * Get the value of message
     *
     * @return the value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the value of receiveEpoch
     *
     * @return the value of receiveEpoch
     */
    public Long getReceiveEpoch() {
        return receiveEpoch;
    }

    /**
     * Get the value of clearEpoch
     *
     * @return the value of clearEpoch
     */
    public Long getClearEpoch() {
        return clearEpoch;
    }

    /**
     * Get the value of updateEpoch
     *
     * @return the value of updateEpoch
     */
    public Long getUpdateEpoch() {
        return updateEpoch;
    }

    /**
     * Get the value of logs. If an event changes state instead of over writing
     * the state of the event a new version is created and the old version is
     * added to the logs.
     *
     * @return the value of logs
     */
    public List<IncidentEvent> getLogs() {
        return logs;
    }

    /**
     * Get the value of active. Is the event currently active.
     *
     * @return the value of active
     */
    public boolean isActive() {
        return active;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id. Unique storage key
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the value of externalId. The supplied id of the event.
     *
     * @param externalId new value of externalId
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * Set the value of type.
     *
     * @param type new value of type
     */
    public void setType(IncidentEventType type) {
        this.type = type;
    }

    /**
     * Set the value of priority.
     *
     * @param priority new value of priority
     */
    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    /**
     * Set the value of name.
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the value of device. The string representation of the device index
     * that triggered the event. eg. Biu1Mdbu1
     *
     * @param device new value of device
     */
    public void setDevice(String device) {
        this.device = device;
    }

    /**
     * Set the value of message.
     *
     * @param message new value of message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Set the value of receiveEpoch.
     *
     * @param receiveEpoch new value of receiveEpoch
     */
    public void setReceiveEpoch(Long receiveEpoch) {
        this.receiveEpoch = receiveEpoch;
    }

    /**
     * Set the value of clearEpoch.
     *
     * @param clearEpoch new value of clearEpoch
     */
    public void setClearEpoch(Long clearEpoch) {
        this.clearEpoch = clearEpoch;
    }

    /**
     * Set the value of updateEpoch.
     *
     * @param updateEpoch new value of updateEpoch
     */
    public void setUpdateEpoch(Long updateEpoch) {
        this.updateEpoch = updateEpoch;
    }

    /**
     * Set the value of logs. If an event changes state instead of over writing
     * the state of the event a new version is created and the old version is
     * added to the logs. CascadeType.PERSIST, CascadeType.MERGE,
     * CascadeType.REMOVE
     *
     * @param logs new value of logs
     */
    public void setLogs(List<IncidentEvent> logs) {
        this.logs = logs;
    }

    /**
     * Set the value of active. Is the event currently active.
     *
     * @param active new value of active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
//</editor-fold>

    /**
     * Calculates the total duration of the event including all log versions
     *
     * @return String representation of the time in the form "%d min, %d sec"
     */
    public String totalDuration() {
        if (clearEpoch > 0) {
            long totalDuration;
            // calculate the current duration
            totalDuration = clearEpoch - receiveEpoch;
            // calculate the cumulative log duration
            for (IncidentEvent ie : logs) {
                if (ie.getClearEpoch() > 0) {
                    // alarm duration in millis
                    long logDuration = ie.getClearEpoch() - ie.getReceiveEpoch();
                    totalDuration = totalDuration + logDuration;
                }
            }
            // convert duration into mins and secs
            return String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(totalDuration),
                    TimeUnit.MILLISECONDS.toSeconds(totalDuration)
                    - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(totalDuration)));
        }
        return null;
    }

    /**
     * Incident event manual deactivation.
     */
    public void manualDeactivate() {
        if (clearEpoch == 0) {
            clearEpoch = new Date().getTime();
            dap.update(this);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    /**
     * Warning - this method won't work in the case the id fields are not set.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IncidentEvent other = (IncidentEvent) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
//</editor-fold>

}
