/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/


/*
 * Created May 19, 2014.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.app.model;

import com.jnoc.per.entity.Incident;
import com.jnoc.per.entity.Incident_;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.SortOrder;

/**
 * An implementation of the ModelQuery allowing dynamic CriteriaQuery to be
 * created at runtime.
 *
 * @version 3.0-SNAPSHOT
 * @since Build 3.0.0 (May 19, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class IncidentModelQuery implements ModelQuery {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger (IncidentModelQuery.class.getName());
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance of IncidentModelQuery
     */
    public IncidentModelQuery ()
    {
    }
//</editor-fold>

    /**
     * A type safe CriteriaQuery dynamically constructed of an optional 
     * root filter (applied automatically based on the users Metier),
     * an optional filter that can be set via a URL query parameter, and a 
     * global filter that is implemented as the user generates a 'keyup' event
     * in the search field.
     * 
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param builder
     * @param filters
     * @param rootFilter
     * @param optionalFilter
     * @return A type safe CriteriaQuery that can be queried against the 
     * persistence layer.
     */
    @Override
    public CriteriaQuery query(
            int first,
            int pageSize,
            String sortField,
            SortOrder sortOrder,
            CriteriaBuilder builder,
            Map filters,
            Map rootFilter,
            Map optionalFilter) {

        // Create the CriteriaQuery
        CriteriaQuery query = builder.createQuery(Incident.class);

        // Set the Root class against which the query is to be performed
        Root incident = query.from(Incident.class);

        // Create a new list of Predicates
        List<Predicate> predicates = new ArrayList<>();

        // If a Sort order is specified this is set
        if (sortField != null) {
            if (sortOrder == SortOrder.ASCENDING) {
                query.orderBy(builder.asc(incident.get(sortField)));
            } else {
                query.orderBy(builder.desc(incident.get(sortField)));
            }
        }

        // Implement the Root Filter
        if (!rootFilter.isEmpty()) {
            for (String key : (List<String>) rootFilter.keySet()) {
                List<Predicate> rootPredicate = new ArrayList<>();

                List<String> values = (List<String>) rootFilter.get(key);
                Expression<String> literal;
                if (!values.isEmpty()) {
                    for (String value : values) {
                        // Search term
                        literal = builder.literal((String) value);
                        // Predicate
                        switch (key) {
                            case "das":
                                rootPredicate.add(builder.equal(incident.get(Incident_.das), literal));
                                break;
                            default:
                                // Don't add any predicate by default
                                break;
                        }
                    }
                } else {
                    // This indicates that the user has access to no sites
                    // Search term
                    literal = builder.literal("DOES_NOT_EXIST");
                    // Predicate
                    switch (key) {
                        case "das":
                            rootPredicate.add(builder.equal(incident.get(Incident_.das), literal));
                            break;
                        default:
                            // Don't add any predicate by default
                            break;
                    }
                }
                predicates.add(builder.or(rootPredicate.toArray(new Predicate[rootPredicate.size()])));
            }
        }

        // Implement the Optional Specified Filter
        if (!optionalFilter.isEmpty()) {
            for (String key : (List<String>) optionalFilter.keySet()) {
                List<String> values = (List<String>) optionalFilter.get(key);

                List<Predicate> optionalPredicate = new ArrayList<>();
                for (String value : values) {
                    // Search term
                    Expression<String> literal = builder.literal((String) value);
                    // Predicate
                    switch (key) {
                        case "assignee":
                            if (value.equals("NULL")) {
                                optionalPredicate.add(incident.get(Incident_.assignee).isNull());
                            } else {
                                optionalPredicate.add(builder.equal(incident.get(Incident_.assignee), literal));
                            }
                            break;
                        case "status":
                            optionalPredicate.add(builder.equal(incident.get(Incident_.status), literal));
                            break;
                        default:
                            // Don't add any predicate by default
                            break;
                    }
                }
                predicates.add(builder.or(optionalPredicate.toArray(new Predicate[optionalPredicate.size()])));
            }
        }

        // Implement the Global Filter
        for (Iterator it = filters.keySet().iterator(); it.hasNext();) {
            String filterProperty = (String) it.next();
            String filterValue = (String) filters.get(filterProperty);

            // Search term
            Expression literal = builder.literal("%" + filterValue + "%");

            // When the globalFilter is deleted it returns ""
            if (!"".equals(filterValue)) {
                List<Predicate> globalPredicate = new ArrayList<>();

                globalPredicate.add(builder.like(incident.get(Incident_.cause), literal));
                globalPredicate.add(builder.like(incident.get(Incident_.status), literal));
                globalPredicate.add(builder.like(incident.get(Incident_.title), literal));
                globalPredicate.add(builder.like(incident.get(Incident_.severity), literal));

                predicates.add(builder.or(globalPredicate.toArray(new Predicate[globalPredicate.size()])));
            }
        }
        // Pass all the predicates into the query
        if (predicates.isEmpty()) {
            query.select(incident);
        } else {
            query.where(predicates.toArray(new Predicate[predicates.size()]));
        }

        return query;
    }
    
    /**
     * Determines the class type to associate with the query.
     * 
     * @return Returns the class type to associate with the query.
     */
    @Override
    public Class clazz() {
        return Incident.class;
    }
    
    /**
     * Determines the class type to associate with the query.
     * 
     * @param object
     * @return Returns the class type to associate with the query.
     */
    @Override
    public Long rowKey(Object object) {
        Incident incident = (Incident) object;
        return incident.getId();
    }
    
}
