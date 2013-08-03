/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.app.pojo;

import com.dastrax.per.project.DastraxCst;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;


/**
 * A response object is used to wrap method responses from the EJB layer that
 * might have either multiple response types or contains response mappings that
 * are intended for different layers of the application.
 *
 * @version Build 2.0.0
 * @since Jul 14, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class Response {

    // Variables----------------------------------------------------------------
    private Object object;
    private List<Map<String, String>> jsfMessages = new ArrayList<>();

    // Constructors-------------------------------------------------------------
    public Response() {
    }

    // Getters------------------------------------------------------------------
    public Object getObject() {
        return object;
    }

    public List<Map<String, String>> getJsfMessages() {
        return jsfMessages;
    }

    // Setters------------------------------------------------------------------
    public void setObject(Object object) {
        this.object = object;
    }

    public void setJsfMessages(List<Map<String, String>> jsfMessages) {
        this.jsfMessages = jsfMessages;
    }

    // Methods------------------------------------------------------------------
    public void addJsfScsMsg(String message) {
        Map<String, String> map = new HashMap<>();
        map.put(DastraxCst.ResponseJsf.SUCCESS.toString(), message);
        jsfMessages.add(map);
    }

    public void addJsfWrnMsg(String message) {
        Map<String, String> map = new HashMap<>();
        map.put(DastraxCst.ResponseJsf.WARNING.toString(), message);
        jsfMessages.add(map);
    }

    public void addJsfErrMsg(String message) {
        Map<String, String> map = new HashMap<>();
        map.put(DastraxCst.ResponseJsf.ERROR.toString(), message);
        jsfMessages.add(map);
    }

    public void renderJsfMsgs() {
        for (Map<String, String> jsfMessage : jsfMessages) {
            if (jsfMessage.containsKey(DastraxCst.ResponseJsf.SUCCESS.toString())) {
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        jsfMessage.get(DastraxCst.ResponseJsf.SUCCESS.toString()), 
                        jsfMessage.get(DastraxCst.ResponseJsf.SUCCESS.toString()));
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            }
            if (jsfMessage.containsKey(DastraxCst.ResponseJsf.WARNING.toString())) {
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN,
                        jsfMessage.get(DastraxCst.ResponseJsf.WARNING.toString()), 
                        jsfMessage.get(DastraxCst.ResponseJsf.WARNING.toString()));
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            }
            if (jsfMessage.containsKey(DastraxCst.ResponseJsf.ERROR.toString())) {
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        jsfMessage.get(DastraxCst.ResponseJsf.ERROR.toString()), 
                        jsfMessage.get(DastraxCst.ResponseJsf.ERROR.toString()));
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            }
            if (jsfMessage.containsKey(DastraxCst.ResponseJsf.FATAL.toString())) {
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_FATAL,
                        jsfMessage.get(DastraxCst.ResponseJsf.FATAL.toString()), 
                        jsfMessage.get(DastraxCst.ResponseJsf.FATAL.toString()));
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            }
        }
    }

}
