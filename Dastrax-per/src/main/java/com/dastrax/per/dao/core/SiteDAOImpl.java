/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Address;
import com.dastrax.per.entity.core.Site;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

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
    @EJB
    AuditDAO auditDAO;

    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void create(Site site) {
        em.persist(site);
        if (site.getVar() != null) {
            if (site.getVar().getVarSites() == null) {
                site.getVar().setVarSites(new ArrayList<Site>());
            }
            site.getVar().getVarSites().add(site);
        }
        em.merge(site);
        // Create Audit
        auditDAO.create("Created new Site: " + site.getName() + ".");
    }

    ////////////////////////////////////////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void updateName(String id, String name) {
        Site site = findSiteById(id);
        if (site != null) {
            site.setName(name);
            // Create Audit
            auditDAO.create("Updated Site name to " + name + ".");
        }
    }

    @Override
    public void updateAddress(String id, Address address) {
        Site site = findSiteById(id);
        if (site != null) {
            site.setAddress(address);
            em.merge(site);
            // Create Audit
            auditDAO.create("Updated " + site.getName() + " address.");
        }
    }

    @Override
    public void updatePackageType(String id, String type) {
        Site site = findSiteById(id);
        if (site != null) {
            site.setPackageType(type);
            em.merge(site);
            // Create Audit
            auditDAO.create("Updated " + site.getName() + " support type to " + type + ".");
        }
    }

    @Override
    public void updatePackageResponse(String id, int hrs) {
        Site site = findSiteById(id);
        if (site != null) {
            site.setResponseHrs(hrs);
            em.merge(site);
            // Create Audit
            auditDAO.create("Updated " + site.getName() + " response time to " + hrs + " hrs.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void delete(String id) {
        Site s = findSiteById(id);
        if (s != null) {
            if (s.getClient() != null) {
                s.getClient().getClientSites().remove(s);
            }
            if (s.getVar() != null) {
                s.getVar().getVarSites().remove(s);
            }
            
            // Create Audit
            auditDAO.create("Deleted " + s.getName() + ".");
            em.merge(s);
            em.remove(s);
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
    
    @Override
    public Site findSiteByIP(String dmsIP) {
        List<Site> list = em.createNamedQuery("Site.findByIP")
                .setParameter("dmsIP", dmsIP)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
    @Override
    public CriteriaBuilder criteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    @Override
    public List<Site> lazyLoadTable(CriteriaQuery ticketQuery, int first, int pageSize) {
        return em.createQuery(ticketQuery)
                .setFirstResult(first)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public int lazyLoadRowCount(CriteriaQuery countQuery) {
        return em.createQuery(countQuery).getResultList().size();
    }

}
