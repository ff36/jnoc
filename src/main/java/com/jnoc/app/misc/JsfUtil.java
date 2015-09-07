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
 * Created Mar 10, 2013.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 

 */
package com.jnoc.app.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

/**
 * Jsf Utility functions
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Mar 10, 2013)
 * @author Tarka L'Herpiniere

 */
public class JsfUtil {

    /**
     * Creates an array of entity objects to be displayed in a pick list with
     * the option of adding a blank entry.
     *
     * @param entities
     * @param selectOne
     * @return SelectItem[]
     */
    public static SelectItem[] getSelectItems(
            List<?> entities,
            boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }

    /**
     * Add an error message to the JSF presentation layer.
     *
     * @param ex
     * @param defaultMsg
     */
    public static void addErrorMessage(Exception ex, String defaultMsg) {
        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg);
        } else {
            addErrorMessage(defaultMsg);
        }
    }

    /**
     * Add an error message to the JSF presentation layer.
     *
     * @param messages
     */
    public static void addErrorMessages(List<String> messages) {
        for (String message : messages) {
            addErrorMessage(message);
        }
    }

    /**
     * Add an error message to the JSF presentation layer.
     *
     * @param msg
     */
    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(
                FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    /**
     * Add a success message to the JSF presentation layer.
     *
     * @param msg
     */
    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(
                FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }

    /**
     * Add a warning message to the JSF presentation layer.
     *
     * @param msg
     */
    public static void addWarningMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(
                FacesMessage.SEVERITY_WARN, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    /**
     * Add a fatal message to the JSF presentation layer.
     *
     * @param msg
     */
    public static void addFatalMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(
                FacesMessage.SEVERITY_FATAL, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    /**
     * Obtain any request parameters from the URL.
     *
     * @param key (This is the identifier used to specify the parameter)
     * @return The value of the specified parameter if one exists
     */
    public static String getRequestParameter(String key) {
        return FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get(key);
    }

    /**
     * Obtain a list of all the query parameters in the URL. As of JSF 2.2 this
     * needs to be called from the constructor.
     *
     * @return The value of the specified parameter if one exists
     */
    public static Map<String, List<String>> getRequestParameters() {
        // Get all the query parameters
        Map<String, String> parameterMap
                = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
        
        // Response map
        Map<String, List<String>> response = new HashMap<>(parameterMap.size());

        // Convert the parameters to a List<String, List<String>>
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            if (entry.getValue().contains(",")) {
                String[] value = entry.getValue().split(",");
                response.putIfAbsent(entry.getKey(), Arrays.asList(value));
            } else {
                List<String> single = new ArrayList(1);
                single.add(entry.getValue());
                response.putIfAbsent(entry.getKey(), single);
            }
        }
        
        return response;
    }

    /**
     * Instead of retrieving a string parameter and then retrieving the related
     * object from the database it is possible to specify a converter and have
     * the lookup automatically encompassed into this method.
     *
     * @param requestParameterName
     * @param converter
     * @param component
     * @return An object that is represented by the string value in the
     * parameter
     */
    public static Object getObjectFromRequestParameter(
            String requestParameterName,
            Converter converter,
            UIComponent component) {
        String theId = JsfUtil.getRequestParameter(requestParameterName);
        return converter.getAsObject(
                FacesContext.getCurrentInstance(), component, theId);
    }
}
