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
 * Created Aug 8, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.service.das;

import com.jnoc.per.entity.DAS;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Create DAS CDI bean. Provides methods for creating DAS
 * 
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class CreateDas implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(CreateDas.class.getName());
    private static final long serialVersionUID = 1L;
    
    private DAS das;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public CreateDas() {        
        this.das = new DAS();
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of das
     *
     * @return the value of das
     */
    public DAS getDas() {
        return das;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of das.
     *
     * @param das new value of das
     */
    public void setDas(DAS das) {
        this.das = das;
    }

//</editor-fold>

}
