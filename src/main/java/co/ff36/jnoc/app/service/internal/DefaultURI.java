/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package co.ff36.jnoc.app.service.internal;

import co.ff36.jnoc.app.services.URI;
import co.ff36.jnoc.per.entity.Attachment;
import co.ff36.jnoc.per.entity.Company;
import co.ff36.jnoc.per.entity.User;
import co.ff36.jnoc.per.project.JNOC;
import co.ff36.jnoc.per.project.JNOC.URIType;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class generates URL and URN values for specified resources.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 17, 2013)
 * @author Tarka L'Herpiniere

 */
public class DefaultURI implements URI {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private final User user;
    private final Company company;
    private final URIType uriType;
    private final String mime;
    private final String file;
    private final Attachment attachment;
    private final boolean viaCDN;

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    private DefaultURI(User user, Company company, URIType uriType, String mime, String file, Attachment attachment, boolean viaCDN) {
        this.user = user;
        this.company = company;
        this.uriType = uriType;
        this.mime = mime;
        this.file = file;
        this.attachment = attachment;
        this.viaCDN = viaCDN;
    }
//</editor-fold>

    /**
     * Generates a URI for the specified resource type. The returned value can
     * optionally be delivered via the application Content Delivery Network.
     *
     * @return String representation of the URL to the requested resource.
     */
    @Override
    public String generate() {

        switch (uriType) {
            case USER_PROFILE_IMAGE:
                return userProfileImage(user, viaCDN);
            case COMPANY_LOGO:
                return companyLogo(company, viaCDN);
            case FILE_TYPE_ICON:
                return fileIcon(mime, viaCDN);
            case TEMPORARY_FILE:
                return temporaryFile(file, viaCDN);
            case ATTACHMENT:
                return attachment(attachment, viaCDN);
            case ICON:
                return icon(file, viaCDN);
            case IMAGE:
                return image(file, viaCDN);
            case LOGO:
                return logo(file, viaCDN);
        }
        return null;
    }

    /**
     * Performs an external HTTP request to see if a file is present at the
     * specified URL. The HTTP status in the returned header HTTP is used to
     * determine if the resource is present.
     *
     * @param URL
     * @return true if the resource exists, otherwise false.
     * <b>Important</b>
     * The request is made by an anonymous actor. If the resource is present but
     * the access rights refuse access by an anonymous actor the response will
     * be false even though the resource may in fact be present.
     */
    @Override
    public boolean urlExists(String URL) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con
                    = (HttpURLConnection) new URL(URL).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Generates the URL to the specified users profile image.
     *
     * @param id
     * @param viaCDN
     * @return If a profile image is present the URL to the resource is
     * returned. If no profile image is present a fallback image path is
     * returned.
     */
    private String userProfileImage(User user, boolean viaCDN) {

        String origin, url;

        // Deliver either via CDN or directly
        if (viaCDN) {
            origin = cdn;
        } else {
            origin = baseURL + "/" + s3Bucket;
        }

        // Generate the theoretical URL
        url = protocol
                + origin
                + "/"
                + JNOC.StorageDirectory.USERS.getValue()
                + "/"
                + user.getAccount().getS3id()
                + "/"
                + JNOC.StorageDirectory.USER_PROFILE.getValue()
                + "/"
                + JNOC.StorageFile.USER_PROFILE_GRAPHIC.getValue();

        // Check the theoretical URL to determin if we need a fallback graphic
        if (!urlExists(url)) {
            url = protocol
                    + origin
                    + "/"
                    + JNOC.StorageDirectory.CORE.getValue()
                    + "/"
                    + JNOC.StorageDirectory.CORE_GRAPHICS.getValue()
                    + "/"
                    + JNOC.StorageDirectory.CORE_IMAGES.getValue()
                    + "/"
                    + JNOC.StorageFile.CORE_PROFILE_GRAPHIC_HOLDER.getValue();
        }
        return url;
    }

