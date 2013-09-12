/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.service.monitor;

import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.project.DastraxCst;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

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
    private CartesianChartModel cpuUtil;
    private CartesianChartModel diskSpace;
    private CartesianChartModel memory;
    private CartesianChartModel networkIn;
    private CartesianChartModel networkOut;
    private CartesianChartModel cpuTemp;
    
    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    SiteDAO siteDAO;
    
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

    public CartesianChartModel getCpuUtil() {
        return cpuUtil;
    }

    public CartesianChartModel getDiskSpace() {
        return diskSpace;
    }

    public CartesianChartModel getMemory() {
        return memory;
    }

    public CartesianChartModel getNetworkIn() {
        return networkIn;
    }

    public CartesianChartModel getNetworkOut() {
        return networkOut;
    }

    public CartesianChartModel getCpuTemp() {
        return cpuTemp;
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
        rootNode = new DefaultTreeNode("root", null);
        rootNode.setExpanded(true);
        
        //DMS
        TreeNode dms = new DefaultTreeNode(new Inventory("DMS", "", "1200", "1200-302-0911050", true), rootNode); 
        dms.setExpanded(true);
        
        // BIU
        TreeNode biu1 = new DefaultTreeNode(new Inventory("BIU1", "Sector 1", "3.2.0", "00467A15600109030003", true), dms);
        biu1.setExpanded(true);
          
        //MDBU  
        TreeNode mdbu1 = new DefaultTreeNode(new Inventory("MDBU1", "700 Public Safety / LAPD", "5.7", "03165M01657509020007", true), biu1);
        TreeNode mdbu3 = new DefaultTreeNode(new Inventory("MDBU3", "800 Public Safety / 800PS", "5.7", "03165M01657509030004", true), biu1);
        TreeNode mdbu4 = new DefaultTreeNode(new Inventory("MDBU4", "AWS-1", "5.7", "02913M01657709020006", true), biu1);
          
        //ODU  
        TreeNode odu1 = new DefaultTreeNode(new Inventory("ODU1", "", "", "03425A15600309C00001", true), biu1);
        odu1.setExpanded(true);
        
        //DOU  
        TreeNode dou1 = new DefaultTreeNode(new Inventory("DOU1", "", "1.2", "03176M01658609B00005", true), odu1);
        dou1.setExpanded(true);
        
        //ROU 
        TreeNode rou1 = new DefaultTreeNode(new Inventory("ROU1", "ROU1 IDF2204", "2.5.0", " 00467A15601709060012", true), dou1);
        rou1.setExpanded(true);
        
        //RDU  
        TreeNode rdu1 = new DefaultTreeNode(new Inventory("RDU1", "AWS-1 ", "5.8", "03153M01808911700158", true), rou1);
        TreeNode rdu2 = new DefaultTreeNode(new Inventory("RDU2", "AWS-1 ", "5.7", "03425M01807709C00012", true), rou1);
        TreeNode rdu3 = new DefaultTreeNode(new Inventory("RDU3", "AWS-1 ", "5.7", "03425M01808509C00020", true), rou1);
        
        selectedNode = null;
    }
    
    public void onNodeSelect(NodeSelectEvent event) {  
          createSampleCharts();
    }  
    
    private void createSampleCharts() { 
        long ONE_MINUTE=60000;
        long time = new Date().getTime();
        
        // CPU util
        cpuUtil = new CartesianChartModel();  
        LineChartSeries series1 = new LineChartSeries();
        for (int i = 60; i > 0; i = i - 4) {
            series1.set(time - (i * ONE_MINUTE), 0.3 + Math.random());
        }
        cpuUtil.addSeries(series1);   
        
        // Disk space
        diskSpace = new CartesianChartModel();  
        LineChartSeries series2 = new LineChartSeries();
        for (int i = 60; i > 0; i = i - 4) {
            series2.set(time - (i * ONE_MINUTE), 136);
        }
        diskSpace.addSeries(series2);
        
        // Memory
        memory = new CartesianChartModel();  
        LineChartSeries series3 = new LineChartSeries();
        for (int i = 60; i > 0; i = i - 4) {
            series3.set(time - (i * ONE_MINUTE), Math.random());
        }
        memory.addSeries(series3);
        
        // network in
        networkIn = new CartesianChartModel();  
        LineChartSeries series4 = new LineChartSeries();
        for (int i = 60; i > 0; i = i - 4) {
            series4.set(time - (i * ONE_MINUTE), 10000 + (int)(Math.random() * 40000));
        }
        networkIn.addSeries(series4);
        
        // network out
        networkOut = new CartesianChartModel();  
        LineChartSeries series5 = new LineChartSeries();
        for (int i = 60; i > 0; i = i - 4) {
            series5.set(time - (i * ONE_MINUTE), 10000 + (int)(Math.random() * 40000));
        }
        networkOut.addSeries(series5);
        
        // CPU Temp
        cpuTemp = new CartesianChartModel();  
        LineChartSeries series6 = new LineChartSeries();
        for (int i = 60; i > 0; i = i - 4) {
            series6.set(time - (i * ONE_MINUTE), 140 + (int)(Math.random() * 10));
        }
        cpuTemp.addSeries(series6);
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
