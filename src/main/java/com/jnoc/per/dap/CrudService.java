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

package com.jnoc.per.dap;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * Data Access Service. This class should be implemented when
 * persistence related functions are required.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Mar 18, 2014)
 * @author Tarka L'Herpiniere

 */
public interface CrudService {
    
    /**
     * Create a new record in the persistence layer. Any persistence inherited
     * attributes that are allocated at creation time will be present in the
     * newly created entity for the return value.
     * 
     * @param t the entity to be created
     * @return the newly created entity including any attributes inherited at
     * creation time.
     */
    public Object create(Object t);
    
    /**
     * Updates an existing entity record in the persistence layer. 
     * Any persistence inherited attributes that are allocated at update time 
     * will be present in the newly created entity for the return value.
     * 
     * @param t the entity to be updated
     * @return the newly updated entity including any attributes inherited at
     * creation time.
     */
    public Object update(Object t);
    
    /**
     * Removes an existing entity record in the persistence layer.
     * 
     * @param type The entity class type to be removed
     * @param id The id of the entity record to be removed
     */
    public void delete(Class type,Object id);
    
    /**
     * Finds a record from the persistence layer of the specified entity type
     * with the specified record id.
     * 
     * @param type The entity class type to be found
     * @param id The id of the entity record to be found
     * @return The specified entity record object
     */
    public Object find(Class type, Object id);
    
    /**
     * Queries the persistence layer for records using a NamedQuery.
     * 
     * @param namedQueryName The <b>name</b> of the NamedQuery specified in the 
     * entity class
     * @return A List of records that match the NamedQuery query.
     */
    public List findWithNamedQuery(String namedQueryName);
    
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
    public List findWithNamedQuery(String namedQueryName, int resultLimit);
    
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
    public List findWithNamedQuery(String namedQueryName, Map parameters);
    
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
    public List findWithNamedQuery(String namedQueryName, Map parameters, int resultLimit);
    
    /**
     * Obtains a EntityManager.CriteriaBuilder
     * 
     * @return 
     */
    public CriteriaBuilder getCriteriaBuilder();
    
    /**
     * Queries the persistence layer for records using a CriteriaQuery.
     * 
     * @param query The CriteriaQuery
     * @return A List of records that match the CriteriaQuery query.
     */
    public List findWithCriteriaQuery(CriteriaQuery query);
    
    /**
     * Queries the persistence layer for records using a CriteriaQuery and 
     * limiting the number of results.
     * 
     * @param query The CriteriaQuery
     * @param resultLimit The maximum number of records to be returned.
     * @return A List with a maximum size equal to resultLimit with records 
     * that match the CriteriaQuery.
     */
    public List findWithCriteriaQuery(CriteriaQuery query, int resultLimit);
    
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
    public List findWithCriteriaQuery(CriteriaQuery query, int first, int resultLimit);
    
}
