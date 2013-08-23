/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Audit;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @version Build 2.0.0
 * @since Aug 8, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface AuditDAO {

    /**
     * 
     * @param event 
     */
    public void create(String event);
    /**
     * 
     * @param id
     * @return Audit with the specified ID or null if none exists
     */
    public Audit findAuditById(String id);
    /**
     * 
     * @return all Audits
     */
    public List<Audit> findAllAudits();
    /**
     * 
     * @param qty
     * @return the specified quantity of the most recent audits
     */
    public List<Audit> findRecent(int qty);
    
}
