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

import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.dap.QueryParameter;
import co.ff36.jnoc.per.entity.Permission;
import co.ff36.jnoc.per.entity.User;

import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 * This class acts as a gateway between the SHIRO API and our custom database
 * structure. In order to allow the use of the SHIRO security API we need to
 * let SHIRO know where in the database it can find the users credentials.
 *
 * @version 1.0.0
 * @since Build 1.0.0 (2013)
 * @author Tarka L'Herpiniere

 */
public class ShiroJnocRealm extends AuthorizingRealm {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(
            ShiroJnocRealm.class.getName());
    protected boolean permissionsLookupEnabled = true;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo (
            AuthenticationToken token) throws AuthenticationException {
        
        // Seperate out the token
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String email = upToken.getUsername().toLowerCase();
        
        // Null username is invalid
        if (email == null) {
            throw new AccountException("Null usernames are not" 
                    + " allowed by this realm.");
        }
        
        // Init return object
        SimpleAuthenticationInfo info = null;
        
        try {
            CrudService dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
            
            @SuppressWarnings("unchecked")
			List<User> users = (List<User>)dap.findWithNamedQuery(
                    "User.findByEmail", 
                    QueryParameter.with("email", email).parameters());
            
            User user = users.get(0);
            
            // Make sure a password was found
            if (user.getPassword() == null) {
                throw new UnknownAccountException("No password found for user [" 
                        + email + "]");
            }
            
            // Add requires principals to the PrincipalCollection
            SimplePrincipalCollection principals = 
                    new SimplePrincipalCollection();
            principals.add(user, "user");
            
            
            info = new SimpleAuthenticationInfo(
                    principals, 
                    user.getPassword().toCharArray());
            //exit authentication
//            PasswordService psvc = new DefaultPasswordService();
//            String newencrypted = psvc.encryptPassword(upToken.getPassword());
//            LOG.log(Level.ALL, "password db:"+user.getPassword()+", param:"+ newencrypted);
//            info = new SimpleAuthenticationInfo(
//                    principals, 
//                    newencrypted.toCharArray());
        } catch (NamingException ne) {
            LOG.log(Level.SEVERE, "JNDI NamingException", ne);
            throw new UnknownAccountException("Exception during authentication");
        }
        return info;
    }
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        
        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException(
                    "PrincipalCollection method argument cannot be null.");
        }
        
        User currentUser = (User) getAvailablePrincipal(principals);
        
        try {
            
            CrudService dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
            
            @SuppressWarnings("unchecked")
			List<User> users = (List<User>)dap.findWithNamedQuery(
                    "User.findByEmail", 
                    QueryParameter
                            .with("email", currentUser.getEmail()).parameters());
            
            User user = users.get(0);
            
            // Add the metier to the set
            Set<String> metiers = new HashSet<>();
            metiers.add(user.getMetier().getName());
            
            // Add the permissions to the set
            Set<String> permissions = new HashSet<>();
            for (Permission p : user.getPermissions()) {
                permissions.add(p.getExpression());
            }
            
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(metiers);
            info.setStringPermissions(permissions);
            
            return info;
            
        } catch (NamingException ne) {
            LOG.log(Level.SEVERE, "JNDI NamingException during authorization", ne);
            throw new UnknownAccountException("Exception during authentication");
        }
    }
//</editor-fold>

}
