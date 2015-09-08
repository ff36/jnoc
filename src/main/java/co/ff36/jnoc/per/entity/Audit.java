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
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import co.ff36.jnoc.app.misc.TemporalUtil;
import co.ff36.jnoc.app.security.SessionUser;
import co.ff36.jnoc.per.dap.CrudService;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Audit holds information pertaining to users usage activities.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere

 */
@NamedQueries({
    @NamedQuery(name = "Audit.findAll", query = "SELECT e FROM Audit e"),
    @NamedQuery(name = "Audit.findByID", query = "SELECT e FROM Audit e WHERE e.id = :id"),
    @NamedQuery(name = "Audit.findSince", query = "SELECT e FROM Audit e WHERE e.createEpoch > :time"),
    @NamedQuery(name = "Audit.findReverse", query = "SELECT e FROM Audit e ORDER BY e.createEpoch DESC"),})
@Entity
public class Audit implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Audit.class.getName());
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long createEpoch;
    private String description;
    @ManyToOne
    private User author;
    @ManyToOne
    private User target;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Audit() {
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
     * Get the value of createEpoch
     *
     * @return the value of createEpoch
     */
    public long getCreateEpoch() {
        return createEpoch;
    }

    /**
     * Get the value of createEpoch as a string
     *
     * @return the value of createEpoch as a string
     */
    public String getCreateTimestamp() {
        return TemporalUtil.epochToStringDateTime(createEpoch);
    }
    
    /**
     * Get the value of description. The audit event description
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the value of author. The initiating user that triggered the audit
     *
     * @return the value of author
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Get the value of target. If the authors action interacted with another
     * user the target user is stored here.
     *
     * @return the value of target
     */
    public User getTarget() {
        return target;
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
     * Set the value of createEpoch.
     *
     * @param createEpoch new value of createEpoch
     */
    public void setCreateEpoch(long createEpoch) {
        this.createEpoch = createEpoch;
    }

    /**
     * Set the value of description.
     *
     * @param description new value of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the value of author.
     *
     * @param author new value of author
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * Set the value of target.
     *
     * @param target new value of target
     */
    public void setTarget(User target) {
        this.target = target;
    }

//</editor-fold>
    
    /**
     * Logs an audit event to the persistence layer.
     * This method is asynchronous and will return straight away regardless of 
     * the the operations success.
     * 
     * @param description 
     * @return the persisted version of the audit
     */
    @Asynchronous
    public Future<Audit> log(String description) {
        return log(description, SessionUser.getCurrentUser(), SessionUser.getCurrentUser());
    }

    @Asynchronous
    public Future<Audit> log(String description, User author) {
        return log(description, author, SessionUser.getCurrentUser());
    }

    @Asynchronous
    public Future<Audit> log(String description, User author, User target) {
        createEpoch = new Date().getTime();
        this.description = description;
        this.author = author;
        this.target = target;
        return new AsyncResult<>((Audit) dap.create(this));
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
        if (!(object instanceof Audit)) {
            return false;
        }
        Audit other = (Audit) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}
