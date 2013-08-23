/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao.csm;

import com.dastrax.per.dao.core.AuditDAO;
import com.dastrax.per.entity.csm.DmsAlarm;
import com.dastrax.per.entity.csm.DmsComment;
import com.dastrax.per.entity.csm.DmsTicket;
import com.dastrax.per.project.DastraxCst;
import java.util.Calendar;
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
 * @since Aug 2, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class DmsTicketDAOImpl implements DmsTicketDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(DmsTicketDAOImpl.class.getName());

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
    public DmsTicket create(DmsTicket ticket) {
        em.persist(ticket);
        return ticket;
    }

    ////////////////////////////////////////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public DmsTicket updateAlarms(String ticketId, List<DmsAlarm> dmsA) {
        DmsTicket dmsT = findDmsTicketById(ticketId);
        if (dmsT != null) {
            dmsT.setAlarms(dmsA);

            if (dmsT.getAlarms().size() > 0 && dmsT.getAlarms().size() <= 4) {
                dmsT.setPriority("Sev 1");
            } else if (dmsT.getAlarms().size() >= 5 && dmsT.getAlarms().size() <= 9) {
                dmsT.setPriority("Sev 2");
            } else if (dmsT.getAlarms().size() > 10) {
                dmsT.setPriority("Sev 3");
            }
        }
        em.merge(dmsT);
        return dmsT;
    }

    @Override
    public DmsTicket updateStatus(String id, String status) {
        DmsTicket t = findDmsTicketById(id);
        if (t != null) {
            if (t.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())
                    && status.equals(DastraxCst.TicketStatus.OPEN.toString())) {
                t.setCloseEpoch(null);
            }
            t.setStatus(status);
            if (status.equals(DastraxCst.TicketStatus.SOLVED.toString())) {
                Calendar cal = Calendar.getInstance();
                t.setCloseEpoch(cal.getTimeInMillis());
            }
            em.merge(t);
            // Create Audit
            auditDAO.create("Updated ticket DMST-" + t.getCause() + " status to " + status + ".");
        }
        return t;
    }

    @Override
    public DmsTicket updateComment(String id, DmsComment comment) {
        DmsTicket t = findDmsTicketById(id);
        if (t != null) {
            t.getComments().add(comment);
            em.merge(t);
            // Create Audit
            auditDAO.create("Added a comment to ticket DMST-" + t.getCause() + ".");
        }
        return t;
    }

    @Override
    public DmsTicket update(DmsTicket dmsT) {
        em.merge(dmsT);
        // Create Audit
        auditDAO.create("Changed DMST-" + dmsT.getCause() + ".");
        return dmsT;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void delete(String id) {
        DmsTicket t = findDmsTicketById(id);
        if (t != null) {
            em.remove(t);
            // Create Audit
            auditDAO.create("Deleted ticket DMST-" + id + ".");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public List<DmsTicket> findAllDmsTickets() {
        return em.createNamedQuery("DmsTicket.findAll")
                .getResultList();
    }

    @Override
    public DmsTicket findDmsTicketById(String id) {
        List<DmsTicket> list = em.createNamedQuery("DmsTicket.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public DmsTicket findDmsTicketByCause(String cause) {
        List<DmsTicket> list = em.createNamedQuery("DmsTicket.findByCause")
                .setParameter("cause", cause)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public CriteriaBuilder criteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    @Override
    public List<DmsTicket> lazyLoadTable(CriteriaQuery ticketQuery, int first, int pageSize) {
        return em.createQuery(ticketQuery)
                .setFirstResult(first)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public int lazyLoadRowCount(CriteriaQuery countQuery) {
        return em.createQuery(countQuery).getResultList().size();
    }
}
