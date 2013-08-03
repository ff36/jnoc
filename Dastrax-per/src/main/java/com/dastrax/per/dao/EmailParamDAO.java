/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao;

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

    public void create(EmailParam param);
    public void delete(String id);
    public EmailParam findParamByToken(String token);
    public EmailParam findParamByEmail(String email);
    
}
