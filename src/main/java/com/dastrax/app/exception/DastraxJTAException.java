/*
 * Created Jul 13, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.exception;

/**
 * This is the custom JSF exception. 
 * This is used by the <DastraxExceptionHandler.class>
 *
 * @version 2.0.0
 * @since Build 2.0.0 (May 9, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DastraxJTAException extends Exception {

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DastraxJTAException() {
        super();
    }
    
    public DastraxJTAException(String message) {
        super(message);
    }
    
    public DastraxJTAException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DastraxJTAException(Throwable cause) {
        super(cause);
    }
//</editor-fold>

}
