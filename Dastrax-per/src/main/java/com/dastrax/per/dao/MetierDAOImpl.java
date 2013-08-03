/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao;

import com.dastrax.per.entity.core.Metier;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @version Build 2.0.0
 * @since Jul 12, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class MetierDAOImpl implements MetierDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(MetierDAOImpl.class.getName());
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;

    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void create(Metier metier) {
        em.persist(metier);
    }

    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Metier findMetierById(String id) {
        List<Metier> list = em.createNamedQuery("Metier.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Metier findMetierByName(String name) {
        List<Metier> list = em.createNamedQuery("Metier.findByName")
                .setParameter("name", name)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Metier> findAllMetiers() {
        return em.createNamedQuery("Metier.findAll")
                .getResultList();
    }

}
