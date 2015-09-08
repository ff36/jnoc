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

import java.io.IOException;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.markdown4j.Markdown4jProcessor;
import org.primefaces.event.TabChangeEvent;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Comment holds information pertaining to support comments.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 11, 2013)
 * @author Tarka L'Herpiniere

 */
@NamedQueries({
    @NamedQuery(name = "Comment.findAll", query = "SELECT e FROM Comment e"),
    @NamedQuery(name = "Comment.findByID", query = "SELECT e FROM Comment e WHERE e.id = :id"),})
@Entity
public class Comment implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Comment.class.getName());
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long createEpoch;
    @ManyToOne
    private User commenter;
    @Column(length = 8000)
    private String comment;
    @ManyToOne
    private Nexus nexus;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
    @Transient
    private String markdown;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Comment() {
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
    public Long getCreateEpoch() {
        return createEpoch;
    }

    /**
     * Get the value of createEpoch as a time stamp
     *
     * @return the value of createEpoch as a time stamp
     */
    public String getCreateTimeStamp() {
        return TemporalUtil.epochToStringDateTime(createEpoch);
    }

    /**
     * Get the value of comment
     *
     * @return the value of comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Get the value of nexus
     *
     * @return the value of nexus
     */
    public Nexus getNexus() {
        return nexus;
    }

    /**
     * Get the value of commenter
     *
     * @return the value of commenter
     */
    public User getCommenter() {
        return commenter;
    }

    /**
     * Get the value of markdown
     *
     * @return the value of markdown
     */
    public String getMarkdown() {
        return markdown;
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
    public void setCreateEpoch(Long createEpoch) {
        this.createEpoch = createEpoch;
    }

    /**
     * Set the value of comment.
     *
     * @param comment new value of comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Set the value of nexus.
     *
     * @param nexus new value of nexus
     */
    public void setNexus(Nexus nexus) {
        this.nexus = nexus;
    }

    /**
     * Set the value of commenter.
     *
     * @param commenter new value of commenter
     */
    public void setCommenter(User commenter) {
        this.commenter = commenter;
    }

//</editor-fold>
    
    /**
     * Comments can have their nexus modified
     *
     * @param newNexus
     */
    public void changeNexus(Long newNexus) {
        if (newNexus != null) {
            nexus = (Nexus) dap.find(Nexus.class, newNexus);
        } else {
            nexus = null;
        }
        

    }

    /**
     * Update the persistence layer with a new version of the comment.
     */
    public void update() {
        dap.update(this);
    }

    /**
     * Renders the markdown for a preview.
     *
     * @param event
     */
    public void previewListener(TabChangeEvent event) {
        if ("Preview".equals(event.getTab().getTitle())) {
            parse();
        }
    }

    /**
     * Renders the comment markdown.
     * 
     * @return The comment parsed as markdown 
     */
    public String parse() {
        try {
            markdown = new Markdown4jProcessor().process(comment);
            return markdown;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
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
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}
