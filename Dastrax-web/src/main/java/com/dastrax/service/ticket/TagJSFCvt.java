/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.ticket;

import com.dastrax.per.dao.core.TagDAO;
import com.dastrax.per.entity.core.Tag;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

/**
 *
 * @version Build 2.1.0
 * @since Sep 16, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@FacesConverter("tagCvt")
public class TagJSFCvt implements Converter {

    // EJB----------------------------------------------------------------------
    @EJB
    TagDAO tagDAO;
    
    // Methods------------------------------------------------------------------
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
            return tagDAO.findTagById(string);
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return ((Tag) o).getId();
    }

}
