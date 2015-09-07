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
public class JnocExceptionFactory extends ExceptionHandlerFactory {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private ExceptionHandlerFactory parent;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public JnocExceptionFactory() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of parent
     *
     * @param parent new value of parent
     */
    public JnocExceptionFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler handler = new JnocExceptionHandler(parent.getExceptionHandler());
        return handler;
    }
//</editor-fold>

}
