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
 * Created Jul 29, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 

 */
package com.jnoc.app.converter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import com.jnoc.per.dap.CrudService;
import com.jnoc.per.dap.QueryParameter;
import com.jnoc.per.entity.Metier;

/**
 * JSF Converter class to map between presentation layer String representations
 * and backing object implementations.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (May 25, 2015)
 */
@Named
@FacesConverter("metierNameCvt")
public class MetierNameJSFCvt implements Converter {
	private static final Logger LOG = Logger.getLogger(MetierNameJSFCvt.class.getName());
    //<editor-fold defaultstate="collapsed" desc="Properties">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String name) {
        try {
            @SuppressWarnings("unchecked")
			List<Metier> varMetiers = dap.findWithNamedQuery(
                  "Metier.findByName", 
                  QueryParameter
                          .with("name", name)
                          .parameters());
        	return varMetiers.get(0);
        } catch (NumberFormatException e) {
        	LOG.log(Level.CONFIG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        try {
            return ((Metier) o).getName().toString();
        } catch (NullPointerException | ClassCastException e) {
        	LOG.log(Level.CONFIG, e.getMessage(), e);
            return null;
        }
    }
//</editor-fold>

}
