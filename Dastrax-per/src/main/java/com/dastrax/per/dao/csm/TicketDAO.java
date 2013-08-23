/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.csm;

import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.csm.Comment;
import com.dastrax.per.entity.csm.Ticket;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @version Build 2.0.0
 * @since Aug 2, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface TicketDAO {

    /**
     * 
     * @param ticket
     * @return persisted ticket
     */
    public Ticket create(Ticket ticket);
    /**
     * 
     * @param ticket
     * @return persisted ticket
     */
    public Ticket update(Ticket ticket);
    /**
     * 
     * @param id
     * @param status
     * @return persisted ticket
     */
    public Ticket updateStatus(String id, String status);
    /**
     * 
     * @param id
     * @param comment
     * @return persisted ticket
     */
    public Ticket updateComment(String id, Comment comment);
    /**
     * 
     * @param id
     * @param satisfied
     * @param feedback
     * @return persisted ticket
     */
    public Ticket updateSatisfaction(String id, String satisfied, String feedback);
    /**
     * 
     * @param id
     * @param closer
     * @return persisted ticket
     */
    public Ticket updateCloser(String id, Subject closer);
    /**
     * 
     * @param id 
     */
    public void delete(String id);
    /**
     * 
     * @return all tickets
     */
    public List<Ticket> findAllTickets();
    /**
     * 
     * @param id
     * @return specified ticket or null if none exists
     */
    public Ticket findTicketById(String id);
    /**
     * 
     * @param status
     * @return all tickets with the specified status
     */
    public List<Ticket> findAllTicketsByStatus(String status);
    /**
     * 
     * @param status
     * @return All tickets EXCLUDING specified status
     */
    public List<Ticket> findAllTicketsExcluding(String status);
    /**
     * 
     * @param statusOne
     * @param statusTwo
     * @return All tickets EXCLUDING specified statuses
     */
    public List<Ticket> findAllTicketsExcluding(String statusOne, String statusTwo);
    /**
     * 
     * @return CriteriaBuilder
     */
    public CriteriaBuilder criteriaBuilder();
    /**
     * 
     * @param accountQuery
     * @param first
     * @param pageSize
     * @return the tickets that match the dynamic Criteria Query, the first record
     * address and the quantity specified by the page size.
     */
    public List<Ticket> lazyLoadTable(CriteriaQuery accountQuery, int first, int pageSize);
    /**
     * 
     * @param countQuery
     * @return returns the quantity of results from the dynamic Criteria Query
     */
    public int lazyLoadRowCount(CriteriaQuery countQuery);
}
