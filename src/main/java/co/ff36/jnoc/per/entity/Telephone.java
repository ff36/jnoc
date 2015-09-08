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


package co.ff36.jnoc.per.entity;

import co.ff36.jnoc.app.misc.Countries;
import co.ff36.jnoc.per.project.JNOC.TelephoneType;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Telephone holds information pertaining to contact telephone information.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere

 */
@NamedQueries({
    @NamedQuery(name = "Telephone.findAll", query = "SELECT e FROM Telephone e"),
    @NamedQuery(name = "Telephone.findByID", query = "SELECT e FROM Telephone e WHERE e.id = :id")
})
@Entity
public class Telephone implements Serializable {
	private static final Logger LOG = Logger.getLogger(Telephone.class.getName());
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private TelephoneType type;
    private String country;
    private String number;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private final String defaultPhoneCountry;
    @Transient
    private final List<Locale> countries;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Telephone() {
        this.defaultPhoneCountry = System.getenv("JNOC_DEFAULT_COUNTRY");
        this.country = this.defaultPhoneCountry;
        this.type = TelephoneType.DESK;
        this.countries = new Countries().getWorldCountries();
    }

    public Telephone(String country) {
        this.defaultPhoneCountry = System.getenv("JNOC_DEFAULT_COUNTRY");
        this.country = country;
        this.type = TelephoneType.DESK;
        this.countries = new Countries().getWorldCountries();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public TelephoneType getType() {
        return type;
    }

    /**
     * Get the value of country. ISO2 country code
     *
     * @return the value of country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Get the value of number
     *
     * @return the value of number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Get the value of countries
     *
     * @return the value of countries
     */
    public List<Locale> getCountries() {
        return countries;
    }
    
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id.
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the value of type.
     *
     * @param type new value of type
     */
    public void setType(TelephoneType type) {
        this.type = type;
    }

    /**
     * Set the value of country. ISO2 country code
     *
     * @param country new value of country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Set the value of number.
     *
     * @param number new value of number
     */
    public void setNumber(String number) {
        this.number = number;
    }
//</editor-fold>

    /**
     * Attempts to determine if a phone number is in a valid format based on the
     * number, country and type.
     *
     * This method does not check if the number is registered. It only attempts
     * to determine if the number format is correct.
     *
     * @return true if the number format is valid, otherwise false
     */
    public boolean validFormat() {

        try {
            if (country == null) {
                country = System.getenv("JNOC_DEFAULT_COUNTRY");
            }
            
            if (number.length() > 3) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber phoneNumber;
                phoneNumber = phoneUtil.parse(number, country);
                return phoneUtil.isValidNumber(phoneNumber);
            }
            return false;
        } catch (NumberParseException npe) {
            System.err.println("NumberParseException was thrown: " + npe.toString());
            return false;
        } catch (NullPointerException npe) {
            // Number is null
            return false;
        }

    }

    /**
     * Attempts to convert the local phone number into a valid international 
     * format based on the number and country.
     *
     * @return If the number is in a valid format the telephone number in 
     * international format is returned. If the number is not in a valid format
     * null is returned.
     */
    public String internationalFormat() {

        String result = null;

        try {
            if (validFormat()) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber phoneNumber;
                phoneNumber = phoneUtil.parse(number, country);
                result = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            }

        } catch (NumberParseException npe) {
            // Bad number format
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
        }

        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Telephone other = (Telephone) obj;
        return Objects.equals(this.number, other.number);
    }
//</editor-fold>

}
