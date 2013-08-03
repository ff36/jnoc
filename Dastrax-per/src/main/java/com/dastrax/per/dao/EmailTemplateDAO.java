/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao;

import com.dastrax.per.entity.core.EmailTemplate;
import javax.ejb.Local;

/**
 *
 * @version Build 2.0.0
 * @since Jul 14, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface EmailTemplateDAO {

    public EmailTemplate findTemplateById(String id);
}
