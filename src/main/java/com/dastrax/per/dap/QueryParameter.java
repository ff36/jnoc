/*
 * Created May 15, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.per.dap;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder Utility to help make query parameter map creation more efficient. 
 *
 * @version 3.0.0
 * @since Build 3.0-SNAPSHOT (May 15, 2014)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class QueryParameter {
    
    //<editor-fold defaultstate="collapsed" desc="Builder">
    private Map parameters = null;
    
    private QueryParameter(String name, Object value) {
        this.parameters = new HashMap();
        this.parameters.put(name, value);
    }
    
    public static QueryParameter with(String name, Object value) {
        return new QueryParameter(name, value);
    }
    
    public QueryParameter and(String name, Object value) {
        this.parameters.put(name, value);
        return this;
    }
    
    public Map parameters() {
        return this.parameters;
    }
    //</editor-fold>
}
