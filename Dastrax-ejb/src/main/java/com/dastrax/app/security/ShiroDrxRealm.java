/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.app.security;

import com.dastrax.per.dao.SubjectDAO;
import com.dastrax.per.entity.core.Permission;
import com.dastrax.per.entity.core.Subject;
import java.util.HashSet;
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
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 *
 * @version Build 2.0.0
 * @since Jul 15, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class ShiroDrxRealm extends AuthorizingRealm {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(ShiroDrxRealm.class.getName());

    // Variables----------------------------------------------------------------
    protected boolean permissionsLookupEnabled = true;

    // JNDI---------------------------------------------------------------------
    private static final String JNDI_SUB = ResourceBundle.getBundle("Config").getString("Subject");

    // Methods------------------------------------------------------------------
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {

        // Seperate out the token
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String email = upToken.getUsername().toLowerCase();

        // Null username is invalid
        if (email == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        // Init return object
        SimpleAuthenticationInfo info = null;

        try {
            SubjectDAO subject = (SubjectDAO) InitialContext.doLookup(JNDI_SUB);
            Subject s = subject.findSubjectByEmail(email);

            // Make sure a password was found
            if (s.getPassword() == null) {
                throw new UnknownAccountException("No password found for user [" + email + "]");
            }

            // Add requires principals to the PrincipalCollection
            SimplePrincipalCollection principals = new SimplePrincipalCollection();
            principals.add(email, "email");
            principals.add(s.getUid(), "uid");

            info = new SimpleAuthenticationInfo(principals, s.getPassword().toCharArray());
        } catch (NamingException ne) {
            LOG.log(Level.SEVERE, "JNDI NamingException during authentication", ne);
            throw new UnknownAccountException("Exception during authentication");
        }
        return info;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        String email = (String) getAvailablePrincipal(principals);

        try {
            SubjectDAO subject = (SubjectDAO) InitialContext.doLookup(JNDI_SUB);
            Subject s = subject.findSubjectByEmail(email);
            
            // Add the metier to the set
            Set<String> metiers = new HashSet<>();
            metiers.add(s.getMetier().getName());
            
            // Add the permissions to the set
            Set<String> permissions = new HashSet<>();
            for (Permission p : s.getPermissions()) {
                permissions.add(p.getName());
            }
            
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(metiers);
            info.setStringPermissions(permissions);
        
            return info;
        
        } catch (NamingException ne) {
            LOG.log(Level.SEVERE, "JNDI NamingException during authorization", ne);
            throw new UnknownAccountException("Exception during authentication");
        } 
    }

}
