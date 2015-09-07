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
 * Created 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.app.exception;

import com.jnoc.app.misc.JsfUtil;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * This is the custom JSF exception handler. We can intercept exceptions that
 * would normally reach the presentation layer and handle them. Specifically we
 * do not want to display any critical data relating to the exception and we
 * want to send an email to the developers that the exception has happened.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (May 9, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class JnocExceptionHandler extends ExceptionHandlerWrapper {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(JnocExceptionHandler.class.getName());
    private ExceptionHandler wrapped;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public JnocExceptionHandler() {
    }

    JnocExceptionHandler(ExceptionHandler exception) {
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

        final Iterator<ExceptionQueuedEvent> i
                = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context
                    = (ExceptionQueuedEventContext) event.getSource();

            // Add a message to the presentation layer
            JsfUtil.addFatalMessage(
                    ResourceBundle
                    .getBundle("source-bundle")
                    .getString("fatal.error"));
            FacesContext.getCurrentInstance().getPartialViewContext()
                    .getRenderIds()
                    .add("form:growl");

            i.remove();

        }
        // Parent handle
        getWrapped().handle();
    }
}
