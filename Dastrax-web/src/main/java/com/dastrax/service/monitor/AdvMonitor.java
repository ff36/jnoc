/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.monitor;

import com.dastrax.cnx.monitor.DeviceUtil;
import com.dastrax.cnx.pojo.Device;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.project.DastraxCst;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @version Build 2.0.0
 * @since Sep 9, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class AdvMonitor implements Serializable {
    
    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(AdvMonitor.class.getName());

    // Variables----------------------------------------------------------------
    private List<Site> sites = new ArrayList<>();
    private Site selectedSite;
    private TreeNode rootNode;  
    private TreeNode selectedNode;
    
    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    SiteDAO siteDAO;
    @EJB
    DeviceUtil deviceUtil;
    
    // Getters------------------------------------------------------------------
    public List<Site> getSites() {
        return sites;
    }

    public Site getSelectedSite() {
        return selectedSite;
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }
    
    // Setters------------------------------------------------------------------
    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public void setSelectedSite(Site selectedSite) {
        this.selectedSite = selectedSite;
    }

    public void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
    
    // Methods------------------------------------------------------------------
    @PostConstruct
    private void postConstruct() {
        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            sites = siteDAO.findAllSites();
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            sites = s.getCompany().getVarSites();
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            sites = s.getCompany().getClientSites();
        }
        // set the selected site as the first in the list
        if (!sites.isEmpty()) {
          selectedSite = sites.get(0);
          selectSite();
        }
    }
    
    public void selectSite() {
        // Get all the devices
        List<Device> devices = deviceUtil.getDeviceTreeWithRoot(selectedSite);
        // Find the root devices
        for (Device device : devices) {
            if (device.getNode().equals("ROOT")) {
                rootNode = createTree(device, null);
            }
        }
        rootNode.setExpanded(true);
        
        selectedNode = null;
    }
    
    /**
     * Create the device tree
     * @param device
     * @param parentNode
     * @return 
     */
    private TreeNode createTree(Device device, TreeNode parentNode) {
        TreeNode node = new DefaultTreeNode(device, parentNode);
        for (Device dev : device.getChildren()) {
            TreeNode newNode = createTree(dev, node);
            newNode.setExpanded(true);
        }
        return node;
    }
    
    public void onNodeSelect(NodeSelectEvent event) {  
          
    }  

    /**
     * This is a simple class designed to tidy up the outer-class by
     * encapsulating some variables that are needed to complete some of the out
     * methods.
     */
    public class Inventory {
        // Variables----------------------------------------------------------------
        private String device;
        private String band;
        private String firmware;
        private String serial;
        private boolean status;

        // Constructors-------------------------------------------------------------
        public Inventory(String device, String band, String firmware, String serial, boolean status) {
            this.device = device;
            this.band = band;
            this.firmware = firmware;
            this.serial = serial;
            this.status = status;
        }

        // Getters------------------------------------------------------------------
        public String getDevice() {
            return device;
        }

        public String getBand() {
            return band;
        }

        public String getFirmware() {
            return firmware;
        }

        public String getSerial() {
            return serial;
        }

        public boolean isStatus() {
            return status;
        }
        
        // Setters------------------------------------------------------------------
        public void setDevice(String device) {
            this.device = device;
        }

        public void setBand(String band) {
            this.band = band;
        }

        public void setFirmware(String firmware) {
            this.firmware = firmware;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
        
        
    }
}
