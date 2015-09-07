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
 * Created May 13, 2014.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.jnoc.app.services;

import java.util.List;

/**
 * Methods dedicated to handling Domain Name System (DNSManager) based functions.
 *
 * @version 1.0.0
 * @since Build 1.0.0 (Jul 17, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public interface DNSManager {
    
    /**
     * This method will attempt to create a new CNAME record on route 53 with
     * the specified prefix. ie. if <b>abcde</b> is passed as the prefix, the
     * CNAME <b>abcde.example.com</b> is created. The CNAME record will point
     * back to the application BaseUrl as defined in the config.properties file.
     *
     * @param prefix
     * @return true if the CNAME is successfully created.
     */
    public boolean createCNAME(String prefix);
    
    /**
     * Delete the specified sub-domain CNAME record from route 53.
     *
     * @param prefix
     * @return true if the CNAME is removed from route 53 or the record set does
     * not exist
     */
    public boolean deleteCNAME(String prefix);
    
    /**
     * Extracts the string prefixing the first period in the URL.
     *
     * <b>example.com</b> will return <b>null</b>. <b>abcde.example.com</b>
     * will return <b>abcde</b>
     *
     * @param contextURL
     * @return the CNAME prefix if it exists and is valid. Otherwise null
     */
    public String extract(String contextURL);
    
    /**
     * Determines whether the specified prefix is already a registered record
     * set on route 53.
     *
     * @param prefix
     * @return true if the CNAME already exists on route 53. Otherwise false
     */
    public boolean recordSetExists(String prefix);
    
    /**
     * Retrieves a list of existing record sets on route 53 with the system
     * zoneId.
     * 
     * @param maxItems For all record Sets maxItems should be set to 0.
     * @return A list of all existing record sets from route 53. The list can be
     * empty. If an exception is encountered null is returned.
     */
    public List listRecordSets(int maxItems);
    
}
