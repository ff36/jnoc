/*
 * Created Jul 10, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.exception;

/**
 * This is the custom JSF exception. 
 * This is used by the <DastraxExceptionHandler.class>
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DuplicateEmailException extends Exception {

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DuplicateEmailException() {
        super();
    }
    
    public DuplicateEmailException(String message) {
        super(message);
    }
    
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DuplicateEmailException(Throwable cause) {
        super(cause);
    }
//</editor-fold>
}
