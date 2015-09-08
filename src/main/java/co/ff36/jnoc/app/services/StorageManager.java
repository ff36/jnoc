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

package co.ff36.jnoc.app.services;

import co.ff36.jnoc.per.project.JNOC;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
import java.util.List;

/**
 * This class contains methods to facilitate functions relating to file storage.
 * Given the distributed nature of the application it <b>VERY STRONGLY</b>
 * advised that you <b>NEVER</b> store files locally. The nature of a load
 * balanced application means that web servers get constructed and torn down
 * without notice. Furthermore, users response server can change mid sessions
 * meaning that locally stored files are destroyed. This class facilitates CRUD
 * functions for external storage.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (Aug 5, 2013)
 * @author Tarka L'Herpiniere

 */
public interface StorageManager {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    String s3Bucket = System.getenv("JNOC_S3_BUCKET");
//</editor-fold>

    /**
     * Stored objects are treated as Key Value Pairs. The <b>Key</b> is the
     * fully qualified path of the file. ie
     * directory_1/directory_2/file_name.ext. The
     * <b>Value</b> is the actual object itself. This method uses the
     *
     * @param keyType Enum specified in the JNOC
     * @param id The id of the user, company, attachment etc.
     * @return String representation of the objects fully qualified key.
     */
    public String keyGenerator(JNOC.KeyType keyType, String id);

    /**
     * Get a specified object from storage.
     *
     * @param key
     * @return If an object value exists at the given key then the object is
     * returned. be <b>null</b>.
     */
    public S3Object get(String key);

    /**
     * List objects from storage.
     *
     * @param key The highest level key to evaluate the list against. If a key
     * is specified as directory_1/directory_2/ every object that has a key that
     * is a descendent of directory_1/directory_2/ will be listed. This can be
     * interpreted as directory_1/directory_2/*.
     * @return ObjectListing.
     */
    public ObjectListing list(String key);

    /**
     * Write a specified object to storage.
     *
     * @param key the destination key
     * @param file the file to write
     * @param acl permission to allocate to the file
     * @return
     */
    public CompleteMultipartUploadResult put(
            String key,
            File file,
            CannedAccessControlList acl);

    
    /**
     * Copy a specified object in storage from the source to the destination.
     * This method does <b>NOT</b> delete the original object.
     * 
     * @param sourceKey
     * @param destinationKey
     * @param acl determines the resulting objects access permissions
     * @return CopyObjectResult containing copy information. 
     */
    public CopyObjectResult copy(
            String sourceKey,
            String destinationKey,
            CannedAccessControlList acl);

    /**
     * Deletes a specified object from storage.
     *
     * @param keys is a list of object keys
     */
    public void delete(List<String> keys);

}
