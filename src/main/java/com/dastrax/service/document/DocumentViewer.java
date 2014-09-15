/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.document;

import com.dastrax.app.misc.JsfUtil;
import com.dastrax.app.service.internal.DefaultURI;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Attachment;
import com.dastrax.per.project.DTX;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

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
                            new DefaultURI.Builder(DTX.URIType.ATTACHMENT)
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
