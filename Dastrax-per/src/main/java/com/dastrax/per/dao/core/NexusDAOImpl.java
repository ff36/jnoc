/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Nexus;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @version Build 2.0.0
 * @since Aug 6, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class NexusDAOImpl implements NexusDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(NexusDAOImpl.class.getName());
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;

    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public List<Nexus> findAllNexus() {
        return em.createNamedQuery("Nexus.findAll")
                .getResultList();
    }

    @Override
    public Nexus findNexusById(String id) {
        List<Nexus> list = em.createNamedQuery("Nexus.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
}
