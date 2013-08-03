/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.per.entity.core;

import com.dastrax.per.util.CountriesUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Telephone.findAll", query = "SELECT e FROM Telephone e"),
    @NamedQuery(name = "Telephone.findByPK", query = "SELECT e FROM Telephone e WHERE e.id = :id")
})
@Entity
public class Telephone implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @Version
    private int version;
    @TableGenerator(name = "Telephone_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Id
    private String id;
    private String type;
    private String country;
    private String localNumber;
    @Transient
    private boolean validNumber;

    // Constructors-------------------------------------------------------------
    public Telephone() {
        country = CountriesUtil.iso3ToCountryName(
                ResourceBundle.getBundle("Config").getString("DefaultPhoneCountry"));
    }

    // Getters------------------------------------------------------------------
    public int getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    public String getLocalNumber() {
        return localNumber;
    }

    public boolean isValidNumber() {
        return validNumber;
    }

    // Setters------------------------------------------------------------------
    public void setVersion(int version) {
        this.version = version;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLocalNumber(String localNumber) {
        this.localNumber = localNumber;
    }

    public void setValidNumber(boolean validNumber) {
        this.validNumber = validNumber;
    }

    // Methods------------------------------------------------------------------
    /**
     * Determines if a phone number is valid
     *
     * @param localNumber
     * @param iso2Code
     * @return true if the number is valid, false if the number is invalid
     */
    public boolean isValidPhoneNumber(String localNumber, String iso2Code) {
        boolean numberIsValid;

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber;
        try {
            phoneNumber = phoneUtil.parse(localNumber, iso2Code);
            numberIsValid = phoneUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException npe) {
            numberIsValid = false;
            System.err.println("NumberParseException was thrown: " + npe.toString());
        }

        return numberIsValid;
    }

    /**
     * Determines if a phone number is valid
     *
     * @return true if the number is valid, false if the number is invalid
     */
    public boolean validatePhoneNumber() {
        boolean numberIsValid = false;

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber;
        try {
            if (localNumber != null && localNumber.length() > 3) {
                String iso2Code = CountriesUtil.countryToIso2Code(country);
                phoneNumber = phoneUtil.parse(localNumber, iso2Code);
                numberIsValid = phoneUtil.isValidNumber(phoneNumber);
            }
        } catch (NumberParseException npe) {
            System.err.println("NumberParseException was thrown: " + npe.toString());
        }

        validNumber = numberIsValid;
        return numberIsValid;
    }

    /**
     * Converts
     *
     * @this PhoneNumber objects into International Format Phone Numbers
     *
     * @return the telephone number in international format
     */
    public String internationalFormat() {

        String result = null;

        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber;
            String iso2Code = CountriesUtil.countryToIso2Code(country);
            phoneNumber = phoneUtil.parse(localNumber, iso2Code);
            result = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException npe) {
            // Bad number format
        }

        return result;
    }

    /**
     * Converts PhoneNumber objects into International Format Phone Numbers
     *
     * @param localNumber
     * @param iso2Code
     * @return Phone Number as a String in International format
     */
    public String internationalFormat(String localNumber, String iso2Code) {

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(localNumber, iso2Code);
        } catch (NumberParseException npe) {
            System.err.println("NumberParseException was thrown: " + npe.toString());
        }

        return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    }

    /**
     * Converts PhoneNumber objects into national Format Phone Numbers
     *
     * @param localNumber
     * @param iso2Code
     * @return Phone Number as a String in national format
     */
    public String nationalFormat(String localNumber, String iso2Code) {

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(localNumber, iso2Code);
        } catch (NumberParseException npe) {
            System.err.println("NumberParseException was thrown: " + npe.toString());
        }

        return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Telephone)) {
            return false;
        }
        Telephone other = (Telephone) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.iconsult.application.entity.Telephone[ id=" + id + " ]";
    }
}
