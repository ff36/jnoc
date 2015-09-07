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
 * Created May 26, 2014.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.service.navigation;

import java.util.Objects;

/**
 * Navigation is based on menu items. These construct the individual navigation
 * items that users use to navigate around the site.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (May 26, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class MenuItem {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String id;
    private String label;
    private String outcome;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public MenuItem() {
    }
    
    public MenuItem(String id, String label, String outcome) {
        this.id = id;
        this.label = label;
        this.outcome = outcome;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get the value of label
     *
     * @return the value of label
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Get the value of outcome
     *
     * @return the value of outcome
     */
    public String getOutcome() {
        return outcome;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id.
     *
     * @param id new value of id
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Set the value of label.
     *
     * @param label new value of label
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Set the value of outcome.
     *
     * @param outcome new value of outcome
     */
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.label);
        hash = 97 * hash + Objects.hashCode(this.outcome);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MenuItem other = (MenuItem) obj;
        return Objects.equals(this.id, other.id);
    }
//</editor-fold>

}
