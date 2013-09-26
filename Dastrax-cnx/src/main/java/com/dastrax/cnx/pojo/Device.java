/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.cnx.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version Build 2.0.0
 * @since Sep 19, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class Device {

    // Variables----------------------------------------------------------------
    private String id;
    private String node;
    private String address;
    private String frequency;
    private String firmware;
    private Device parent;
    private List<Device> children = new ArrayList<>();
    
    // Constructors-------------------------------------------------------------
    public Device() {
    }
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getNode() {
        return node;
    }

    public String getAddress() {
        return address;
    }

    public String getFrequency() {
        return frequency;
    }

    public Device getParent() {
        return parent;
    }

    public List<Device> getChildren() {
        return children;
    }

    public String getFirmware() {
        return firmware;
    }

    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setParent(Device parent) {
        this.parent = parent;
    }

    public void setChildren(List<Device> children) {
        this.children = children;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }
    
}
