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
package com.jnoc.app.upload;

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

