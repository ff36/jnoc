/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.entity.core;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Address.findAll", query = "SELECT e FROM Address e"),
    @NamedQuery(name = "Address.findByPK", query = "SELECT e FROM Address e WHERE e.id = :id")
})
@Entity
public class Address implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Version
    private int version;
    @TableGenerator(name = "Address_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Id
    private String id;
    private String lineOne;
    private String lineTwo;
    private String lineThree;
    private String lineFour;
    private String lineFive;
    private String country;
    private String latitude;
    private String longitude;

    // Constructors-------------------------------------------------------------
    public Address() {
    }

    // Getters------------------------------------------------------------------
    public int getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    public String getLineOne() {
        return lineOne;
    }

    public String getLineTwo() {
        return lineTwo;
    }

    public String getLineThree() {
        return lineThree;
    }

    public String getLineFour() {
        return lineFour;
    }

    public String getLineFive() {
        return lineFive;
    }

    public String getCountry() {
        return country;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    // Setters------------------------------------------------------------------  
    public void setId(String id) {
        this.id = id;
    }

    public void setLineOne(String lineOne) {
        this.lineOne = lineOne;
    }

    public void setLineTwo(String lineTwo) {
        this.lineTwo = lineTwo;
    }

    public void setLineThree(String lineThree) {
        this.lineThree = lineThree;
    }

    public void setLineFour(String lineFour) {
        this.lineFour = lineFour;
    }

    public void setLineFive(String lineFive) {
        this.lineFive = lineFive;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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
        if (!(object instanceof Address)) {
            return false;
        }
        Address other = (Address) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.iconsult.application.entity.Address[ id=" + id + " ]";
    }

    public String buildCompleteAddress() {

        String result = "Not Available";

        /*
         * Format the address in such a way that allows for any of the
         * address lines not to be present.
         */
        if (lineOne != null && lineOne.length() > 0) {
            result = lineOne;
        }

        if (lineTwo != null && lineTwo.length() > 0) {
            if (result.equals("Not Available")) {
                result = lineTwo;
            } else {
                result = result + ", " + lineTwo;
            }
        }

        if (lineThree != null && lineThree.length() > 0) {
            if (result.equals("Not Available")) {
                result = lineThree;
            } else {
                result = result + ", " + lineThree;
            }
        }

        if (lineFour != null && lineFour.length() > 0) {
            if (result.equals("Not Available")) {
                result = lineFour;
            } else {
                result = result + ", " + lineFour;
            }
        }

        if (lineFive != null && lineFive.length() > 0) {
            if (result.equals("Not Available")) {
                result = lineFive;
            } else {
                result = result + ", " + lineFive;
            }
        }

        return result;
    }
    
}

