/*
 * Created May 19, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.model;

import com.dastrax.per.entity.Audit;
import com.dastrax.per.entity.Audit_;
import com.dastrax.per.entity.Contact_;
import com.dastrax.per.entity.User_;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
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
public class AuditModelQuery implements ModelQuery {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(AuditModelQuery.class.getName());
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance of AuditModelService
     */
    public AuditModelQuery() {
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
        CriteriaQuery query = builder.createQuery(Audit.class);

        // Set the Root class against which the query is to be performed
        Root audit = query.from(Audit.class);

        // Create a new list of Predicates
        List<Predicate> predicates = new ArrayList<>();

        // If a Sort order is specified this is set
        if (sortField != null) {
            if (sortOrder == SortOrder.ASCENDING) {
                query.orderBy(builder.asc(audit.get(sortField)));
            } else {
                query.orderBy(builder.desc(audit.get(sortField)));
            }
        }

        // Implement the Root Filter
        if (!rootFilter.isEmpty()) {
            for (String key : (Set<String>) rootFilter.keySet()) {
                List<Long> values = (List<Long>) rootFilter.get(key);

                List<Predicate> rootPredicate = new ArrayList<>();
                for (Long value : values) {
                    // Search term
                    Expression literal = builder.literal(value);
                    // Predicate
                    switch (key) {
                        case "author":
                            rootPredicate.add(builder.equal(audit.join(Audit_.author, JoinType.LEFT).get(User_.contact).get(Contact_.id), literal));
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
            for (String key : (Set<String>) optionalFilter.keySet()) {
                List<String> values = (List<String>) optionalFilter.get(key);

                List<Predicate> optionalPredicate = new ArrayList<>();
                for (String value : values) {
                    // Search term
                    Expression literal = builder.literal((String) value);
                    // Predicate
                    switch (key) {
                        case "author":
                            optionalPredicate.add(builder.like(audit.join(Audit_.author, JoinType.LEFT).get(User_.contact).get(Contact_.firstName), literal));
                            optionalPredicate.add(builder.like(audit.join(Audit_.author, JoinType.LEFT).get(User_.contact).get(Contact_.lastName), literal));
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
            Expression literal = builder.literal((String) "%" + filterValue + "%");

            // When the globalFilter is deleted it returns ""
            if (!"".equals(filterValue)) {
                // JPQL equivilant: SELECT e FROM User e JOIN e.company c JOIN e.contact ct WHERE c.name LIKE :literal OR ct.firstName LIKE :literal ... etc
                List<Predicate> globalPredicate = new ArrayList<>();

                globalPredicate.add(builder.like(audit.get(Audit_.createEpoch), literal));
                globalPredicate.add(builder.like(audit.get(Audit_.description), literal));
                globalPredicate.add(builder.like(audit.join(Audit_.author, JoinType.LEFT).get(User_.contact).get(Contact_.firstName), literal));
                globalPredicate.add(builder.like(audit.join(Audit_.author, JoinType.LEFT).get(User_.contact).get(Contact_.lastName), literal));

                predicates.add(builder.or(globalPredicate.toArray(new Predicate[globalPredicate.size()])));
            }
        }
        // Pass all the predicates into the query
        if (predicates.isEmpty()) {
            query.select(audit);
        } else {
            query.where(predicates.toArray(new Predicate[predicates.size()]));
        }

        return query;
    }

}