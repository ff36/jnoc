/*
 * Created 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.exception;

import com.dastrax.app.misc.JsfUtil;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This is the custom JSF exception handler. We can intercept exceptions
 * that would normally reach the presentation layer and handle them.
 * Specifically we do not want to display any critical data relating to the
 * exception and we want to send an email to the developers that the exception
 * has happened.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (May 9, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DastraxExceptionHandler extends ExceptionHandlerWrapper {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(DastraxExceptionHandler.class.getName());
    private static final String JNDI_EXU = ResourceBundle.getBundle("config").getString("Exception");
    private ExceptionHandler wrapped;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DastraxExceptionHandler() {
    }
    
    DastraxExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }
//</editor-fold>

    @Override
    public void handle() throws FacesException {

        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context
                    = (ExceptionQueuedEventContext) event.getSource();

            // Handle the exception
            try {
                // Send the email
                ExceptionUtil exu = (ExceptionUtil) InitialContext.doLookup(JNDI_EXU);
                exu.report(new Exception(context.getException()));

                // Add a message to the presentation layer
                JsfUtil.addFatalMessage(ResourceBundle.getBundle("source-bundle").getString("fatal.error"));
                FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:growl");

            } catch (NamingException ex) {
                LOG.log(Level.SEVERE, "JNDI Naming Exception whilst trying to report an exception", ex);
            } finally {
                // Remove the exception from queue
                i.remove();
            }
        }
        // Parent handle
        getWrapped().handle();
    }
}
