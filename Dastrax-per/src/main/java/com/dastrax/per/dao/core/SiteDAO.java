/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Address;
import com.dastrax.per.entity.core.Site;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @version Build 2.0.0
 * @since Jul 25, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface SiteDAO {

    /**
     * 
     * @param site 
     */
    public void create(Site site);
    /**
     * 
     * @param id
     * @param name 
     */
    public void updateName(String id, String name);
    /**
     * 
     * @param id
     * @param address 
     */
    public void updateAddress(String id, Address address);
    /**
     * 
     * @param id
     * @param type 
     */
    public void updatePackageType(String id, String type);
    /**
     * 
     * @param id
     * @param hrs 
     */
    public void updatePackageResponse(String id, int hrs);
    /**
     * 
     * @param id 
     */
    public void delete(String id);
    /**
     * 
     * @param id
     * @return Subject if one exists otherwise null.
     */
    public Site findSiteById(String id);
    /**
     * 
     * @return List of all registered Sites, if none exist the list is empty. 
     */
    public List<Site> findAllSites();
    /**
     * 
     * @return list of sites that are not allocated to a VAR
     */
    public List<Site> findSitesByNullVar();
    /**
     * 
     * @param varId
     * @return 
     */
    public List<Site> findSitesByNullClient(String varId);
    /**
     * 
     * @param dmsIP
     * @return site with the specified IP or null if none exists
     */
    public Site findSiteByIP(String dmsIP);
    /**
     * 
     * @return CriteriaBuilder
     */
    public CriteriaBuilder criteriaBuilder();
    /**
     * 
     * @param accountQuery
     * @param first
     * @param pageSize
     * @return the tickets that match the dynamic Criteria Query, the first record
     * address and the quantity specified by the page size.
     */
    public List<Site> lazyLoadTable(CriteriaQuery accountQuery, int first, int pageSize);
    /**
     * 
     * @param countQuery
     * @return returns the quantity of results from the dynamic Criteria Query
     */
    public int lazyLoadRowCount(CriteriaQuery countQuery);
}
