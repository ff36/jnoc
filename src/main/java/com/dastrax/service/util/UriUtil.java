/*
 * Created Jul 17, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.dastrax.app.security.SessionUser;
import com.dastrax.app.service.internal.DefaultURI;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;

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
public class UriUtil implements Serializable  {

	private static Logger LOG = Logger.getLogger(UriUtil.class.getName());
	
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public UriUtil() {
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

    /**
     * get JNDI name from META-INF/persistence.xml
     * @return
     */
    private static String JNDIName;
    public static String getDataSourceJNDI(){
    	if(JNDIName==null){
    		UriUtil util = new UriUtil();
        	PersitenceSAXParser parser = util.new PersitenceSAXParser();
        	try {
        		JNDIName = parser.parse().trim();
    		} catch (SAXException | IOException | ParserConfigurationException e) {
    			LOG.log(Level.SEVERE, e.getMessage(), e);
    		}
    	}
    	return JNDIName;
    }
    
    /**
     * parse persistence.xml, SAX Parser
     */
    private class PersitenceSAXParser extends DefaultHandler{
    	private String preTag;
    	private String jndiName;
    	
    	public void characters(char[] ch, int start, int length)
                throws SAXException {
            String data = new String(ch, start, length);
            if ("jta-data-source".equals(preTag)) {
            	jndiName = data;
            }
        }
    	
    	public void startElement(String uri, String localName, String name,
                Attributes attr) throws SAXException {
            preTag = name;
        }
    	
    	public void endElement(String uri, String localName, String name)
                throws SAXException {
            preTag = null;
        }
    	
    	/**
    	 * return 
    	 * @throws IOException 
    	 * @throws SAXException 
    	 * @throws ParserConfigurationException 
    	 */
    	public String parse() throws SAXException, IOException, ParserConfigurationException{
    		SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            InputStream is = UriUtil.class.getResourceAsStream("/META-INF/persistence.xml");

            saxParser.parse(is, this);
            
            is.close();
            return this.jndiName;
    	}
    	
    }
    
    public static void main(String[] args) {
		System.out.println(UriUtil.getDataSourceJNDI());
	}
    
}
