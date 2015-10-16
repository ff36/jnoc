package co.ff36.jnoc.app.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Call {
//	private static String bucketName = "tuxming";
//	private static String baseUrl = "s3-ap-northeast-1.amazonaws.com";
	private static String protocol = System.getenv("JNOC_ACCESS_PROTOCOL");
	private static String baseUrl = System.getenv("JNOC_S3_BASE_URL");
	private static String bucketName = System.getenv("DTX_S3_BUCKET");


	private static AWSStorage root = null;

	public static void main(String[] args) throws IOException {
		new S3Call().getAWSStorage("icon/icon1.png");
	}

	public boolean uploadAWSStroage(File file, String name){
		AmazonS3 s3client = new AmazonS3Client( new EnvironmentVariableCredentialsProvider());
		try {
			s3client.putObject(new PutObjectRequest(
					bucketName, name, file));
			return true;
		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
			return false;
		} catch (AmazonClientException ace) {
			ace.printStackTrace();
			return false;
		}
	}

	public boolean deleteAWSStorage(String name){
		AmazonS3 s3Client = new AmazonS3Client( new EnvironmentVariableCredentialsProvider());
		try {
			s3Client.deleteObject(new DeleteObjectRequest(bucketName, name));
			return true;
		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
			return false;
		} catch (AmazonClientException ace) {
			ace.printStackTrace();
			return false;
		}
	}

	public AWSStorage getAWSStorage(String key){
		return null;
		/**
		AmazonS3 s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
		try {
			System.out.println("Downloading an object");
			S3Object s3object = s3Client.getObject(new GetObjectRequest(
					bucketName, key));
			System.out.println("Content-Type: "  + 
					s3object.getObjectMetadata().getContentType());
			displayTextInputStream(s3object.getObjectContent());

			// Get a range of bytes from an object.

			GetObjectRequest rangeObjectRequest = new GetObjectRequest(
					bucketName, key);
			rangeObjectRequest.setRange(0, 10);
			S3Object objectPortion = s3Client.getObject(rangeObjectRequest);

			System.out.println("Printing bytes retrieved.");
			displayTextInputStream(objectPortion.getObjectContent());

		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
		} catch (AmazonClientException ace) {
			ace.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		 **/
	}

	private static void displayTextInputStream(InputStream input)
			throws IOException {
		// Read one text line at a time and display.
		BufferedReader reader = new BufferedReader(new 
				InputStreamReader(input));
		while (true) {
			String line = reader.readLine();
			if (line == null) break;

			System.out.println("    " + line);
		}
		System.out.println();
	}

	/**
	 * default false, when upload new file or delete new file, @param update is true.
	 * @param update
	 * @return
	 */
	public AWSStorage getRootAWSStorage(boolean update){
		getAllAndBuild(update);
		return root;
	}

	public void getAllAndBuild(boolean update){

		if(!update){
			if(root!=null){
				return;
			}
		}

		root = new AWSStorage(bucketName);
		root.setChildren(new ArrayList<AWSStorage>());

		EnvironmentVariableCredentialsProvider provider = new EnvironmentVariableCredentialsProvider();

		//		System.out.println(provider.getCredentials().getAWSAccessKeyId());
		//		System.out.println(provider.getCredentials().getAWSSecretKey());

		AmazonS3 s3client = new AmazonS3Client(provider);

		try {
			//            System.out.println("Listing objects");

			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
			.withBucketName(bucketName);
			ObjectListing objectListing;            
			do {
				objectListing = s3client.listObjects(listObjectsRequest);
				for (S3ObjectSummary objectSummary : 
					objectListing.getObjectSummaries()) {
					//                    System.out.println(" - " + objectSummary.getKey() + "  " +
					//                            "(size = " + objectSummary.getSize() + 
					//                            ")");

					
					String objKey = objectSummary.getKey();
					root.addChildren(objKey);
//					String objKey = objectSummary.getKey();
//					if(objKey.endsWith("/")){
//						AWSStorage store = new AWSStorage(objKey);
//						if(root.getChildren().contains(store)==false){
//							root.getChildren().add(store);
//							store.setChildren(new ArrayList<AWSStorage>());
//						}
//					}else{
//						AWSStorage store = new AWSStorage(objKey);
//						store.setUrl(genericURL(objKey));
//						if(objKey.indexOf("/")>=0){
//							String parentName = objKey.substring(0, objKey.lastIndexOf("/")+1);
//							AWSStorage parent = new AWSStorage(parentName);
//
//							int index = root.getChildren().indexOf(parent);
//							if(index>=0){
//								AWSStorage folder = root.getChildren().get(index);
//								if(folder.getChildren()==null){
//									folder.setChildren(new ArrayList<AWSStorage>());
//								}
//								folder.getChildren().add(store);
//							}
//
//						}else{
//							root.getChildren().add(store);
//						}
//					}

				}
				listObjectsRequest.setMarker(objectListing.getNextMarker());
			} while (objectListing.isTruncated());
			System.out.println(root);
		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
		} catch (AmazonClientException ace) {
			ace.printStackTrace();
		}
	}

	public static String genericURL(String name){
		return protocol+baseUrl+"/"+bucketName+"/"+name;
	}

}
