/*
 * Created May 14, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.services;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.dastrax.per.project.DTX;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

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
 * @author <tarka@solid.com>
 */
public interface StorageManager {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    String s3Bucket = ResourceBundle.getBundle("config").getString("S3Bucket");
//</editor-fold>

    /**
     * Stored objects are treated as Key Value Pairs. The <b>Key</b> is the
     * fully qualified path of the file. ie
     * directory_1/directory_2/file_name.ext. The
     * <b>Value</b> is the actual object itself. This method uses the
     *
     * @param keyType Enum specified in the DTX
     * @param id The id of the user, company, attachment etc.
     * @return String representation of the objects fully qualified key.
     */
    public String keyGenerator(DTX.KeyType keyType, String id);

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
