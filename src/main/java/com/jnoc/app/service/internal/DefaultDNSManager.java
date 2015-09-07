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


/*
 * Created Jul 17, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 

 */
package com.jnoc.app.service.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Default;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
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
import com.jnoc.app.misc.JsfUtil;
import com.jnoc.app.services.DNSManager;

/**
 * Methods dedicated to handling Domain Name System (DNSManager) based
 * functions. The AWS Route 53 DNSManager service offers anAPI to control the
 * DNSManager records.
 *
 * @version 1.0.0
 * @since Build 1.0.0 (Jul 17, 2013)
 * @author Tarka L'Herpiniere

 */
@Default
public class DefaultDNSManager implements DNSManager {

	// <editor-fold defaultstate="collapsed" desc="Properties">
	private static final Logger LOG = Logger.getLogger(DefaultDNSManager.class
			.getName());
	private final AmazonRoute53 route53 = new Route53Client()
			.getRoute53Client();
	private final String value = System.getenv("JNOC_BASE_URL");
	private final String name = "." + value;
	private final String zoneId = System.getenv("JNOC_ROUTE53_ZONE_ID");

	// </editor-fold>

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

			route53.changeResourceRecordSets(new ChangeResourceRecordSetsRequest()
					.withHostedZoneId(zoneId).withChangeBatch(changeBatch));

			return true;

		} else {
			JsfUtil.addWarningMessage(ResourceBundle.getBundle("source-bundle")
					.getString("valid.dns.exists"));
			return false;
		}
	}

	/**
	 * Delete the specified sub-domain CNAME record from route 53.
	 *
	 * @param prefix
	 * @return true if the CNAME is removed from route 53 or the record set does
	 *         not exist
	 */
	@Override
	public boolean deleteCNAME(String prefix) {

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

			route53.changeResourceRecordSets(new ChangeResourceRecordSetsRequest()
					.withHostedZoneId(zoneId).withChangeBatch(changeBatch));
			// TODO: This should check the ChangeResourceRecordSetsResult
			return true;

		} else {
			return true;
		}

	}

	/**
	 * Extracts the string prefixing the first period in the URL.
	 *
	 * <b>example.com</b> will return <b>null</b>. <b>abcde.example.com</b> will
	 * return <b>abcde</b>
	 *
	 * @param contextURL
	 * @return the CNAME prefix if it exists and is valid. Otherwise null
	 */
	@Override
	public String extract(String contextURL) {

		String subdomain = null;

		// Determin if the context contains a prefix
		String baseUrl = System.getenv("JNOC_BASE_URL");
		String accessProtocol = System.getenv("JNOC_ACCESS_PROTOCOL");
		String applicationURL = accessProtocol + baseUrl + "/";

		if (!contextURL.equals(applicationURL)) {
			StringTokenizer st = new StringTokenizer(contextURL, "/.");

			for (int i = 0; i < 2; i++) {
				subdomain = st.nextToken();
			}

			// Check to make sure the CNAME string is not UAT
			if (subdomain == null || subdomain.equals("uat")) {
				subdomain = null;
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

		// Generate a query to find if the record set exists
		ListResourceRecordSetsRequest lrrsr = new ListResourceRecordSetsRequest();
		lrrsr.setHostedZoneId(zoneId);
		lrrsr.setMaxItems("1");
		lrrsr.setStartRecordName(prefix + name);

		try {
			// Request the query
			ListResourceRecordSetsResult lrrsr1 = route53
					.listResourceRecordSets(lrrsr);

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

	/**
	 * Retrieves a list of existing record sets on route 53 with the system
	 * zoneId.
	 *
	 * @param maxItems
	 *            For all record Sets maxItems should be set to 0.
	 * @return A list of all existing record sets from route 53. The list can be
	 *         empty. If an exception is encountered null is returned.
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
			ListResourceRecordSetsResult lrrsr1 = route53
					.listResourceRecordSets(lrrsr);

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
	
	 */
	protected class Route53Client {

		// <editor-fold defaultstate="collapsed" desc="Properties">
		private AmazonRoute53Client client = null;

		// </editor-fold>

		// <editor-fold defaultstate="collapsed" desc="Constructors">
		public Route53Client() {
			client = new AmazonRoute53Client(new EnvironmentVariableCredentialsProvider());
		}

		// </editor-fold>

		// <editor-fold defaultstate="collapsed" desc="Getters">
		public AmazonRoute53Client getRoute53Client() {
			return client;
		}
		// </editor-fold>

	}

}
