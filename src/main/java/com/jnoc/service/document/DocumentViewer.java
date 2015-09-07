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


/*
 * Created Aug 8, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.jnoc.service.document;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.jnoc.app.misc.JsfUtil;
import com.jnoc.app.service.internal.DefaultURI;
import com.jnoc.per.dap.CrudService;
import com.jnoc.per.entity.Attachment;
import com.jnoc.per.project.JNOC;

/**
 * Document Viewer table CDI bean. Provides methods for viewing and editing
 * document without downloading them.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class DocumentViewer implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    private final String documentId;
    private String url;
    private boolean render;
    private boolean error;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DocumentViewer() {
        this.documentId = JsfUtil.getRequestParameter("document");
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of url. The encoded URL to show the document in Google
     * document viewer.
     *
     * @return the value of url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the value of render.
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * Get the value of error.
     *
     * @return the value of error
     */
    public boolean isError() {
        return error;
    }

    
//</editor-fold>
    
    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     *
     */
    public void init() {

        try {
            Attachment attachment = (Attachment) 
                    dap.find(Attachment.class, Long.parseLong(documentId));
            // Create the link
            url = "http://docs.google.com/viewer?url="
                    + URLEncoder.encode(
                            new DefaultURI.Builder(JNOC.URIType.ATTACHMENT)
                            .withAttachment(attachment)
                            .create()
                            .generate(),
                            "UTF-8")
                    + "&embedded=true";
            render = true;
        } catch (NullPointerException 
                | UnsupportedEncodingException 
                | NumberFormatException e) {
            // Document was null or the id was not a number
            error = true;
        }

    }

}
