/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao;

import com.dastrax.per.entity.core.Address;
import com.dastrax.per.entity.core.Site;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @version Build 2.0.0
 * @since Jul 25, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class SiteDAOImpl implements SiteDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(SubjectDAOImpl.class.getName());
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;

    // EJB----------------------------------------------------------------------
    @EJB
    CompanyDAO companyDAO;
    
    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void create(Site site) {
        if (site.getVar() != null) {
            site.getVar().getVarSites().add(site);
        }
        em.merge(site);
        // TODO Create Audit
    }

    ////////////////////////////////////////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void updateName(String id, String name) {
        Site site = findSiteById(id);
        if (site != null) {
            site.setName(name);
        }
    }

    @Override
    public void updateAddress(String id, Address address) {
        Site site = findSiteById(id);
        if (site != null) {
            site.setAddress(address);
            em.merge(site);
        }
    }
    
    @Override
    public void updatePackageType(String id, String type) {
        Site site = findSiteById(id);
        if (site != null) {
            site.setPackageType(type);
            em.merge(site);
        }
    }
    
    @Override
    public void updatePackageResponse(String id, int hrs) {
        Site site = findSiteById(id);
        if (site != null) {
            site.setResponseHrs(hrs);
            em.merge(site);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void delete(String id) {
        Site s = findSiteById(id);
        if (s != null) {
            s.getClient().getClientSites().remove(s);
            s.getVar().getVarSites().remove(s);
            em.merge(s);
            em.remove(s);
            // TODO Create Audit
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Site findSiteById(String id) {
        List<Site> list = em.createNamedQuery("Site.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Site> findAllSites() {
        return em.createNamedQuery("Site.findAll")
                .getResultList();
    }

    @Override
    public List<Site> findSitesByNullVar() {
        return em.createNamedQuery("Site.findByNullVar")
                .getResultList();
    }

    @Override
    public List<Site> findSitesByNullClient(String varId) {
        return em.createNamedQuery("Site.findByNullClient")
                .setParameter("varId", varId)
                .getResultList();
    }
}
