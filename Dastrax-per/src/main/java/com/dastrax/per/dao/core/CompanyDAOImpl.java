/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Contact;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.project.DastraxCst;
import java.util.List;
import java.util.UUID;
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
 * @since Jul 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class CompanyDAOImpl implements CompanyDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(CompanyDAOImpl.class.getName());
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;

    // EJB----------------------------------------------------------------------
    @EJB
    AuditDAO auditDAO;

    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Company createVar(Company company) {
        company.setS3id(UUID.randomUUID().toString());
        company.setType(DastraxCst.CompanyType.VAR.toString());
        em.merge(company);
        for (Site s : company.getVarSites()) {
            s.setVar(company);
        }
        em.merge(company);
        // Create Audit
        auditDAO.create("Created new VAR company: " + company.getName() + ".");
        return company;
    }

    @Override
    public Company createClient(Company company) {
        company.setS3id(UUID.randomUUID().toString());
        company.setType(DastraxCst.CompanyType.CLIENT.toString());
        em.merge(company);
        for (Site s : company.getClientSites()) {
            s.setClient(company);
        }
        company.getParentVAR().getClients().add(company);
        em.merge(company);
        // Create Audit
        auditDAO.create("Created new Client company: " + company.getName() + ".");
        return company;
    }

    ////////////////////////////////////////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void updateName(String id, String name) {
        Company company = findCompanyById(id);
        if (company != null) {
            company.setName(name);
        }
        // Create Audit
        auditDAO.create("Updated company name: " + name + ".");
    }

    @Override
    public void updateContacts(String id, List<Contact> contacts) {
        Company company = findCompanyById(id);
        if (company != null) {
            company.setContacts(contacts);
            em.merge(company);
        }
        // Create Audit
        auditDAO.create("Updated company contacts.");
    }

    @Override
    public void updateVarSites(String id, List<Site> varSites) {
        Company company = findCompanyById(id);
        if (company != null) {
            for (Site site : company.getVarSites()) {
                site.setVar(null);
            }
            em.merge(company);
            company.setVarSites(varSites);
            for (Site site : company.getVarSites()) {
                site.setVar(company);
            }
            em.merge(company);
        }
        // Create Audit
        auditDAO.create("Updated VAR sites.");
    }

    @Override
    public void updateClientSites(String id, List<Site> clientSites) {
        Company company = findCompanyById(id);
        if (company != null) {
            for (Site site : company.getClientSites()) {
                site.setClient(null);
            }
            em.merge(company);
            company.setClientSites(clientSites);
            for (Site site : company.getClientSites()) {
                site.setClient(company);
            }
            em.merge(company);
        }
        // Create Audit
        auditDAO.create("Updated Client sites.");
    }

    @Override
    public void updateVarClients(String id, List<Company> clients) {
        Company company = findCompanyById(id);
        if (company != null) {
            for (Company client : company.getClients()) {
                client.setParentVAR(null);
            }
            em.merge(company);
            company.setClients(clients);
            for (Company client : company.getClients()) {
                client.setParentVAR(company);
            }
            em.merge(company);
        }
        // Create Audit
        auditDAO.create("Updated VAR Clients.");
    }
    ////////////////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void delete(String id) {
        Company c = findCompanyById(id);
        if (c != null) {
            for (Subject s : c.getMembers()) {
                s.setCompany(null);
            }
            if (c.getType().equals(DastraxCst.CompanyType.VAR.toString())) {
                for (Site s : c.getVarSites()) {
                    s.setVar(null);
                }
                for (Company cc : c.getClients()) {
                    cc.setParentVAR(null);
                    for (Site s : cc.getClientSites()) {
                        s.setClient(null);
                    }
                    cc.setClientSites(null);
                }
                c.setClients(null);
            }
            if (c.getType().equals(DastraxCst.CompanyType.CLIENT.toString())) {
                for (Site s : c.getClientSites()) {
                    s.setClient(null);
                }
                c.setParentVAR(null);
            }
            // Create Audit
            auditDAO.create("Deleted company (" + c.getName() + ")");
            em.merge(c);
            em.remove(c);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Company findCompanyById(String id
            ) {
        List<Company> list = em.createNamedQuery("Company.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Company> findAllCompanies() {
        return em.createNamedQuery("Company.findAll")
                .getResultList();

    }

    @Override
    public Company findCompanyBySubdomain(String subdomain
            ) {
        List<Company> list = em.createNamedQuery("Company.findBySubdomain")
                .setParameter("subdomain", subdomain)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Company> findCompaniesByType(String type
            ) {
        return em.createNamedQuery("Company.findByType")
                .setParameter("type", type)
                .getResultList();
    }

    @Override
    public List<Company> findByNullParentVar(String type
            ) {
        return em.createNamedQuery("Company.findByNullParentVar")
                .setParameter("type", type)
                .getResultList();
    }
    
    @Override
    public CriteriaBuilder criteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    @Override
    public List<Company> lazyLoadTable(CriteriaQuery ticketQuery, int first, int pageSize) {
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
