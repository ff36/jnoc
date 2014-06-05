/*
 * Created Jul 29, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.converter;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.DAS;
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
@FacesConverter("dasCvt")
public class DASJSFCvt implements Converter {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return (DAS) dap.find(DAS.class, string);
    }
    
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o != null) {
            return ((DAS) o).getId().toString();
        } else {
            return null;
        }
        
    }
//</editor-fold>

}
