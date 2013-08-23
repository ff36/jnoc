/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Filter;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @version Build 2.0.0
 * @since Aug 5, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface FilterDAO {

    /**
     * 
     * @param id
     * @return filter with the specified ID or null if none exists
     */
    public Filter findFilterById(String id);
    /**
     * 
     * @return all filters
     */
    public List<Filter> findAllFilters();
    
}
