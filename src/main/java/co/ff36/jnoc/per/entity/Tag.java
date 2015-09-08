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
import javax.persistence.Column;
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
 * Tag holds information pertaining object search tags. Tags are unique search
 * strings that help users isolate resources by customized tags.
 * 
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere

 */
@NamedQueries({
    @NamedQuery(name = "Tag.findAll", query = "SELECT e FROM Tag e"),
    @NamedQuery(name = "Tag.findByID", query = "SELECT e FROM Tag e WHERE e.id = :id"),
    @NamedQuery(name = "Tag.findByName", query = "SELECT e FROM Tag e WHERE e.name = :name"),
})
@Entity
public class Tag implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(unique = true)
    private String name;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Tag() {
    }
    
    public Tag(String name) {
        this.name = name;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of id. Unique storage key
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Get the value of name.
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id. Unique storage key
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Set the value of name.
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    /**
     * Warning - this method won't work in the case the id fields are not set.
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>
    
}
