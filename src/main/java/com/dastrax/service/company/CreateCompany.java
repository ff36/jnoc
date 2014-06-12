/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.company;

import com.dastrax.app.security.SessionUser;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Company;
import com.dastrax.per.project.DTX.CompanyType;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Create Company CDI bean. Provides methods for creating companies
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class CreateCompany implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    private Company company;
    private List<Company> vars;
    private boolean render;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public CreateCompany() {
        this.company = new Company();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of company
     *
     * @return the value of company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Get the value of vars
     *
     * @return the value of vars
     */
    public List<Company> getVars() {
        return vars;
    }

    /**
     * Get the value of render
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of company.
     *
     * @param company new value of company
     */
    public void setCompany(Company company) {
        this.company = company;
    }

//</editor-fold>
    
    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     *
     */
    public void init() {
        
        // Default to create a client company
        company.setType(CompanyType.CLIENT);

        // VAR can only set their own company as the parent
        if (SessionUser.getCurrentUser().isVAR()) {
            company.setNewParent(SessionUser.getCurrentUser().getCompany());
        }

        // Admins can set any VAR as the parent
        if (SessionUser.getCurrentUser().isAdministrator()) {
            vars = dap.findWithNamedQuery("Company.findByType",
                    QueryParameter
                    .with("type", CompanyType.VAR)
                    .parameters());
        }

        // Lazy load the company
        company.setNewParent(new Company());
        company.getNewParent().setType(CompanyType.VAR);
        company.getNewParent().lazyLoad();
        
        // Allow the page to render
        render = true;

    }

}
