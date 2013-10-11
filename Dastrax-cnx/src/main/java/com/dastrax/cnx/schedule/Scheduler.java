/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dastrax.cnx.schedule;

import com.dastrax.cnx.monitor.DevicePoll;
import com.dastrax.cnx.snmp.SnmpUtil;
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

    // Methods------------------------------------------------------------------
    @Schedule(minute = "*/2", hour = "*")
    public void everyTwoMins() {
        devicePoll.cacheDeviceStatus();
        snmpUtil.cacheDmsStatus();
    }
    
}
