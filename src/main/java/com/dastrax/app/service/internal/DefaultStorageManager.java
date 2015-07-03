/*
 * Created Jul 31, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.service.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.dastrax.app.services.StorageManager;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
import com.dastrax.per.project.DTX.KeyType;

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
 * @since Build 3.0.0 (Jul 31, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DefaultStorageManager implements StorageManager {

	// <editor-fold defaultstate="collapsed" desc="Properties">
	private static final Logger LOG = Logger
			.getLogger(DefaultStorageManager.class.getName());
	private final S3 s3 = new S3();
	private CrudService dap;

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Constructors">
	public DefaultStorageManager() {
		try {
			dap = (CrudService) InitialContext.doLookup(ResourceBundle
					.getBundle("config").getString("CRUD"));
		} catch (NamingException ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
	}

	// </editor-fold>

	/**
	 * Stored objects are treated as Key Value Pairs. The <b>Key</b> is the
	 * fully qualified path of the file. ie
	 * directory_1/directory_2/file_name.ext. The <b>Value</b> is the actual
	 * object itself. This method uses the
	 *
	 * @param keyType
	 *            Enum specified in the DTX
	 * @param id
	 *            The id of the user, company, attachment (s3id) etc.
	 * @return String representation of the objects fully qualified key.
	 */
	@Override
	public String keyGenerator(KeyType keyType, String id) {

		String key = null;
		User user;
		Company company;

		switch (keyType) {
		case USER_PROFILE_IMAGE:
			user = (User) dap.find(User.class, Long.parseLong(id));
			key = DTX.StorageDirectory.USERS.getValue() + "/"
					+ user.getAccount().getS3id() + "/"
					+ DTX.StorageDirectory.USER_PROFILE.getValue() + "/"
					+ DTX.StorageFile.USER_PROFILE_GRAPHIC.getValue();
			break;
		case COMPANY_LOGO:
			company = (Company) dap.find(Company.class, Long.parseLong(id));
			key = DTX.StorageDirectory.COMPANIES.getValue() + "/"
					+ company.getS3id() + "/"
					+ DTX.StorageDirectory.COMPANY_LOGOS.getValue() + "/"
					+ DTX.StorageFile.COMPANY_LOGO.getValue();
			break;
		case USER_DIRECTORY:
			user = (User) dap.find(User.class, Long.parseLong(id));
			key = DTX.StorageDirectory.USERS.getValue() + "/"
					+ user.getAccount().getS3id();
			break;
		case COMPANY_DIRECTORY:
			company = (Company) dap.find(Company.class, Long.parseLong(id));
			key = DTX.StorageDirectory.COMPANIES.getValue() + "/"
					+ company.getS3id();
			break;
		case TEMPORARY_FILE:
			key = DTX.StorageDirectory.CORE.getValue() + "/"
					+ DTX.StorageDirectory.TEMPORARY.getValue() + "/" + id;
			break;
		case TICKET_ATTACHEMENT:
			key = DTX.StorageDirectory.TICKETS.getValue() + "/"
					+ DTX.StorageDirectory.TICKET_ATTACHMENTS.getValue() + "/"
					+ id;
			break;
		case EMAIL_BLACKLIST:
			key = DTX.StorageDirectory.CORE.getValue() + "/"
					+ DTX.StorageDirectory.CORE_EMAIL.getValue() + "/"
					+ DTX.StorageFile.EMAIL_BLACKLIST.getValue();
			break;
		}
		return key;
	}

	/**
	 * Read a specified object from storage.
	 *
	 * @param key
	 * @return If an object value exists at the given key then the object is
	 *         returned. be <b>null</b>.
	 */
	@Override
	public S3Object get(String key) {

		try {
			return s3.getClient()
					.getObject(new GetObjectRequest(s3Bucket, key));
		} catch (AmazonServiceException ase) {
			LOG.log(Level.SEVERE, "AWS error while getting object", ase);
		} catch (AmazonClientException ace) {
			LOG.log(Level.SEVERE, "AWS error while getting object", ace);
		}
		return new S3Object();

	}

	/**
	 * List objects from storage.
	 *
	 * @param key
	 *            The highest level key to evaluate the list against. If a key
	 *            is specified as directory_1/directory_2/ every object that has
	 *            a key that is a descendent of directory_1/directory_2/ will be
	 *            listed. This can be interpreted as directory_1/directory_2/*.
	 * @return ObjectListing.
	 */
	@Override
	public ObjectListing list(String key) {

		ObjectListing objectListing = null;

		try {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
					.withBucketName(s3Bucket).withPrefix(key);
			do {
				objectListing = s3.getClient().listObjects(listObjectsRequest);
				listObjectsRequest.setMarker(objectListing.getNextMarker());
			} while (objectListing.isTruncated());

		} catch (AmazonServiceException ase) {
			LOG.log(Level.SEVERE, "AWS error while listing object", ase);
		} catch (AmazonClientException ace) {
			LOG.log(Level.SEVERE, "AWS error while retreiving object", ace);
		}
		return objectListing;
	}

	/**
	 * Write a specified object to storage.
	 *
	 * @param key
	 *            the destination key
	 * @param file
	 *            the file to write
	 * @param acl
	 *            permission to allocate to the file
	 * @return
	 */
	@Override
	public CompleteMultipartUploadResult put(String key, File file,
			CannedAccessControlList acl) {

		// Create a list of UploadPartResponse objects. 1 for each part upload.
		List<PartETag> partETags = new ArrayList<>();

		// Initialize.
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
				s3Bucket, key).withCannedACL(acl);
		InitiateMultipartUploadResult initResponse = s3.getClient()
				.initiateMultipartUpload(initRequest);

		long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.
		long contentLength = file.length();

		try {
			long filePosition = 0;
			for (int i = 1; filePosition < contentLength; i++) {
				// Last part can be less than 5 MB. Adjust part size.
				partSize = Math.min(partSize, (contentLength - filePosition));

				// Create request to upload a part.
				UploadPartRequest uploadRequest = new UploadPartRequest()
						.withBucketName(s3Bucket).withKey(key)
						.withUploadId(initResponse.getUploadId())
						.withPartNumber(i).withFileOffset(filePosition)
						.withFile(file).withPartSize(partSize);

				// Upload part and add response to our list.
				partETags.add(s3.getClient().uploadPart(uploadRequest)
						.getPartETag());

				filePosition += partSize;
			}

			// Complete.
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
					s3Bucket, key, initResponse.getUploadId(), partETags);

			return s3.getClient().completeMultipartUpload(compRequest);

		} catch (AmazonServiceException ase) {
			LOG.log(Level.SEVERE, "AWS error while putting object", ase);
			throw ase;
		} catch (AmazonClientException ace) {
			LOG.log(Level.SEVERE, "AWS error while listing object", ace);
			throw ace;
		}
	}

	/**
	 * Copy a specified object in storage from the source to the destination.
	 * This method does <b>NOT</b> delete the original object.
	 *
	 * @param sourceKey
	 * @param destinationKey
	 * @param acl
	 *            determines the resulting objects access permissions
	 * @return CopyObjectResult containing copy information.
	 */
	@Override
	public CopyObjectResult copy(String sourceKey, String destinationKey,
			CannedAccessControlList acl) {

		try {
			return s3.getClient().copyObject(
					new CopyObjectRequest(s3Bucket, sourceKey, s3Bucket,
							destinationKey).withCannedAccessControlList(acl));

		} catch (AmazonServiceException ase) {
			LOG.log(Level.SEVERE, "AWS error while copying object", ase);
		} catch (AmazonClientException ace) {
			LOG.log(Level.SEVERE, "AWS error while listing object", ace);
		}
		return new CopyObjectResult();
	}

	/**
	 * Deletes a specified object from storage.
	 *
	 * @param keys
	 *            is a list of object keys
	 */
	@Override
	public void delete(List<String> keys) {

		try {
			// Create KeyVersion List
			List<KeyVersion> keyVersionList = new ArrayList<>();
			for (String key : keys) {
				keyVersionList.add(new KeyVersion(key));
			}
			// Multi-object delete by specifying only keys (no version ID)
			DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(
					s3Bucket).withQuiet(false);
			multiObjectDeleteRequest.setKeys(keyVersionList);

			if (keys.size() > 0) {
				s3.getClient().deleteObjects(multiObjectDeleteRequest);
			}
		} catch (AmazonServiceException ase) {
			LOG.log(Level.SEVERE,
					"Error response from AWS while deleting objects", ase);
		} catch (AmazonClientException ace) {
			LOG.log(Level.SEVERE,
					"Error response from AWS while deleting objects", ace);
		}
	}

	/**
	 * In order to perform CRUD operations using AWS S3 we need to create and
	 * use a AmazonS3Client. This class is responsible for creating that object.
	 * It only has a single getter that returns the AmazonS3Client.
	 *
	 * @version 2.0.0
	 * @since Build 2.0.0 (Mar 10, 2013)
	 * @author Tarka L'Herpiniere
	 * @author <tarka@solid.com>
	 */
	private class S3 {

		// <editor-fold defaultstate="collapsed" desc="Properties">
		private AmazonS3 client;

		// </editor-fold>

		// <editor-fold defaultstate="collapsed" desc="Constructors">
		public S3() {
			this.client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
		}

		// </editor-fold>

		// <editor-fold defaultstate="collapsed" desc="Getters">
		/**
		 * Get the value of client
		 *
		 * @return the value of client
		 */
		public AmazonS3 getClient() {
			return client;
		}
		// </editor-fold>

	}

}
