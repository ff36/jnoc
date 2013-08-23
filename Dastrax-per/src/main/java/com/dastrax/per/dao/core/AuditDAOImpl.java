/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Audit;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Aug 8, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class AuditDAOImpl implements AuditDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(AuditDAOImpl.class.getName());

    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_NoSQL_PU")
    private EntityManager em;

    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void create(String event) {
        Audit audit = new Audit();
        audit.setCreated(Calendar.getInstance().getTimeInMillis());
        audit.setEvent(event);
        if (SecurityUtils.getSubject().getPrincipal() != null) {
            audit.setAuthor(SecurityUtils.getSubject().getPrincipals().asList().get(1).toString());
        }
        em.persist(audit);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Audit findAuditById(String id) {
        List<Audit> list = em.createNamedQuery("Audit.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Audit> findAllAudits() {
        return em.createNamedQuery("Audit.findAll")
                .getResultList();
    }

    @Override
    public List<Audit> findRecent(int qty) {
        return em.createNamedQuery("Audit.findReverse")
                .setMaxResults(qty)
                .getResultList();
    }

}
