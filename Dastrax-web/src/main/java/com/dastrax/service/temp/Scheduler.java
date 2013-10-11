/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dastrax.service.temp;

import com.dastrax.cnx.monitor.DevicePoll;
import com.dastrax.cnx.report.EventLogs;
import com.dastrax.cnx.snmp.SnmpUtil;
import com.dastrax.csm.util.DmsTicketUtil;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;

/**
 *
 * @author tarka
 */
@Stateless
@LocalBean
@Startup
public class Scheduler {

    // EJB----------------------------------------------------------------------
    @EJB
    DevicePoll devicePoll;
    @EJB
    SnmpUtil snmpUtil;
    @EJB
    DmsTicketUtil dmsTicketUtil;
    @EJB
    EventLogs eventLogs;

    // Methods------------------------------------------------------------------
    @Schedule(minute = "*/2", hour = "*")
    public void everyTwoMins() {
        devicePoll.cacheDeviceStatus();
        snmpUtil.cacheDmsStatus();
        dmsTicketUtil.queryQueue();
    }
    
    @Schedule(minute = "*", hour = "*/2")
    public void everySixHours() {
        eventLogs.processLogs();
    }
    
}
