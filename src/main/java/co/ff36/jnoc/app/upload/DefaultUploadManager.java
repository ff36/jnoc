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

package co.ff36.jnoc.app.upload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;

import co.ff36.jnoc.app.service.internal.DefaultStorageManager;
import co.ff36.jnoc.app.service.internal.DefaultURI;
import co.ff36.jnoc.app.services.StorageManager;
import co.ff36.jnoc.per.entity.Attachment;
import co.ff36.jnoc.per.entity.Company;
import co.ff36.jnoc.per.entity.User;
import co.ff36.jnoc.per.project.JNOC;
import co.ff36.jnoc.per.project.JNOC.CroppableType;
import co.ff36.jnoc.per.project.JNOC.URIType;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Methods dedicated to handling file uploads. Specifically the transition to
 * and from storage.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (May 13, 2014)
 * @author Tarka L'Herpiniere

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
                    JNOC.KeyType.TEMPORARY_FILE,
                    temporaryFileName);

            // Create a temporary file
            File file = File.createTempFile(temporaryFileName, ".tmp");
            FileOutputStream fos = new FileOutputStream(file);
            InputStream inputstream = event.getFile().getInputstream();
            fos.write(IOUtils.toByteArray(inputstream));

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
                            JNOC.KeyType.TEMPORARY_FILE,
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
     * Transitions email attachments file into external temporary storage.
     * During the upload a class level UploadFile.class is created storing all
     * the information about the uploaded file.
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
     * @param part email body part
     * @return An UploadFile containing information about the uploaded file.
     */
    @Override
    public UploadFile upload(MimeBodyPart part) {

        try {
            // Create a storage manager
            StorageManager storage = new DefaultStorageManager();

            // Create a temporary file name
            String temporaryFileName, key;
            temporaryFileName = UUID.randomUUID().toString();
            key = storage.keyGenerator(
                    JNOC.KeyType.TEMPORARY_FILE,
                    temporaryFileName);

            // Create a temporary file
            File file = File.createTempFile(temporaryFileName, ".tmp");
            FileOutputStream fos = new FileOutputStream(file);
            InputStream inputstream = part.getInputStream();
            fos.write(IOUtils.toByteArray(inputstream));

            // Upload the file to the storage temporary file directory
            storage.put(key, file, CannedAccessControlList.PublicRead);

            // TODO: This should really check the upload was successfull
            // Create the upload file
            UploadFile uploadFile = new UploadFile();
            uploadFile.getMeta().setNewName(temporaryFileName);
            uploadFile.getMeta().setOriginalName(part.getFileName());
            uploadFile.getMeta().setUri(
                    new DefaultURI.Builder(URIType.TEMPORARY_FILE)
                    .withFile(temporaryFileName)
                    .create()
                    .generate());
            uploadFile.getMeta().setS3Key(
                    storage.keyGenerator(
                            JNOC.KeyType.TEMPORARY_FILE,
                            temporaryFileName));
            String mime = StringUtils.substringBefore(part.getContentType(), ";");
            uploadFile.getMeta().setContentType(mime.toLowerCase());
            uploadFile.getMeta().setSize(part.getSize());

            uploadFile.setUploaded(true);
            return uploadFile;
        } catch (IOException | MessagingException ex) {
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
            // TODO This should work with try-with-resource but doesn't!
            S3ObjectInputStream objectData = object.getObjectContent();

            /*
             Crop the scaled image.
             600 is the display width set in the view for the crop.
             The lose of presision working in int for upscaled images causes
             an out of range exception if working close to the right or bottom
             of the image so we need to work in doubles and by casting back to
             int rounds down.
             */
            double scale, x, y, w, h;
            scale = (double) 600 / (double) uploadFile.getImage().getWidth();
            x = uploadFile.getImage().getImageCrop().getX() / scale;
            y = uploadFile.getImage().getImageCrop().getY() / scale;
            w = uploadFile.getImage().getImageCrop().getWidth() / scale;
            h = uploadFile.getImage().getImageCrop().getHeight() / scale;

            BufferedImage cropped = ImageIO.read(objectData)
                    .getSubimage(
                            (int) x,
                            (int) y,
                            (int) w,
                            (int) h);

            // Crop the image with respect to the type
            switch (type) {
                case USER_PROFILE_IMAGE:
                    cropped = Thumbnails.of(cropped).size(100, 100)
                            .asBufferedImage();
                    break;
                case COMPANY_LOGO:
                    int xValue = (100
                            / uploadFile.getImage().getImageCrop().getY())
                            * uploadFile.getImage().getImageCrop().getX();
                    cropped = Thumbnails.of(cropped).size(xValue, 100)
                            .asBufferedImage();
                    break;

            }

            // Create a temporary file
            File file = File.createTempFile(
                    uploadFile.getMeta().getNewName(), ".tmp");
            ImageIO.write(
                    cropped,
                    uploadFile.getMeta().getContentType().split("/")[1],
                    file);

            // Upload the file to the storage temporary file directory
            String key = storage.keyGenerator(
                    JNOC.KeyType.TEMPORARY_FILE,
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
                        JNOC.KeyType.USER_PROFILE_IMAGE,
                        user.getId().toString());
                break;
            case COMPANY_LOGO:
                Company company = (Company) owner;
                destination = storage.keyGenerator(
                        JNOC.KeyType.COMPANY_LOGO,
                        company.getId().toString());
                break;
            case ATTACHMENT:
                Attachment attachment = (Attachment) owner;
                destination = storage.keyGenerator(
                        JNOC.KeyType.TICKET_ATTACHEMENT,
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
