/*
 * Created May 13, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.email;

import java.util.ResourceBundle;
import javax.ejb.Asynchronous;

/**
 * This is the public utility that is used to convert an <Email.class> into an
 * actual email and send it using AWS SES.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public interface Emailer {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    String emailSender = ResourceBundle.getBundle("config").getString("SenderEmailAddress");
    String baseUrl = ResourceBundle.getBundle("config").getString("BaseUrl");
    String protocol = ResourceBundle.getBundle("config").getString("AccessProtocol");
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
