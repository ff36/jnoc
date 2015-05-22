/*
 * Created May 9, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.misc;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.constraints.Pattern;

/**
 * Handles IP address manipulation and validation.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (May 9, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
// PENDING! This class should use the InetAddress.class to account for IPv6 and IPv4
public class IpAddress {
	private static final Logger LOG = Logger.getLogger(IpAddress.class.getName());
    //<editor-fold defaultstate="collapsed" desc="Properties">
    @Pattern(regexp = "^([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])$", message = "Invalid IP")
    private String ipa, ipb, ipc, ipd;  
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public IpAddress() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of ipa
     *
     * @return the value of ipa
     */
    public String getIpa() {
        return ipa;
    }
    
    /**
     * Get the value of ipb
     *
     * @return the value of ipb
     */
    public String getIpb() {
        return ipb;
    }
    
    /**
     * Get the value of ipc
     *
     * @return the value of ipc
     */
    public String getIpc() {
        return ipc;
    }
    
    /**
     * Get the value of ipd
     *
     * @return the value of ipd
     */
    public String getIpd() {
        return ipd;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of ipa
     *
     * @param ipa new value of ipa
     */
    public void setIpa(String ipa) {
        this.ipa = ipa;
    }
    
    /**
     * Set the value of ipb
     *
     * @param ipb new value of ipb
     */
    public void setIpb(String ipb) {
        this.ipb = ipb;
    }
    
    /**
     * Set the value of ipc
     *
     * @param ipc new value of ipc
     */
    public void setIpc(String ipc) {
        this.ipc = ipc;
    }
    
    /**
     * Set the value of ipd
     *
     * @param ipd new value of ipd
     */
    public void setIpd(String ipd) {
        this.ipd = ipd;
    }
//</editor-fold>

    /**
     * Converts the input value to a 3 digit integer with 0 prefix(s)
     */
    public void formatA() {
        try {
            int num = Integer.parseInt(ipa);
            ipa = String.format("%03d", num);
        } catch (NumberFormatException nfe) {
        	LOG.log(Level.SEVERE, nfe.getMessage(), nfe);
        }
    }

    /**
     * Converts the input value to a 3 digit integer with 0 prefix(s)
     */
    public void formatB() {
        try {
            int num = Integer.parseInt(ipb);
            ipb = String.format("%03d", num);
        } catch (NumberFormatException nfe) {
        	LOG.log(Level.SEVERE, nfe.getMessage(), nfe);
        }
    }

    /**
     * Converts the input value to a 3 digit integer with 0 prefix(s)
     */
    public void formatC() {
        try {
            int num = Integer.parseInt(ipc);
            ipc = String.format("%03d", num);
        } catch (NumberFormatException nfe) {
        	LOG.log(Level.SEVERE, nfe.getMessage(), nfe);
        }
    }

    /**
     * Converts the input value to a 3 digit integer with 0 prefix(s)
     */
    public void formatD() {
        try {
            int num = Integer.parseInt(ipd);
            ipd = String.format("%03d", num);
        } catch (NumberFormatException nfe) {
        	LOG.log(Level.SEVERE, nfe.getMessage(), nfe);
        }
    }

    /**
     * Converts the 4 IP octet into a full 12 digit format IP address
     *
     * @return 12 digit format IP address
     */
    public String concatIP() {
        if (ipa != null && ipb != null && ipc != null && ipd != null) {
            return ipa + "." + ipb + "." + ipc + "." + ipd;
        } else {
            return null;
        }
    }
    
    /**
     * Checks if the IP address made up of the property 4 octet constitutes a 
     * valid IP range. No guarantee is provided that the IP actually exists. 
     * It simply checks that the format is correct.
     * 
     * @return true if, and only if the IP address made up of the property
     * octet constitutes a valid IP range.
     */
    public boolean valid() {
        return ipa != null && ipb != null && ipc != null && ipd != null;
    }
}
