/*
 * Created Jul 29, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.converter;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Tag;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

/**
 * JSF Converter class to map between presentation layer String representations
 * and backing object implementations.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 29, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@FacesConverter("tagCvt")
public class TagJSFCvt implements Converter {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    /**
     * Normally this would just be a straight database lookup. However because
     * we want to create tags on the fly it is possible to get inputs that 
     * have no value so we want to retrieve it from the temporary ID and create
     * a new Tag to be persisted with no ID.
     * @param fc
     * @param uic
     * @param string
     * @return 
     */
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string.startsWith("tempID")) {
            return new Tag(string.substring(string.indexOf("(") +1,string.indexOf(")")));
        } else {
            return (Tag) dap.find(Tag.class, string);
        }
    }
    
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o != null) {
            return ((Tag) o).getId().toString();
        } else {
            return null;
        }
        
    }
//</editor-fold>

}