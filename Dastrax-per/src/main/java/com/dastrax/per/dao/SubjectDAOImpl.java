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
import com.dastrax.per.project.DastraxCst;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class SubjectDAOImpl implements SubjectDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(SubjectDAOImpl.class.getName());
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;

    // EJB----------------------------------------------------------------------
    @EJB
    MetierDAO metierDAO;

    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Subject createAdmin(Subject user) throws DuplicateEmailException {

        if (findSubjectByEmail(user.getEmail()) == null) {
            // Email is available so we can go ahead and persist
            user.setEmail(user.getEmail().toLowerCase());
            user.setMetier(metierDAO.findMetierByName(
                    DastraxCst.Metier.ADMIN.toString()));

            user.getAccount().setS3id(UUID.randomUUID().toString());
            user.getAccount().setConfirmed(false);
            user.getAccount().setLocked(false);
            user.getAccount().setCreationEpoch(
                    Calendar.getInstance().getTimeInMillis());
            user.getAccount().setLastPswResetEpoch(
                    Calendar.getInstance().getTimeInMillis());
            em.persist(user);
            return user;
            // TODO Create Audit
        } else {
            // Email already registered
            throw new DuplicateEmailException();
        }
    }

    @Override
    public Subject createVar(Subject user) throws DuplicateEmailException {

        if (findSubjectByEmail(user.getEmail()) == null) {
            // Email is available so we can go ahead and persist
            user.setEmail(user.getEmail().toLowerCase());
            user.setMetier(metierDAO.findMetierByName(
                    DastraxCst.Metier.VAR.toString()));

            user.getAccount().setS3id(UUID.randomUUID().toString());
            user.getAccount().setConfirmed(false);
            user.getAccount().setLocked(false);
            user.getAccount().setCreationEpoch(
                    Calendar.getInstance().getTimeInMillis());
            user.getAccount().setLastPswResetEpoch(
                    Calendar.getInstance().getTimeInMillis());
            em.persist(user);
            user.getCompany().getMembers().add(user);
            em.merge(user);
            return user;
            // TODO Create Audit
        } else {
            // Email already registered
            throw new DuplicateEmailException();
        }
    }

    @Override
    public Subject createClient(Subject user) throws DuplicateEmailException {

        if (findSubjectByEmail(user.getEmail()) == null) {
            // Email is available so we can go ahead and persist
            user.setEmail(user.getEmail().toLowerCase());
            user.setMetier(metierDAO.findMetierByName(
                    DastraxCst.Metier.CLIENT.toString()));

            user.getAccount().setS3id(UUID.randomUUID().toString());
            user.getAccount().setConfirmed(false);
            user.getAccount().setLocked(false);
            user.getAccount().setCreationEpoch(
                    Calendar.getInstance().getTimeInMillis());
            user.getAccount().setLastPswResetEpoch(
                    Calendar.getInstance().getTimeInMillis());
            em.persist(user);
            user.getCompany().getMembers().add(user);
            em.merge(user);
            return user;
            // TODO Create Audit
        } else {
            // Email already registered
            throw new DuplicateEmailException();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void confirm(String uid, String password) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.getAccount().setConfirmed(true);
            s.setPassword(password);
            s.getAccount().setLastPswResetEpoch(
                    Calendar.getInstance().getTimeInMillis());
            // TODO Create Audit
        }
    }

    @Override
    public void updateSessionDetails(String uid) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.getAccount().setLastSessionEpoch(
                    s.getAccount().getCurrentSessionEpoch());
            s.getAccount().setCurrentSessionEpoch(
                    Calendar.getInstance().getTimeInMillis());
            // TODO Create Audit
        }
    }

    @Override
    public void updateName(String uid, String firstName, String lastName) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.getContact().setFirstName(firstName);
            s.getContact().setLastName(lastName);
            // TODO Create Audit
        }
    }

    @Override
    public void updateACL(String uid, boolean acl) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.getAccount().setLocked(acl);
            // TODO Create Audit
        }
    }

    @Override
    public void updateCompany(String uid, Company company) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.getCompany().getMembers().remove(s);
            em.merge(s);
            s.setCompany(company);
            s.getCompany().getMembers().add(s);
            em.merge(s);
            // TODO Create Audit
        }
    }

    @Override
    public void updateAddress(String uid, List<Address> addresses) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.getContact().setAddresses(addresses);
            em.merge(s);
            // TODO Create Audit
        }
    }

    @Override
    public void updateTelephone(String uid, List<Telephone> telephones) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.getContact().setTelephones(telephones);
            em.merge(s);
            // TODO Create Audit
        }
    }

    @Override
    public void updateContact(String uid, Contact contact) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.setContact(contact);
            em.merge(s);
            // TODO Create Audit
        }
    }

    @Override
    public void updateEmail(String uid, String newEmail) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.setEmail(newEmail);
            em.merge(s);
            // TODO Create Audit
        }
    }

    @Override
    public void updatePassword(String uid, String password) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            s.setPassword(password);
            s.getAccount().setLastPswResetEpoch(
                    Calendar.getInstance().getTimeInMillis());
            em.merge(s);
            // TODO Create Audit
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void delete(String uid) {
        Subject s = findSubjectByUid(uid);
        if (s != null) {
            if (s.getCompany() != null) {
                s.getCompany().getMembers().remove(s);
                em.merge(s);
            }
            em.remove(s);
            // TODO Create Audit
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Subject findSubjectByUid(String uid) {
        List<Subject> list = em.createNamedQuery("Subject.findByPK")
                .setParameter("uid", uid)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Subject findSubjectByEmail(String email) {
        List<Subject> list = em.createNamedQuery("Subject.findByEmail")
                .setParameter("email", email)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String findSubjectPasswordByUid(String uid) {
        List<String> list = em.createNamedQuery("Subject.findPWByPK")
                .setParameter("uid", uid)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Subject> findAllSubjects() {
        return em.createNamedQuery("Subject.findAll")
                .getResultList();
    }

    @Override
    public List<Subject> findAllSubjectsByMetier(String metier) {
        return em.createNamedQuery("Subject.findByMetier")
                .setParameter("name", metier)
                .getResultList();
    }

    @Override
    public List<Subject> findSubjectsByNullCompany() {
        return em.createNamedQuery("Subject.findByNullCompany")
                .getResultList();
    }
}
