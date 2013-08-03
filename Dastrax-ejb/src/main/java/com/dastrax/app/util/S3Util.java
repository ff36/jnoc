/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.app.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.dastrax.app.pojo.Response;
import com.dastrax.per.project.DastraxCst.S3ContentType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;

/**
 *
 * @version Build 2.0.0
 * @since Jul 31, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class S3Util {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(S3Util.class.getName());

    // Variables----------------------------------------------------------------
    S3Client s3Client = new S3Client();

    // Project Stage------------------------------------------------------------
    String s3Bucket = ResourceBundle.getBundle("Config").getString("S3Bucket");

    // Constructor--------------------------------------------------------------
    public S3Util() {
    }

    // Methods------------------------------------------------------------------
    /**
     * Retrieves a single object from S3 with the specified Key name
     *
     * @param s3Key
     * @return the object as an InputStream if one exists. Otherwise null.
     */
    public InputStream getObject(String s3Key) {

        InputStream objectData = null;

        try {
            S3Object object = s3Client.client.getObject(
                    new GetObjectRequest(s3Bucket, s3Key));
            objectData = object.getObjectContent();
        } catch (AmazonServiceException ase) {
            LOG.log(Level.SEVERE, "Error response from AWS while retreiving object", ase);
        } catch (AmazonClientException ace) {
            LOG.log(Level.SEVERE, "Error response from AWS while retreiving object", ace);
        }
        return objectData;
    }

    /**
     * listS3Objects will return all the objects listed in a bucket that have
     * the specified prefix.
     *
     * @param prefix
     * @return ArrayList<KeyVersion>
     */
    public ArrayList<DeleteObjectsRequest.KeyVersion> listObjects(String prefix) {

        ObjectListing objectListing;
        List<DeleteObjectsRequest.KeyVersion> justKeys = new ArrayList<>();

        try {
            ListObjectsRequest listObjectRequest
                    = new ListObjectsRequest().withBucketName(s3Bucket)
                    .withPrefix(prefix);

            objectListing = s3Client.client.listObjects(listObjectRequest);

            for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
                justKeys.add(new DeleteObjectsRequest.KeyVersion(s3ObjectSummary.getKey()));
            }

        } catch (AmazonServiceException ase) {
            LOG.log(Level.SEVERE, "Error response from AWS while listing object", ase);
        } catch (AmazonClientException ace) {
            LOG.log(Level.SEVERE, "Error response from AWS while retreiving object", ace);
        }
        return (ArrayList) justKeys;
    }

    /**
     * Places a single item from an input stream to S3 at the specified
     * endpoint. The file will have private access. ie. anybody with the URL can
     * access the file.
     *
     * @param s3Key
     * @param inputStream
     * @param metadata
     * @param acl (CannedAccessControlList.Private)
     * @return
     * @throws java.lang.Exception
     */
    public PutObjectResult putObject(
            String s3Key,
            InputStream inputStream,
            ObjectMetadata metadata,
            CannedAccessControlList acl) throws Exception {

        PutObjectResult result = null;
        /*
         * Create an access control list ACL with canned permissions for the
         * uploaded file and put the object in S3
         */
        try {
            result = s3Client.client.putObject(new PutObjectRequest(
                    s3Bucket,
                    s3Key,
                    inputStream,
                    metadata).withCannedAcl(acl));

            // Create an audit log of the event
            //AuditDAO audit = (AuditDAO) InitialContext.doLookup(JNDI_AUD);
            //audit.create("Object placed on S3 (Key: " + s3Bucket + s3Key + ")");
        } catch (AmazonServiceException ase) {
            LOG.log(Level.SEVERE, "Error response from AWS while putting object", ase);
            throw ase;
        } catch (AmazonClientException ace) {
            LOG.log(Level.SEVERE, "Error response from AWS while listing object", ace);
            throw ace;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Unknown Error whilst putting object to S3", ex);
                }
            }
        }
        return result;
    }

    /**
     * Handles deleting single Objects from Non-Versioned Buckets.
     *
     * @param keyName
     * @throws java.lang.Exception
     */
    public void deleteObject(String keyName) throws Exception {

        try {
            s3Client.client.deleteObject(new DeleteObjectRequest(s3Bucket, keyName));
            // Create an audit log of the event
            //AuditDAO audit = (AuditDAO) InitialContext.doLookup(JNDI_AUD);
            //audit.create("Object deleted from S3 (Key: " + s3Bucket + keyName + ")");
        } catch (AmazonServiceException ase) {
            LOG.log(Level.SEVERE, "Error response from AWS while deleting object", ase);
            throw ase;
        } catch (AmazonClientException ace) {
            LOG.log(Level.SEVERE, "Error response from AWS while deleting object", ace);
            throw ace;
        }
    }

    /**
     * Handles deleting multiple objects from Non-Versioned Bucket.
     *
     * @param justKeys
     * @return Keys
     */
    public DeleteObjectsResult deleteObjects(
            ArrayList<DeleteObjectsRequest.KeyVersion> justKeys) {

        DeleteObjectsResult result = null;

        try {
            /*
             * Multi-object delete by specifying only keys (no version ID)
             */
            DeleteObjectsRequest multiObjectDeleteRequest
                    = new DeleteObjectsRequest(s3Bucket).withQuiet(false);
            multiObjectDeleteRequest.setKeys(justKeys);

            if (justKeys.size() > 0) {

                try {
                    result = s3Client.client.deleteObjects(multiObjectDeleteRequest);
                } catch (MultiObjectDeleteException mode) {
                    System.out.println(mode);
                }

            }

        } catch (AmazonServiceException ase) {
            LOG.log(Level.SEVERE, "Error response from AWS while deleting objects", ase);
        } catch (AmazonClientException ace) {
            LOG.log(Level.SEVERE, "Error response from AWS while deleting objects", ace);
        }
        return result;
    }

    /**
     * Removes a directory and all included objects from the specified S3 directory.
     * This is equivalent of calling listObject() and passing the results to the
     * deleteObjects().
     * 
     * @param dirPath
     * @return 
     */
    public DeleteObjectsResult deleteDirectory(String dirPath) {
        return deleteObjects(listObjects(dirPath));
    }
            
    /**
     * S3 requires a meta data object to save items. At a minimum it needs the
     * content length of the stream that will be saved. Additionally a content
     * type can be set. The content type can be determined from the extension
     * (eg. pdf, or jpg). However, it can also be specified using the Constants
     * defined.
     *
     * @param inputStream
     * @param contentType type specified from IConsultConstants
     * @return
     */
    public ObjectMetadata setMetadata(
            InputStream inputStream, S3ContentType contentType) {

        byte[] contentBytes;
        Long contentLength = null;

        // Get the stream bytes
        try {
            contentBytes = IOUtils.toByteArray(inputStream);
            contentLength = Long.valueOf(contentBytes.length);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while setting S3 metadata", e);
        }

        /*
         * Save the byte length to a ObjectMetadata
         */
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);

        String mime = null;
        switch (contentType) {
            case PDF:
                mime = "application/pdf";
                break;
            case JPEG:
                mime = "image/jpeg";
                break;
            case PNG:
                mime = "image/png";
                break;
            case GIF:
                mime = "image/gif";
                break;
            case BMP:
                mime = "image/bmp";
                break;
            case TIFF:
                mime = "image/tiff";
                break;
            case PLAIN:
                mime = "text/plain";
                break;
            case RTF:
                mime = "text/rtf";
                break;
            case MSWORD:
                mime = "application/msword";
                break;
            case ZIP:
                mime = "application/zip";
                break;
        }
        if (mime != null) {
            metadata.setContentType(mime);

        }
        return metadata;
    }

    /**
     * Performs image resizing before placing the image on S3 in the specified
     * location.
     *
     * @param bufferedImage
     * @param s3Key
     * @param box (Bounding box dimensions)
     * @param contentType
     * @param acl
     * @return PutObjectResult
     * @throws java.lang.Exception
     */
    public Response imageToS3(
            BufferedImage bufferedImage,
            String s3Key,
            int box,
            S3ContentType contentType,
            CannedAccessControlList acl) throws Exception {

        Response response = new Response();

        PutObjectResult por = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            if (bufferedImage != null) {

                // Resize the image
                BufferedImage resizeImage = Scalr.resize(bufferedImage, box, Scalr.OP_ANTIALIAS);
                bufferedImage.flush();
                
                // Supported content types for images
                String format = null;
                switch (contentType) {
                    case JPEG:
                        format = "jpg";
                        break;
                    case PNG:
                        format = "png";
                        break;
                    case GIF:
                        format = "gif";
                        break;
                    case BMP:
                        format = "bmp";
                        break;
                }

                // Write the buffered
                ImageIO.write(resizeImage, format, out);

                por = putObject(
                        s3Key,
                        new ByteArrayInputStream(out.toByteArray()),
                        setMetadata(
                        new ByteArrayInputStream(out.toByteArray()),
                        contentType),
                        acl);

            } else {
                // Image is null
                response.addJsfErrMsg("Image cannot be empty.");
                LOG.log(Level.SEVERE, "BufferedImage cannot be null", bufferedImage);
            }

        } catch (Exception e) {
            response.addJsfErrMsg("There was an error saving your image. Please contact support.");
            throw e;
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                response.addJsfErrMsg("There was an error saving your image. Please contact support.");
                LOG.log(Level.SEVERE, "Error closing the streams", ex);
            }
        }
        response.setObject(por);
        return response;
    }

    /**
     * Placing the image on S3 in the specified location.
     *
     * @param bufferedImage
     * @param s3Key
     * @param contentType
     * @param acl
     * @return PutObjectResult
     * @throws java.lang.Exception
     */
    public Response imageToS3(
            BufferedImage bufferedImage,
            String s3Key,
            S3ContentType contentType,
            CannedAccessControlList acl) throws Exception {

        Response response = new Response();

        PutObjectResult por = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            if (bufferedImage != null) {

                // Supported content types for images
                String format = null;
                switch (contentType) {
                    case JPEG:
                        format = "jpg";
                        break;
                    case PNG:
                        format = "png";
                        break;
                    case GIF:
                        format = "gif";
                        break;
                    case BMP:
                        format = "bmp";
                        break;
                }

                // Write the buffered
                ImageIO.write(bufferedImage, format, out);

                por = putObject(
                        s3Key,
                        new ByteArrayInputStream(out.toByteArray()),
                        setMetadata(
                        new ByteArrayInputStream(out.toByteArray()),
                        contentType),
                        acl);

            } else {
                // Image is null
                response.addJsfErrMsg("Image cannot be empty.");
                LOG.log(Level.SEVERE, "BufferedImage cannot be null", bufferedImage);
            }

        } catch (Exception e) {
            response.addJsfErrMsg("There was an error saving your image. Please contact support.");
            throw e;
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                response.addJsfErrMsg("There was an error saving your image. Please contact support.");
                LOG.log(Level.SEVERE, "Error closing the streams", ex);
            }
        }
        response.setObject(por);
        return response;
    }

    /**
     * Create an S3 client to perform actions on AWS S3 objects.
     *
     * @author Tarka L'Herpiniere <info@tarka.tv>
     * @since Mar 10, 2013
     */
    protected class S3Client {

        // Variables----------------------------------------------------------------
        private AmazonS3 client = null;

        // Constructor--------------------------------------------------------------
        public S3Client() {
            try {

                InputStream credentialsAsStream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("AwsApi.properties");
                AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);

                client = new AmazonS3Client(credentials);

            } catch (IOException t) {
                System.err.println("Error creating AmazonDynamoDBClient: " + t);
            }
        }
    }

}
