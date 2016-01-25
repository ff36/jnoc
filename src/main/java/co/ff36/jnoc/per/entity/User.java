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

package co.ff36.jnoc.per.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Pattern;

import org.apache.shiro.SecurityUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.extensions.event.ImageAreaSelectEvent;

import co.ff36.jnoc.app.email.DefaultEmailer;
import co.ff36.jnoc.app.email.Email;
import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.app.security.Password;
import co.ff36.jnoc.app.security.SessionUser;
import co.ff36.jnoc.app.service.internal.DefaultStorageManager;
import co.ff36.jnoc.app.services.StorageManager;
import co.ff36.jnoc.app.upload.DefaultUploadManager;
import co.ff36.jnoc.app.upload.UploadFile;
import co.ff36.jnoc.app.upload.UploadManager;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.dap.QueryParameter;
import co.ff36.jnoc.per.project.JNOC;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * User holds information pertaining to system account users.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere

 */
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT e FROM User e"),
    @NamedQuery(name = "User.findByID", query = "SELECT e FROM User e WHERE e.id = :id"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT e FROM User e WHERE e.email = :email"),
    @NamedQuery(name = "User.findByMetier", query = "SELECT e FROM User e WHERE e.metier.name = :name"),
    @NamedQuery(name = "User.findByNullCompany", query = "SELECT e FROM User e WHERE e.company IS NULL"),
    @NamedQuery(name = "User.findByCompany", query = "SELECT e FROM User e JOIN e.company c WHERE c.id = :id"),
})
@Table(name = "SUBJECT")
@Entity
public class User implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(User.class.getName());
    private static final long serialVersionUID = 1L;

    @Version
    private int version;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = JNOC.EMAIL_REGEX, message = "Invalid Email")
    @Column(unique = true)
    private String email;
    private String password;
    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Contact contact;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Account account;
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Permission> permissions;
    @ManyToOne
    private Metier metier;
    @ManyToOne
    private Company company;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
    @Transient
    private String newEmail;
    @Transient
    private List<Metier> metiers;
    @Transient
    private UploadFile uploadFile;
    @Transient
    private Permission newPermission;
    @Transient
    private List<Company> availableCompanies;
    @Transient
    private Request request;
    @Transient
    Password newPassword;
    
    /////////////////group////////////////////////////////////
    @ManyToMany
	@JoinTable(name="subject_role", 
		joinColumns={@JoinColumn(name="User_ID")},
		inverseJoinColumns={@JoinColumn(name="Role_ID")}
	)
    private List<Role> roles = new ArrayList<Role>();
    @Transient
    private Role newGroup;
    @Transient
    private List<Role> allGroups;
    ////////////////////end group//////////////////////////////
    
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public User() {
        this.availableCompanies = new ArrayList<>();
        this.newPassword = new Password();
        this.contact = new Contact();
        this.account = new Account();
        this.metier = new Metier();
        this.company = new Company();
        this.metiers = new ArrayList<>();
        this.newPermission = new Permission();
        this.permissions = new ArrayList<>();
        this.uploadFile = new UploadFile();
        this.request = new Request();
        
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
            LOG.log(Level.CONFIG, null, ex);
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of version. Unique storage version
     *
     * @return the value of version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Get the value of id. Unique storage id
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the value of contact.
     *
     * @return the value of contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Get the value of account.
     *
     * @return the value of account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Get the value of permissions.
     *
     * @return the value of permissions
     */
    public List<Permission> getPermissions() {
        return permissions;
    }

    /**
     * Get the value of metier.
     *
     * @return the value of metier
     */
    public Metier getMetier() {
        return metier;
    }

    /**
     * Get the value of email.
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the value of password.
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the value of company.
     *
     * @return the value of company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Get the value of newEmail.
     *
     * @return the value of newEmail
     */
    public String getNewEmail() {
        return newEmail;
    }

    /**
     * Get the value of metiers.
     *
     * @return the value of metiers
     */
    public List<Metier> getMetiers() {
        return metiers;
    }

    /**
     * Get the value of newPermission.
     *
     * @return the value of newPermission
     */
    public Permission getNewPermission() {
        return newPermission;
    }

    /**
     * Get the value of availableCompanies.
     *
     * @return the value of availableCompanies
     */
    public List<Company> getAvailableCompanies() {
        return availableCompanies;
    }

    /**
     * Get the value of request.
     *
     * @return the value of request
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Get the value of newPassword.
     *
     * @return the value of newPassword
     */
    public Password getNewPassword() {
        return newPassword;
    }

    /**
     * Get the value of uploadFile.
     *
     * @return the value of uploadFile
     */
    public UploadFile getUploadFile() {
        return uploadFile;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of id. Unique storage key
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the value of contact. CascadeType.ALL
     *
     * @param contact new value of contact
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * Set the value of account. CascadeType.PERSIST, CascadeType.REMOVE
     *
     * @param account new value of account
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Set the value of permissions. CascadeType.ALL
     *
     * @param permissions new value of permissions
     */
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Set the value of metier.
     *
     * @param metier new value of metier
     */
    public void setMetier(Metier metier) {
        this.metier = metier;
    }

    /**
     * Set the value of email. Users authentication email
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the value of password.
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set the value of version.
     *
     * @param version new value of version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Set the value of company.
     *
     * @param company new value of company
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * Set the value of newEmail.
     *
     * @param newEmail new value of newEmail
     */
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    /**
     * Set the value of metiers.
     *
     * @param metiers new value of metiers
     */
    public void setMetiers(List<Metier> metiers) {
        this.metiers = metiers;
    }

    /**
     * Set the value of newPermission.
     *
     * @param newPermission new value of newPermission
     */
    public void setNewPermission(Permission newPermission) {
        this.newPermission = newPermission;
    }

    /**
     * Set the value of availableCompanies.
     *
     * @param availableCompanies new value of availableCompanies
     */
    public void setAvailableCompanies(List<Company> availableCompanies) {
        this.availableCompanies = availableCompanies;
    }

    /**
     * Set the value of request.
     *
     * @param request new value of request
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * Set the value of newPassword.
     *
     * @param newPassword new value of newPassword
     */
    public void setNewPassword(Password newPassword) {
        this.newPassword = newPassword;
    }

//</editor-fold>
    
    /**
     * check account is active
     * @return
     */
    public boolean isComfirmed(){
    	return this.getAccount().isConfirmed();
    }
    
    /**
     * Loads availableCompanies data from the persistence layer and sets the
     * class level properties. This information is generally not needed so it is
     * not loaded by default.
     */
    public void lazyLoad() {
        // Admins can add to any company
        if (SessionUser.getCurrentUser().isAdministrator()) {
            // Make sure its the right kind of company
            if ("CLIENT".equals(metier.getName())) {
                availableCompanies = dap.findWithNamedQuery(
                        "Company.findByType", 
                        QueryParameter
                                .with("type", JNOC.CompanyType.CLIENT)
                                .parameters());
            }
            // Make sure its the right kind of company
            if ("VAR".equals(metier.getName())) {
                availableCompanies = dap.findWithNamedQuery(
                        "Company.findByType", 
                        QueryParameter
                                .with("type", JNOC.CompanyType.VAR)
                                .parameters());
            }
            // Make sure its the right kind of company
            if ("UNDEFINED".equals(metier.getName())) {
                List<Company> vs = dap.findWithNamedQuery(
                        "Company.findByType", 
                        QueryParameter
                                .with("type", JNOC.CompanyType.VAR)
                                .parameters());
                List<Company> cls = dap.findWithNamedQuery(
                        "Company.findByType", 
                        QueryParameter
                                .with("type", JNOC.CompanyType.CLIENT)
                                .parameters());
                availableCompanies.addAll(vs);
                availableCompanies.addAll(cls);
            }
        }
        // VAR can only add to their own company and client companies
        if (SessionUser.getCurrentUser().isVAR()) {
//            List<Long> companies = 
//                (List<Long>) new DefaultAttributeFilter()
//                        .authorizedCompanies(true)
//                        .get("company");
            if ("CLIENT".equals(metier.getName())) {
                availableCompanies.addAll(company.getClients());
            }
            if ("VAR".equals(metier.getName())) {
                availableCompanies.add(company);
            }
            
        }
        // Clients can only add to their own company
        if (SessionUser.getCurrentUser().isClient()) {
            availableCompanies.add(company);
        }

        // Populate all the metiers
        metiers = dap.findWithNamedQuery("Metier.findAll");
        // Undefined is internal only
        Iterator<Metier> iterator = metiers.iterator();
		while (iterator.hasNext()) {
	            if ("UNDEFINED".equals(iterator.next().getName())) {
	                iterator.remove();
	            }
		}
    }

    /**
     * Creates a new user, adds it to the persistence layer and adds storage
     * related resources.
     */
    public void create() {

        // Move the email from transient field to persistent
        email = newEmail;

        // Set the account creation date and s3id
        account.setCreationEpoch(Calendar.getInstance().getTimeInMillis());
        account.setS3id(UUID.randomUUID().toString());
        
        // If the new user is an admin we want to reset the company to null
        if (isAdministrator()) {
            company = null;
        }
        
        // Persist the user
        User u = (User) dap.create(this);

        // Save the profile image if one exists
        if (uploadFile.isUploaded()) {
            saveProfileImage();
        }

        // Send a welcome confirmation email and write the Token
        Token token = newUserEmail();
        token.create();
        
        try {
            JsfUtil.addSuccessMessage("New user created");
        } catch (Exception e) {
            // Request does not come from JSF
        	LOG.log(Level.CONFIG, e.getMessage(), e);
        }
    }

    /**
     * Update the persistence layer with a new version of the user.
     */
    public void update() {
        User user = (User) dap.update(this);
        this.version = user.version;
        JsfUtil.addSuccessMessage("Saved");
    }

    /**
     * If the users company has been changed we need to match the metier to the
     * company type. This is for converting undefined to a user type.
     */
    public void changeCompany() {
        if (JNOC.CompanyType.CLIENT.equals(this.company.getType())) {
            this.metier = (Metier) dap.findWithNamedQuery(
                        "Metier.findByName", 
                        QueryParameter
                                .with("name", JNOC.Metier.CLIENT.toString())
                                .parameters())
                    .get(0);
        }
        if (JNOC.CompanyType.VAR.equals(this.company.getType())) {
            this.metier = (Metier) dap.findWithNamedQuery(
                        "Metier.findByName", 
                        QueryParameter
                                .with("name", JNOC.Metier.VAR.toString())
                                .parameters())
                    .get(0);
        }
        
        update();
    }
    
    public List<Company> filterCompanies(String query){
    	List<Company> companies = new ArrayList<Company>();

    	for (Company company : availableCompanies) {
    		if(company.getName().toLowerCase().startsWith(query.toLowerCase()))
    		companies.add(company);
		}
    	return companies;
    }
    
    /**
     * Close a user accounts. We cannot technically delete a user account as it
     * will inevitably have dependencies on tickets etc. Instead we remove any
     * identifying features relating to the account and flag it as closed.
     */
    public void close() {

        // Delete the S3 directory
        StorageManager storage = new DefaultStorageManager();
        ObjectListing list = storage.list(
                storage.keyGenerator(JNOC.KeyType.USER_DIRECTORY, id.toString()));
        List<String> keys = new ArrayList<>();
        List<S3ObjectSummary> objectSummaries = list.getObjectSummaries();
        for (S3ObjectSummary objectSummary : objectSummaries) {
            keys.add(objectSummary.getKey());
        }
        storage.delete(keys);

        /*
         * We cannot just delete a subject from the db when we close their
         * account as they might have relationships
         */
        Contact holder = new Contact();
        holder.setFirstName(contact.getFirstName() + " " + contact.getLastName() + " (Closed)");
        contact = holder;
        
        account.setCloseEpoch(Calendar.getInstance().getTimeInMillis());
        account.setS3id(null);
        company = null;
        password = null;
        email = UUID.randomUUID().toString().substring(0, 7)
                + "@"
                + UUID.randomUUID().toString().substring(0, 7)
                + "."
                + UUID.randomUUID().toString().substring(0, 3);

        // Update the user
        update();

        // Account is not {CascadeType.MERGE}
        account.update();
    }

    /**
     * Performs the necessary checks and functions to set a new password.
     */
    public void updatePassword() {
        if (newPassword.fullCheck()) {
            newPassword.encrypt();
            password = newPassword.getEncrypted();
            update();
            newPassword = new Password();
        } else {
            // Failed validation checks
        }
    }
    
    /**
     * Convenience method to determine if this user has a metier of type
     * Administrator.
     *
     * @return True if the current user has a metier of Administrator. Otherwise
     * false.
     */
    public boolean isAdministrator() {
        try {
            return metier.getName().equals(JNOC.Metier.ADMIN.toString());
        } catch (NullPointerException e) {
            return false;
        }
    }
    
    /**
     * Convenience method to determine if this user has a metier of type
     * Undefined.
     *
     * @return True if the current user has a metier of Administrator. Otherwise
     * false.
     */
    public boolean isUndefined() {
        try {
            return metier.getName().equals(JNOC.Metier.UNDEFINED.toString());
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Convenience method to determine if this user has a metier of type VAR.
     *
     * @return True if the current user has a metier of type VAR. Otherwise
     * false.
     */
    public boolean isVAR() {
        try {
            return metier.getName().equals(JNOC.Metier.VAR.toString());
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Convenience method to determine if this user has a metier of type Client.
     *
     * @return True if the current user has a metier of type Client. Otherwise
     * false.
     */
    public boolean isClient() {
        try {
            return metier.getName().equals(JNOC.Metier.CLIENT.toString());
        } catch (NullPointerException e) {
            return false;
        }
    }
    
    /**
     * Convenience method to determine if this user has a metier.
     *
     * @return True if the current user has a metier. Otherwise false.
     */
    public boolean isNothing() {
        try {
            return metier.getName().isEmpty();
        } catch (NullPointerException npe) {
            return true;
        }
    }

    /**
     * Convenience method to determine if this user has the implied permission.
     * 
     * @param permission
     * @return true if the subject has the implied permission otherwise false
     */
    public boolean hasPermission(String permission) {
        return SecurityUtils.getSubject().isPermitted(permission);
    }
    
    /**
     * Convenience method to determine if this user has the implied page 
     * permission. If not then the user is redirected to their login page
     * which in turn redirects then to their specific dashboard.
     * 
     * As a security measure if the redirect fails the users session is ended.
     * 
     * @param permission
     */
    public void hasPagePermission(String permission){
        try {
            if(!SecurityUtils.getSubject().isPermitted(permission)) {
            ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
            ectx.redirect(ectx.getRequestContextPath() + "/login.jsf");
        }
        } catch (IOException e) {
            // As a security precaution we should log the user out.
            SecurityUtils.getSubject().logout();
        }
    }
    
    /**
     * Convenience method to determine if this user has the implied page 
     * permission. If not then the user is redirected to their login page
     * which in turn redirects then to their specific dashboard.
     * 
     * As a security measure if the redirect fails the users session is ended.
     * 
     * @param role
     */
    public void hasPageRole(String role){
        try {
            if(!SecurityUtils.getSubject().hasRole(role)) {
            ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
            ectx.redirect(ectx.getRequestContextPath() + "/login.jsf");
        }
        } catch (IOException e) {
            // As a security precaution we should log the user out.
            SecurityUtils.getSubject().logout();
        }
    }
    
    /**
     * Uploads the user profile image to the default storage.
     *
     * @param event
     */
    public void uploadProfileImage(FileUploadEvent event) {

        UploadManager uploader = new DefaultUploadManager();
        uploadFile = uploader.upload(event);
        uploadFile.setType(JNOC.UploadType.PROFILE_IMAGE);
        uploadFile.isUploaded();

    }

    /**
     * Saves the user profile image from its temporary location to its permanent
     * storage location.
     */
    public void saveProfileImage() {
        UploadManager uploader = new DefaultUploadManager();
        if (uploadFile.isUploaded()) {
            uploader.save(uploadFile, this);
        }
    }

    /**
     * Uploaded images stored in the temporary storage directory can offer the
     * option to be cropped. This listener method determines the coordinates and
     * dimensions of the area to be cropped in relation to the original image.
     *
     * @param event a Primefaces ImageAreaSelectEvent
     */
    public void cropListener(ImageAreaSelectEvent event) {
        uploadFile.getImage().getImageCrop().setX(event.getX1());
        uploadFile.getImage().getImageCrop().setY(event.getY1());
        uploadFile.getImage().getImageCrop().setWidth(event.getWidth());
        uploadFile.getImage().getImageCrop().setHeight(event.getHeight());
    }

    /**
     * Saves the newly cropped version user profile image over the original in 
     * the temporary storage location.
     */
    public void cropProfileImage() {
        UploadManager uploader = new DefaultUploadManager();
        uploader.crop(uploadFile, JNOC.CroppableType.USER_PROFILE_IMAGE);
    }
    
    /**
     * This is just a convenience method for Primefaces Collector.
     */
    public void resetPermission() {
        newPermission = new Permission();
    }

    
    /**
     * at account list page. when you click resend token. whill call this.
     */
    public void resendToken(){
    	
    	List<Token> tokens = dap.findWithNamedQuery(
                "Token.findByUserID",
                QueryParameter
                .with("user", this.id.toString())
                .parameters());
    	
    	if(tokens!=null && tokens.size()>0){
    		Token token = tokens.get(0);
    		dap.delete(Token.class, token.getId());
    	}
    	
    	Token token = newUserEmail();
    	token.create();
    	
    	try {
            JsfUtil.addSuccessMessage("Send Comfirm Success");
        } catch (Exception e) {
            // Request does not come from JSF
        	LOG.log(Level.CONFIG, e.getMessage(), e);
        }
    	
    }
    
    /**
     * Construct an email to the newly requested address and persist the
     * Token to confirm the authentication at a later date.
     *
     * @return The populated Token object
     */
    public Token newEmailEmail() {

        // Build an new email
        Email em = new Email();

        // Retreive the email template from the database.
        Template temp = (Template) dap.find(
                Template.class, JNOC.EmailTemplate.CHANGE_EMAIL.getValue());
        em.setTemplate(temp);
        
        // Generate a new pincode
        Integer pin = new Random().nextInt(9000) + 1000;
        
        // Set the recipient
        em.setRecipientEmail(newEmail.toLowerCase());
        // Set the persistable params
        em.getParameters().setEmail(newEmail.toLowerCase());
        em.getParameters().setUser(id.toString());
        em.getParameters().setId(new Random().nextLong());
        em.getParameters().setCreateEpoch(Calendar.getInstance().getTimeInMillis());
        Map<String, String> params = new HashMap(1);
        params.put("email", newEmail.toLowerCase());
        em.getParameters().setParameters(params);
        
        // Persist the token
        dap.create(em.getParameters());
        
        // Tidy the tokens
        em.getParameters().tidy();
        
        // Set the variables
        Map<JNOC.EmailVariableKey, String> vars = new HashMap<>();
        vars.put(JNOC.EmailVariableKey.NAME, contact.getFirstName());
        em.setVariables(vars);

        // Send the email
        sendEmail(em);

        return em.getParameters();
    }
    
    /**
     * Once the registration is successful of the new user an email needs to be
     * constructed and sent containing all the required info.
     *
     * @return The populated Token object
     */
    private Token newUserEmail() {

        // Build an new email
        Email em = new Email();

        // Retreive the email template from the database.
        Template temp = (Template) dap.find(
                Template.class, JNOC.EmailTemplate.NEW_ACCOUNT.getValue());
        em.setTemplate(temp);

        // Generate a new pincode
        Integer pin = new Random().nextInt(9000) + 1000;

        // Set the recipient
        em.setRecipientEmail(email.toLowerCase());
        // Set the persistable params
        em.getParameters().setEmail(email.toLowerCase());
        em.getParameters().setUser(id.toString());
        em.getParameters().setId(new Random().nextLong());
        em.getParameters().setCreateEpoch(Calendar.getInstance().getTimeInMillis());
        Map<String, String> params = new HashMap(1);
        params.put("pin", pin.toString());
        em.getParameters().setParameters(params);

        // Set the variables
        Map<JNOC.EmailVariableKey, String> variables = new HashMap<>();
        variables.put(JNOC.EmailVariableKey.PIN, pin.toString());
        variables.put(JNOC.EmailVariableKey.NAME, contact.getFirstName());
        em.setVariables(variables);

        // Send the email
        sendEmail(em);

        return em.getParameters();
    }

    /**
     * Anonymous users who are not registered or affiliated with the system can
     * request an account from an administrator via a form that posts an email
     * to administrators.
     */
    public void newUserRequestEmail() {

        // Build an new email
        Email em = new Email();

        // Retreive the email template from the database.
        Template temp = (Template) dap.find(
                Template.class, JNOC.EmailTemplate.ACCOUNT_REQUEST.getValue());
        em.setTemplate(temp);
        
        // Set the recipient
        em.setRecipientEmail(request.getRecipient());

        // Set the variables
        Map<JNOC.EmailVariableKey, String> vars = new HashMap<>();
        vars.put(JNOC.EmailVariableKey.NAME, request.getName());
        vars.put(JNOC.EmailVariableKey.COMPANY, request.getCompany());
        vars.put(JNOC.EmailVariableKey.USER_EMAIL, request.getEmail().toLowerCase());
        vars.put(JNOC.EmailVariableKey.TELEPHONE, request.getPhone());

        em.setVariables(vars);

        // Send the email
        sendEmail(em);
    }

    /**
     * when company type change,  update companies
     * @param event
     */
    public void changCompanyType(AjaxBehaviorEvent event){
	        if (SessionUser.getCurrentUser().isAdministrator()) {
	            // Make sure its the right kind of company
	            if ("CLIENT".equals(metier.getName())) {
	                availableCompanies = dap.findWithNamedQuery(
	                        "Company.findByType", 
	                        QueryParameter
	                                .with("type", JNOC.CompanyType.CLIENT)
	                                .parameters());
	            }
	            // Make sure its the right kind of company
	            if ("VAR".equals(metier.getName())) {
	                availableCompanies = dap.findWithNamedQuery(
	                        "Company.findByType", 
	                        QueryParameter
	                                .with("type", JNOC.CompanyType.VAR)
	                                .parameters());
	            }
	        }
	        // VAR can only add to their own company and client companies
	        if (SessionUser.getCurrentUser().isVAR()) {
	            if ("CLIENT".equals(metier.getName())) {
	                availableCompanies.addAll(company.getClients());
	            }
	            if ("VAR".equals(metier.getName())) {
	                availableCompanies.add(company);
	            }
	            
	        }
	        // Clients can only add to their own company
	        if (SessionUser.getCurrentUser().isClient()) {
	        	System.out.println("isClient");
	            availableCompanies.add(company);
	        }
	        
    }
    
    /**
     * Construct an email to the specified address and persist the
     * Token to confirm the authentication at a later date.
     *
     * @return The populated Token object
     */
    public Token newAnonymousPassword() {

        // Build an new email
        Email em = new Email();

        // Retreive the email template from the database.
        Template temp = (Template) dap.find(
                Template.class, JNOC.EmailTemplate.CHANGE_PASSWORD.getValue());
        em.setTemplate(temp);
        
        // Generate a new pincode
        Integer pin = new Random().nextInt(9000) + 1000;
        
        // Set the recipient
        em.setRecipientEmail(newEmail.toLowerCase());
        // Set the persistable params
        em.getParameters().setEmail(newEmail.toLowerCase());
        em.getParameters().setUser(id.toString());
        em.getParameters().setId(new Random().nextLong());
        em.getParameters().setCreateEpoch(Calendar.getInstance().getTimeInMillis());
        Map<String, String> params = new HashMap(1);
        params.put("email", newEmail.toLowerCase());
        em.getParameters().setParameters(params);
        
        // Persist the token
        dap.create(em.getParameters());
        
        // Tidy the tokens
        em.getParameters().tidy();
        
        // Set the variables
        Map<JNOC.EmailVariableKey, String> vars = new HashMap<>();
        vars.put(JNOC.EmailVariableKey.NAME, contact.getFirstName());
        em.setVariables(vars);

        // Send the email
        sendEmail(em);

        return em.getParameters();
    }
    
    
    /**
     * Sends asynchronous email.
     *
     * @param eml The email object containing all the email information
     */
    private void sendEmail(Email eml) {
        // Send the email
        new DefaultEmailer().send(eml);
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.email);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        return true;
    }
//</editor-fold>

    /**
     * A user request object to help a non-registered user request an account.
     *
     *
     * @version 2.0.0
     * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
     * @author Tarka L'Herpiniere
    
     */
    public class Request {

        //<editor-fold defaultstate="collapsed" desc="Properties">
        private final String recipient = System.getenv("JNOC_ACCOUNT_REQUEST_EMAIL");
        private String name;
        private String company;
        private String email;
        private String phone;
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public Request() {
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Getters">
        /**
         * Get the value of recipient.
         *
         * @return the value of recipient
         */
        public String getRecipient() {
            return recipient;
        }

        /**
         * Get the value of name.
         *
         * @return the value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Get the value of company.
         *
         * @return the value of company
         */
        public String getCompany() {
            return company;
        }

        /**
         * Get the value of email.
         *
         * @return the value of email
         */
        public String getEmail() {
            return email;
        }

        /**
         * Get the value of phone.
         *
         * @return the value of phone
         */
        public String getPhone() {
            return phone;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Setters">
        /**
         * Set the value of name.
         *
         * @param name new value of name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Set the value of company.
         *
         * @param company new value of company
         */
        public void setCompany(String company) {
            this.company = company;
        }

        /**
         * Set the value of email.
         *
         * @param email new value of email
         */
        public void setEmail(String email) {
            this.email = email;
        }

        /**
         * Set the value of phone.
         *
         * @param phone new value of phone
         */
        public void setPhone(String phone) {
            this.phone = phone;
        }
//</editor-fold>

    }

    //////////////////////////// group ///////////////////////////////////
    ////role == group////
    public void updateGroup(){
    	Set<Permission> tpermissions = new HashSet<Permission>();
    	for (Role role : this.roles) {
			List<PermissionTemplate> pts = role.getPermissions();
			for (PermissionTemplate permissionTemplate : pts) {
				tpermissions.add(new Permission(permissionTemplate.getExpression()));
			}
		}
    	this.permissions.clear();
    	this.permissions.addAll(tpermissions);
    	
    	User user = (User) dap.update(this);
    	this.version = user.version;
    	
    	JsfUtil.addSuccessMessage("Saved");
    }
    
    public List<Role> getAllGroups() {
    	if(allGroups==null||allGroups.isEmpty()){
    		this.allGroups  = (List<Role>) dap.findWithNamedQuery("Role.findAll");
    	}
		return allGroups;
	}
    
    public void resetGroup(){
    	this.newGroup = null;
    }
    
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Role getNewGroup() {
		return newGroup;
	}

	public void setNewGroup(Role newGroup) {
		this.newGroup = newGroup;
	}

	public void setAllGroups(List<Role> allGroups) {
		this.allGroups = allGroups;
	}
}
