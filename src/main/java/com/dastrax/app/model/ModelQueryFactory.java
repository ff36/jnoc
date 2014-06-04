/*
 * Created May 19, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.model;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * CDI Factory class that allows specific ModelQuery implementations to be 
 * selected and injected at runtime.
 *
 * @version 3.0-SNAPSHOT
 * @since Build 3.0.0 (May 19, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class ModelQueryFactory {

    @Produces
    @ModelQueryQualifier
    public ModelQuery getModelQuery(
            @Any Instance<ModelQuery> instance, 
            InjectionPoint ip){
        Annotated gtAnnotated = ip.getAnnotated();
        ModelQueryType gtAnnotation = gtAnnotated.getAnnotation(ModelQueryType.class);
        Class<? extends ModelQuery> modelService = gtAnnotation.value().getClazz();
        return instance.select(modelService).get();
    }

}
