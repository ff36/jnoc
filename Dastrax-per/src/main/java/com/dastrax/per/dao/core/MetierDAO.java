/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Metier;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @version Build 2.0.0
 * @since Jul 12, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface MetierDAO {

    /**
     * 
     * @param metier 
     */
    public void create(Metier metier);
    /**
     * 
     * @param id
     * @return Metier with specified ID or null if none exists
     */
    public Metier findMetierById(String id);
    /**
     * 
     * @param name
     * @return Metier with the specified name or null if none exists
     */
    public Metier findMetierByName(String name);
    /**
     * 
     * @return all metier
     */
    public List<Metier> findAllMetiers();
}
