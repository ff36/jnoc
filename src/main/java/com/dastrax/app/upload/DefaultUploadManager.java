/*
 * Created May 13, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.upload;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.dastrax.app.services.StorageManager;
import com.dastrax.app.service.internal.DefaultStorageManager;
import com.dastrax.app.service.internal.DefaultURI;
import com.dastrax.per.entity.Attachment;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
import com.dastrax.per.project.DTX.CroppableType;
import com.dastrax.per.project.DTX.URIType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import org.primefaces.event.FileUploadEvent;

/**
 * Methods dedicated to handling file uploads. Specifically the transition to
 * and from storage.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (May 13, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DefaultUploadManager implements UploadManager {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(DefaultUploadManager.class.getName());
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DefaultUploadManager() {
    }
//</editor-fold>

    /**
     * Transitions file uploads into external temporary storage. During the
     * upload a class level UploadFile.class is created storing all the
     * information about the uploaded file.
     *
     * No file type checks are performed in this class. It is expected that the
     * checks are performed at the point of upload.
     *
     * The uploaded file is stored externally and is not executable. Hence if
     * the file contains malicious executable code it cannot be implemented once
     * uploaded.
     *
     * The temporary file will only exist for a maximum of 24 hours before being
     * deleted. To make the file persistent one of the class 'save' methods
     * should be invoked.
     *
     * @param event Primefaces FileUploadEvent
     * @return the upload file containing all the information relating to the
     * uploaded file. You can check the value of 'uploaded' to determine if the
     * upload was successful of whether the operation failed.
     */
    @Override
    public UploadFile upload(FileUploadEvent event) {

        try {
            // Create a storage manager
            StorageManager storage = new DefaultStorageManager();

            // Create a temporary file name
            String temporaryFileName, key;
            temporaryFileName = UUID.randomUUID().toString();
            key = storage.keyGenerator(
                    DTX.KeyType.TEMPORARY_FILE,
                    temporaryFileName);

            // Create a temporary file
            File file = File.createTempFile(temporaryFileName, ".tmp");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(event.getFile().getContents());

            // Upload the file to the storage temporary file directory
            storage.put(key, file, CannedAccessControlList.PublicRead);

            // TODO: This should really check the upload was successfull
            // Create the upload file
            UploadFile uploadFile = new UploadFile();
            uploadFile.getMeta().setNewName(temporaryFileName);
            uploadFile.getMeta().setOriginalName(event.getFile().getFileName());
            uploadFile.getMeta().setUri(
                    new DefaultURI.Builder(URIType.TEMPORARY_FILE)
                    .withFile(temporaryFileName)
                    .create()
                    .generate());
            uploadFile.getMeta().setS3Key(
                    storage.keyGenerator(
                            DTX.KeyType.TEMPORARY_FILE,
                            temporaryFileName));
            uploadFile.getMeta().setContentType(event.getFile().getContentType());
            uploadFile.getMeta().setSize(event.getFile().getSize());

            if (event.getFile().getContentType().split("/")[0].equals("image")) {
                BufferedImage bi = ImageIO.read(event.getFile().getInputstream());
                uploadFile.getImage().setHeight(bi.getHeight());
                uploadFile.getImage().setWidth(bi.getWidth());
            }

            uploadFile.setUploaded(true);
            return uploadFile;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        // If the operation fails return a new empty upload file
        return new UploadFile();
    }

    /**
     * Uploaded images stored in the temporary storage directory can offer the
     * option to be cropped. Implementing a crop will overwrite the original
     * uploaded file with a new temporary file.
     *
     * The temporary file will only exist for a maximum of 24 hours before being
     * deleted. To make the file persistent one of the class 'save' methods
     * should be invoked.
     *
     * @param uploadFile
     * @param type Image type to be cropped. This will determine the cropped
     * image dimensions.
     */
    @Override
    public void crop(UploadFile uploadFile, CroppableType type) {
        try {
            StorageManager storage = new DefaultStorageManager();
            S3Object object = storage.get(uploadFile.getMeta().getS3Key());
            S3ObjectInputStream objectData = object.getObjectContent();

            BufferedImage cropped = ImageIO.read(objectData)
                    .getSubimage(
                            uploadFile.getImage().getImageCrop().getX(),
                            uploadFile.getImage().getImageCrop().getY(),
                            uploadFile.getImage().getImageCrop().getWidth(),
                            uploadFile.getImage().getImageCrop().getHeight());

            // Crop the image with respect to the type
            switch (type) {
                case USER_PROFILE_IMAGE:
                    cropped = Thumbnails.of(cropped).size(100, 100).asBufferedImage();
                    break;
                case COMPANY_LOGO:
                    int xValue = (100 / uploadFile.getImage().getImageCrop().getY())
                            * uploadFile.getImage().getImageCrop().getX();
                    cropped = Thumbnails.of(cropped).size(xValue, 100).asBufferedImage();
                    break;

            }

            // Create a temporary file
            File file = File.createTempFile(uploadFile.getMeta().getNewName(), ".tmp");
            ImageIO.write(cropped, null, file);

            // Upload the file to the storage temporary file directory
            String key = storage.keyGenerator(
                    DTX.KeyType.TEMPORARY_FILE,
                    uploadFile.getMeta().getNewName());
            storage.put(key, file, CannedAccessControlList.PublicRead);

            objectData.close();

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Saving a file transitions it from its temporary location (where it can
     * only survive a maximum of 24 hours before being automatically deleted) to
     * its permanent persist location.
     *
     * @param uploadFile
     * @param owner
     * @return whether the operation was successful or not.
     */
    @Override
    public boolean save(UploadFile uploadFile, Object owner) {

        StorageManager storage = new DefaultStorageManager();
        String source, destination = null;

        // Copy the file from the storage temporary file directory
        source = uploadFile.getMeta().getS3Key();

        switch (uploadFile.getType()) {
            case PROFILE_IMAGE:
                User user = (User) owner;
                destination = storage.keyGenerator(
                        DTX.KeyType.USER_PROFILE_IMAGE,
                        user.getId().toString());
                break;
            case COMPANY_LOGO:
                Company company = (Company) owner;
                destination = storage.keyGenerator(
                        DTX.KeyType.COMPANY_LOGO,
                        company.getId().toString());
                break;
            case ATTACHMENT:
                Attachment attachment = (Attachment) owner;
                destination = storage.keyGenerator(
                        DTX.KeyType.TICKET_ATTACHEMENT,
                        attachment.getS3id());
                break;
        }
        try {
            storage.copy(source, destination, CannedAccessControlList.PublicRead);
            return true;
        } catch (NullPointerException npe) {
            // Either source or destination where null
            return false;
        }
        // TODO: Should actively delete the file from the temp directory
    }

}
