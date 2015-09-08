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

package com.jnoc.app.exception;

/**
 * This is the custom JSF exception. 
 * This is used by the <JnocExceptionHandler.class>
 *
 * @version 2.0.0
 * @since Build 2.0.0 (May 9, 2013)
 * @author Tarka L'Herpiniere

 */
public class JnocJTAException extends Exception {

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public JnocJTAException() {
        super();
    }
    
    public JnocJTAException(String message) {
        super(message);
    }
    
    public JnocJTAException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public JnocJTAException(Throwable cause) {
        super(cause);
    }
//</editor-fold>

}
