/*
 * Created Jul 17, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.util;

import com.dastrax.app.security.SessionUser;
import com.dastrax.app.service.internal.DefaultURI;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * The application needs to dynamically generate links to media resources stored
 * on AWS S3. This class provides methods to generate links to the requested
 * resources.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 17, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@RequestScoped
public class PathUtil {

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public PathUtil() {
    }
//</editor-fold>

    /**
     * Generates the URL for the specified icon located on S3. This methods is
     * supposed to be accessed directly form the presentation layer and will
     * return a CDN path. The CDN caches resources at edge locations so changes
     * to images should be version to enable the CDN to aquire the latest
     * version.
     *
     * @param fileName including its extension
     * @param viaCDN
     * @return the full CDN path to the icon
     */
    public String icon(String fileName, boolean viaCDN) {
        return new DefaultURI.Builder(DTX.URIType.ICON)
                .withFile(fileName)
                .withViaCDN(viaCDN)
                .create()
                .generate();
    }

    /**
     * Generates the URL for the specified image located on S3. This methods is
     * supposed to be accessed directly form the presentation layer and will
     * return a CDN path. The CDN caches resources at edge locations so changes
     * to images should be version to enable the CDN to aquire the latest
     * version.
     *
     * @param fileName including its extension
     * @param viaCDN
     * @return the full path to the image
     */
    public String image(String fileName, boolean viaCDN) {
        return new DefaultURI.Builder(DTX.URIType.IMAGE)
                .withFile(fileName)
                .withViaCDN(viaCDN)
                .create()
                .generate();
    }

    /**
     * Generates the URL for the specified logo located on S3. This methods is
     * supposed to be accessed directly form the presentation layer and will
     * return a CDN path. The CDN caches resources at edge locations so changes
     * to images should be version to enable the CDN to aquire the latest
     * version.
     *
     * @param fileName including its extension
     * @param viaCDN
     * @return the full path to the image
     */
    public String logo(String fileName, boolean viaCDN) {
        return new DefaultURI.Builder(DTX.URIType.LOGO)
                .withFile(fileName)
                .withViaCDN(viaCDN)
                .create()
                .generate();
    }

    /**
     * Generates the URL for the authenticated subject profile image located on
     * S3. This methods is supposed to be accessed directly form the
     * presentation layer and will return a direct URL to the resource on S3.
     * The method will determine the subject from Shiro.
     *
     * @param viaCDN
     * @return If a profile image is present the direct URL to the resource on
     * S3 is returned. If no profile image is found a fallback image path is
     * returned.
     */
    public String profileImage(boolean viaCDN) {
        return profileImage(SessionUser.getCurrentUser(), viaCDN);
    }

    /**
     * Generates the URL for the specified subject profile image located on S3.
     * This methods is supposed to be accessed directly form the presentation
     * layer and will return a direct URL to the resource on S3.
     *
     * @param user
     * @param viaCDN
     * @return If a profile image is present the direct URL to the resource on
     * S3 is returned. If no profile image is found a fallback image path is
     * returned.
     */
    public String profileImage(User user, boolean viaCDN) {
        return new DefaultURI.Builder(DTX.URIType.USER_PROFILE_IMAGE)
                .withUser(user)
                .withViaCDN(viaCDN)
                .create()
                .generate();
    }

    /**
     * Generates the URL for the current users company logo.
     *
     * @param viaCDN
     * @return If a logo is present the direct URL to the resource is
     * returned. If no logo is found a fallback image path is returned.
     */
    public String companyLogo(boolean viaCDN) {
        return companyLogo(SessionUser.getCurrentUser().getCompany(), viaCDN);
    }

    /**
     * Generates the URL for the specified company logo.
     *
     * @param company
     * @param viaCDN
     * @return If a logo is present the direct URL to the resource is
     * returned. If no logo is found a fallback image path is returned.
     */
    public String companyLogo(Company company, boolean viaCDN) {
        return new DefaultURI.Builder(DTX.URIType.COMPANY_LOGO)
                .withCompany(company)
                .withViaCDN(viaCDN)
                .create()
                .generate();
    }

}
