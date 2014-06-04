/*
 * Created May 19, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An ENUM providing a list of possible implementation classes for ModelQuery.
 *
 * @version 3.0-SNAPSHOT
 * @since Build 3.0.0 (May 19, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Retention(RUNTIME)
@Target({FIELD, TYPE, METHOD})
public @interface ModelQueryType {

    ModelQueries value();

    public enum ModelQueries {

        AUDIT(AuditModelQuery.class),
        COMPANY(CompanyModelQuery.class),
        DAS(DasModelQuery.class),
        TICKET(TicketModelQuery.class),
        INCIDENT(IncidentModelQuery.class),
        USER(UserModelQuery.class);

        Class<? extends ModelQuery> clazz;

        private ModelQueries(Class<? extends ModelQuery> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends ModelQuery> getClazz() {
            return clazz;
        }
    }

}
