/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao;

import com.dastrax.per.entity.core.Address;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.entity.core.Contact;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.core.Telephone;
import com.dastrax.per.exception.DuplicateEmailException;
import java.util.List;
import javax.ejb.Local;

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
     * @param uid
     * @param addresses 
     */
    public void updateAddress(String uid, List<Address> addresses);
    /**
     * 
     * @param uid
     * @param telephones 
     */
    public void updateTelephone(String uid, List<Telephone> telephones);
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
}
