/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import com.dastrax.app.security.SessionUser;
import com.dastrax.per.project.DTX.FilterType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Filter holds information pertaining to application invoked filters.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Filter.findAll", query = "SELECT e FROM Filter e"),
    @NamedQuery(name = "Filter.findByID", query = "SELECT e FROM Filter e WHERE e.id = :id"),})
@Entity
public class Filter implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private FilterType type;
    private String name;
    private String expression;
    @ManyToOne
    private Nexus nexus;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public Filter() {
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
     * Get the value of nexus
     *
     * @return the value of nexus
     */
    public Nexus getNexus() {
        return nexus;
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
     * Get the value of type
     *
     * @return the value of type
     */
    public FilterType getType() {
        return type;
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
     * Set the value of nexus.
     *
     * @param nexus new value of nexus
     */
    public void setNexus(Nexus nexus) {
        this.nexus = nexus;
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
     * Set the value of type.
     *
     * @param type new value of type
     */
    public void setType(FilterType type) {
        this.type = type;
    }
//</editor-fold>

    /**
     * Set the value of expression.
     *
     * @param expression new value of expression
     */
    public void setExpression(Map<String, List<String>> expression) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            this.expression = mapper.writeValueAsString(expression);
        } catch (JsonProcessingException ex) {
            // Unable to convert map to JSON string
        }
    }

    /**
     * Get the value of expression
     *
     * @return the value of expression
     */
    public Map<String, List<String>> getExpression() {
        try {

            // Convert id values
            String exp = this.expression.replaceAll(
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
        }
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final Filter other = (Filter) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
//</editor-fold>

}
