/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Filter;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @version Build 2.0.0
 * @since Aug 5, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class FilterDAOImpl implements FilterDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(FilterDAOImpl.class.getName());
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;

    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Filter findFilterById(String id) {
        List<Filter> list = em.createNamedQuery("Filter.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Filter> findAllFilters() {
        return em.createNamedQuery("Filter.findAll")
                .getResultList();
    }


}
