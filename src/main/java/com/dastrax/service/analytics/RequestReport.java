/*
 * Created May 10, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.analytics;

import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.DAS;
import com.dastrax.per.entity.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Analytics report request CDI bean. Until Analytics is live, users can 
 * request a manual report to be generated and sent to them.
 * 
 *
 * @version 3.0.0
 * @since Build 3.0-SNAPSHOT (May 10, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class RequestReport implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    
    private List<DAS> das;
    private DAS selectedDas;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public RequestReport() {
        this.das = new ArrayList<>();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of das. A list of available DAS instalation that the
     * current subject is authorized to access.
     *
     * @return the value of das
     */
    public List<DAS> getDas() {
        return das;
    }
    
    /**
     * Get the value of selectedDas.
     *
     * @return the value of selectedDas
     */
    public DAS getSelectedDas() {
        return selectedDas;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of selectedDas.
     *
     * @param selectedDas new value of selectedDas
     */
    public void setSelectedDas(DAS selectedDas) {
        this.selectedDas = selectedDas;
    }
//</editor-fold>

    /**
     * Initialize the properties to make the page functional.
     */
    @PostConstruct
    public void init() {
        // ADMIN access
        if (SessionUser.getCurrentUser().isAdministrator()) {
            das = (List<DAS>) dap.findWithNamedQuery("DAS.findAll");
        } else {
           User user = (User) dap.find(User.class, SessionUser.getCurrentUser().getId());
            das = user.getCompany().getDas(); 
        }
        // set the selected DAS as the first in the list
        if (!das.isEmpty()) {
            selectedDas = das.get(0);
        }
    }
    
    /**
     * TODO: Implement a send email to send the report request.
     */
    public void sendRequest() {
        
    }

}
