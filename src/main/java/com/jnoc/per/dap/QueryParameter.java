/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jnoc.per.dap;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder Utility to help make query parameter map creation more efficient. 
 *
 * @version 3.0.0
 * @since Build 3.0-SNAPSHOT (May 15, 2014)
 * @author Tarka L'Herpiniere

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
