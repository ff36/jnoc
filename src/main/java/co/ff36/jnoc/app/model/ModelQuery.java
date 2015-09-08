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

package co.ff36.jnoc.app.model;

import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.primefaces.model.SortOrder;

/**
 * ModelQuery allowing dynamic CriteriaQuery to be created at runtime for
 * various entity class implementations.
 *
 * @version 3.0-SNAPSHOT
 * @since Build 3.0.0 (May 19, 2014)
 * @author Tarka L'Herpiniere

 */
public interface ModelQuery {
    
    /**
     * A type safe CriteriaQuery dynamically constructed of an optional 
     * root filter (applied automatically based on the users Metier),
     * an optional filter that can be set via a URL query parameter, and a 
     * global filter that is implemented as the user generates a 'keyup' event
     * in the search field.
     * 
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param builder
     * @param filters
     * @param rootFilter
     * @param optionalFilter
     * @return A type safe CriteriaQuery that can be queried against the 
     * persistence layer.
     */
    public CriteriaQuery query(
            int first, 
            int pageSize, 
            String sortField, 
            SortOrder sortOrder,
            CriteriaBuilder builder,
            Map filters, 
            Map rootFilter, 
            Map optionalFilter
    );
    
    /**
     * Determines the class type to associate with the query.
     * 
     * @return Returns the class type to associate with the query.
     */
    public Class clazz();
    
    /**
     * Determines the class type to associate with the query.
     * 
     * @param object
     * @return Returns the class type to associate with the query.
     */
    public Long rowKey(Object object);
    
}
