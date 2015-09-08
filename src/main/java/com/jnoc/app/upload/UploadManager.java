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

package com.jnoc.app.upload;

import com.jnoc.per.project.JNOC;
import javax.mail.internet.MimeBodyPart;
import org.primefaces.event.FileUploadEvent;

/**
 * Methods dedicated to handling file uploads. Specifically the transition to
 * and from storage.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (May 15, 2014)
 * @author Tarka L'Herpiniere

 */
public interface UploadManager {
    
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
     * @return An UploadFile containing information about the uploaded file.
     */
    public UploadFile upload(FileUploadEvent event);
    
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
    public UploadFile upload(MimeBodyPart part);
    
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
    public void crop(UploadFile uploadFile, JNOC.CroppableType type);
    
    /**
     * Saving an file transitions it from its temporary location (where it can
     * only survive a maximum of 24 hours before being automatically deleted) to
     * its permanent persist location. 
     * 
     * @param uploadFile
     * @param owner
     * @return whether the operation was successful or not.
     */
    public boolean save(UploadFile uploadFile, Object owner);

}
