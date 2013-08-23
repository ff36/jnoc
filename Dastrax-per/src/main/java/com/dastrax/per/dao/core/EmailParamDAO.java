/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.EmailParam;
import javax.ejb.Local;

/**
 *
 * @version Build 2.0.0
 * @since Jul 14, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface EmailParamDAO {

    /**
     * 
     * @param param 
     */
    public void create(EmailParam param);
    /**
     * 
     * @param id 
     */
    public void delete(String id);
    /**
     * 
     * @param token
     * @return emailParam with the specified ID or null if none exists
     */
    public EmailParam findParamByToken(String token);
    /**
     * 
     * @param email
     * @return emailParam with the specified Email or null if none exists
     */
    public EmailParam findParamByEmail(String email);
    
}
