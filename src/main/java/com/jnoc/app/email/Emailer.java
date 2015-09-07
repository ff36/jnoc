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

 */

package com.jnoc.app.email;

import javax.ejb.Asynchronous;

/**
 * This is the public utility that is used to convert an <Email.class> into an
 * actual email and send it using AWS SES.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (2013)
 * @author Tarka L'Herpiniere

 */
public interface Emailer {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    String emailSender = System.getenv("JNOC_SENDER_EMAIL");
    String baseUrl = System.getenv("JNOC_BASE_URL");
    String protocol = System.getenv("JNOC_ACCESS_PROTOCOL");
//</editor-fold>
    
    /**
     * This is the public method used to construct and send an email. The method
     * requires an <Email.class> to be passed as the only argument. The method
     * is @Asynchronous and completes immediately regardless of the outcome.
     * 
     * Apache Velocity is used to combine the variables with the template before
     * rendering out a string to be sent as the final email.
     *
     * @param email
     */
    @Asynchronous
    public void send(Email email);
    
}
