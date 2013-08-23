/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.EmailTemplate;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @version Build 2.0.0
 * @since Jul 14, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class EmailTemplateDAOImpl implements EmailTemplateDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(EmailTemplateDAOImpl.class.getName());
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;

    // EJB----------------------------------------------------------------------
    @EJB
    AuditDAO auditDAO;
    
    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public EmailTemplate findTemplateById(String id) {
        List<EmailTemplate> list = em.createNamedQuery("EmailTemplate.findByPK")
                .setParameter("id", id)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

}
