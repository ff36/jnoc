/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.ticket.dms;

import com.dastrax.per.dao.csm.DmsTicketDAO;
import com.dastrax.per.entity.csm.DmsTicket;
import com.dastrax.per.entity.csm.DmsTicket_;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * This class is dedicated to creating a PF LazyDataModel for DMS Tickets. Due
 * to the nature of lazy loading we want to dynamically create queries at
 * runtime based on specified filter conditions so that only the specified items
 * are searched and returned from the database. This increases the efficiency of
 * the search facility exponentially. The class accepts 3 filters that can be
 * optionally passed in. The root filter is the master filter applied by the
 * code base based on the specific subject to restrict the possible scope of the
 * search to only their authorized items. The optional filter is a database
 * filter that can be optionally passed in to create filter presents that the
 * subject can quickly access. The global filter is linked to the search field
 * in the table and will match the typed value against the object attributes of
 * the remaining values after the root and optional filters have been applied.
 *
 * @version Build 2.0.0
 * @since Aug 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class DmsTicketModel extends LazyDataModel<DmsTicket> {

    // Variables----------------------------------------------------------------
    private final DmsTicketDAO dmsTicketDAO;
    private final Map<String, List<String>> rootFilter;
    private final Map<String, List<String>> optionalFilter;

    // Constructors-------------------------------------------------------------
    public DmsTicketModel(
            DmsTicketDAO dmsTicketDAO,
            Map<String, List<String>> rootFilter,
            Map<String, List<String>> optionalFilter) {
        this.dmsTicketDAO = dmsTicketDAO;
        this.rootFilter = rootFilter;
        this.optionalFilter = optionalFilter;
    }

    // Methods------------------------------------------------------------------
    @Override
    public DmsTicket getRowData(String rowKey) {
        return dmsTicketDAO.findDmsTicketById(rowKey);
    }

    @Override
    public Object getRowKey(DmsTicket ticket) {
        return ticket.getId();
    }

    @Override
    public List<DmsTicket> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {

        // Criteria
        CriteriaBuilder builder = dmsTicketDAO.criteriaBuilder();
        CriteriaQuery query = builder.createQuery(DmsTicket.class);

        // From
        Root ticket = query.from(DmsTicket.class);

        // Predicates
        List<Predicate> predicates = new ArrayList<>();

        // Sort
        if (sortField != null) {
            if (sortOrder == SortOrder.ASCENDING) {
                query.orderBy(builder.asc(ticket.get(sortField)));
            } else {
                query.orderBy(builder.desc(ticket.get(sortField)));
            }
        }

        // Root Filter
        if (!rootFilter.isEmpty()) {
            for (String key : rootFilter.keySet()) {
                List<Predicate> rootPredicate = new ArrayList<>();

                List<String> values = (List<String>) rootFilter.get(key);
                Expression literal;
                if (!values.isEmpty()) {
                    for (String value : values) {
                        // Search term
                        literal = builder.literal((String) value);
                        // Predicate
                        switch (key) {
                            case "site":
                                rootPredicate.add(builder.equal(ticket.get(DmsTicket_.site), literal));
                                break;
                            default:
                                // Don't add any predicate by default
                                break;
                        }
                    }
                } else {
                    // This indicates that the user has access to no sites
                    // Search term
                    literal = builder.literal((String) "DOES_NOT_EXIST");
                    // Predicate
                    switch (key) {
                        case "site":
                            rootPredicate.add(builder.equal(ticket.get(DmsTicket_.site), literal));
                            break;
                        default:
                            // Don't add any predicate by default
                            break;
                    }
                }
                predicates.add(builder.or(rootPredicate.toArray(new Predicate[rootPredicate.size()])));
            }
        }

        // Optional Specified Filter
        if (!optionalFilter.isEmpty()) {
            for (String key : optionalFilter.keySet()) {
                List<String> values = (List<String>) optionalFilter.get(key);

                List<Predicate> optionalPredicate = new ArrayList<>();
                for (String value : values) {
                    // Search term
                    Expression literal = builder.literal((String) value);
                    // Predicate
                    switch (key) {
                        case "assignee":
                            if (value.equals("NULL")) {
                                optionalPredicate.add(ticket.get(DmsTicket_.assignee).isNull());
                            } else {
                                optionalPredicate.add(builder.equal(ticket.get(DmsTicket_.assignee), literal));
                            }
                            break;
                        case "status":
                            optionalPredicate.add(builder.equal(ticket.get(DmsTicket_.status), literal));
                            break;
                        default:
                            // Don't add any predicate by default
                            break;
                    }
                }
                predicates.add(builder.or(optionalPredicate.toArray(new Predicate[optionalPredicate.size()])));
            }
        }

        // Global Filter
        for (Iterator it = filters.keySet().iterator(); it.hasNext();) {
            String filterProperty = (String) it.next();
            String filterValue = (String) filters.get(filterProperty);

            // Search term
            Expression literal = builder.literal((String) "%" + filterValue + "%");

            // When the globalFilter is deleted it returns ""
            if (!"".equals(filterValue)) {
                List<Predicate> globalPredicate = new ArrayList<>();

                globalPredicate.add(builder.like(ticket.get(DmsTicket_.cause), literal));
                globalPredicate.add(builder.like(ticket.get(DmsTicket_.status), literal));
                globalPredicate.add(builder.like(ticket.get(DmsTicket_.title), literal));
                globalPredicate.add(builder.like(ticket.get(DmsTicket_.priority), literal));

                predicates.add(builder.or(globalPredicate.toArray(new Predicate[globalPredicate.size()])));
            }
        }
        // Pass all the predicates into the query
        if (predicates.isEmpty()) {
            query.select(ticket);
        } else {
            query.where(predicates.toArray(new Predicate[predicates.size()]));
        }

        // paginate
        List<DmsTicket> data = dmsTicketDAO.lazyLoadTable(query, first, pageSize);

        // row count
        int rowCount = dmsTicketDAO.lazyLoadRowCount(query);
        setRowCount(rowCount);

        return data;
    }

}
