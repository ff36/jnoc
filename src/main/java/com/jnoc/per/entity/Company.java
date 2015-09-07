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


/*
 * Created Jul 10, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.per.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.extensions.event.ImageAreaSelectEvent;
import org.primefaces.model.DualListModel;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.jnoc.app.misc.JsfUtil;
import com.jnoc.app.service.internal.DefaultStorageManager;
import com.jnoc.app.services.StorageManager;
import com.jnoc.app.upload.DefaultUploadManager;
import com.jnoc.app.upload.UploadFile;
import com.jnoc.app.upload.UploadManager;
import com.jnoc.per.dap.CrudService;
import com.jnoc.per.dap.QueryParameter;
import com.jnoc.per.project.JNOC;
import com.jnoc.per.project.JNOC.CompanyType;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Account holds information pertaining to User accounts.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Company.findAll", query = "SELECT e FROM Company e"),
    @NamedQuery(name = "Company.findByID", query = "SELECT e FROM Company e WHERE e.id = :id"),
    @NamedQuery(name = "Company.findByType", query = "SELECT e FROM Company e WHERE e.type = :type")
})
@Entity
public class Company implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Company.class.getName());
    private static final long serialVersionUID = 1L;

    @Version
    private int version;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String s3id;
    private String name;
    @Enumerated(EnumType.STRING)
    private CompanyType type;
    @OneToOne(cascade = {CascadeType.ALL})
    private Contact contact;
    @OneToMany
    private List<DAS> das;
    // VAR have children CLIENT
    @OneToMany
    private List<Company> clients;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
    @Transient
    private DualListModel<DAS> linkedAndAvailableDas;
    @Transient
    private DualListModel<Company> linkedAndAvailableClientCompanies;
    @Transient
    private UploadFile uploadFile;
    @Transient
    private Company newParent;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Company() {
        this.clients = new ArrayList<>();
        this.das = new ArrayList<>();
        this.contact = new Contact();
        this.uploadFile = new UploadFile();

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
     * Get the value of version
     *
     * @return the value of version
     */
    public int getVersion() {
        return version;
    }

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
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of contact
     *
     * @return the value of contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Get the value of das
     *
     * @return the value of das
     */
    public List<DAS> getDas() {
        return das;
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public CompanyType getType() {
        return type;
    }

    /**
     * Get the value of clients
     *
     * @return the value of clients
     */
    public List<Company> getClients() {
        return clients;
    }

    /**
     * Get the value of linkedAndAvailableDas
     *
     * @return the value of linkedAndAvailableDas
     */
    public DualListModel<DAS> getLinkedAndAvailableDas() {
        return linkedAndAvailableDas;
    }

    /**
     * Get the value of linkedAndAvailableClientCompanies
     *
     * @return the value of linkedAndAvailableClientCompanies
     */
    public DualListModel<Company> getLinkedAndAvailableClientCompanies() {
        return linkedAndAvailableClientCompanies;
    }

    /**
     * Get the value of uploadFile
     *
     * @return the value of uploadFile
     */
    public UploadFile getUploadFile() {
        return uploadFile;
    }

    /**
     * Get the value of newParent. Companies being created of type Client will
     * set this during registration.
     *
     * @return the value of newParent
     */
    public Company getNewParent() {
        return newParent;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of version. Auto incremented persistence record version
     *
     * @param version new value of version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Set the value of id. Auto incremented persistence record id
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the value of s3id.
     *
     * @param s3id new value of s3id
     */
    public void setS3id(String s3id) {
        this.s3id = s3id;
    }

    /**
     * Set the value of name.
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the value of contact. CascadeType.ALL
     *
     * @param contact new value of contact
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * Set the value of das.
     *
     * @param das new value of das
     */
    public void setSites(List<DAS> das) {
        this.das = das;
    }

    /**
     * Set the value of type.
     *
     * @param type new value of type
     */
    public void setType(CompanyType type) {
        this.type = type;
    }

    /**
     * Set the value of clients.
     *
     * @param clients new value of clients
     */
    public void setClients(List<Company> clients) {
        this.clients = clients;
    }

    /**
     * Set the value of linkedAndAvailableDas.
     *
     * @param linkedAndAvailableDas new value of linkedAndAvailableDas
     */
    public void setLinkedAndAvailableDas(DualListModel<DAS> linkedAndAvailableDas) {
        this.linkedAndAvailableDas = linkedAndAvailableDas;
    }

    /**
     * Set the value of linkedAndAvailableClientCompanies.
     *
     * @param linkedAndAvailableClientCompanies new value of
     * linkedAndAvailableClientCompanies
     */
    public void setLinkedAndAvailableClientCompanies(DualListModel<Company> linkedAndAvailableClientCompanies) {
        this.linkedAndAvailableClientCompanies = linkedAndAvailableClientCompanies;
    }

    /**
     * Set the value of newParent. Companies being created of type Client will
     * set this during registration.
     *
     * @param newParent new value of newParent
     */
    public void setNewParent(Company newParent) {
        this.newParent = newParent;
    }

//</editor-fold>
    
    /**
     * Loads linkedAndAvailableDas and linkedAndAvailableClientCompanies data
     * from the persistence layer and sets the class level properties. This
     * information is generally not needed so it is not loaded by default.
     */
    public void lazyLoad() {
        linkedAndAvailableDas = linkedAndAvailableDas();
        if (JNOC.CompanyType.VAR.equals(type)) {
            linkedAndAvailableClientCompanies = linkedAndAvailableClientCompanies();
        }
    }

    /**
     * Creates a new company, adds it to the persistence layer and adds storage
     * related resources.
     *
     */
    public void create() {

        try {
            das = linkedAndAvailableDas.getTarget();
        } catch (NullPointerException npe) {
            // Do nothing. The linkedAndAvailableDas was null
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
        }

        if (JNOC.CompanyType.VAR.equals(type)) {
            try {
                clients = linkedAndAvailableClientCompanies.getTarget();
            } catch (NullPointerException npe) {
            	LOG.log(Level.CONFIG, npe.getMessage(), npe);
                // Do nothing. The linkedAndAvailableClientCompanies was null
            }

        }

        // Set the new storage ID
        this.s3id = UUID.randomUUID().toString();

        // Persist the new company
        Company newCompany = (Company) dap.create(this);

        // If its a company of type client add it to the parent
        if (JNOC.CompanyType.CLIENT.equals(type)) {
            try {
                Company parent = (Company) dap.find(
                        Company.class,
                        newParent.getId());
                parent.clients.add(newCompany);
                parent.update(false, false);
            } catch (NullPointerException npe) {
            	LOG.log(Level.CONFIG, npe.getMessage(), npe);
                // Do nothing. The parent company is null
            }

        }

        if (uploadFile.isUploaded()) {
            // Save the logo
            saveLogo();
        }
        
        JsfUtil.addSuccessMessage(newCompany.getName() + " has been created.");

    }

    /**
     * Removes the company from the persistence layer and any associated
     * resources linked to the company.
     */
    public void delete() {

        // List and remove everything from the storage
        StorageManager storage = new DefaultStorageManager();
        String key = storage.keyGenerator(
                JNOC.KeyType.COMPANY_DIRECTORY, id.toString());
        List<String> keys = new ArrayList<>();
        for (S3ObjectSummary summary : storage.list(key).getObjectSummaries()) {
            keys.add(summary.getKey());
        }
        storage.delete(keys);

        // Find any users associated with the company and remove the association
        List<User> users = dap.findWithNamedQuery(
                "User.findByCompany",
                QueryParameter.with("id", id).parameters());
        for (User user : users) {
            user.setCompany(null);
            dap.update(user);
        }

        // If its a Client remove its association with the parent VAR
        if (JNOC.CompanyType.CLIENT.equals(type)) {
            try {
                Company parent = parent();
                parent.getClients().remove(this);
                parent.update(false, false);
            } catch (NullPointerException npe) {
                // Do nothing. the parent company is null
            	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            }
        }

        // Delete the actual company from the persistence layer
        dap.delete(Company.class, id);
    }

    /**
     * Update the persistence layer with a new version of the company
     *
     * @param updateDAS Should be true if the company DAS has been modified
     * @param updateClients Should be set to true if the company clients has
     * been modified.
     */
    public void update(boolean updateDAS, boolean updateClients) {
        if (updateDAS) {
            das = linkedAndAvailableDas.getTarget();
        }
        if (updateClients) {
            if (JNOC.CompanyType.VAR.equals(type)) {
                clients = linkedAndAvailableClientCompanies.getTarget();
            }
        }
        Company updatedCompany = (Company) dap.update(this);
        this.version = updatedCompany.getVersion();
    }

    /**
     * Determines das that are available and linked to a company.
     *
     * @return A Primefaces DualListModel with available sites as the Source
     * List and the Linked sites as the Target List.
     */
    private DualListModel<DAS> linkedAndAvailableDas() {

        DualListModel<DAS> linkedAvailableDas = new DualListModel<>();

        /*
         If the company is of type VAR we need to set the Available
         Sites List (Source) as Sites that have no other linked association.
         The Linked Sites List (Target) should be Sites that are already
         linked to the company. (ie. Company.Sites)
         */
        if (JNOC.CompanyType.VAR.equals(type)) {

            // Only companies hold associations so get all companies
            List<Company> companies
                    = (List<Company>) dap.findWithNamedQuery("Company.findAll");

            List<DAS> linkedSites = new ArrayList<>();
            for (Company c : companies) {
                linkedSites.addAll(c.getDas());
            }

            // Get all the das, cross reference and remove linked ones
            List<DAS> allSites
                    = (List<DAS>) dap.findWithNamedQuery("DAS.findAll");
            allSites.removeAll(linkedSites);

            linkedAvailableDas.setSource(allSites);
            linkedAvailableDas.setTarget(das);
        }

        /*
         If the company is of type CLIENT we need to set the Available
         Sites List (Source) as Sites that are associated with the parent
         VAR but are not associated with any other CLIENT. The Linked 
         Sites List (Target) should be Sites that are already
         linked to the company. (ie. Company.Sites)
         */
        if (JNOC.CompanyType.CLIENT.equals(type)) {

            List<DAS> parentSites = new ArrayList<>();

            // Find the parent company
            Company parent = parent();
            try {
                parentSites = new ArrayList<>(parent.getDas());

                // Which of the parent sites are already linked
                List<DAS> linkedSites = new ArrayList<>();
                for (Company client : parent.getClients()) {
                    linkedSites.addAll(client.getDas());
                }

                // Cross reference and remove linked ones
                parentSites.removeAll(linkedSites);

            } catch (NullPointerException npe) {
                // Do nothing. The Das was null
            	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            }

            linkedAvailableDas.setSource(parentSites);
            linkedAvailableDas.setTarget(das);

        }

        return linkedAvailableDas;
    }

    /**
     * Determines companies of type CLIENT that are available and linked to a
     * company of type VAR.
     *
     * @return A Primefaces DualListModel with available sites as the Source
     * List and the Linked sites as the Target List.
     */
    private DualListModel<Company> linkedAndAvailableClientCompanies() {

        DualListModel<Company> linkedAvailableClientCompanies = new DualListModel<>();

        /*
         The Available Company List (Source) are companies of type CLIENT
         that do not have a parent company of type VAR. The Linked 
         Company List (Target) should be Companies that are already
         linked to the company. (ie. Company.Sites)
         */
        List<Company> vars
                = (List<Company>) dap.findWithNamedQuery(
                        "Company.findByType",
                        QueryParameter
                        .with("type", JNOC.CompanyType.VAR)
                        .parameters()
                );

        List<Company> linkedCompanies = new ArrayList<>();

        for (Company var : vars) {
            linkedCompanies.addAll(var.getClients());
        }

        List<Company> clientz
                = (List<Company>) dap.findWithNamedQuery(
                        "Company.findByType",
                        QueryParameter
                        .with("type", JNOC.CompanyType.CLIENT)
                        .parameters()
                );

        // Cross reference and remove linked companies
        clientz.removeAll(linkedCompanies);

        linkedAvailableClientCompanies.setSource(clientz);
        linkedAvailableClientCompanies.setTarget(clients);

        return linkedAvailableClientCompanies;
    }

    /**
     * Retrieves a client company parent company (VAR -> CLIENT)
     *
     * @return If the supplied company has a parent company (VAR) the parent
     * company is returned. Otherwise null
     */
    public Company parent() {
        if (JNOC.CompanyType.CLIENT.equals(type)) {

            // If the company is being created we want to grab it from the gui
            if (this.id == null) {
                return this.newParent;
            }
            
            // Only companies hold associations so get all companies
            List<Company> companies
                    = (List<Company>) dap.findWithNamedQuery(
                            "Company.findByType",
                            QueryParameter
                            .with("type", JNOC.CompanyType.VAR)
                            .parameters()
                    );

            for (Company c : companies) {
                // Get the VAR company CLIENT companies
                for (Company client : c.getClients()) {
                    // Does the current company match any of the CLIENT
                    if (client.equals(this)) {
                        // If it matches return the VAR type company
                        return c;
                    }
                }
            }

        } else {
            // Companies of type VAR do not have parents
            return null;
        }

        // No parent was found 
        return null;
    }

    /**
     * Uploads the company logo to the default storage.
     *
     * @param event
     */
    public void uploadLogo(FileUploadEvent event) {

        UploadManager uploader = new DefaultUploadManager();
        uploadFile = uploader.upload(event);
        uploadFile.setType(JNOC.UploadType.COMPANY_LOGO);
        uploadFile.isUploaded();

    }

    /**
     * Saves the logo from its temporary location to its permanent storage
     * location.
     */
    public void saveLogo() {
        UploadManager uploader = new DefaultUploadManager();
        if (uploadFile.isUploaded()) {
            uploader.save(uploadFile, this);
        }
    }

    /**
     * Uploaded images stored in the temporary storage directory can offer the
     * option to be cropped. This listener method determines the coordinates and
     * dimensions of the area to be cropped in relation to the original image.
     *
     * @param event a Primefaces ImageAreaSelectEvent
     */
    public void cropListener(ImageAreaSelectEvent event) {
        uploadFile.getImage().getImageCrop().setX(event.getX1());
        uploadFile.getImage().getImageCrop().setY(event.getY1());
        uploadFile.getImage().getImageCrop().setWidth(event.getWidth());
        uploadFile.getImage().getImageCrop().setHeight(event.getHeight());
    }

    /**
     * Saves the newly cropped version user logo over the original in the
     * temporary storage location.
     */
    public void cropLogo() {
        UploadManager uploader = new DefaultUploadManager();
        uploader.crop(uploadFile, JNOC.CroppableType.COMPANY_LOGO);
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
        if (!(object instanceof Company)) {
            return false;
        }
        Company other = (Company) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}
