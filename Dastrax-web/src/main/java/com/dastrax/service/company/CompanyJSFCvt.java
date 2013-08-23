/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.company;

import com.dastrax.per.dao.core.CompanyDAO;
import com.dastrax.per.entity.core.Company;
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
@FacesConverter("companyCvt")
public class CompanyJSFCvt implements Converter {

    // EJB----------------------------------------------------------------------
    @EJB
    CompanyDAO companyDAO;
    
    // Methods------------------------------------------------------------------
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return companyDAO.findCompanyById(string);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return ((Company) o).getId();
    }

}
