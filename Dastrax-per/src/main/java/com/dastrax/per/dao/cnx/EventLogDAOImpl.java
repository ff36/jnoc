/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.cnx;

import com.dastrax.per.dao.core.AuditDAO;
import com.dastrax.per.entity.cnx.EventLog;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @version Build 2.0.0
 * @since Sep 18, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class EventLogDAOImpl implements EventLogDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(EventLogDAOImpl.class.getName());

    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_NoSQL_PU")
    private EntityManager em;

    // EJB----------------------------------------------------------------------
    @EJB
    AuditDAO auditDAO;

    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public EventLog create(EventLog log) {
        em.persist(log);
        return log;
    }
    
    @Override
    public List<EventLog> create(List<EventLog> logs) {
        for (EventLog log : logs) {
           em.persist(log); 
        }
        return logs;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public List<EventLog> findAllEventLogs() {
        return em.createNamedQuery("EventLog.findAll")
                .getResultList();
    }

    @Override
    public EventLog findEventLogById(String id) {
        List<EventLog> list = em.createNamedQuery("EventLog.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
    @Override
    public int findLastEntryBySite(String site) {
        List<EventLog> list = em.createNamedQuery("EventLog.findLastEntryBySite")
                .setParameter("site", site)
                .setMaxResults(1)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0).getExternalId();
        } else {
            return 0;
        }
    }
    
    @Override
    public CriteriaBuilder criteriaBuilder() {
        return em.getCriteriaBuilder();
    }
    
    @Override
    public List<EventLog> report(CriteriaQuery query) {
        return em.createQuery(query)
                .getResultList();
    }

}
