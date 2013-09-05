/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.app.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * This is a custom exception handler factory that allows us to intercept the
 * exceptions that would reach the JSF presentation layer.
 * 
 * @version Build 2.0.0
 * @since Sep 5, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class DastraxExceptionFactory extends ExceptionHandlerFactory {
   
    // Variables----------------------------------------------------------------
    private ExceptionHandlerFactory parent;
 
    // Constructors-------------------------------------------------------------
   public DastraxExceptionFactory(ExceptionHandlerFactory parent) {
    this.parent = parent;
   }
 
    // Methods------------------------------------------------------------------
    @Override
    public ExceptionHandler getExceptionHandler() {
 
        ExceptionHandler handler = new DastraxExceptionHandler(parent.getExceptionHandler());
 
        return handler;
    }
 
}
