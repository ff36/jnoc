/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.cnx.monitor;

import com.dastrax.cnx.pojo.Device;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.entity.core.Site;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import static com.googlecode.cqengine.query.QueryFactory.equal;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 *
 * @version Build 2.0.0
 * @since Sep 27, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class DevicePoll {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(DevicePoll.class.getName());

    // Variables----------------------------------------------------------------
    private final String cacheName = "DEVICE_STATUS_CACHE";
    private final Cache cache;

    // EJB----------------------------------------------------------------------
    @EJB
    DeviceUtil deviceUtil;
    @EJB
    SiteDAO siteDAO;

    // Constructors-------------------------------------------------------------
    public DevicePoll() {
        final CacheManager manager = CacheManager.getInstance();
        cache = manager.getCache(cacheName);
    }

    // Methods------------------------------------------------------------------
    public void cacheDeviceStatus() {
        // Get all the sites
        List<Site> sites = siteDAO.findAllSites();

        // Get all the sites
        for (Site site : sites) {
            // Obtain the devices
            List<Device> devices = deviceUtil.getDeviceTree(site);

            // Create a new element and place it in the cache
            Element element = new Element(site.getId(), devices);
            cache.put(element);

        }
    }

    /**
     * Obtain the cached device tree of a site
     *
     * @param id
     * @return an integer representation of the device status. If it is not
     * possible to determine the status of the site device tree 0 is returned.
     * If all devices are operating properly then 1 is returned. If any
     * individual devices are down then a 2 is returned.
     */
    public int cachedDeviceStatus(String id) {
        Element e = cache.get(id);
        if (e != null) {
            List<Device> lds = (List<Device>) e.getObjectValue();
            if (!lds.isEmpty()) {
                // Create an indexed map to manipulate the data       
                IndexedCollection<Device> devices = CQEngine.newInstance();
                devices.addIndex(NavigableIndex.onAttribute(STATUS));
                devices.addAll((List<Device>) e.getObjectValue());

                // Create the CQEngine Query
                Query<Device> query = equal(STATUS, "FAILED");

                // Execute the query and check if there are any results
                if (devices.retrieve(query).size() > 0) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * Grants CQEngine access to the Device attributes
     */
    public static final Attribute<Device, String> STATUS = new SimpleAttribute<Device, String>("state") {
        @Override
        public String getValue(Device device) {
            return device.getState();
        }
    };
}
