/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dastrax.service.temp;

import com.dastrax.cnx.report.EventLogs;
import com.dastrax.csm.util.DmsTicketUtil;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author tarka
 */
@Named
@RequestScoped
public class PageTrigger {
    
    // EJB----------------------------------------------------------------------
    @EJB
    DmsTicketUtil dmsTicketUtil;
    @EJB
    EventLogs eventLogs;
    
    // Methods------------------------------------------------------------------
    @Asynchronous
    public void dmsTicketTrigger() {
        dmsTicketUtil.queryQueue();
    }
    
    @Asynchronous
    public void eventLogTrigger() {
        eventLogs.processLogs();
    }
    
}
