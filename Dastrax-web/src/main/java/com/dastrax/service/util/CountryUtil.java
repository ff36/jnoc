/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.util;

import com.dastrax.per.util.CountriesUtil;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @version Build 2.0.0
 * @since Jul 25, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@RequestScoped
public class CountryUtil {

    // EJB----------------------------------------------------------------------
    @EJB
    CountriesUtil countriesUtil;

    // Getters------------------------------------------------------------------
    public CountriesUtil getCountriesUtil() {
        return countriesUtil;
    }

    // Setters------------------------------------------------------------------
    public void setCountriesUtil(CountriesUtil countriesUtil) {
        this.countriesUtil = countriesUtil;
    }

}
