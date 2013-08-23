/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.account;

import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Subject;
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
@FacesConverter("subjectCvt")
public class SubjectJSFCvt implements Converter {

    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    
    // Methods------------------------------------------------------------------
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return subjectDAO.findSubjectByUid(string);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return ((Subject) o).getUid();
    }

}
