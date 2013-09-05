/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.app.exception;

import com.dastrax.app.util.ExceptionUtil;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This is the custom dastrax JSF exception handler. We can intercept exceptions
 * that would normally reach the presentation layer and handle them.
 * Specifically we do not want to display any critical data relating to the
 * exception and we want to send an email to the developers that the exception
 * has happened.
 * 
 * @version Build 2.0.0
 * @since Sep 5, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class DastraxExceptionHandler extends ExceptionHandlerWrapper {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(DastraxExceptionHandler.class.getName());
    
    // Variables----------------------------------------------------------------
    private ExceptionHandler wrapped;
    
    // JNDI---------------------------------------------------------------------
    private static final String JNDI_EXU = ResourceBundle.getBundle("Config").getString("Exception");
    
    // Constructors-------------------------------------------------------------
    public DastraxExceptionHandler() {
    }
    
    DastraxExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }
 
    // Getters------------------------------------------------------------------
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }
 
    // Methods------------------------------------------------------------------
    @Override
    public void handle() throws FacesException {
 
        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context =
                    (ExceptionQueuedEventContext) event.getSource();
 
            // get the exception from context
            Throwable t = context.getException();

            // handle the exception
            try {
                // Send the email
                ExceptionUtil exu = (ExceptionUtil) InitialContext.doLookup(JNDI_EXU);
                exu.report((Exception) t);
                
                // Add a message to the presentation layer
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_FATAL, 
                        "This is embarrassing! We seem to have run into an unexpected error. The development team has been notified and we will endeavour to fix this as soon as possible. In the mean time you can try refreshing the page and trying again. If you have a moment please help us improve the system by clicking on the feedback tab in the bottom right corner and telling use what you where doing that caused this message to be displayed.",
                        "");
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:growl");
                
            } catch (NamingException ex) {
                LOG.log(Level.SEVERE, "JNDI Naming Exception whilst trying to report an exception", ex);
            } finally {
                //remove the exception from queue
                i.remove();
            }
        }
        // parent handle
        getWrapped().handle();
    }
}
