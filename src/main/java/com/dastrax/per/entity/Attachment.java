/*
 * Created Jul 12, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.entity;

import com.dastrax.app.service.internal.DefaultStorageManager;
import com.dastrax.app.service.internal.DefaultURI;
import com.dastrax.app.services.StorageManager;
import com.dastrax.app.upload.DefaultUploadManager;
import com.dastrax.app.upload.UploadFile;
import com.dastrax.app.upload.UploadManager;
import com.dastrax.per.project.DTX;
import com.dastrax.service.navigation.Navigator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import org.primefaces.event.FileUploadEvent;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * Attachment holds information pertaining to uploaded file attachments.
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 12, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@NamedQueries({
    @NamedQuery(name = "Attachment.findAll", query = "SELECT e FROM Attachment e"),
    @NamedQuery(name = "Attachment.findByID", query = "SELECT e FROM Attachment e WHERE e.id = :id"),})
@Entity
public class Attachment implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String s3id;
    private String title;
    private String mime;
    private Long uploadEpoch;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private UploadFile uploadFile;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CDI">
    @Transient
    @Inject
    private Navigator navigator;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Attachment() {
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
     * Get the value of s3id. The unique storage id
     *
     * @return the value of s3id
     */
    public String getS3id() {
        return s3id;
    }

    /**
     * Get the value of title. This is the displayed name of the file
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the value of mime.
     *
     * @return the value of mime
     */
    public String getMime() {
        return mime;
    }

    /**
     * Get the value of uploadEpoch
     *
     * @return the value of uploadEpoch
     */
    public Long getUploadEpoch() {
        return uploadEpoch;
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
     * Set the value of s3id.
     *
     * @param s3id new value of s3id
     */
    public void setS3id(String s3id) {
        this.s3id = s3id;
    }

    /**
     * Set the value of title.
     *
     * @param title new value of title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the value of mime.
     *
     * @param mime new value of mime
     */
    public void setMime(String mime) {
        this.mime = mime;
    }

    /**
     * Set the value of uploadEpoch.
     *
     * @param uploadEpoch new value of uploadEpoch
     */
    public void setUploadEpoch(Long uploadEpoch) {
        this.uploadEpoch = uploadEpoch;
    }
//</editor-fold>

    /**
     * Uploads the file to the default storage and sets the attachment
     * properties based on the uploaded file.
     *
     * @param event
     */
    public void upload(FileUploadEvent event) {

        UploadManager uploader = new DefaultUploadManager();
        uploadFile = uploader.upload(event);
        uploadFile.setType(DTX.UploadType.ATTACHMENT);

        // If the upload was successfull then set the properties
        if (uploadFile.isUploaded()) {
            // Set the properties based on the uploaded file
            s3id = uploadFile.getMeta().getNewName();
            mime = uploadFile.getMeta().getContentType();
            title = uploadFile.getMeta().getOriginalName();
            uploadEpoch = Calendar.getInstance().getTimeInMillis();
        }

    }

    /**
     * Saves the attachment from its temporary location to its permanent
     * storage location.
     */
    public void save() {
        UploadManager uploader = new DefaultUploadManager();
        uploader.save(uploadFile, this);
    }

    /**
     * Removes an attachment from storage.
     */
    public void remove() {
        // Generate the S3 Key
        StorageManager storage = new DefaultStorageManager();
        List<String> keys = new ArrayList<>();
        keys.add(storage.keyGenerator(DTX.KeyType.TICKET_ATTACHEMENT, s3id));
        storage.delete(keys);
    }

    /**
     * Redirects subjects to view the attachment in the document editor
     *
     */
    public void viewAttachment() {
        navigator.navigate("DOCUMENT_WITH_PARAM", "document=" + id);
    }

    /**
     * Generates the path to the file type icon that matches the files mime type.
     * @param viaCDN
     * @return The URI path (either via the CDN or directly) to the icon that
     * matches the file type.
     */
    public String fileTypeIcon(boolean viaCDN) {
        return new DefaultURI.Builder(DTX.URIType.FILE_TYPE_ICON)
                .withMime(mime)
                .withViaCDN(viaCDN)
                .create()
                .generate();
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Attachment)) {
            return false;
        }
        Attachment other = (Attachment) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}