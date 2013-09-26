/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.cnx;

import com.dastrax.per.entity.cnx.EventLog;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @version Build 2.0.0
 * @since Sep 18, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface EventLogDAO {

    public EventLog create(EventLog log);
    public List<EventLog> create(List<EventLog> logs);
    public List<EventLog> findAllEventLogs();
    public EventLog findEventLogById(String id);
    public int findLastEntryBySite(String site);
    public CriteriaBuilder criteriaBuilder();
    public List<EventLog> report(CriteriaQuery query);
}
