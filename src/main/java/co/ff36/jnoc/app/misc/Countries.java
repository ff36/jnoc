/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package co.ff36.jnoc.app.misc;

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
