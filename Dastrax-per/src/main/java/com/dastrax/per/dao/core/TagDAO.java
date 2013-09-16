/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.per.dao.core;

import com.dastrax.per.entity.core.Tag;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @version Build 2.1.0
 * @since Sep 16, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Local
public interface TagDAO {
    
    public Tag createTag(Tag tag);
    public Tag findTagById(String id);
    public Tag findTagByName(String name);
    public List<Tag> findAllTags();
}
