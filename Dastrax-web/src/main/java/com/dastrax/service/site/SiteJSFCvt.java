/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.site;

import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.entity.core.Site;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

/**
 *
 * @version Build 2.0.0
 * @since Jul 29, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@FacesConverter("siteCvt")
public class SiteJSFCvt implements Converter {

    // EJB----------------------------------------------------------------------
    @EJB
    SiteDAO siteDAO;
    
    // Methods------------------------------------------------------------------
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return siteDAO.findSiteById(string);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return ((Site) o).getId();
    }

}
