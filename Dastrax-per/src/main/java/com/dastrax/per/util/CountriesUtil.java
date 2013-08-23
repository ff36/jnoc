/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */
package com.dastrax.per.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class CountriesUtil {

    // Variables----------------------------------------------------------------
    private List<String> worldCountries = listAll();
    
    // Getters------------------------------------------------------------------
    public List<String> getWorldCountries() {
        return worldCountries;
    }

    // Setters------------------------------------------------------------------
    public void setWorldCountries(List<String> worldCountries) {
        this.worldCountries = worldCountries;
    }
    
    // Comparator---------------------------------------------------------------
    /**
     * This is to organize the list with specific priority countries at the top
     */
    private static class SubjectLocale implements Comparable<CountriesUtil.SubjectLocale> {
        // Subjects Locale.
        private final Locale subjectsLocale;
        
        // Which Locales get priority.
        private static final Locale[] priorityLocales = {
            Locale.UK,
            Locale.US
        };

        public SubjectLocale(Locale subjectsLocale) {
            this.subjectsLocale = subjectsLocale;
        }

        public String getCountry() {
            return subjectsLocale.getCountry();
        }

        @Override
        public int compareTo(CountriesUtil.SubjectLocale it) {
            // No duplicates in the country field.
            if (getCountry().equals(it.getCountry())) {
                return 0;
            }
            for (Locale priorityLocale : priorityLocales) {
                Locale priority = priorityLocale;
                if (getCountry().equals(priority.getCountry())) {
                    // I come first.
                    return -1;
                }
                if (it.getCountry().equals(priority.getCountry())) {
                    // It comes first.
                    return 1;
                }
            }
            // Default to straight comparison.
            return getCountry().compareTo(it.getCountry());
        }
    }



    // Methods------------------------------------------------------------------
    /**
     * Generate a list of world countries in the application locale
     *
     * @return List<String>
     */
    public static List<String> listAll() {
        Set<CountriesUtil.SubjectLocale> byLocale = new TreeSet();
        // Gather them all up.
        for (Locale locale : Locale.getAvailableLocales()) {
            final String isoCountry = locale.getDisplayCountry();
            if (isoCountry.length() > 0) {
                byLocale.add(new CountriesUtil.SubjectLocale(locale));
            }
        }
        // Roll them out of the set.
        ArrayList<String> list = new ArrayList<>();
        for (CountriesUtil.SubjectLocale l : byLocale) {
            list.add(l.subjectsLocale.getDisplayCountry());
        }
        return list;
    }
    
    /**
     * Converts a country name as a string into an ISO country code
     * 
     * @param countryName
     * @return an ISO country code or null if one does not exist.
     */
    public static String countryToIso3Code(String countryName) {
        String isoCode = null;

        for (Locale locale : Locale.getAvailableLocales()) {
            final String isoCountry = locale.getDisplayCountry();
            if (isoCountry.equals(countryName)) {
                isoCode = locale.getISO3Country();
            }
        }
        return isoCode;  
    }
    
    /**
     * Converts an ISO country code into a country name string
     * 
     * @param isoCode
     * @return an ISO country code or null if one does not exist.
     */
    public static String iso3ToCountryCode(String isoCode) {
        String countryCode = null;

        for (Locale locale : Locale.getAvailableLocales()) {
            final String isoCountryCode = locale.getISO3Country();
            if (isoCountryCode.equals(isoCode)) {
                countryCode = locale.getISO3Country();
            }
        }
        return countryCode;  
    }
    
    /**
     * Converts an ISO country code into a country name string
     *
     * @param isoCode
     * @return an ISO country code or null if one does not exist.
     */
    public static String iso3ToCountryName(String isoCode) {
        String countryName = null;

        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                final String isoCountryCode = locale.getISO3Country();
                if (isoCountryCode.equals(isoCode)) {
                    countryName = locale.getDisplayCountry();
                }
            } catch (MissingResourceException e) {
                /*
                 * Its possible that no ISO3 code exists so we want to catch
                 * the exception but do nothing.
                 */
            }
        }
        return countryName;
    }
    
    /**
     * Converts a country name string into an ISO2 country code
     * 
     * @param countryName
     * @return an ISO country code or null if one does not exist.
     */
    public static String countryToIso2Code(String countryName) {
        String isoCode = null;

        for (Locale locale : Locale.getAvailableLocales()) {
            final String isoCountry = locale.getDisplayCountry();
            if (isoCountry.equals(countryName)) {
                isoCode = locale.getCountry();
            }
        }
        return isoCode;  
    }

}
