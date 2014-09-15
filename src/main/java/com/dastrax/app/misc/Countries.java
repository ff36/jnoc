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

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private final List<Locale> worldCountries;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Countries() {
        worldCountries = compileWorldCountries();
    }

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of worldCountries.
     *
     * @return the value of worldCountries
     */
    public List<Locale> getWorldCountries() {
        return worldCountries;
    }
//</editor-fold>

    /**
     * Get a sorted list of world locals
     *
     * @return All Locals registered in Java
     */
    private List<Locale> compileWorldCountries() {

        List<Locale> countries = new ArrayList<>(8810);

        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {
            Locale country = new Locale("", countryCode);
            if (!country.getCountry().isEmpty()) {
                countries.add(country);
            }
        }

        // TODO: Why is this causing an out of index exception!!!???
        // Sort the list
        Collections.sort(countries, new Comparator<Locale>() {

            @Override
            public int compare(Locale o1, Locale o2) {
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
            }
        });

        return countries;
    }

}
