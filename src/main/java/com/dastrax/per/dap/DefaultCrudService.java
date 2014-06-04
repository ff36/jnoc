/*
 * Created May 15, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.per.dap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * Default Data Access Service. This class should be implemented when
 * persistence related functions are required.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Mar 18, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Stateless
@Local(CrudService.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DefaultCrudService implements CrudService {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    @PersistenceContext(unitName = "Dastrax_PU")
    private EntityManager em;
//</editor-fold>

    /**
     * Create a new record in the persistence layer. Any persistence inherited
     * attributes that are allocated at creation time will be present in the
     * newly created entity for the return value.
     * 
     * @param t the entity to be created
     * @return the newly created entity including any attributes inherited at
     * creation time.
     */
    @Override
    public Object create(Object t) {
        this.em.persist(t);
        this.em.flush();
        this.em.refresh(t);
        return t;
    }

    /**
     * Updates an existing entity record in the persistence layer. 
     * Any persistence inherited attributes that are allocated at update time 
     * will be present in the newly created entity for the return value.
     * 
     * @param t the entity to be updated
     * @return the newly updated entity including any attributes inherited at
     * creation time.
     */
    @Override
    public Object update(Object t) {
        return (Object)this.em.merge(t);
    }

    /**
     * Removes an existing entity record in the persistence layer.
     * 
     * @param type The entity class type to be removed
     * @param id The id of the entity record to be removed
     */
    @Override
    public void delete(Class type, Object id) {
        Object ref = this.em.getReference(type, id);
        this.em.remove(ref);
    }

    /**
     * Finds a record from the persistence layer of the specified entity type
     * with the specified record id.
     * 
     * @param type The entity class type to be found
     * @param id The id of the entity record to be found
     * @return The specified entity record object or null if none is found
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object find(Class type, Object id) {
        return (Object) this.em.find(type, id);
    }
    
    /**
     * Queries the persistence layer for records using a NamedQuery.
     * 
     * @param namedQueryName The <b>name</b> of the NamedQuery specified in the 
     * entity class
     * @return A List of records that match the NamedQuery query.
     */
    @Override
    public List findWithNamedQuery(String namedQueryName) {
        return this.em.createNamedQuery(namedQueryName).getResultList();
    }

    /**
     * Queries the persistence layer for records using a NamedQuery and limiting
     * the number of results.
     * 
     * @param namedQueryName The <b>name</b> of the NamedQuery specified in the 
     * entity class
     * @param resultLimit The maximum number of records to be returned.
     * @return A List with a maximum size equal to resultLimit with records 
     * that match the NamedQuery query.
     */
    @Override
    public List findWithNamedQuery(String namedQueryName, int resultLimit) {
        return this.em.createNamedQuery(namedQueryName).
                setMaxResults(resultLimit).
                getResultList();
    }
    
    /**
     * Queries the persistence layer for records using a NamedQuery with 
     * parameters.  
     * 
     * @param namedQueryName The <b>name</b> of the NamedQuery specified in the 
     * entity class
     * @param parameters The map Key represents the parameter name as specified
     * in the entity class NamedQuery and the map Value is the value to assign
     * to that parameter.
     * @return A List of records that match the NamedQuery query.
     */
    @Override
    public List findWithNamedQuery(String namedQueryName, Map parameters) {
        return findWithNamedQuery(namedQueryName, parameters, 0);
    }
    
    /**
     * Queries the persistence layer for records using a NamedQuery with 
     * parameters and limiting the number of results.
     * 
     * @param namedQueryName The <b>name</b> of the NamedQuery specified in the 
     * entity class
     * @param parameters The map Key represents the parameter name as specified
     * in the entity class NamedQuery and the map Value is the value to assign
     * to that parameter.
     * @param resultLimit The maximum number of records to be returned.
     * @return A List with a maximum size equal to resultLimit with records 
     * that match the NamedQuery query.
     */
    @Override
    public List findWithNamedQuery(String namedQueryName, Map parameters, int resultLimit) {
        Set<Entry> rawParameters = parameters.entrySet();
        Query query = this.em.createNamedQuery(namedQueryName);
        if(resultLimit > 0)
            query.setMaxResults(resultLimit);
        for (Entry entry : rawParameters) {
            query.setParameter((String) entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    /**
     * Obtains a EntityManager.CriteriaBuilder
     * 
     * @return 
     */
    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }
    
    /**
     * Queries the persistence layer for records using a CriteriaQuery.
     * 
     * @param query The CriteriaQuery
     * @return A List of records that match the CriteriaQuery query.
     */
    @Override
    public List findWithCriteriaQuery(CriteriaQuery query) {
        return em.createQuery(query).getResultList();
    }
    
    /**
     * Queries the persistence layer for records using a CriteriaQuery and 
     * limiting the number of results.
     * 
     * @param query The CriteriaQuery
     * @param resultLimit The maximum number of records to be returned.
     * @return A List with a maximum size equal to resultLimit with records 
     * that match the CriteriaQuery.
     */
    @Override
    public List findWithCriteriaQuery(CriteriaQuery query, int resultLimit) {
        return em.createQuery(query)
                .setMaxResults(resultLimit)
                .getResultList();
    }
    
    /**
     * Queries the persistence layer for records using a CriteriaQuery and 
     * limiting the number of results and setting the first result.
     * 
     * @param query The CriteriaQuery
     * @param first The first result record to be returned
     * @param resultLimit The maximum number of records to be returned.
     * @return A List with a maximum size equal to resultLimit with records 
     * that match the CriteriaQuery.
     */
    @Override
    public List findWithCriteriaQuery(CriteriaQuery query, int first, int resultLimit) {
        return em.createQuery(query)
                .setFirstResult(first)
                .setMaxResults(resultLimit)
                .getResultList();
    }
    
}
