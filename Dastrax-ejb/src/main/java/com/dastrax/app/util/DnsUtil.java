/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.app.util;

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
import com.dastrax.app.pojo.Response;
import com.dastrax.per.project.DastraxCst;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @version Build 2.0.0
 * @since Jul 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class DnsUtil {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(DnsUtil.class.getName());

    // Variables----------------------------------------------------------------
    private final AmazonRoute53 route53 = new Route53Client().getRoute53Client();
    private final String stage = ResourceBundle.getBundle("Config").getString("ProjectStage");
    private final String value = ResourceBundle.getBundle("Config").getString("BaseUrl");
    private final String name = "." + ResourceBundle.getBundle("Config").getString("BaseUrl");
    private final String zoneId = ResourceBundle.getBundle("Config").getString("HostedZoneId");

    // Methods------------------------------------------------------------------
    /**
     * This method will attempt to create a new sub-domain CNAME record on route
     * 53 with the specified prefix as the sub-domain. ie. if 'abcde' is passed
     * as the prefix, the sub-domain abcde.example.com is created. The CNAME
     * record will point back to the application BaseUrl as defined in the
     * Config.properties file.
     *
     * @param prefix
     * @return
     */
    public Response createSubdomain(String prefix) {

        Response response = new Response();

        /*
         * Use project stage to determin what URL to use for the CNAME record
         * If in DEV stage we don't want to write anything to route53
         */
        if (!stage.equals(DastraxCst.ProjectStage.DEV.toString())) {

            if (!subdomainExists(prefix)) {

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

                response.setObject((Boolean) true);

            } else {
                response.addJsfWrnMsg(
                        ResourceBundle
                        .getBundle("EjbBundle")
                        .getString("valid.dns.exists"));
                response.setObject((Boolean) false);
            }

        } else {
            /*
             * If using the DEV stage we want to fake the route 53 response to 
             * return that the operation was successfull.
             */
            response.setObject((Boolean) true);
        }
        return response;
    }

    /**
     * Delete the specified sub-domain CNAME record from route53. It is assumed
     * that this method can only be called for an existing CNAME record so no
     * checks are performed to authenticate the existence of the record prior to
     * deletion.
     *
     * @param prefix
     */
    public void deleteSubdomain(String prefix) {

        /*
         * Use project stage to determin what URL to use for the CNAME record
         * If in DEV stage we don't want to write anything to route53
         */
        if (!stage.equals(DastraxCst.ProjectStage.DEV.toString())) {

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

            route53.changeResourceRecordSets(new ChangeResourceRecordSetsRequest()
                    .withHostedZoneId(zoneId)
                    .withChangeBatch(changeBatch));
        }

    }

    /**
     * When a subject access their login page from a sub-domain (ie
     * company.example.com) we need to extract the sub-domain name and use it to
     * specify the page preferences.
     *
     * @param contextURL
     * @return the sub-domain prefix if it exists and is valid. Otherwise null
     */
    public String extract(String contextURL) {

        String subdomain = null;

        /*
         * We know that valid subdomains for this system can only have a 
         * single level subdomain so we can simply extract the string up
         * to the first period. The URL value can must be able to recognise
         * one of the following varients:
         * http://example.com (No subdomain present)
         * http://uat.example.com (UAT 3 char prefixes no subdomain)
         * http://subdomain.uat.example.com (UAT 3 char prefixes with subdomain)
         * http://subdomain.example.com (Live subdomain present)
         */

        /*
         * Determin whether the subdomain is infact a subdomain and not the root
         * context
         */
        String baseUrl = ResourceBundle.getBundle("Config").getString("BaseUrl");
        String accessProtocol = ResourceBundle.getBundle("Config").getString("AccessProtocol");
        String applicationURL = accessProtocol + baseUrl + "/";

        if (!contextURL.equals(applicationURL)) {
            StringTokenizer st = new StringTokenizer(contextURL, "/.");

            for (int i = 0; i < 2; i++) {
                subdomain = st.nextToken();
            }

            /*
             * Check to make sure the subdomain string is not the uat subdomain
             */
            if (subdomain == null || subdomain.equals("uat")) {
                subdomain = null;
            }
        }
        return subdomain;
    }

    /**
     * Determines whether the specified prefix is already registered as a
     * sub-domain on route 53.
     *
     * @param prefix
     * @return true if the sub-domain already exists on route 53. Otherwise
     * false
     */
    private boolean subdomainExists(String prefix) {
        boolean result = false;

        ListResourceRecordSetsRequest lrrsr = new ListResourceRecordSetsRequest();
        lrrsr.setHostedZoneId(zoneId);
        lrrsr.setMaxItems("1");
        lrrsr.setStartRecordName(prefix + name);

        try {
            ListResourceRecordSetsResult lrrsr1 = route53.listResourceRecordSets(lrrsr);

            for (ResourceRecordSet rrs : lrrsr1.getResourceRecordSets()) {
                if (rrs.getName().equals(prefix + name)) {
                    result = true;
                }
            }

        } catch (AmazonServiceException ase) {
            LOG.log(Level.INFO, "Resource does not exist on route 53", ase);
        }

        return result;
    }
    
    public List<String> allSubdomains() {
        List<String> result = new ArrayList<>();
        ListResourceRecordSetsRequest lrrsr = new ListResourceRecordSetsRequest();
        lrrsr.setHostedZoneId(zoneId);
        
        try {
            ListResourceRecordSetsResult lrrsr1 = route53.listResourceRecordSets(lrrsr);

            for (ResourceRecordSet rrs : lrrsr1.getResourceRecordSets()) {
                result.add(rrs.getName());
            }

        } catch (AmazonServiceException ase) {
            LOG.log(Level.INFO, "DNS Service exception on route 53", ase);
        }
        return result;
    }

    /**
     * Used to create a Route53 client to access AWS route 53 resources.
     *
     * @author Tarka L'Herpiniere <info@tarka.tv>
     * @since Mar 10, 2013
     */
    public class Route53Client {

        // Variables------------------------------------------------------------
        private AmazonRoute53Client client = null;

        // Constructor----------------------------------------------------------
        public Route53Client() {
            try {
                try (InputStream credentialsAsStream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("AwsApi.properties")) {

                    AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
                    client = new AmazonRoute53Client(credentials);
                }
            } catch (IOException io) {
                LOG.log(Level.INFO, "Error creating AmazonRoute53Client", io);
            }
        }

        // Getters--------------------------------------------------------------
        public AmazonRoute53Client getRoute53Client() {
            return client;
        }

    }

}
