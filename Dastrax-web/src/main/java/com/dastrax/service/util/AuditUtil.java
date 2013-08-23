/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.util;

import com.dastrax.app.util.TemporalUtil;
import com.dastrax.per.dao.core.AuditDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Audit;
import com.dastrax.per.entity.core.Subject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @version Build 2.0.0
 * @since Aug 8, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class AuditUtil implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(AuditUtil.class.getName());

    // Variables----------------------------------------------------------------
    private List<Audit> audits = new ArrayList<>();

    // EJB----------------------------------------------------------------------
    @EJB
    AuditDAO auditDAO;
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    TemporalUtil temporalUtil;

    // Constructors-------------------------------------------------------------
    @PostConstruct
    private void setAudits() {
        audits = auditDAO.findRecent(10);
        sortAudits();
    }

    // Getters------------------------------------------------------------------
    public List<Audit> getAudits() {
        return audits;
    }

    public TemporalUtil getTemporalUtil() {
        return temporalUtil;
    }

    // Setters------------------------------------------------------------------
    public void setAudits(List<Audit> audits) {
        this.audits = audits;
    }

    // Methods------------------------------------------------------------------
    /*
     * Places the retreived values into chronological order with the most recent
     * first.
     */
    private void sortAudits() {
        Collections.sort(audits,
                new Comparator<Audit>() {
                    @Override
                    public int compare(Audit c1, Audit c2) {
                        if (c1.getCreated() == c2.getCreated()) {
                            return 0;
                        } else if (c1.getCreated() > c2.getCreated()) {
                            return -1;
                        }
                        return 1;
                    }
                }
                );
    }

    /**
     * The NoSQL DB only stored subjects as UID String references. When the 
     * subject needs to be displayed we need to convert them back into a
     * Subject
     * @param id
     * @return The subject represented by the specified ID
     */
    public Subject author(String id) {
        return subjectDAO.findSubjectByUid(id);
    }
}
