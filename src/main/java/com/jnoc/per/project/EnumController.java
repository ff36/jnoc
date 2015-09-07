/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/


/*
 * Created Jul 10, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 

 */
package com.jnoc.per.project;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.jnoc.per.project.JNOC.CompanyType;
import com.jnoc.per.project.JNOC.DMSType;
import com.jnoc.per.project.JNOC.TelephoneType;
import com.jnoc.per.project.JNOC.TicketSeverity;
import com.jnoc.per.project.JNOC.TicketStatus;
import com.jnoc.per.project.JNOC.TicketTopic;

/**
 * JSF offers a default implementation converter for ENUM types. This class
 * is a simple CDI named class that can be accessed from the presentation layer
 * to display ENUM values as a list. The ENUM constants originate from the JNOC
 * class.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (Jul 10, 2013)
 * @author Tarka L'Herpiniere

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
