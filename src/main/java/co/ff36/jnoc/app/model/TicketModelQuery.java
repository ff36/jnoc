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

package co.ff36.jnoc.app.model;

import co.ff36.jnoc.per.entity.Ticket;
import co.ff36.jnoc.per.project.JNOC.TicketStatus;

import co.ff36.jnoc.per.entity.Company_;
import co.ff36.jnoc.per.entity.Contact_;
import co.ff36.jnoc.per.entity.DAS_;
import co.ff36.jnoc.per.entity.Tag_;
import co.ff36.jnoc.per.entity.Ticket_;
import co.ff36.jnoc.per.entity.User_;

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

 */
public class TicketModelQuery implements ModelQuery {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(TicketModelQuery.class.getName());
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance of AuditModelService
     */
    public TicketModelQuery() {
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
        CriteriaQuery query = builder.createQuery(Ticket.class);

        // Set the Root class against which the query is to be performed
        Root ticket = query.from(Ticket.class);

        // Create a new list of Predicates
        List<Predicate> predicates = new ArrayList<>();

        // If a Sort order is specified this is set
        if (sortField != null) {
            // The sort needs to be customised for requesters and assignees
            if (sortField.contains("assignee")) {
                if (sortOrder == SortOrder.ASCENDING) {
                    query.orderBy(builder.asc(ticket.join(Ticket_.assignee).join(User_.contact).get(Contact_.firstName)));
                } else {
                    query.orderBy(builder.desc(ticket.join(Ticket_.assignee).join(User_.contact).get(Contact_.firstName)));
                }
            } else if (sortField.contains("requester")) {
                if (sortOrder == SortOrder.ASCENDING) {
                    query.orderBy(builder.asc(ticket.join(Ticket_.requester).join(User_.contact).get(Contact_.firstName)));
                } else {
                    query.orderBy(builder.desc(ticket.join(Ticket_.requester).join(User_.contact).get(Contact_.firstName)));
                }
            } else {
                if (sortOrder == SortOrder.ASCENDING) {
                    query.orderBy(builder.asc(ticket.get(sortField)));
                } else {
                    query.orderBy(builder.desc(ticket.get(sortField)));
                }
            }
        } else {
            query.orderBy(builder.desc(ticket.get(Ticket_.id)));
        }

        // Implement the Root Filter
        if (!rootFilter.isEmpty()) {
            for (Object key : rootFilter.keySet()) {
                String k = (String) key;
                List<Long> values = (List<Long>) rootFilter.get(key);

                List<Predicate> rootPredicate = new ArrayList<>();
                for (Long value : values) {
                    // Search term
                    Expression literal = builder.literal(value);
                    // Predicate
                    switch (k) {
                        case "company":
                            rootPredicate.add(builder.equal(ticket.join(Ticket_.requester).join(User_.company).get(Company_.id), literal));
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

                    // Predicate
                    switch (key) {
                        case "assignee":
                            // Search term
                            Expression<String> assigneeLiteral = builder.literal(value);
                            if ("null".equals(value.toLowerCase())) {
                                optionalPredicate.add(ticket.get(Ticket_.assignee).isNull());
                            } else {
                                optionalPredicate.add(builder.equal(ticket.join(Ticket_.assignee, JoinType.LEFT).get(User_.email), assigneeLiteral));
                            }
                            break;
                        case "requester":
                            // Search term
                            Expression<String> requesterLiteral = builder.literal(value);
                            optionalPredicate.add(builder.equal(ticket.join(Ticket_.requester, JoinType.LEFT).get(User_.email), requesterLiteral));
                            break;
                        case "status":
                            // Search term
                            String ticketStatus = value;
                            Expression statusLiteral = builder.literal(TicketStatus.valueOf(ticketStatus));
                            optionalPredicate.add(builder.equal(ticket.get(Ticket_.status), statusLiteral));
                            break;
                        case "company":
                            // Search term
                            Expression companyLiteral = builder.literal(value);
                            optionalPredicate.add(builder.equal(ticket.join(Ticket_.requester, JoinType.LEFT).join(User_.company, JoinType.LEFT).get(Company_.id), companyLiteral));
                            break;
                        case "das":
                            // Search term
                            Expression dasLiteral = builder.literal(Long.parseLong( value));
                            optionalPredicate.add(builder.equal(ticket.join(Ticket_.das, JoinType.LEFT).get(DAS_.id), dasLiteral));
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

                globalPredicate.add(builder.like(ticket.get(Ticket_.id), literal));
                globalPredicate.add(builder.like(ticket.get(Ticket_.status), literal));
                globalPredicate.add(builder.like(ticket.get(Ticket_.title), literal));
                globalPredicate.add(builder.like(ticket.get(Ticket_.topic), literal));
                globalPredicate.add(builder.like(ticket.get(Ticket_.email), literal));
                globalPredicate.add(builder.like(ticket.get(Ticket_.severity), literal));
                globalPredicate.add(builder.like(ticket.join(Ticket_.requester, JoinType.LEFT).join(User_.contact, JoinType.LEFT).get(Contact_.firstName), literal));
                globalPredicate.add(builder.like(ticket.join(Ticket_.requester, JoinType.LEFT).join(User_.contact, JoinType.LEFT).get(Contact_.lastName), literal));
                if (nullAssignee(optionalFilter)) {
                    globalPredicate.add(builder.like(ticket.join(Ticket_.assignee, JoinType.LEFT).join(User_.contact, JoinType.LEFT).get(Contact_.firstName), literal));
                    globalPredicate.add(builder.like(ticket.join(Ticket_.assignee, JoinType.LEFT).join(User_.contact, JoinType.LEFT).get(Contact_.lastName), literal));
                }
                globalPredicate.add(builder.like(ticket.join(Ticket_.tags, JoinType.LEFT).get(Tag_.name), literal));

                predicates.add(builder.or(globalPredicate.toArray(new Predicate[globalPredicate.size()])));
            }
        }
        // Pass all the predicates into the query
        if (predicates.isEmpty()) {
            query.select(ticket);
        } else {
            query.where(predicates.toArray(new Predicate[predicates.size()]));
        }

        return query;
    }

    /**
     * The global filter fails if the assignee value is 'NULL' so we need to
     * skip adding the assignee predicator check if the assignee is 'NULL'. This
     * method simply determines whether the optionalFilter has a 'NULL'
     * assignee.
     */
    private boolean nullAssignee(Map optionalFilter) {
        if (!optionalFilter.isEmpty()) {
            for (String key : (List<String>) optionalFilter.keySet()) {
                List<String> values = (List<String>) optionalFilter.get(key);

                for (String value : values) {
                    // Predicate
                    switch (key) {
                        case "assignee":
                            return !value.equals("NULL");

                        default:
                            break;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines the class type to associate with the query.
     *
     * @return Returns the class type to associate with the query.
     */
    @Override
    public Class clazz() {
        return Ticket.class;
    }

    /**
     * Determines the class type to associate with the query.
     *
     * @param object
     * @return Returns the class type to associate with the query.
     */
    @Override
    public Long rowKey(Object object) {
        Ticket ticket = (Ticket) object;
        return ticket.getId();
    }
    
}
