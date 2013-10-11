/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.cnx.report;

import com.dastrax.per.dao.cnx.EventLogDAO;
import com.dastrax.per.entity.cnx.EventLog;
import com.dastrax.per.entity.cnx.EventLog_;
import com.dastrax.per.entity.core.Site;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import static com.googlecode.cqengine.query.QueryFactory.ascending;
import static com.googlecode.cqengine.query.QueryFactory.between;
import static com.googlecode.cqengine.query.QueryFactory.orderBy;
import static com.googlecode.cqengine.query.QueryFactory.queryOptions;
import com.googlecode.cqengine.resultset.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @version Build 2.0.0
 * @since Sep 24, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class Generator {

    // EJB----------------------------------------------------------------------
    @EJB
    EventLogDAO eventLogDAO;

    // Methods------------------------------------------------------------------
    public List<EventLog> generateReport(Site site, long from, long to, String frequency) {

        // Criteria
        CriteriaBuilder builder = eventLogDAO.criteriaBuilder();
        CriteriaQuery<EventLog> query = builder.createQuery(EventLog.class);

        // Root
        Root<EventLog> eventLog = query.from(EventLog.class);

        // Predicates
        List<Predicate> predicates = new ArrayList<>();

        // Build the query
        predicates.add(builder.equal(eventLog.get(EventLog_.site), site.getId()));
        if (frequency != null) {
            predicates.add(builder.equal(eventLog.get(EventLog_.frequency), frequency));
        }
        predicates.add(builder.greaterThan(eventLog.get(EventLog_.receivedTime), from));
        predicates.add(builder.lessThan(eventLog.get(EventLog_.receivedTime), to));

        // Pass all the predicates into the query
        if (predicates.isEmpty()) {
            query.select(eventLog);
        } else {
            query.where(predicates.toArray(new Predicate[predicates.size()]));
        }

        // Execute the query
        return eventLogDAO.report(query);
    }

    /**
     * Converts the generated reports into data points that can be plotted on a
     * graph. The granularity specifies the time span of the sampling group.
     *
     * @param eventLogs
     * @param from
     * @param to
     * @return
     */
    public Double normalizeReport(long from, long to, List<EventLog> eventLogs) {
        // Set the sampling group size to the time span
        long sample = to - from;

        // Create an indexed map to manipulate the data       
        IndexedCollection<EventLog> logs = CQEngine.newInstance();
        logs.addIndex(NavigableIndex.onAttribute(LOG_RECEIVED_TIME));
        for (EventLog eventLog : eventLogs) {
            // Only want to add events that exceed the threshhold (5 mins)
            long totalDowntime = eventLog.getDownTime() + eventLog.getHysteresis();
            if (totalDowntime > 300000) {
                logs.add(eventLog);
            }
        }
        // Create the CQEngine Query
        Query<EventLog> query = between(LOG_RECEIVED_TIME, from, to);

        // Calculate the downtime based on overlaped events in the window
        long cumulative = 0;
        EventLog previous = null;

        ResultSet results = logs.retrieve(query, queryOptions(orderBy(ascending(LOG_RECEIVED_TIME))));
        Iterator itr = results.iterator();
        while (itr.hasNext()) {
            EventLog result = (EventLog) itr.next();
            if (previous != null) {
                // If the start and end of the event wrap the previous event end
                if (result.getReceivedTime() <= previous.getClearedTime() && result.getClearedTime() > previous.getClearedTime()) {
                    long additionalDuration = result.getClearedTime() - previous.getClearedTime();
                    cumulative += additionalDuration;
                }
                // If the start is greater than the previous event end
                if (result.getReceivedTime() > previous.getClearedTime()) {
                    cumulative += result.getDownTime();
                }
            } else {
                // Sets the initial down time when previous is null
                cumulative = result.getDownTime();
            }
            // Set the result so we can access it in the next iteration
            previous = result;
        }
        
        return ((double)(sample - cumulative) / (double)sample) * 100;
    }

    /**
     * Grants CQEngine access to the EventLog attributes
     */
    public static final Attribute<EventLog, Long> LOG_RECEIVED_TIME = new SimpleAttribute<EventLog, Long>("receiveTime") {
        @Override
        public Long getValue(EventLog log) {
            return log.getReceivedTime();
        }
    };

}
