/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Nexus;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @version Build 2.0.0
 * @since Aug 6, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface NexusDAO {

    /**
     * 
     * @return all nexus
     */
    public List<Nexus> findAllNexus();
    /**
     * 
     * @param id
     * @return nexus with the specified ID or null if none exists
     */
    public Nexus findNexusById(String id);
}
