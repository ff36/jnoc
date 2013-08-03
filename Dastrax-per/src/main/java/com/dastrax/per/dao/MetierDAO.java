/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao;

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

    public void create(Metier metier);
    public Metier findMetierById(String id);
    public Metier findMetierByName(String name);
    public List<Metier> findAllMetiers();
}
