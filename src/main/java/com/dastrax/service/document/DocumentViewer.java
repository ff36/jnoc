/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.document;

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
import javax.persistence.Transient;

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
    private String documentId;
    private String url;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @Transient
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DocumentViewer() {
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
     * Get the value of documentId
     *
     * @return the value of documentId
     */
    public String getDocumentId() {
        return documentId;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of documentId.
     *
     * @param documentId new value of documentId
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;

    }
//</editor-fold>

    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     * 
     * @throws java.io.UnsupportedEncodingException
     */
    public void init() throws UnsupportedEncodingException {
        // Load the document from the database
        Attachment attachment = (Attachment) dap.find(Attachment.class, documentId);
        
        if (attachment != null) {
            // Create the link
            url = "http://docs.google.com/viewer?url="
                    + URLEncoder.encode(
                            new DefaultURI.Builder(DTX.URIType.ATTACHMENT)
                            .withAttachment(attachment)
                            .create()
                            .generate(),
                            "UTF-8")
                    + "&embedded=true";
        }

    }

}
