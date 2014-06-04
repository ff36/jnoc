/*
 * Created Mar 17, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Creates a list of world countries in the users local language.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Mar 17, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class Countries {

    /**
     * Get a sorted list of world locals
     *
     * @return All Locals registered in Java
     */
    public static List<Locale> getWorldCountries() {
        
        List<Locale> worldCountries = new ArrayList<>();
        
        String[] locales = Locale.getISOCountries();
        
        for (String countryCode : locales) {
            Locale country = new Locale("", countryCode);
            if (!country.getCountry().isEmpty()) {
                worldCountries.add(country);
            }
        }
        
        // Comparator to sort the countries into order with priority
        Comparator<Locale> comparator = (Locale o1, Locale o2) -> {
            if (o1.getDisplayCountry().equals(o2.getDisplayCountry())) {
                return 0;
            }
            // Set the priority locals to be at the top
            Locale[] priorityLocales = {
                Locale.US,
                Locale.UK
            };
            for (Locale priorityLocale : priorityLocales) {
                Locale priority = priorityLocale;
                if (o1.getDisplayCountry().equals(priority.getDisplayCountry())) {
                    return -1;
                }
                if (o2.getDisplayCountry().equals(priority.getDisplayCountry())) {
                    return 1;
                }
            }
            // Default to straight comparison.
            return o1.getDisplayCountry().compareTo(o2.getDisplayCountry());
        };
        // Sort the list
        Collections.sort(worldCountries, comparator);
        
        return worldCountries;
    }


}
