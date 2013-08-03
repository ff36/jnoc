/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.app.util;

import com.dastrax.per.dao.CompanyDAO;
import com.dastrax.per.dao.SubjectDAO;
import com.dastrax.per.entity.core.Company;
import com.dastrax.per.project.DastraxCst;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.NoResultException;

/**
 *
 * @version Build 2.0.0
 * @since Jul 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class UriUtil {

    // Project Stage------------------------------------------------------------
    private final String s3Bucket = ResourceBundle.getBundle("Config").getString("S3Bucket");
    private final String baseURL = ResourceBundle.getBundle("Config").getString("S3PublicBaseUrl");
    private final String protocol = ResourceBundle.getBundle("Config").getString("AccessProtocol");
    private final String cdn = ResourceBundle.getBundle("Config").getString("CDNHTTPBaseUrl");

    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    CompanyDAO companyDAO;

    // Methods------------------------------------------------------------------    
    /**
     * Generates the full URL to a subject profile graphic on S3 from the UUID
     *
     * @param uid
     * @return If a profile image is present the direct URL to the resource on
     * S3 is returned. If no profile image is found a fallback image path is
     * returned.
     */
    public String profileImage(String uid) {

        // Generate the theoretical URL
        String url = protocol
                + baseURL
                + "/"
                + s3Bucket
                + "/"
                + DastraxCst.S3_SUBJECTS_DIRECTORY
                + "/"
                + subjectDAO.findSubjectByUid(uid).getAccount().getS3id()
                + "/"
                + DastraxCst.S3_SUBJECT_PROFILE_DIRECTORY
                + "/"
                + DastraxCst.S3_SUBJECT_PROFILE_GRAPHIC;

        /*
         * Check to see if there is anything at the theorectical URL. If not
         * the the subject hasn't yet added a graphic so we want to set the
         * URL to display the fallback graphic.
         */
        if (!urlExists(url)) {
            url = protocol
                    + baseURL
                    + "/"
                    + s3Bucket
                    + "/"
                    + DastraxCst.S3_CORE_DIRECTORY
                    + "/"
                    + DastraxCst.S3_CORE_GRAPHICS_DIRECTORY
                    + "/"
                    + DastraxCst.S3_CORE_IMAGES_DIRECTORY
                    + "/"
                    + DastraxCst.S3_CORE_PROFILE_GRAPHIC_HOLDER;
        }
        return url;
    }

    /**
     * Generates the full URL to a clinic logo graphic on S3 from the clinic ID
     *
     * @param companyId
     * @return If a clinic logo is present the direct URL to the resource on S3
     * is returned. If no clinic logo is found a fallback image path is
     * returned.
     */
    public String companyLogo(String companyId) throws NoResultException {

        // Generate the theoretical URL
         String url = protocol
                    + baseURL
                    + "/"
                    + s3Bucket
                    + "/"
                    + DastraxCst.S3_COMPANIES_DIRECTORY
                    + "/"
                    + companyDAO.findCompanyById(companyId)
                    + "/"
                    + DastraxCst.S3_COMPANY_LOGOS_DIRECTORY
                    + "/"
                    + DastraxCst.S3_COMPANY_LOGO;

        /*
         * Check to see if there is anything at the theorectical URL. If not
         * theh the subject hasn't yet added a graphic so we want to set the
         * URL to display the fallback graphic.
         */
        if (!urlExists(url)) {
            url = protocol
                    + baseURL
                    + "/"
                    + s3Bucket
                    + "/"
                    + DastraxCst.S3_CORE_DIRECTORY
                    + "/"
                    + DastraxCst.S3_CORE_GRAPHICS_DIRECTORY
                    + "/"
                    + DastraxCst.S3_CORE_IMAGES_DIRECTORY
                    + "/"
                    + DastraxCst.S3_CORE_COMPANY_LOGO_HOLDER;
        }

        return url;

    }

    /**
     * Generates the full URL to a clinic logo graphic on S3 from the clinic ID
     *
     * @param company
     * @return If a clinic logo is present the direct URL to the resource on S3
     * is returned. If no clinic logo is found a fallback image path is
     * returned.
     */
    public String companyLogo(Company company) throws NoResultException {

        // Generate the theoretical URL
        String url = protocol
                + baseURL
                + "/"
                + s3Bucket
                + "/"
                + DastraxCst.S3_COMPANIES_DIRECTORY
                + "/"
                + company.getS3id()
                + "/"
                + DastraxCst.S3_COMPANY_LOGOS_DIRECTORY
                + "/"
                + DastraxCst.S3_COMPANY_LOGO;

        /*
         * Check to see if there is anything at the theorectical URL. If not
         * theh the subject hasn't yet added a graphic so we want to set the
         * URL to display the fallback graphic.
         */
        if (!urlExists(url)) {
            url = protocol
                    + baseURL
                    + "/"
                    + s3Bucket
                    + "/"
                    + DastraxCst.S3_CORE_DIRECTORY
                    + "/"
                    + DastraxCst.S3_CORE_GRAPHICS_DIRECTORY
                    + "/"
                    + DastraxCst.S3_CORE_IMAGES_DIRECTORY
                    + "/"
                    + DastraxCst.S3_CORE_COMPANY_LOGO_HOLDER;
        }
        return url;
    }

    /**
     * Performs an external HTTP URL request to see if a file is present at the
     * specified URL. Only the header is returned and the HTTP status is used to
     * determine if the resource is present.
     *
     * @param Url
     * @return true if the resource exists, otherwise false
     */
    public boolean urlExists(String Url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(Url).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            return false;
        }
    }

}