    /**
     * Generates the URL to the specified company logo image.
     *
     * @param id
     * @param viaCDN
     * @return If a logo image is present the URL to the resource is returned.
     * If no logo image is present a fallback image path is returned.
     */
    private String companyLogo(Company company, boolean viaCDN) {

        String origin, url;

        // Deliver either via CDN or directly
        if (viaCDN) {
            origin = cdn;
        } else {
            origin = baseURL + "/" + s3Bucket;
        }

        try {

            // Generate the theoretical URL
            url = protocol
                    + origin
                    + "/"
                    + JNOC.StorageDirectory.COMPANIES.getValue()
                    + "/"
                    + company.getS3id()
                    + "/"
                    + JNOC.StorageDirectory.COMPANY_LOGOS.getValue()
                    + "/"
                    + JNOC.StorageFile.COMPANY_LOGO.getValue();

            // Check the theoretical URL to determin if we need a fallback graphic
            if (!urlExists(url)) {
                url = protocol
                        + origin
                        + "/"
                        + JNOC.StorageDirectory.CORE.getValue()
                        + "/"
                        + JNOC.StorageDirectory.CORE_GRAPHICS.getValue()
                        + "/"
                        + JNOC.StorageDirectory.CORE_IMAGES.getValue()
                        + "/"
                        + JNOC.StorageFile.CORE_COMPANY_LOGO_HOLDER.getValue();
            }

        } catch (NullPointerException npe) {
            // Company is null. This means its an administrator!
            url = logo("jnoc_logo_v3.png", true);
        }

        return url;

    }

    /**
     * Generates the URL to an icon that reflects the file type as specified in
     * the MIME type.
     *
     * @param id
     * @param viaCDN
     * @return the URL to the icon that reflects the specified file MIME type.
     * If the MIME type is not recognized the URL to a standard icon is
     * returned.
     */
    private String fileIcon(String file, boolean viaCDN) {

        String origin, iconTitle;

        // Deliver either via CDN or directly
        if (viaCDN) {
            origin = cdn;
        } else {
            origin = baseURL + "/" + s3Bucket;
        }

        switch (file) {
            case "application/pdf":
                iconTitle = "pdf";
                break;
            case "image/jpeg":
                iconTitle = "jpg";
                break;
            case "image/png":
                iconTitle = "png";
                break;
            case "image/gif":
                iconTitle = "gif";
                break;
            case "image/bmp":
                iconTitle = "bmp";
                break;
            case "image/tiff":
                iconTitle = "tiff";
                break;
            case "text/rtf":
                iconTitle = "rtf";
                break;
            case "application/msword":
                iconTitle = "doc";
                break;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                iconTitle = "docx";
                break;
            case "application/vnd.ms-excel":
                iconTitle = "xls";
                break;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                iconTitle = "xlsx";
                break;
            case "application/vnd.ms-powerpoint":
                iconTitle = "ppt";
                break;
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                iconTitle = "pptx";
                break;
            case "application/zip":
                iconTitle = "zip";
                break;
            case "video/mpeg":
                iconTitle = "mpg";
                break;
            case "video/quicktime":
                iconTitle = "mov";
                break;
            case "video/x-msvideo":
                iconTitle = "avi";
                break;
            case "video/x-ms-wmv":
                iconTitle = "wmv";
                break;
            case "video/x-flv":
                iconTitle = "flv";
                break;
            case "text/plain":
                iconTitle = "txt";
                break;
            case "application/postscript":
                iconTitle = "eps";
                break;
            default:
                iconTitle = "txt";
                break;
        }

        return protocol
                + origin
                + "/"
                + JNOC.StorageDirectory.CORE.getValue()
                + "/"
                + JNOC.StorageDirectory.CORE_GRAPHICS.getValue()
                + "/"
                + JNOC.StorageDirectory.CORE_ICONS.getValue()
                + "/"
                + "color"
                + "/"
                + iconTitle + ".png";
    }

    /**
     * Generates the URL to a temporary file
     *
     * @param id
     * @param viaCDN
     * @return the URL to the temporary file
     */
    private String temporaryFile(String id, boolean viaCDN) {

        String origin;

        // Deliver either via CDN or directly
        if (viaCDN) {
            origin = cdn;
        } else {
            origin = baseURL + "/" + s3Bucket;
        }

        // Generate the theoretical URL
        return protocol
                + origin
                + "/"
                + JNOC.StorageDirectory.CORE.getValue()
                + "/"
                + JNOC.StorageDirectory.TEMPORARY.getValue()
                + "/"
                + id;

    }

    /**
     * Generates the URL to an attachment
     *
     * @param attachment
     * @param viaCDN
     * @return the URL to the attachment
     */
    private String attachment(Attachment attachment, boolean viaCDN) {

        String origin;

        // Deliver either via CDN or directly
        if (viaCDN) {
            origin = cdn;
        } else {
            origin = baseURL + "/" + s3Bucket;
        }

        // Generate the theoretical URL
        return protocol
                + origin
                + "/"
                + JNOC.StorageDirectory.TICKETS.getValue()
                + "/"
                + JNOC.StorageDirectory.TICKET_ATTACHMENTS.getValue()
                + "/"
                + attachment.getS3id();

    }

