/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.upload;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.fileupload.FileUploadRenderer;

/**
 * This class is a custom extention of the Primefaces file upload so 
 * that we can detect whether a form is being submitted for upload.
 * (ie. multi-part) or a standard AJAX request. This basically allows both a 
 * file upload and normal ajax requests to be processed in the same form.
 * 
 * @version 3.0.0
 * @since Build 3.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class PrimeFacesFileUploadRenderer extends FileUploadRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context.getExternalContext()
                .getRequestContentType()
                .toLowerCase()
                .startsWith("multipart/")) {
            super.decode(context, component);
        }
    }

}

