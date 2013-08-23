/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.site;

import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.entity.core.Company_;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Site_;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * This class is dedicated to creating a PF LazyDataModel for SiteModel. Due
 * to the nature of lazy loading we want to dynamically create queries at runtime
 * based on specified filter conditions so that only the specified items are
 * searched and returned from the database. This increases the efficiency of the
 * search facility exponentially. The class accepts 3 filters that can be
 * optionally passed in. The root filter is the master filter applied by the code 
 * base based on the specific subject to restrict the possible scope of the search
 * to only their authorized items. The optional filter is a database filter that 
 * can be optionally passed in to create filter presents that the subject can
 * quickly access. The global filter is linked to the search field in the table
 * and will match the typed value against the object attributes of the remaining
 * values after the root and optional filters have been applied.
 * @version Build 2.0.0
 * @since Jul 22, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class SiteModel extends LazyDataModel<Site> {

    // Variables----------------------------------------------------------------
    private final SiteDAO siteDAO;
    private final Map<String, List<String>> rootFilter;
    private final Map<String, List<String>> optionalFilter;

    // Constructors-------------------------------------------------------------
    public SiteModel(
            SiteDAO siteDAO, 
            Map<String, List<String>> rootFilter,
            Map<String, List<String>> optionalFilter) {
        this.siteDAO = siteDAO;
        this.rootFilter = rootFilter;
        this.optionalFilter = optionalFilter;
    }

    // Methods------------------------------------------------------------------
    @Override
    public Site getRowData(String rowKey) {
        return siteDAO.findSiteById(rowKey);
    }

    @Override
    public Object getRowKey(Site site) {
        return site.getId();
    }

    @Override
    public List<Site> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {

        // Criteria
        CriteriaBuilder builder = siteDAO.criteriaBuilder();
        CriteriaQuery query = builder.createQuery(Site.class);

        // From
        Root site = query.from(Site.class);

        // Predicates
        List<Predicate> predicates = new ArrayList<>();
        
        // Sort
        if (sortField != null) {
            if (sortOrder == SortOrder.ASCENDING) {
                query.orderBy(builder.asc(site.get(sortField)));
            } else {
                query.orderBy(builder.desc(site.get(sortField)));
            }
        }

        // Root Filter
        if (!rootFilter.isEmpty()) {
            for (String key : rootFilter.keySet()) {
                List<String> values = (List<String>) rootFilter.get(key);

                List<Predicate> rootPredicate = new ArrayList<>();
                for (String value : values) {
                    // Search term
                    Expression literal = builder.literal((String) value);
                    // Predicate
                    switch (key) {
                        case "company":                         
                            rootPredicate.add(builder.equal(site.join(Site_.var, JoinType.LEFT).get(Company_.id), literal));
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
                        case "company":
                            optionalPredicate.add(builder.equal(site.join(Site_.var, JoinType.LEFT).get(Company_.id), literal));
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
                // JPQL equivilant: SELECT e FROM Subject e JOIN e.company c JOIN e.contact ct WHERE c.name LIKE :literal OR ct.firstName LIKE :literal ... etc
                List<Predicate> globalPredicate = new ArrayList<>();

                globalPredicate.add(builder.like(site.get(Site_.name), literal));
                globalPredicate.add(builder.like(site.join(Site_.var, JoinType.LEFT).get(Company_.name), literal));
                globalPredicate.add(builder.like(site.join(Site_.client, JoinType.LEFT).get(Company_.name), literal));
                globalPredicate.add(builder.like(site.get(Site_.packageType), literal));
                globalPredicate.add(builder.like(site.get(Site_.dmsIP), literal));

                predicates.add(builder.or(globalPredicate.toArray(new Predicate[globalPredicate.size()])));
            }
        }
        // Pass all the predicates into the query
        if (predicates.isEmpty()) {
            query.select(site);
        } else {
            query.where(predicates.toArray(new Predicate[predicates.size()]));
        }
        
        // paginate
        List<Site> data = siteDAO.lazyLoadTable(query, first, pageSize);

        // row count
        int rowCount = siteDAO.lazyLoadRowCount(query);
        setRowCount(rowCount);

        return data;
    }

}
