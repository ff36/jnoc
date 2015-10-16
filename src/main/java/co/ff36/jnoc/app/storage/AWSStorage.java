package co.ff36.jnoc.app.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;

import co.ff36.jnoc.app.misc.JsfUtil;


public class AWSStorage implements Comparable<AWSStorage>{
	private String name;
	private String url;
	private String fullName;
	private AWSStorage parent;
	private List<AWSStorage> children=new ArrayList<AWSStorage>();
	
	private File file;
	
	public AWSStorage() {
		super();
	}

	public AWSStorage(String name, String url, String fullName) {
		super();
		this.name = name;
		this.url = url;
		this.fullName = fullName;
	}

	public void upload(FileUploadEvent event) {

		try {
			String fileName = event.getFile().getFileName();
//			String extsion = fileName.substring(fileName.lastIndexOf("."));
			file = File.createTempFile(fileName, ".tmp");
			
			FileOutputStream fos = new FileOutputStream(file);
	        InputStream inputstream = event.getFile().getInputstream();
	        fos.write(IOUtils.toByteArray(inputstream));
	        
//	        System.out.println(this.name+fileName);
	        
	        S3Call call = new S3Call();
			call.uploadAWSStroage(file, this.fullName+fileName);
			call.getAllAndBuild(true);
			
	        JsfUtil.addSuccessMessage("Succesful! "+fileName+ " is uploaded.");
	        fos.close();
	        inputstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void delete(){
		S3Call call = new S3Call();
		call.deleteAWSStorage(this.fullName);
		call.getAllAndBuild(true);
		JsfUtil.addSuccessMessage("Succesful! "+this.name+ " is deleted.");
	}
	
	public String buildURL(){
		
		if(url==null)
			return null;
		
		String temp = null;
		try {
			temp = "http://docs.google.com/viewer?url="
			        + URLEncoder.encode(url,"UTF-8")
			        + "&embedded=true";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	public boolean isLeaf(){
//		if(children == null || children.isEmpty())
//			return false;
//		return true;
		if(name.endsWith("/")){
			return false;
		}else
			return true;
	}
	
	public void addChildren(String name){
		if(name==null || name.trim().length()==0){
			return;
		}
		
		this.addChildren("", name);
//		if(name.contains("/")){
//			String currFullname = name.substring(0, name.indexOf("/")+1);
//			AWSStorage node = new AWSStorage(currFullname, S3Call.genericURL(currFullname), currFullname);
//			this.getChildren().add(node);
//			addChildren(currFullname, name, node);
//			
//		}else{
//			this.getChildren().add(new AWSStorage(name, S3Call.genericURL(name), name));
//		}
	}
	
	public void addChildren(String fullName, String name){
		System.out.println(fullName+"--"+name);
		
		String temp = name.replaceFirst(fullName, "");
		if(temp.length()==0){
			return;
		}
		
		if(temp.contains("/")){
			String currName = temp.substring(0, temp.indexOf("/")+1);
			AWSStorage subNode = new AWSStorage(currName, S3Call.genericURL(fullName+currName), fullName+currName);
			
			List<AWSStorage> nodes = this.getChildren();
			int index = nodes.indexOf(subNode);
			if(index>=0){
				AWSStorage existNode = nodes.get(index);
				existNode.addChildren(fullName+currName, name);
			}else{
				nodes.add(subNode);
				subNode.addChildren(fullName+currName, name);
			}
			
			
		}else{
			this.getChildren().add(new AWSStorage(temp, S3Call.genericURL(name), name));
		}
		
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public AWSStorage(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AWSStorage getParent() {
		return parent;
	}

	public void setParent(AWSStorage parent) {
		this.parent = parent;
	}

	public List<AWSStorage> getChildren() {
		return children;
	}

	public void setChildren(List<AWSStorage> children) {
		this.children = children;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AWSStorage other = (AWSStorage) obj;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}

	@Override
	public int compareTo(AWSStorage o) {
		return this.fullName.compareTo(o.getFullName());
	}

	@Override
	public String toString() {
		return "AWSStorage [name=" + name + ", children=" + children + "]";
	}
	
}
