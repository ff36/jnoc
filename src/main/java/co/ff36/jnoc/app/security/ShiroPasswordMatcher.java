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

package co.ff36.jnoc.app.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.credential.PasswordMatcher;

/**
 * This class extends SHIRO PasswordMatcher extending the SHIRO API to enable 
 * authentication against encrypted passwords.
 *
 * @version 1.0.0
 * @since Build 1.0.0 (Jul 15, 2013)
 * @author Tarka L'Herpiniere

 */
public class ShiroPasswordMatcher extends PasswordMatcher {

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    /**
     * Converts the stored encrypted user password into a char[] so SHIRO 
     * can match it with new authentication requests.
     * 
     * @param storedAccountInfo
     * @return 
     */
    @Override
    protected Object getStoredPassword(AuthenticationInfo storedAccountInfo) {
        Object stored = super.getStoredPassword(storedAccountInfo);
        
        if (stored instanceof char[]) {
            stored = String.valueOf((char[]) stored);
        }
        
        return stored;
    }
//</editor-fold>
    
}
