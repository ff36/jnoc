/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.entity.core.Tag;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @version Build 2.0.0
 * @since Sep 16, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class TagDAOImpl implements TagDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(TagDAOImpl.class.getName());
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;

    // EJB----------------------------------------------------------------------
    @EJB
    AuditDAO auditDAO;

    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Tag createTag(Tag tag) {

        if (findTagByName(tag.getName()) == null) {
            // Tag is available so we can go ahead and persist            
            em.persist(tag);
            // Create Audit
            auditDAO.create("Created new Tag: " + tag.getName() + ".");
            return tag;
        } else {
            // Tag already registered
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public Tag findTagById(String id) {
        List<Tag> list = em.createNamedQuery("Tag.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
    @Override
    public Tag findTagByName(String name) {
        List<Tag> list = em.createNamedQuery("Tag.findByName")
                .setParameter("name", name)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
    @Override
    public List<Tag> findAllTags() {
        return em.createNamedQuery("Tag.findAll")
                .getResultList();
    }
    
}
