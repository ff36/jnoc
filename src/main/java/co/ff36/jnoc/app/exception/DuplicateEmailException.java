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

package co.ff36.jnoc.app.exception;

/**
 * This is the custom JSF exception. 
 * This is used by the <JnocExceptionHandler.class>
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 10, 2013)
 * @author Tarka L'Herpiniere

 */
public class DuplicateEmailException extends Exception {

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public DuplicateEmailException() {
        super();
    }
    
    public DuplicateEmailException(String message) {
        super(message);
    }
    
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DuplicateEmailException(Throwable cause) {
        super(cause);
    }
//</editor-fold>
}
