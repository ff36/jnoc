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

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 * 
 * Address holds information pertaining to contact address information.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere

 */
@NamedQueries({
    @NamedQuery(name = "Address.findAll", query = "SELECT e FROM Address e"),
    @NamedQuery(name = "Address.findByID", query = "SELECT e FROM Address e WHERE e.id = :id")
})
@Entity
public class Address implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String lineOne;
    private String lineTwo;
    private String lineThree;
    private String lineFour;
    private String lineFive;
    private String country;
    private String latitude;
    private String longitude;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Address() {
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
     * Get the value of lineOne
     *
     * @return the value of lineOne
     */ 
    public String getLineOne() {
        return lineOne;
    }
    
    /**
     * Get the value of lineTwo
     *
     * @return the value of lineTwo
     */ 
    public String getLineTwo() {
        return lineTwo;
    }
    
    /**
     * Get the value of lineThree
     *
     * @return the value of lineThree
     */ 
    public String getLineThree() {
        return lineThree;
    }
    
    /**
     * Get the value of lineFour
     *
     * @return the value of lineFour
     */ 
    public String getLineFour() {
        return lineFour;
    }
    
    /**
     * Get the value of lineFive
     *
     * @return the value of lineFive
     */ 
    public String getLineFive() {
        return lineFive;
    }
    
    /**
     * Get the value of country
     *
     * @return the value of country
     */ 
    public String getCountry() {
        return country;
    }
    
    /**
     * Get the value of latitude
     *
     * @return the value of latitude
     */ 
    public String getLatitude() {
        return latitude;
    }
    
    /**
     * Get the value of longitude
     *
     * @return the value of longitude
     */ 
    public String getLongitude() {
        return longitude;
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
     * Set the value of lineOne.
     *
     * @param lineOne new value of lineOne
     */
    public void setLineOne(String lineOne) {
        this.lineOne = lineOne;
    }
    
    /**
     * Set the value of lineTwo.
     *
     * @param lineTwo new value of lineTwo
     */
    public void setLineTwo(String lineTwo) {
        this.lineTwo = lineTwo;
    }
    
    /**
     * Set the value of lineThree.
     *
     * @param lineThree new value of
     */
    public void setLineThree(String lineThree) {
        this.lineThree = lineThree;
    }
    
    /**
     * Set the value of lineFour.
     *
     * @param lineFour new value of lineFour
     */
    public void setLineFour(String lineFour) {
        this.lineFour = lineFour;
    }
    
    /**
     * Set the value of lineFive.
     *
     * @param lineFive new value of lineFive
     */
    public void setLineFive(String lineFive) {
        this.lineFive = lineFive;
    }
    
    /**
     * Set the value of country.
     *
     * @param country new value of country
     */
    public void setCountry(String country) {
        this.country = country;
    }
    
    /**
     * Set the value of latitude.
     *
     * @param latitude new value of latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    
    /**
     * Set the value of longitude.
     *
     * @param longitude new value of longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
//</editor-fold>

    /**
     * Constructs a concatenated address based on the available properties.
     * 
     * @return The concatenation of supplied address parts
     */
    public String buildCompleteAddress() {

        String result = "";

        /*
         * Format the address in such a way that allows for any of the
         * address lines not to be present.
         */
        if (lineOne != null) {
            lineOne = lineOne.trim();
            if (!lineOne.isEmpty()) {
                result = lineOne;
            }
        }

        if (lineTwo != null) {
            lineTwo = lineTwo.trim();
            if (!lineTwo.isEmpty()) {
                if (result.isEmpty()) {
                    result = lineTwo;
                } else {
                    result = result + ", " + lineTwo;
                }
            }
        }

        if (lineThree != null) {
            lineThree = lineThree.trim();
            if (!lineThree.isEmpty()) {
                if (result.isEmpty()) {
                    result = lineThree;
                } else {
                    result = result + ", " + lineThree;
                }
            }
        }

        if (lineFour != null) {
            lineFour = lineFour.trim();
            if (!lineFour.isEmpty()) {
                if (result.isEmpty()) {
                    result = lineFour;
                } else {
                    result = result + ", " + lineFour;
                }
            }
        }

        if (lineFive != null) {
            lineFive = lineFive.trim();
            if (!lineFive.isEmpty()) {
                if (result.isEmpty()) {
                    result = lineFive;
                } else {
                    result = result + ", " + lineFive;
                }
            }
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
        final Address other = (Address) obj;
        if (!Objects.equals(this.lineOne, other.lineOne)) {
            return false;
        }
        if (!Objects.equals(this.lineTwo, other.lineTwo)) {
            return false;
        }
        if (!Objects.equals(this.lineThree, other.lineThree)) {
            return false;
        }
        if (!Objects.equals(this.lineFour, other.lineFour)) {
            return false;
        }
        if (!Objects.equals(this.lineFive, other.lineFive)) {
            return false;
        }
        if (!Objects.equals(this.country, other.country)) {
            return false;
        }
        if (!Objects.equals(this.latitude, other.latitude)) {
            return false;
        }
        if (!Objects.equals(this.longitude, other.longitude)) {
            return false;
        }
        return true;
    }
//</editor-fold>
    
}
