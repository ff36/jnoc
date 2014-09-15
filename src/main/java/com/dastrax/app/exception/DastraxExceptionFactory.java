/*
 * Created 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * This is a custom exception handler factory that allows us to intercept the
 * exceptions that would reach the JSF presentation layer.
 *
 * @version {version}
 * @since Build {build} (May 9, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DastraxExceptionFactory extends ExceptionHandlerFactory {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private ExceptionHandlerFactory parent;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DastraxExceptionFactory() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of parent
     *
     * @param parent new value of parent
     */
    public DastraxExceptionFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler handler = new DastraxExceptionHandler(parent.getExceptionHandler());
        return handler;
    }
//</editor-fold>

}
