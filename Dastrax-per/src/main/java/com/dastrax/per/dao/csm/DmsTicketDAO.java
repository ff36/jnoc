/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.csm;

import com.dastrax.per.entity.csm.DmsAlarm;
import com.dastrax.per.entity.csm.DmsComment;
import com.dastrax.per.entity.csm.DmsTicket;
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
public interface DmsTicketDAO {

    /**
     * 
     * @param ticket
     * @return persisted DMSTicket
     */
    public DmsTicket create(DmsTicket ticket);
    /**
     * 
     * @param ticketId
     * @param dmsA
     * @return persisted DMSTicket
     */
    public DmsTicket updateAlarms(String ticketId, List<DmsAlarm> dmsA);
    /**
     * 
     * @param id
     * @param status
     * @return persisted DMSTicket
     */
    public DmsTicket updateStatus(String id, String status);
    /**
     * 
     * @param id
     * @param comment
     * @return persisted DMSTicket
     */
    public DmsTicket updateComment(String id, DmsComment comment);
    /**
     * 
     * @param dmsT
     * @return persisted DMSTicket
     */
    public DmsTicket update(DmsTicket dmsT);
    /**
     * 
     * @param id 
     */
    public void delete(String id);
    /**
     * 
     * @return all tickets
     */
    public List<DmsTicket> findAllDmsTickets();
    /**
     * 
     * @param id
     * @return ticket with specified id or null if one does not exist
     */
    public DmsTicket findDmsTicketById(String id);
    /**
     * 
     * @param cause
     * @return ticket with specified cause or null if one does not exist
     */
    public DmsTicket findDmsTicketByCause(String cause);
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
    public List<DmsTicket> lazyLoadTable(CriteriaQuery accountQuery, int first, int pageSize);
    /**
     * 
     * @param countQuery
     * @return returns the quantity of results from the dynamic Criteria Query
     */
    public int lazyLoadRowCount(CriteriaQuery countQuery);
    
}
