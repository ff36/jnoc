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

package com.jnoc.app.model;

import com.jnoc.per.entity.Company;
import com.jnoc.per.entity.Company_;
import com.jnoc.per.entity.DAS;
import com.jnoc.per.entity.DAS_;
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

 */
public class DasModelQuery implements ModelQuery {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(DasModelQuery.class.getName());
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance of DasModelQuery
     */
    public DasModelQuery() {
    }
//</editor-fold>

    /**
     * A type safe CriteriaQuery dynamically constructed of an optional root
     * filter (applied automatically based on the users Metier), an optional
     * filter that can be set via a URL query parameter, and a global filter
     * that is implemented as the user generates a 'keyup' event in the search
     * field.
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
        CriteriaQuery query = builder.createQuery(DAS.class);

        // Set the Root class against which the query is to be performed
        Root das = query.from(DAS.class);
        Root company = query.from(Company.class);

        // Create a new list of Predicates
        List<Predicate> predicates = new ArrayList<>();

        // If a Sort order is specified this is set
        if (sortField != null) {
            if (sortOrder == SortOrder.ASCENDING) {
                query.orderBy(builder.asc(das.get(sortField)));
            } else {
                query.orderBy(builder.desc(das.get(sortField)));
            }
        }

        // Implement the Root Filter
        if (!rootFilter.isEmpty()) {
            for (Object key : rootFilter.keySet()) {
                String k = (String) key;
                List<Long> values = (List<Long>) rootFilter.get((String) key);

                List<Predicate> rootPredicate = new ArrayList<>();
                for (Long value : values) {
                    // Search term
                    Expression literal = builder.literal(value);
                    // Predicate
                    switch (k) {
                        case "company":
                            rootPredicate.add(builder.equal(company.get(Company_.id), literal));
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
                    Expression literal = builder.literal((String) value);
                    // Predicate
                    switch (key) {
                        case "company":
                            optionalPredicate.add(builder.equal(company.get(Company_.id), literal));
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
                // JPQL equivilant: SELECT e FROM Subject e JOIN e.company c JOIN e.contact ct WHERE c.name LIKE :literal OR ct.firstName LIKE :literal ... etc
                List<Predicate> globalPredicate = new ArrayList<>();

                globalPredicate.add(builder.like(das.get(DAS_.name), literal));
                globalPredicate.add(builder.like(company.get(Company_.name), literal));
                globalPredicate.add(builder.like(company.get(Company_.name), literal));
                globalPredicate.add(builder.like(das.get(DAS_.packageType), literal));
                globalPredicate.add(builder.like(das.get(DAS_.dmsIp), literal));

                predicates.add(builder.or(globalPredicate.toArray(new Predicate[globalPredicate.size()])));
            }
        }
        // Pass all the predicates into the query
        if (predicates.isEmpty()) {
            query.select(das);
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
        return DAS.class;
    }

    /**
     * Determines the class type to associate with the query.
     *
     * @param object
     * @return Returns the class type to associate with the query.
     */
    @Override
    public Long rowKey(Object object) {
        DAS das = (DAS) object;
        return das.getId();
    }

}
