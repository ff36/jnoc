/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao.csm;

import com.dastrax.per.dao.core.AuditDAO;
import com.dastrax.per.dao.core.TagDAO;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.core.Tag;
import com.dastrax.per.entity.csm.Comment;
import com.dastrax.per.entity.csm.Ticket;
import java.util.ArrayList;
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
public class TicketDAOImpl implements TicketDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(TicketDAOImpl.class.getName());
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;

    // EJB----------------------------------------------------------------------
    @EJB
    AuditDAO auditDAO;
    @EJB
    TagDAO tagDAO;

    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Ticket create(Ticket ticket) {
        // If the tags exist we need to make them managed
        List<Tag> tags = null;
        if (!ticket.getTags().isEmpty()) {
            tags = ticket.getTags();
            ticket.setTags(null);
        }
        // Persist the ticket
        ticket.setOpenEpoch(Calendar.getInstance().getTimeInMillis());
        ticket.setSatisfied("0");
        em.persist(ticket);

        // Merge the tags
        if (tags != null) {
            ticket.setTags(tags);
            em.merge(ticket);
        }
        // Create Audit
        auditDAO.create("Created ticket DTX-" + ticket.getId() + ".");
        return ticket;
    }

    ////////////////////////////////////////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Ticket update(Ticket ticket) {
        if (ticket != null) {
            // If the tags exist we need to make them managed
            List<Tag> tags = null;
            if (!ticket.getTags().isEmpty()) {
                tags = ticket.getTags();
                ticket.setTags(null);
            }
            // Merge the ticket
            em.merge(ticket);
            // Merge the tags
            if (tags != null) {
                ticket.setTags(tags);
                em.merge(ticket);
            }
            // Create Audit
            auditDAO.create("Updated ticket DTX-" + ticket.getId() + ".");
        }
        return ticket;
    }

    @Override
    public Ticket updateStatus(String id, String status) {
        Ticket t = findTicketById(id);
        if (t != null) {
            t.setStatus(status);
            em.merge(t);
            // Create Audit
            auditDAO.create("Updated ticket DTX-" + t.getId() + " status to " + status + ".");
        }
        return t;
    }

    @Override
    public Ticket updateComment(String id, Comment comment) {
        Ticket t = findTicketById(id);
        if (t != null) {
            t.getComments().add(comment);
            em.merge(t);
            // Create Audit
            auditDAO.create("Added a comment to ticket DTX-" + t.getId() + ".");
        }
        return t;
    }

    @Override
    public Ticket updateSatisfaction(String id, String satisfied, String feedback) {
        Ticket t = findTicketById(id);
        if (t != null) {
            t.setSatisfied(satisfied);
            t.setFeedback(feedback);
            em.merge(t);
            // Create Audit
            auditDAO.create("Satisfaction for ticket DTX-" + t.getId() + " was modified.");
        }
        return t;
    }

    @Override
    public Ticket updateCloser(String id, Subject closer) {
        Ticket t = findTicketById(id);
        if (t != null) {
            t.setCloser(closer);
            em.merge(t);
            // Create Audit
            auditDAO.create("Ticket DTX-" + t.getId() + " was closed by " + closer.getContact().buildFullName());
        }
        return t;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void delete(String id) {
        Ticket t = findTicketById(id);
        if (t != null) {
            em.remove(t);
            // Create Audit
            auditDAO.create("Deleted ticket DTX-" + id + ".");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public List<Ticket> findAllTickets() {
        return em.createNamedQuery("Ticket.findAll")
                .getResultList();
    }

    @Override
    public Ticket findTicketById(String id) {
        List<Ticket> list = em.createNamedQuery("Ticket.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Ticket> findAllTicketsByStatus(String status) {
        return em.createNamedQuery("Ticket.findAllByStatus")
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Ticket> findAllTicketsExcluding(String status) {
        return em.createNamedQuery("Ticket.findAllExceptStatus")
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Ticket> findAllTicketsExcluding(String statusOne, String statusTwo) {
        return em.createNamedQuery("Ticket.findAllExceptMultiStatus")
                .setParameter("status1", statusOne)
                .setParameter("status2", statusTwo)
                .getResultList();
    }

    @Override
    public CriteriaBuilder criteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    @Override
    public List<Ticket> lazyLoadTable(CriteriaQuery ticketQuery, int first, int pageSize) {
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
