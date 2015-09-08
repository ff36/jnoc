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

import co.ff36.jnoc.app.misc.TemporalUtil;
import co.ff36.jnoc.per.dap.CrudService;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 * 
 * Account holds information pertaining to User accounts.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere

 */
@NamedQueries({
    @NamedQuery(name = "Account.findAll", query = "SELECT e FROM Account e"),
    @NamedQuery(name = "Account.findByID", query = "SELECT e FROM Account e WHERE e.id = :id")
})
@Entity
public class Account implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Account.class.getName());
    private static final long serialVersionUID = 1L;
    
    @Version
    private int version;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String s3id;
    private boolean confirmed;
    private boolean locked;
    private Long creationEpoch;
    private Long lastSessionEpoch;
    private Long currentSessionEpoch;
    private Long closeEpoch;
    private boolean certified;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Account() {
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
            LOG.log(Level.CONFIG, null, ex);
        }
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
     * Get the value of s3id
     *
     * @return the value of s3id
     */ 
    public String getS3id() {
        return s3id;
    }
    
    /**
     * Get the value of confirmed
     *
     * @return the value of confirmed
     */ 
    public boolean isConfirmed() {
        return confirmed;
    }
    
    /**
     * Get the value of locked
     *
     * @return the value of locked
     */ 
    public boolean isLocked() {
        return locked;
    }
    
    /**
     * Get the value of creationEpoch
     *
     * @return the value of creationEpoch
     */ 
    public Long getCreationEpoch() {
        return creationEpoch;
    }
    
    /**
     * Get the value of creationEpoch as a string
     *
     * @return the value of creationEpoch as a system formatted string
     */ 
    public String getCreationDate() {
        return TemporalUtil.epochToStringDate(creationEpoch);
    }
    
    /**
     * Get the value of lastSessionEpoch
     *
     * @return the value of lastSessionEpoch
     */ 
    public Long getLastSessionEpoch() {
        return lastSessionEpoch;
    }
    
    /**
     * Get the value of currentSessionEpoch
     *
     * @return the value of currentSessionEpoch
     */ 
    public Long getCurrentSessionEpoch() {
        return currentSessionEpoch;
    }
    
    /**
     * Get the value of currentSessionEpoch as a string
     *
     * @return the value of currentSessionEpoch as a system formatted string
     */ 
    public String getCurrentSessionTimeStamp() {
        return TemporalUtil.epochToStringDateTime(currentSessionEpoch);
    }
    
    /**
     * Get the value of version
     *
     * @return the value of version
     */ 
    public int getVersion() {
        return version;
    }
    
    /**
     * Get the value of closeEpoch
     *
     * @return the value of closeEpoch
     */ 
    public Long getCloseEpoch() {
        return closeEpoch;
    }

    public boolean isCertified() {
        return certified;
    }
    
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    
    /**
     * Set the value of id. Auto incremented persistence record id
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Set the value of s3id. Unique storage key
     *
     * @param s3id new value of s3id
     */
    public void setS3id(String s3id) {
        this.s3id = s3id;
    }
    
    /**
     * Set the value of version. Auto incremented persistence record version 
     *
     * @param version new value of version
     */
    public void setVersion(int version) {
        this.version = version;
    }
    
    /**
     * Set the value of confirmed. Account email confirmed
     *
     * @param confirmed new value of confirmed 
     */
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
    
    /**
     * Set the value of locked.
     *
     * @param locked new value of locked
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    /**
     * Set the value of creationEpoch.
     *
     * @param creationEpoch new value of creationEpoch
     */
    public void setCreationEpoch(Long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }
    
    /**
     * Set the value of lastSessionEpoch.
     *
     * @param lastSessionEpoch new value of lastSessionEpoch
     */
    public void setLastSessionEpoch(Long lastSessionEpoch) {
        this.lastSessionEpoch = lastSessionEpoch;
    }
    
    /**
     * Set the value of currentSessionEpoch.
     *
     * @param currentSessionEpoch new value of currentSessionEpoch
     */
    public void setCurrentSessionEpoch(Long currentSessionEpoch) {
        this.currentSessionEpoch = currentSessionEpoch;
    }
    
    /**
     * Set the value of closeEpoch. Only present when an account has been closed
     *
     * @param closeEpoch new value of closeEpoch
     */
    public void setCloseEpoch(Long closeEpoch) {
        this.closeEpoch = closeEpoch;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }
    
    
//</editor-fold>
    
    /**
     * Update the persistence layer with a new version of the account.
     */
    public void update() {
        Account account = (Account) dap.update(this);
        this.version = account.version;
    }
    
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
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}
