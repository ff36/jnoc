package co.ff36.jnoc.app.storage;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named
@ViewScoped
public class StorageView implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TreeNode root;
	
	private AWSStorage selectedDocument;
	private S3Call call = new S3Call();
	
	public StorageView(){
		super();
		init();
	}
	
	public void init(){
		AWSStorage storageRoot = call.getRootAWSStorage(false);
		root = new DefaultTreeNode(storageRoot, null);
		buildTree(root, storageRoot);
	}	
	
	private void buildTree(TreeNode treeNode, AWSStorage node) {
		
		List<AWSStorage> storages = node.getChildren();
		if(storages!=null){
			for (AWSStorage storage : storages) {
				TreeNode childNode = new DefaultTreeNode(storage, treeNode);
				buildTree(childNode, storage);
			}
		}
	}
	
	public AWSStorage getSelectedDocument() {
		return selectedDocument;
	}

	public void setSelectedDocument(AWSStorage selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}
	
}
