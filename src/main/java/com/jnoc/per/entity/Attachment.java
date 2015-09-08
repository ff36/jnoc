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

package com.jnoc.per.entity;

import com.jnoc.app.service.internal.DefaultStorageManager;
import com.jnoc.app.service.internal.DefaultURI;
import com.jnoc.app.services.StorageManager;
import com.jnoc.app.upload.DefaultUploadManager;
import com.jnoc.app.upload.UploadFile;
import com.jnoc.app.upload.UploadManager;
import com.jnoc.per.project.JNOC;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.internet.MimeBodyPart;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import org.apache.commons.io.IOUtils;
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
        uploadFile.setType(JNOC.UploadType.ATTACHMENT);

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
     * Uploads the file to the default storage and sets the attachment
     * properties based on the uploaded file.
     *
     * @param part
     */
    public void uploadEmailAttachment(MimeBodyPart part) {

        UploadManager uploader = new DefaultUploadManager();
        uploadFile = uploader.upload(part);
        uploadFile.setType(JNOC.UploadType.ATTACHMENT);

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
     * Saves the attachment from its temporary location to its permanent storage
     * location.
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
        keys.add(storage.keyGenerator(JNOC.KeyType.TICKET_ATTACHEMENT, s3id));
        storage.delete(keys);
    }

    /**
     * Removes an attachment from storage.
     *
     * @throws java.io.IOException
     */
    public void download() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
        ec.setResponseContentType(this.mime); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
        //ec.setResponseContentLength(contentLength); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + this.title + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

// Generate the S3 Key
        StorageManager storage = new DefaultStorageManager();
        InputStream input = storage.get(
                storage.keyGenerator(
                        JNOC.KeyType.TICKET_ATTACHEMENT, s3id))
                .getObjectContent();

        OutputStream output = ec.getResponseOutputStream();
        IOUtils.copy(input, output);

        fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.

        //return new DefaultStreamedContent(objectContent, this.mime, this.title);
    }

    /**
     * Generates the path to the file type icon that matches the files mime
     * type.
     *
     * @param viaCDN
     * @return The URI path (either via the CDN or directly) to the icon that
     * matches the file type.
     */
    public String fileTypeIcon(boolean viaCDN) {
        return new DefaultURI.Builder(JNOC.URIType.FILE_TYPE_ICON)
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
