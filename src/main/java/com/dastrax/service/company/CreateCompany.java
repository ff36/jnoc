/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.company;

import com.dastrax.app.misc.Countries;
import com.dastrax.per.entity.Address;
import com.dastrax.per.entity.Company;
import com.dastrax.per.entity.Contact;
import com.dastrax.per.entity.Telephone;
import com.dastrax.per.project.DTX;
import com.dastrax.per.project.DTX.CompanyType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private final List<Locale> countries;
    private Company company;
    private List<Company> vars;
    private Contact newContact;
    private boolean render;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public CreateCompany() {
        //this.countries = Countries.getWorldCountries();
        this.countries = new ArrayList<>();
        this.company = new Company();
        this.newContact = new Contact();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of countries
     *
     * @return the value of countries
     */
    public List<Locale> getCountries() {
        return countries;
    }
    
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
     * Get the value of newContact
     *
     * @return the value of newContact
     */
    public Contact getNewContact() {
        return newContact;
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
    
    /**
     * Set the value of newContact.
     *
     * @param newContact new value of newContact
     */
    public void setNewContact(Contact newContact) {
        this.newContact = newContact;
    }
//</editor-fold>

    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     *
     * @param companyType The company type that will be created.
     */
    public void init(CompanyType companyType) {
        // Set the company type
        company.setType(companyType);
        // Load the extra site and company data
        company.lazyLoad();
        // Allow the page to render
        render = true;
    }

    /**
     * Provides a preset contact with one address and three phone numbers.
     */
    public void resetContact() {
        newContact = new Contact();

        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address());
        newContact.setAddresses(addresses);

        List<Telephone> telephones = new ArrayList<>();
        Telephone telephone = new Telephone();
        telephone.setType(DTX.TelephoneType.DESK);
        telephones.add(telephone);
        telephone = new Telephone();
        telephone.setType(DTX.TelephoneType.CELL);
        telephones.add(telephone);
        telephone = new Telephone();
        telephone.setType(DTX.TelephoneType.FAX);
        telephones.add(telephone);
        newContact.setTelephones(telephones);
    }

}
