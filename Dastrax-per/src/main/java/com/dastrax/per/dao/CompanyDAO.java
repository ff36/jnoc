/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao;

import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Contact;
import com.dastrax.per.entity.core.Site;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @version Build 2.0.0
 * @since Jul 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface CompanyDAO {

    /**
     * 
     * @param company 
     * @return  
     */
    public Company createVar(Company company);
    /**
     * 
     * @param company 
     * @return  
     */
    public Company createClient(Company company);
    /**
     * 
     * @param id
     * @param name 
     */
    public void updateName(String id, String name);
    /**
     * 
     * @param id
     * @param contacts 
     */
    public void updateContacts(String id, List<Contact> contacts);
    /**
     * 
     * @param id 
     * @param varSites 
     */
    public void updateVarSites(String id, List<Site> varSites);
    /**
     * 
     * @param id 
     * @param clientSites 
     */
    public void updateClientSites(String id, List<Site> clientSites);
    /**
     * 
     * @param id 
     * @param clients 
     */
    public void updateVarClients(String id, List<Company> clients);
    /**
     * 
     * @param id 
     */
    public void delete(String id);
    /**
     * 
     * @param id
     * @return Company if one exists otherwise null. 
     */
    public Company findCompanyById(String id);
    /**
     * 
     * @return List of all companies 
     */
    public List<Company> findAllCompanies();
    /**
     * 
     * @param subdomain
     * @return 
     */
    public Company findCompanyBySubdomain(String subdomain);
    /**
     * 
     * @param type
     * @return 
     */
    public List<Company> findCompaniesByType(String type);
    /**
     * 
     * @param type
     * @return list of companies that are not allocated parent VARs
     */
    public List<Company> findByNullParentVar(String type);
}
