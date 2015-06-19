/*
 * Created May 14, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.services;


/**
 * This class generates URL and URN values for specified resources.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 17, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public interface URI {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    String s3Bucket = System.getenv("DTX_S3_BUCKET");
    String baseURL = System.getenv("DTX_BASE_URL");
    String protocol = System.getenv("DTX_ACCESS_PROTOCOL");
    String cdn = System.getenv("DTX_CDN_BASE_URL");
//</editor-fold>
    
    /**
     * Generates a URI for the specified resource type. The returned value can
     * optionally be delivered via the application Content Delivery Network.
     *
     * @return String representation of the URL to the requested resource.
     */
    public String generate();
    
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
    public boolean urlExists(String URL);
    
}
