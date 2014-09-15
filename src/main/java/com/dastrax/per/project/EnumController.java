/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.project;

import com.dastrax.per.project.DTX.CompanyType;
import com.dastrax.per.project.DTX.DMSType;
import com.dastrax.per.project.DTX.TelephoneType;
import com.dastrax.per.project.DTX.TicketSeverity;
import com.dastrax.per.project.DTX.TicketStatus;
import com.dastrax.per.project.DTX.TicketTopic;
import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * JSF offers a default implementation converter for ENUM types. This class
 * is a simple CDI named class that can be accessed from the presentation layer
 * to display ENUM values as a list. The ENUM constants originate from the DTX
 * class.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class EnumController implements Serializable {
    
    /**
     * Get all the values of the ENUM TelephoneType
     * 
     * @return An array of values from the TelephoneType ENUM
     */
    public TelephoneType[] getTelephoneTypes() {
        return  TelephoneType.values();
    }
    
    /**
     * Get all the values of the ENUM DMSType
     * 
     * @return An array of values from the DMSType ENUM
     */
    public DMSType[] getDMSTypes() {
        return DMSType.values();
    }
    
    /**
     * Get all the values of the ENUM TicketTopic
     * 
     * @return An array of values from the TicketTopic ENUM
     */
    public TicketTopic[] getTicketTopics() {
        return TicketTopic.values();
    }
    
    /**
     * Get all the values of the ENUM TicketSeverity
     * 
     * @return An array of values from the TicketSeverity ENUM
     */
    public TicketSeverity[] getTicketSeverities() {
        return TicketSeverity.values();
    }
    
    /**
     * Get all the values of the ENUM TicketStatus
     * 
     * @return An array of values from the TicketStatus ENUM
     */
    public TicketStatus[] getTicketStatus() {
        return TicketStatus.values();
    }
    
    /**
     * Get all the values of the ENUM CompanyType
     * 
     * @return An array of values from the CompanyType ENUM
     */
    public CompanyType[] getCompanyTypes() {
        return CompanyType.values();
    }
    
}
