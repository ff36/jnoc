/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Nexus holds information pertaining to groups. A User can be evaluated against
 * a nexus to find out if there is an association, either implicit or explicit.
 * 
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Nexus.findAll", query = "SELECT e FROM Nexus e"),
    @NamedQuery(name = "Nexus.findByID", query = "SELECT e FROM Nexus e WHERE e.id = :id"),})
@Entity
public class Nexus implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Nexus.class.getName());
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    private User creator;
    @OneToMany
    private List<User> explicitMembers;
    private String implicitMembers;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Nexus() {
        this.explicitMembers = new ArrayList<>();
        
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
     * Get the value of id. Unique storage key
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the value of name. The display value of the nexus
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of description.
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the value of creator
     *
     * @return the value of creator
     */
    public User getCreator() {
        return creator;
    }

    /**
     * Get the value of explicitMembers. Users who are explicitly mentioned as
     * being associated to the nexus.
     *
     * @return the value of explicitMembers
     */
    public List<User> getExplicitMembers() {
        return explicitMembers;
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
     * Set the value of name. The display value of the nexus
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the value of description.
     *
     * @param description new value of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the value of creator.
     *
     * @param creator new value of creator
     */
    public void setCreator(User creator) {
        this.creator = creator;
    }

    /**
     * Set the value of explicitMembers. Users who are explicitly mentioned as
     * being associated to the nexus.
     *
     * @param explicitMembers new value of explicitMembers
     */
    public void setExplicitMembers(List<User> explicitMembers) {
        this.explicitMembers = explicitMembers;
    }

//</editor-fold>
    
    /**
     * Set the value of implicitMembers. A JSON expression that implies the
     * association of a User defined by a characteristic.
     *
     * @param implicitExpression new value of expression
     */
    public void setImplicitMembers(Map<String, List<String>> implicitExpression) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            this.implicitMembers = mapper.writeValueAsString(implicitExpression);
        } catch (JsonProcessingException ex) {
            // Unable to convert map to JSON string
        	LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Get the value of implicitMembers. A JSON expression that implies the
     * association of a User defined by a characteristic.
     *
     * @return the value of expression
     */
    public Map<String, List<String>> getImplicitMembers() {
        try {

            // Convert id values
            String exp = this.implicitMembers.replaceAll(
                    "ID", SessionUser.getCurrentUser().getId().toString());

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);
            mapper.configure(
                    DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                    true);
            mapper.configure(
                    DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                    false);
            return mapper.readValue(exp,
                    new TypeReference<HashMap<String, List<String>>>() {
                    });
        } catch (IOException ex) {
            // Unable to convert JSON to map
        	LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Evaluates if the currently authenticated subject is mentioned either
     * implicitly or explicitly in the supplied nexus.
     *
     * @return true if he the are otherwise false.
     */
    public boolean authorised() {
        return evaluate(SessionUser.getCurrentUser());
    }

    /**
     * Evaluates if the specified user is mentioned either implicitly or 
     * explicitly in the supplied nexus.
     *
     * @param user
     * @return true if he the are otherwise false.
     */
    public boolean authorised(User user) {
        return evaluate(user);
    }
    
    /**
     * Nexus have both implicit and explicit implications stored in a JSON
     * string. An implicit member is one that is implied by association. The
     * association could be any generic rule that can be evaluated as true or
     * false. For example subjects with a specific Metier would qualify as an
     * implicit association.
     *
     * {"metier":["1","2"]}
     *
     * Here all member that are either metier '1' or metier '2' are members.
     *
     * Explicit implications are subjects that are individually listed.
     *
     *
     * @param subject
     * @param nexus
     * @return True if the user has an association to the nexus. Otherwise false
     */
    private boolean evaluate(User user) {

            // Check if the user has an explicitly mentioned association
            if (explicitMembers.contains(user)) {
                return true;
            } else {
                // Check if the user has an implicitly mentioned association
                if (implicitMembers != null) {
                    for (Map.Entry<String, List<String>> entry
                            : getImplicitMembers().entrySet()) {
                        // Currently only checks for metier associations
                        if (entry.getKey().equals("metier")) {
                            return entry.getValue()
                                    .contains(
                                            user.getMetier()
                                            .getId().toString());
                        }
                    }

                } else {
                    /*
                     If there is no implicit condition stated in the nexus 
                     then the user is authorised
                     */
                    return true;
                }
            }

        return false;
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Warning - this method won't work in the case the id fields are not set.
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Nexus)) {
            return false;
        }
        Nexus other = (Nexus) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}
