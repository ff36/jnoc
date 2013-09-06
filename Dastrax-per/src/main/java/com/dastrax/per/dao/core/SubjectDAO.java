/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Contact;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.exception.DuplicateEmailException;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface SubjectDAO {
    
    /**
     * 
     * @param user
     * @return 
     * @throws DuplicateEmailException 
     */
    public Subject createAdmin(Subject user) throws DuplicateEmailException;
    /**
     * 
     * @param user
     * @return 
     * @throws DuplicateEmailException 
     */
    public Subject createVar(Subject user) throws DuplicateEmailException;
    /**
     * 
     * @param user
     * @return 
     * @throws DuplicateEmailException 
     */
    public Subject createClient(Subject user) throws DuplicateEmailException;
    /**
     * 
     * @param uid
     * @param password 
     */
    public void confirm(String uid, String password);
    /**
     * 
     * @param uid 
     */
    public void updateSessionDetails(String uid);
    /**
     * 
     * @param uid 
     * @param firstName 
     * @param lastName 
     */
    public void updateName(String uid, String firstName, String lastName);
    /**
     * 
     * @param uid
     * @param acl
     */
    public void updateACL(String uid, boolean acl);
    /**
     * 
     * @param uid
     * @param company 
     */
    public void updateCompany(String uid, Company company);
    /**
     * 
     * @param subject
     */
    public void updateAddress(Subject subject);
    /**
     * 
     * @param subject 
     */
    public void updateTelephone(Subject subject);
    /**
     * 
     * @param uid
     * @param contact 
     */
    public void updateContact(String uid, Contact contact);
    /**
     * 
     * @param uid
     * @param newEmail 
     */
    public void updateEmail(String uid, String newEmail);
    /**
     * 
     * @param uid
     * @param password (Shiro encrypted password)
     */
    public void updatePassword(String uid, String password);
    /**
     * 
     * @param subject
     */
    public void updatePermissions(Subject subject);
    /**
     * 
     * @param uid 
     */
    public void delete(String uid);
    /**
     * 
     * @param uid
     * @return Subject if one exists otherwise null. 
     */
    public Subject findSubjectByUid(String uid);
    /**
     * 
     * @param email
     * @return Subject if one exists otherwise null. 
     */
    public Subject findSubjectByEmail(String email);
    /**
     * 
     * @param uid
     * @return String representation of the subjects encrypted password if one 
     * exists otherwise null. 
     */
    public String findSubjectPasswordByUid(String uid);
    /**
     * 
     * @return List of all registered Subject, if none exist the list is empty. 
     */
    public List<Subject> findAllSubjects();
    /**
     *
     * @param metier
     * @return List of all Subject with the specified Metier, if none exist the list is empty.
     */
    public List<Subject> findAllSubjectsByMetier(String metier);
    /**
     * 
     * @return list of subjects that are not allocated companies
     */
    public List<Subject> findSubjectsByNullCompany();
    /**
     * 
     * @param id
     * @return all subjects that belong to the specified company
     */
    public List<Subject> findAllSubjectsByCompany(String id);
    /**
     * 
     * @return all existing subject emails
     */
    public List<String> findAllSubjectEmails();
    /**
     * 
     * @param companies
     * @return all subjects that belong to any of the specified companies
     */
    public List<Subject> findAllChildSubjectsByCompany(List<String> companies);
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
    public List<Subject> lazyLoadTable(CriteriaQuery accountQuery, int first, int pageSize);
    /**
     * 
     * @param countQuery
     * @return returns the quantity of results from the dynamic Criteria Query
     */
    public int lazyLoadRowCount(CriteriaQuery countQuery);
}
