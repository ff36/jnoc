/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.cnx.monitor;

import com.dastrax.cnx.pojo.Device;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.entity.core.Site;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;
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
@Startup
public class DevicePoll {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(DevicePoll.class.getName());

    // Variables----------------------------------------------------------------
    private final String cacheName = "DEVICE_STATUS_CACHE";
    private Cache cache;

    // EJB----------------------------------------------------------------------
    @EJB
    DeviceUtil deviceUtil;
    @EJB
    SiteDAO siteDAO;

    // Constructors-------------------------------------------------------------
//    public DevicePoll() {
//        final CacheManager manager = CacheManager.getInstance();
//        cache = manager.getCache(cacheName);
//    }
//
//    // Methods------------------------------------------------------------------
//    @Schedule(minute = "*/2", hour = "*")
//    protected void getStatus() {
//        // Get all the sites
//        List<Site> sites = siteDAO.findAllSites();
//
//        Element e = cache.get("201");
//
//        // Get all the sites
//        for (Site site : sites) {
//            // Obtain the devices
//            List<Device> devices = deviceUtil.getDeviceTree(site);
//
//            // Create a new element and place it in the cache
//            Element element = new Element(site.getId(), devices);
//            cache.put(element);
//
//        }
//    }
}
