/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.EmailParam;
import java.util.List;
import java.util.logging.Logger;
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
public class EmailParamDAOImpl implements EmailParamDAO {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(EmailParamDAOImpl.class.getName());
    
    // EM Factory---------------------------------------------------------------
    @PersistenceContext(unitName = "Dastrax_SQL_PU")
    private EntityManager em;
    
    // Methods------------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void create(EmailParam param) {
        em.persist(param);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void delete(String token) {
        EmailParam ep = findParamByToken(token);
        em.remove(ep);
    }

    ////////////////////////////////////////////////////////////////////////////
    // QUERY
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public EmailParam findParamByToken(String token) {
        List<EmailParam> list = em.createNamedQuery("EmailParam.findByPK")
                .setParameter("token", token)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public EmailParam findParamByEmail(String email) {
        List<EmailParam> list = em.createNamedQuery("EmailParam.findByEmail")
                .setParameter("email", email)
                .getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

}
