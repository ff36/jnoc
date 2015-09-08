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

package com.jnoc.app.services;


/**
 * This class generates URL and URN values for specified resources.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 17, 2013)
 * @author Tarka L'Herpiniere

 */
public interface URI {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    String s3Bucket = System.getenv("JNOC_S3_BUCKET");
    String baseURL = System.getenv("JNOC_S3_BASE_URL");
    String protocol = System.getenv("JNOC_ACCESS_PROTOCOL");
    String cdn = System.getenv("JNOC_CDN_BASE_URL");
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
