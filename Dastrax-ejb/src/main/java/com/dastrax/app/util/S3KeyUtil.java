/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.app.util;

import com.dastrax.per.dao.core.CompanyDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.project.DastraxCst;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @version Build 2.0.0
 * @since Jul 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class S3KeyUtil {

    // Project Stage------------------------------------------------------------
    private final String s3Bucket = ResourceBundle.getBundle("Config").getString("S3Bucket");
    
    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    CompanyDAO companyDAO;
    
    // Methods------------------------------------------------------------------
     /**
     * Generates the S3 Key name for a profile graphic.
     *
     * @param email
     * @return If a valid email is provided an array is returned containing the
     * s3 bucket at index [0] and the object key at index [1]. If the email is
     * invalid null is returned. IMPORANT: No guarantee is provided that the
     * resource exists! This is simply the theoretical path to the resource.
     */
    public String[] profileGraphic(String email) {

        String key = DastraxCst.S3_SUBJECTS_DIRECTORY
                + "/"
                + subjectDAO.findSubjectByEmail(email).getAccount().getS3id()
                + "/"
                + DastraxCst.S3_SUBJECT_PROFILE_DIRECTORY
                + "/"
                + DastraxCst.S3_SUBJECT_PROFILE_GRAPHIC;

        String[] completeKey = {s3Bucket, key};
        return completeKey;
    }
    
    /**
     * Generates the S3 Key name for a company logo.
     *
     * @param companyId
     * @return If a valid id is provided an array is returned containing the
     * s3 bucket at index [0] and the object key at index [1]. If the id is
     * invalid null is returned. IMPORANT: No guarantee is provided that the
     * resource exists! This is simply the theoretical path to the resource.
     */
    public String[] companyLogo(String companyId) {

        String key = DastraxCst.S3_COMPANIES_DIRECTORY
                + "/"
                + companyDAO.findCompanyById(companyId).getS3id()
                + "/"
                + DastraxCst.S3_COMPANY_LOGOS_DIRECTORY
                + "/"
                + DastraxCst.S3_COMPANY_LOGO;

        String[] completeKey = {s3Bucket, key};
        return completeKey;
    }
    
    /**
     * Generates the theoretical S3 key name for subjects S3 account storage
     * @param uid
     * @return the complete key to the subjects S3 account storage directory
     */
    public String subjectDir(String uid) {
        return s3Bucket
                + "/"
                + DastraxCst.S3_SUBJECTS_DIRECTORY
                + subjectDAO.findSubjectByUid(uid).getAccount().getS3id();
    }
    
    /**
     * Generates the theoretical S3 key name for companies S3 account storage
     * @param id
     * @return the complete key to the companies S3 account storage directory
     */
    public String companyDir(String id) {
        return s3Bucket
                + "/"
                + DastraxCst.S3_COMPANIES_DIRECTORY
                + companyDAO.findCompanyById(id).getS3id();
    }

}
