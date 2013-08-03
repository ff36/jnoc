/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.util;

import com.dastrax.per.dao.SubjectDAO;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.project.DastraxCst;
import com.dastrax.app.util.UriUtil;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @version Build 2.0.0
 * @since Jul 17, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@RequestScoped
public class PathUtil {

    // Project Stage------------------------------------------------------------
    private final String cdn = ResourceBundle.getBundle("Config").getString("CDNHTTPBaseUrl");
    private final String protocol = ResourceBundle.getBundle("Config").getString("AccessProtocol");

    // EJB----------------------------------------------------------------------
    @EJB
    UriUtil uriUtil;
    @EJB
    SubjectDAO subjectDAO;

    // Methods------------------------------------------------------------------
    /**
     * Generates the URL for the specified icon located on S3. This methods is
     * supposed to be accessed directly form the presentation layer and will
     * return a CDN path. The CDN caches resources at edge locations so changes
     * to images should be versioned to enable the CDN to aquire the latest
     * version.
     *
     * @param iconFileName including its extension
     * @return the full CDN path to the icon
     */
    public String s3Icon(String iconFileName) {

        return protocol
                + cdn
                + "/"
                + DastraxCst.S3_CORE_DIRECTORY
                + "/"
                + DastraxCst.S3_CORE_GRAPHICS_DIRECTORY
                + "/"
                + DastraxCst.S3_CORE_ICONS_DIRECTORY
                + "/"
                + iconFileName;
    }

    /**
     * Generates the URL for the specified image located on S3. This methods is
     * supposed to be accessed directly form the presentation layer and will
     * return a CDN path. The CDN caches resources at edge locations so changes
     * to images should be versioned to enable the CDN to aquire the latest
     * version.
     *
     * @param imageFileName including its extension
     * @return the full path to the image
     */
    public String s3Image(String imageFileName) {

        return protocol
                + cdn
                + "/"
                + DastraxCst.S3_CORE_DIRECTORY
                + "/"
                + DastraxCst.S3_CORE_GRAPHICS_DIRECTORY
                + "/"
                + DastraxCst.S3_CORE_IMAGES_DIRECTORY
                + "/"
                + imageFileName;
    }

    /**
     * Generates the URL for the specified logo located on S3. This methods is
     * supposed to be accessed directly form the presentation layer and will
     * return a CDN path. The CDN caches resources at edge locations so changes
     * to images should be versioned to enable the CDN to aquire the latest
     * version.
     *
     * @param logoFileName including its extension
     * @return the full path to the image
     */
    public String s3Logo(String logoFileName) {

        return protocol
                + cdn
                + "/"
                + DastraxCst.S3_CORE_DIRECTORY
                + "/"
                + DastraxCst.S3_CORE_GRAPHICS_DIRECTORY
                + "/"
                + DastraxCst.S3_CORE_LOGOS_DIRECTORY
                + "/"
                + logoFileName;
    }

    /**
     * Generates the URL for the authenticated subject profile image located on
     * S3. This methods is supposed to be accessed directly form the
     * presentation layer and will return a direct URL to the resource on S3.
     * The method will determine the subject from Shiro.
     *
     * @return If a profile image is present the direct URL to the resource on
     * S3 is returned. If no profile image is found a fallback image path is
     * returned.
     */
    public String profileImage() {
        return uriUtil.profileImage(
                SecurityUtils.getSubject().getPrincipals()
                .asList().get(1).toString()
                );
    }

    /**
     * Generates the URL for the specified subject profile image located on S3.
     * This methods is supposed to be accessed directly form the presentation
     * layer and will return a direct URL to the resource on S3.
     *
     * @param uid
     * @return If a profile image is present the direct URL to the resource on
     * S3 is returned. If no profile image is found a fallback image path is
     * returned.
     */
    public String profileImage(String uid) {
        return uriUtil.profileImage(uid);
    }

    /**
     * Generates the URL for the specified clinic logo located on S3. This
     * methods is supposed to be accessed directly form the presentation layer
     * and will return a direct URL to the resource on S3.
     *
     * @param dastraxLogo
     * @return If a logo is present the direct URL to the resource on S3 is
     * returned. If no logo is found a fallback image path is returned.
     */
    public String companyLogo1P(String dastraxLogo) {
        String result = protocol
                + cdn
                + "/"
                + DastraxCst.S3_CORE_DIRECTORY
                + "/"
                + DastraxCst.S3_CORE_GRAPHICS_DIRECTORY
                + "/"
                + DastraxCst.S3_CORE_LOGOS_DIRECTORY
                + "/"
                + dastraxLogo;

        Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());

        if (s.getCompany() != null) {
            result = uriUtil.companyLogo(s.getCompany());
        }
        return result;
    }

    /**
     * Generates the URL for the specified clinic logo located on S3. This
     * methods is supposed to be accessed directly form the presentation layer
     * and will return a direct URL to the resource on S3.
     *
     * @param companyId
     * @return If a logo is present the direct URL to the resource on S3 is
     * returned. If no logo is found a fallback image path is returned.
     */
    public String companyLogo3P(String companyId) {
        return uriUtil.companyLogo(companyId);
    }

}
