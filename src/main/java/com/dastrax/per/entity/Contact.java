/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import com.dastrax.app.misc.TemporalUtil;
import com.dastrax.per.dap.CrudService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 * 
 * Contact holds information pertaining to contact information.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Contact.findAll", query = "SELECT e FROM Contact e"),
    @NamedQuery(name = "Contact.findByID", query = "SELECT e FROM Contact e WHERE e.id = :id"),})
@Entity
public class Contact implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Contact.class.getName());
    private static final long serialVersionUID = 1L;
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String type;
    private String firstName;
    private String lastName;
    private String email;
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Telephone> telephones;
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Address> addresses;
    private Long dobEpoch;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
    @Transient
    private Address newAddress;
    @Transient
    private Telephone newTelephone;
    @Transient
    private Date dob;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Contact() {
        this.addresses = new ArrayList<>();
        this.telephones = new ArrayList<>();
        
        this.newTelephone = new Telephone();
        this.newAddress = new Address();
        
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
//            LOG.log(Level.SEVERE, null, ex);
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
     * Get the value of firstName
     *
     * @return the value of firstName
     */ 
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Get the value of lastName
     *
     * @return the value of lastName
     */ 
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Get the value of telephones
     *
     * @return the value of telephones
     */ 
    public List<Telephone> getTelephones() {
        return telephones;
    }
    
    /**
     * Get the value of addresses
     *
     * @return the value of addresses
     */ 
    public List<Address> getAddresses() {
        return addresses;
    }
    
    /**
     * Get the value of email
     *
     * @return the value of email
     */ 
    public String getEmail() {
        return email;
    }
    
    /**
     * Get the value of dobEpoch
     *
     * @return the value of dobEpoch
     */ 
    public Long getDobEpoch() {
        return dobEpoch;
    }
    
    /**
     * Get the value of type
     *
     * @return the value of type
     */ 
    public String getType() {
        return type;
    }
    
    /**
     * Get the value of dobEpoch
     *
     * @return the value of dobEpoch
     */ 
    public Date getDob() {
        if (dobEpoch != null) {
            return new Date(dobEpoch);
        } else {
            return null;
        }
    }

    /**
     * Get the value of newAddress
     *
     * @return the value of newAddress
     */ 
    public Address getNewAddress() {
        return newAddress;
    }

    /**
     * Get the value of newTelephone
     *
     * @return the value of newTelephone
     */ 
    public Telephone getNewTelephone() {
        return newTelephone;
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
     * Set the value of firstName.
     *
     * @param firstName new value of firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Set the value of lastName.
     *
     * @param lastName new value of lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Set the value of telephones. CascadeType.ALL
     *
     * @param telephones new value of telephones
     */
    public void setTelephones(List<Telephone> telephones) {
        this.telephones = telephones;
    }
    
    /**
     * Set the value of addresses. CascadeType.ALL
     *
     * @param addresses new value of addresses
     */
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
    
    /**
     * Set the value of email.
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Set the value of dobEpoch.
     *
     * @param dobEpoch new value of dobEpoch
     */
    public void setDobEpoch(Long dobEpoch) {
        this.dobEpoch = dobEpoch;
    }

    /**
     * Set the value of dob.
     *
     * @param dob new value of dob
     */
    public void setDob(Date dob) {
        this.dobEpoch = TemporalUtil.dateToEpoch(dob);
    }

    
    /**
     * Set the value of type.
     *
     * @param type new value of type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Set the value of newAddress.
     *
     * @param newAddress new value of newAddress
     */
    public void setNewAddress(Address newAddress) {
        this.newAddress = newAddress;
    }

    /**
     * Set the value of newTelephone.
     *
     * @param newTelephone new value of newTelephone
     */
    public void setNewTelephone(Telephone newTelephone) {
        this.newTelephone = newTelephone;
    }

//</editor-fold>

    /**
     * Update the persistence layer with a new version of the contact.
     */
    public void update() {
        dap.update(this);
    }
    
    /**
     * Constructs a concatenated users name based on the available properties.
     * 
     * @return The concatenation of supplied names as follows:
     * firstName present && lastName present = "firstName lastName";
     * firstName present && lastName missing = "firstName";
     * firstName missing && lastName present = "lastName";
     * firstName missing && lastName missing = "";
     */
    public String buildFullName() {
        String result = "";
        if (firstName != null && firstName.length() > 0) {
            result = firstName;
        }

        if (lastName != null && lastName.length() > 0) {
            if (result.equals("")) {
                result = lastName;
            } else {
                result = result + " " + lastName;
            }
        }
        return result;
    }
    
    /**
     * This is just a convenience method for Primefaces Collector.
     */
    public void resetAddress() {
        newAddress = new Address();
    }

    /**
     * This is just a convenience method for Primefaces Collector.
     */
    public void resetTelephone() {
        newTelephone = new Telephone();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.id);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Contact other = (Contact) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.telephones, other.telephones)) {
            return false;
        }
        if (!Objects.equals(this.addresses, other.addresses)) {
            return false;
        }
        if (!Objects.equals(this.dobEpoch, other.dobEpoch)) {
            return false;
        }
        return true;
    }
//</editor-fold>

}
