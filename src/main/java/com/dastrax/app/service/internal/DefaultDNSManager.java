/*
 * Created Jul 17, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.service.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Default;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53Client;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResult;
import com.amazonaws.services.route53.model.RRType;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.dastrax.app.misc.JsfUtil;
import com.dastrax.app.services.DNSManager;
import com.dastrax.per.project.DTX;

/**
 * Methods dedicated to handling Domain Name System (DNSManager) based
 * functions. The AWS Route 53 DNSManager service offers anAPI to control the
 * DNSManager records.
 *
 * @version 1.0.0
 * @since Build 1.0.0 (Jul 17, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Default
public class DefaultDNSManager implements DNSManager {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(DefaultDNSManager.class.getName());
    private final AmazonRoute53 route53 = new Route53Client().getRoute53Client();
    private final String stage = ResourceBundle.getBundle("config").getString("ProjectStage");
    private final String value = ResourceBundle.getBundle("config").getString("BaseUrl");
    private final String name = "." + ResourceBundle.getBundle("config").getString("BaseUrl");
    private final String zoneId = ResourceBundle.getBundle("config").getString("HostedZoneId");
//</editor-fold>

    /**
     * This method will attempt to create a new CNAME record on route 53 with
     * the specified prefix. ie. if <b>abcde</b> is passed as the prefix, the
     * CNAME <b>abcde.example.com</b> is created. The CNAME record will point
     * back to the application BaseUrl as defined in the config.properties file.
     *
     * @param prefix
     * @return true if the CNAME is successfully created.
     */
    @Override
    public boolean createCNAME(String prefix) {

        // If in DEV stage we don't want to write anything to route 53
        if (stage.equals(DTX.ProjectStage.DEV.toString())) {
            // Fake the response for DEV
            return true;
        } else {

            if (!recordSetExists(prefix)) {

                ResourceRecord resourceRecord = new ResourceRecord();
                resourceRecord.setValue(value);

                List<ResourceRecord> list = new ArrayList<>();
                list.add(resourceRecord);
                Collection<ResourceRecord> rrc = list;

                ResourceRecordSet resourceRecordSet = new ResourceRecordSet();
                resourceRecordSet.setName(prefix + name);

                resourceRecordSet.setType(RRType.CNAME);
                resourceRecordSet.setTTL((long) 300);
                resourceRecordSet.setResourceRecords(rrc);

                Change changes = new Change();
                changes.setAction(ChangeAction.CREATE);
                changes.setResourceRecordSet(resourceRecordSet);

                ChangeBatch changeBatch = new ChangeBatch();
                changeBatch.withChanges(changes);

                route53.changeResourceRecordSets(
                        new ChangeResourceRecordSetsRequest()
                        .withHostedZoneId(zoneId)
                        .withChangeBatch(changeBatch));

                return true;

            } else {
                JsfUtil.addWarningMessage(
                        ResourceBundle
                        .getBundle("source-bundle")
                        .getString("valid.dns.exists"));
                return false;
            }
        }
    }

    /**
     * Delete the specified sub-domain CNAME record from route 53.
     *
     * @param prefix
     * @return true if the CNAME is removed from route 53 or the record set does
     * not exist
     */
    @Override
    public boolean deleteCNAME(String prefix) {

        // If in DEV stage we don't want to write anything to route 53
        if (stage.equals(DTX.ProjectStage.DEV.toString())) {
            // Fake the response for DEV
            return true;
        } else {

            // Make sure the record set exists before trying to delete it.
            if (recordSetExists(prefix)) {

                ResourceRecord resourceRecord = new ResourceRecord();
                resourceRecord.setValue(value);

                List<ResourceRecord> list = new ArrayList<>();
                list.add(resourceRecord);
                Collection<ResourceRecord> rrc = list;

                ResourceRecordSet resourceRecordSet = new ResourceRecordSet();
                resourceRecordSet.setName(prefix + name);

                resourceRecordSet.setType(RRType.CNAME);
                resourceRecordSet.setTTL((long) 300);
                resourceRecordSet.setResourceRecords(rrc);

                Change changes = new Change();
                changes.setAction(ChangeAction.DELETE);
                changes.setResourceRecordSet(resourceRecordSet);

                ChangeBatch changeBatch = new ChangeBatch();
                changeBatch.withChanges(changes);

                route53.changeResourceRecordSets(
                        new ChangeResourceRecordSetsRequest()
                        .withHostedZoneId(zoneId)
                        .withChangeBatch(changeBatch));
                // TODO: This should check the ChangeResourceRecordSetsResult
                return true;

            } else {
                return true;
            }

        }

    }

    /**
     * Extracts the string prefixing the first period in the URL.
     *
     * <b>example.com</b> will return <b>null</b>. <b>abcde.example.com</b>
     * will return <b>abcde</b>
     *
     * @param contextURL
     * @return the CNAME prefix if it exists and is valid. Otherwise null
     */
    @Override
    public String extract(String contextURL) {

        String subdomain = null;

        // Determin if the context contains a prefix
        String baseUrl = ResourceBundle.getBundle("config").getString("BaseUrl");
        String accessProtocol = ResourceBundle.getBundle("config").getString("AccessProtocol");
        String applicationURL = accessProtocol + baseUrl + "/";

        //http:// or https://
        String contextDomain = contextURL.replace(accessProtocol, "");
        //get domain
        contextDomain = contextDomain.substring(0, contextDomain.indexOf("/"));
        //get subdomain
        subdomain = contextDomain.replace(baseUrl, "").replace(".", "").trim();
        
        // Check to make sure the CNAME string is not UAT
        if ("".equals(subdomain) || subdomain == null || subdomain.equals("uat")) {
        	subdomain = null;
        }
        
//        if (!contextURL.equals(applicationURL)) {
//            StringTokenizer st = new StringTokenizer(contextURL, "/.");
//
//            for (int i = 0; i < 2; i++) {
//                subdomain = st.nextToken();
//            }
//
//            // Check to make sure the CNAME string is not UAT
//            if (subdomain == null || subdomain.equals("uat")) {
//                subdomain = null;
//            }
//        }
        
        return subdomain;
    }
    
    /**
     * Determines whether the specified prefix is already a registered record
     * set on route 53.
     *
     * @param prefix
     * @return true if the CNAME already exists on route 53. Otherwise false
     */
    @Override
    public boolean recordSetExists(String prefix) {

        if (stage.equals(DTX.ProjectStage.DEV.toString())) {
            // Fake the response for DEV
            return false;
        } else {
            // Generate a query to find if the record set exists
            ListResourceRecordSetsRequest lrrsr = new ListResourceRecordSetsRequest();
            lrrsr.setHostedZoneId(zoneId);
            lrrsr.setMaxItems("1");
            lrrsr.setStartRecordName(prefix + name);

            try {
                // Request the query
                ListResourceRecordSetsResult lrrsr1 = route53.listResourceRecordSets(lrrsr);

                // Iterate over the result list
                for (ResourceRecordSet rrs : lrrsr1.getResourceRecordSets()) {
                    if (rrs.getName().equals(prefix + name)) {
                        return true;
                    }
                }

            } catch (AmazonServiceException ase) {
                LOG.log(Level.CONFIG, "Resource does not exist on route 53", ase);
            }
            return false;
        }
    }

    /**
     * Retrieves a list of existing record sets on route 53 with the system
     * zoneId.
     *
     * @param maxItems For all record Sets maxItems should be set to 0.
     * @return A list of all existing record sets from route 53. The list can be
     * empty. If an exception is encountered null is returned.
     */
    @Override
    public List<String> listRecordSets(int maxItems) {

        List<String> result = new ArrayList<>();
        try {
            ListResourceRecordSetsRequest lrrsr = new ListResourceRecordSetsRequest();
            lrrsr.setHostedZoneId(zoneId);
            if (maxItems != 0) {
                lrrsr.setMaxItems(String.valueOf(maxItems));
            }
            ListResourceRecordSetsResult lrrsr1 = route53.listResourceRecordSets(lrrsr);

            for (ResourceRecordSet rrs : lrrsr1.getResourceRecordSets()) {
                result.add(rrs.getName());
            }

        } catch (AmazonServiceException ase) {
            LOG.log(Level.CONFIG, "DNS Service exception on route 53", ase);
            return null;
        }
        return result;
    }

    /**
     * In order to access the AWS Route 53 API we need to create and use a
     * AmazonRoute53Client. This class is responsible for creating that object.
     * It only has a single getter that returns the AmazonRoute53Client.
     *
     * @version 2.0.0
     * @since Build 2.0.0 (Mar 10, 2013)
     * @author Tarka L'Herpiniere
     * @author <tarka@solid.com>
     */
    protected class Route53Client {

        //<editor-fold defaultstate="collapsed" desc="Properties">
        private AmazonRoute53Client client = null;
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public Route53Client() {
            try {
                try (InputStream credentialsAsStream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("aws-api.properties")) {

                    AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
                    client = new AmazonRoute53Client(credentials);
                }
            } catch (IOException io) {
                LOG.log(Level.CONFIG, "Error creating AmazonRoute53Client", io);
            }
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Getters">
        public AmazonRoute53Client getRoute53Client() {
            return client;
        }
//</editor-fold>

    }

}
