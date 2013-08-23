/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.app.util;

import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Nexus;
import com.dastrax.per.entity.core.Subject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import org.apache.shiro.SecurityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @version Build 2.0.0
 * @since Aug 7, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class NexusUtil {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(NexusUtil.class.getName());

    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    ExceptionUtil exu;

    /**
     * Determine whether the subject is a member of the nexus
     *
     * @param nexus
     * @return true if he the are otherwise false.
     */
    public boolean authorised(Nexus nexus) {
        Subject subject = subjectDAO.findSubjectByUid(
                SecurityUtils.getSubject().getPrincipals()
                .asList().get(1).toString());
        return evaluateIMP(subject, nexus) | evaluateEXP(subject, nexus);
    }

    /**
     * Privately evaluates explicite subjects in the nexus
     * @param subject
     * @param nexus
     * @return 
     */
    private boolean evaluateEXP(Subject subject, Nexus nexus) {
        if (nexus != null) {
            return nexus.getMembersEXP().contains(subject);
        } else {
            return true;
        }
    }

    /**
     * Privately evaluates implicit subjects in the nexus
     * @param subject
     * @param nexus
     * @return 
     */
    private boolean evaluateIMP(Subject subject, Nexus nexus) {

        if (nexus != null) {
            if (nexus.getMembersIMP() != null) {
                Map<String, List<String>> jsonExp;
                try {
                    // Get a new JSON converter from Jackson
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                    mapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
                    jsonExp = mapper.readValue(nexus.getMembersIMP(), Map.class);

                    for (Map.Entry<String, List<String>> entry : jsonExp.entrySet()) {
                        if (entry.getKey().equals("metier")) {
                            return entry.getValue().contains(subject.getMetier().getId());
                        }
                    }
                } catch (IOException ioe) {
                    exu.report(ioe);
                    LOG.log(Level.SEVERE, "SQS IOException", ioe);
                }
            } else {
                // If there is no implicit condition then the subject is authorised
                return true;
            }
        } else {
            // nexus is null
            return true;
        }
        return false;
    }
}
