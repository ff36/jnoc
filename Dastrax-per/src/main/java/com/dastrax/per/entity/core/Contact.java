/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.per.entity.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
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
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Contact.findAll", query = "SELECT e FROM Contact e"),
    @NamedQuery(name = "Contact.findByPrimaryKey", query = "SELECT e FROM Contact e WHERE e.id = :id"),
})
@Entity
public class Contact implements Serializable {

    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    // Variables----------------------------------------------------------------
    @TableGenerator(name = "Contact_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Id
    private String id;
    private String type;
    @ManyToOne
    private Nexus acl;
    private String firstName;
    private String lastName;
    private String email;
    @OneToMany(cascade = {CascadeType.ALL})
    private List<Telephone> telephones = new ArrayList<>();
    @OneToMany(cascade = {CascadeType.ALL})
    private List<Address> addresses = new ArrayList<>();
    private Long dob;

    // Constructors-------------------------------------------------------------
    public Contact() {
    }

    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Telephone> getTelephones() {
        return telephones;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public String getEmail() {
        return email;
    }

    public Long getDob() {
        return dob;
    }

    public String getType() {
        return type;
    }

    public Nexus getAcl() {
        return acl;
    }

    
    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTelephones(List<Telephone> telephones) {
        this.telephones = telephones;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDob(Long dob) {
        this.dob = dob;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAcl(Nexus acl) {
        this.acl = acl;
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
        if (!(object instanceof Contact)) {
            return false;
        }
        Contact other = (Contact) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.iconsult.application.entity.Contact[ id=" + id + " ]";
    }

    public String buildFullName() {
        String result = "No Name";
        if (firstName != null && firstName.length() > 0) {
                result = firstName;
            }

            if (lastName != null && lastName.length() > 0) {
                if (result.equals("Not Available")) {
                    result = lastName;
                } else {
                    result = result + " " + lastName;
                }
            }
        return result;
    }
    
    public void addAddress() {
        addresses.add(new Address());
    }
    
    public void removeAddress(Address address) {
        Iterator<Address> i = addresses.iterator();
        while (i.hasNext()) {
            Address a = i.next();
            if (a == address) {
                i.remove();
            }
        }
    }
    
    public void addTelephone() {
        telephones.add(new Telephone());
    }
    
    public void removeTelephone(Telephone telephone) {
        Iterator<Telephone> i = telephones.iterator();
        while (i.hasNext()) {
            Telephone t = i.next();
            if (t == telephone) {
                i.remove();
            }
        }
    }
    
}
