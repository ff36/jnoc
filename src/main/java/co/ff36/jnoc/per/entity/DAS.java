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

import co.ff36.jnoc.app.misc.IpAddress;
import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.project.JNOC.DMSType;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Pattern;

/**
 * This class is mapped in the persistence layer allowing instances of this
 * class to be persisted.
 *
 * DAS (Distributed Antenna System) holds information pertaining to hardware
 * instalation sites.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Jul 10, 2013)
 * @author Tarka L'Herpiniere

 */
@NamedQueries({
    @NamedQuery(name = "DAS.findAll", query = "SELECT e FROM DAS e"),
    @NamedQuery(name = "DAS.findByID", query = "SELECT e FROM DAS e WHERE e.id = :id"),
    @NamedQuery(name = "DAS.findByIP", query = "SELECT e FROM DAS e WHERE e.dms = :dms"),})
@Entity
public class DAS implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(DAS.class.getName());
    private static final long serialVersionUID = 1L;

    @Version
    private int version;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String name;
    @OneToOne(cascade = {CascadeType.ALL})
    private Contact contact;
    @OneToOne(cascade = {CascadeType.ALL})
    private Address address;
    private String installer;
    private Long installEpoch;
    private String packageType;
    private int responseHrs;
    @Pattern(regexp = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$", message = "Invalid IP")
    private String dmsIp;
    @Enumerated(EnumType.STRING)
    private DMSType dms;
    private boolean reportingEnabled;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Transient Properties">
    @Transient
    private CrudService dap;
    @Transient
    private IpAddress ipAddress;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DAS() {
        this.contact = new Contact();
        this.address = new Address();
        this.ipAddress = new IpAddress();
        
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
//            LOG.log(Level.SEVERE, null, ex);
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
     * Get the value of id. Unique storage key
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
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
     * Get the value of contact.
     *
     * @return the value of contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Get the value of address.
     *
     * @return the value of address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Get the value of installer.
     *
     * @return the value of installer
     */
    public String getInstaller() {
        return installer;
    }

    /**
     * Get the value of installEpoch.
     *
     * @return the value of installEpoch
     */
    public Long getInstallEpoch() {
        return installEpoch;
    }

    /**
     * Get the value of packageType.
     *
     * @return the value of packageType
     */
    public String getPackageType() {
        return packageType;
    }

    /**
     * Get the value of responseHrs.
     *
     * @return the value of responseHrs
     */
    public int getResponseHrs() {
        return responseHrs;
    }

    /**
     * Get the value of dmsIp.
     *
     * @return the value of dmsIp
     */
    public String getDmsIp() {
        return dmsIp;
    }

    /**
     * Get the value of dms.
     *
     * @return the value of dms
     */
    public DMSType getDms() {
        return dms;
    }

    /**
     * Get the value of reportingEnabled.
     *
     * @return the value of reportingEnabled
     */
    public boolean isReportingEnabled() {
        return reportingEnabled;
    }

    /**
     * Get the value of ipAddress
     *
     * @return the value of ipAddress
     */
    public IpAddress getIpAddress() {
        return ipAddress;
    }
    
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of version. Unique storage version
     *
     * @param version new value of version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Set the value of id. Unique storage key
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the value of name.
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
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
     * Set the value of address. CascadeType.ALL
     *
     * @param address new value of address
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Set the value of installer.
     *
     * @param installer new value of installer
     */
    public void setInstaller(String installer) {
        this.installer = installer;
    }

    /**
     * Set the value of installEpoch.
     *
     * @param installEpoch new value of installEpoch
     */
    public void setInstallEpoch(Long installEpoch) {
        this.installEpoch = installEpoch;
    }

    /**
     * Set the value of packageType.
     *
     * @param packageType new value of packageType
     */
    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    /**
     * Set the value of responseHrs.
     *
     * @param responseHrs new value of responseHrs
     */
    public void setResponseHrs(int responseHrs) {
        this.responseHrs = responseHrs;
    }

    /**
     * Set the value of dmsIp.
     *
     * @param dmsIp new value of dmsIp
     */
    public void setDmsIp(String dmsIp) {
        this.dmsIp = dmsIp;
    }

    /**
     * Set the value of dms.
     *
     * @param dms new value of dms
     */
    public void setDms(DMSType dms) {
        this.dms = dms;
    }

    /**
     * Set the value of reportingEnabled.
     *
     * @param reportingEnabled new value of reportingEnabled
     */
    public void setReportingEnabled(boolean reportingEnabled) {
        this.reportingEnabled = reportingEnabled;
    }
    
    /**
     * Set the value of ipAddress.
     *
     * @param ipAddress new value of ipAddress
     */
    public void setIpAddress(IpAddress ipAddress) {
        this.ipAddress = ipAddress;
    }
    
//</editor-fold>

    /**
     * Creates a new DAS, adds it to the persistence layer and adds storage
     * related resources.
     */
    public void create() {
        // Translate the IP
        this.dmsIp = ipAddress.concatIP();
        
        // Persist the DAS
        DAS das = (DAS) dap.create(this);
        
        JsfUtil.addSuccessMessage(das.getName() + " has been created.");
    }

    /**
     * Removes the das from the persistence layer and any associated resources
     * linked to the DAS.
     */
    public void delete() {
        dap.delete(DAS.class, id);
    }

    /**
     * Update the persistence layer with a new version of the company.
     */
    public void update() {
        DAS das = (DAS) dap.update(this);
        this.version = das.getVersion();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Warning - this method won't work in the case the id fields are not set.
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DAS)) {
            return false;
        }
        DAS other = (DAS) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
//</editor-fold>

}
