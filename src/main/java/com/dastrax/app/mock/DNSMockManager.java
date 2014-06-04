/*
 * Created May 13, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.mock;

import com.dastrax.app.services.DNSManager;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.enterprise.inject.Alternative;
/**
 * Moke Methods dedicated to simulating handling Domain Name System (DNSManager) 
 * based functions during development.
 *
 * @version 1.0.0
 * @since Build 1.0.0 (Jul 17, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Alternative
public class DNSMockManager implements DNSManager {

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
        return true;
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
        return true;
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

        if (!contextURL.equals(applicationURL)) {
            StringTokenizer st = new StringTokenizer(contextURL, "/.");

            for (int i = 0; i < 2; i++) {
                subdomain = st.nextToken();
            }
        }
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
        return false;
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

        List<String> result;
        if (maxItems == 0) {
            maxItems = 20;
        }
        result = new ArrayList<>(maxItems);

        for (int i = 0; i < maxItems; i++) {
            result.add("Record Set: " + i);
        }
        return result;
    }

}