    /**
     * Generates the URL to a system icon
     *
     * @param file
     * @param viaCDN
     * @return the URL to the attachment
     */
    private String icon(String file, boolean viaCDN) {

        String origin;

        // Deliver either via CDN or directly
        if (viaCDN) {
            origin = cdn;
        } else {
            origin = baseURL + "/" + s3Bucket;
        }

        // Generate the theoretical URL
        return protocol
                + origin
                + "/"
                + JNOC.StorageDirectory.CORE.getValue()
                + "/"
                + JNOC.StorageDirectory.CORE_GRAPHICS.getValue()
                + "/"
                + JNOC.StorageDirectory.CORE_ICONS.getValue()
                + "/"
                + file;

    }

    /**
     * Generates the URL to a system image
     *
     * @param file
     * @param viaCDN
     * @return the URL to the attachment
     */
    private String image(String file, boolean viaCDN) {

        String origin;

        // Deliver either via CDN or directly
        if (viaCDN) {
            origin = cdn;
        } else {
            origin = baseURL + "/" + s3Bucket;
        }

        // Generate the theoretical URL
        return protocol
                + origin
                + "/"
                + JNOC.StorageDirectory.CORE.getValue()
                + "/"
                + JNOC.StorageDirectory.CORE_GRAPHICS.getValue()
                + "/"
                + JNOC.StorageDirectory.CORE_IMAGES.getValue()
                + "/"
                + file;

    }

    /**
     * Generates the URL to a system logo
     *
     * @param file
     * @param viaCDN
     * @return the URL to the attachment
     */
    private String logo(String file, boolean viaCDN) {

        String origin;

        // Deliver either via CDN or directly
        if (viaCDN) {
            origin = cdn;
        } else {
            origin = baseURL + "/" + s3Bucket;
        }

        // Generate the theoretical URL
        return protocol
                + origin
                + "/"
                + JNOC.StorageDirectory.CORE.getValue()
                + "/"
                + JNOC.StorageDirectory.CORE_GRAPHICS.getValue()
                + "/"
                + JNOC.StorageDirectory.CORE_LOGOS.getValue()
                + "/"
                + file;

    }

    //<editor-fold defaultstate="collapsed" desc="Builder Pattern">
    /**
     * URI Default Builder Pattern Inner Class
     *
     * @version 3.0.0
     * @since Build 3.0.0 (Apr 17, 2014)
     * @author Tarka L'Herpiniere
    
     */
    public static class Builder {

        private User user = null;
        private Company company = null;
        private final URIType uriType;
        private String mime = null;
        private String file = null;
        private boolean viaCDN = false;
        private Attachment attachment = null;

        /**
         * Create a new instance of DefaultURI
         *
         * @param uriType
         */
        public Builder(URIType uriType) {
            this.uriType = uriType;
        }

        /**
         * Sets the user if a user based resource is requested.
         *
         * @param user
         * @return
         */
        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        /**
         * Sets the company if a company based resource is requested.
         *
         * @param company
         * @return
         */
        public Builder withCompany(Company company) {
            this.company = company;
            return this;
        }

        /**
         * Sets the mimeType if a file based resource is requested.
         *
         * @param mime
         * @return
         */
        public Builder withMime(String mime) {
            this.mime = mime;
            return this;
        }

        /**
         * Sets the file ID if a file based resource is requested.
         *
         * @param file
         * @return
         */
        public Builder withFile(String file) {
            this.file = file;
            return this;
        }

        /**
         * Sets the Attachment if an attachment based resource is requested.
         *
         * @param attachment
         * @return
         */
        public Builder withAttachment(Attachment attachment) {
            this.attachment = attachment;
            return this;
        }

        /**
         * Determines whether the resource URI is accessible through the CDN
         *
         * @param viaCDN
         * @return
         */
        public Builder withViaCDN(boolean viaCDN) {
            this.viaCDN = viaCDN;
            return this;
        }

        public DefaultURI create() {
            return new DefaultURI(user, company, uriType, mime, file, attachment, viaCDN);
        }
    }
    //</editor-fold>
}
