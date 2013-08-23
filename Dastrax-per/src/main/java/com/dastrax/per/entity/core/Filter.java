/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.entity.core;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;

/**
 *
 * @version Build 2.0.0
 * @since Aug 5, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@NamedQueries({
    @NamedQuery(name = "Filter.findAll", query = "SELECT e FROM Filter e"),
    @NamedQuery(name = "Filter.findByPK", query = "SELECT e FROM Filter e WHERE e.id = :id"),
})
@Entity
public class Filter implements Serializable {
    
    // Serial-------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    // Variables----------------------------------------------------------------
    @TableGenerator(name = "Filter_Gen", table = "SEQ_ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", initialValue = 100)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Filter_Gen")
    @Id
    private String id;
    private String type;
    private String name;
    private String expression;
    @ManyToOne
    private Nexus nexus;
    
    // Constructors-------------------------------------------------------------
    public Filter() {
    }
    
    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getExpression() {
        return expression;
    }

    public Nexus getNexus() {
        return nexus;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
    
    // Setters------------------------------------------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setNexus(Nexus nexus) {
        this.nexus = nexus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

}
