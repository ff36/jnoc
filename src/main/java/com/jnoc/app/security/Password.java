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


/*
 * Created May 9, 2014.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 

 */
package com.jnoc.app.security;

import static com.jnoc.per.project.JNOC.PASSWORD_LENGTH;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;

/**
 * This is a simple password object that handles passwords and related services
 * such as encryption, generation
 *
 * @version 3.0.0
 * @since Build 3.0.0 (May 9, 2014)
 * @author Tarka L'Herpiniere

 */
public class Password {
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Password.class.getName());
    private String current, request, confirm, email, encrypted;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Password() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of encrypted
     *
     * @return the value of encrypted
     */
    public String getEncrypted() {
        return encrypted;
    }

    /**
     * Get the value of email
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the value of confirm
     *
     * @return the value of confirm
     */
    public String getConfirm() {
        return confirm;
    }

    /**
     * Get the value of request
     *
     * @return the value of request
     */
    public String getRequest() {
        return request;
    }

    /**
     * Get the value of current
     *
     * @return the value of current
     */
    public String getCurrent() {
        return current;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of encrypted
     *
     * @param encrypted new value of encrypted
     */
    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    /**
     * Set the value of email
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the value of confirm
     *
     * @param confirm new value of confirm
     */
    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    /**
     * Set the value of request
     *
     * @param request new value of request
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * Set the value of current
     *
     * @param current new value of current
     */
    public void setCurrent(String current) {
        this.current = current;
    }

    public void setBothPasswords(String password) {
        request = password;
        confirm = password;
    }
//</editor-fold>

    /**
     * Encrypts a clear text UTF8 password using SHIRO's one way hash.
     *
     * @return true if the encryption was successful. Otherwise false.
     */
    public boolean encrypt() {
        PasswordService psvc = new DefaultPasswordService();
        encrypted = psvc.encryptPassword(request);
        return true;
    }

    /**
     * This is a summary method that combines most methods in this class to
     * perform a complete password validation and returns an encrypted version
     * of the password if all is well.
     *
     * @return true if and only if the password passed all the checks.
     */
    public boolean fullCheck() {

        if (request.length() < 2) {
            setBothPasswords(generate());
        }

        if (strengthCheck()) {
            if (matchRequested()) {
                return true;
            } else {
                // Set the object to null
            }
        } else {
            // Set the object to null
        }
        return false;
    }

    /**
     * System passwords should be of a specified strength. For security this
     * method checks to make sure that the supplied password meets the min
     * requirements.
     *
     * @return true if the password passes the minimum checks.
     */
    public boolean strengthCheck() {

        if (request != null) {
            // Betwen 7 and 100 characters
            if (request.length() > 100) {
                return false;
            }
            if (request.length() < 7) {
                return false;
            }
            // At least 1 number
            String numbers = "(.*[0-9].*)";
            if (!request.matches(numbers)) {
                return false;
            }

            //<editor-fold defaultstate="collapsed" desc="Unused Password Rules">
            //        if (password.contains(email)) {
            //            return false;
            //        }
            //        String upperCaseChars = "(.*[A-Z].*)";
            //        if (!password.matches(upperCaseChars)) {
            //            return false;
            //        }
            //        String lowerCaseChars = "(.*[a-z].*)";
            //        if (!password.matches(lowerCaseChars)) {
            //            return false;
            //        }
            //</editor-fold>
        } else {
            // The requested password is null
            return false;
        }
        // The requested password meets the requirements
        return true;
    }

    /**
     * Check to make sure that the password submitted matches in both instances.
     *
     * @return true if the passwords are identical in both instances. Such that
     * password.getRequest().equals(password.getConfirm()).
     */
    public boolean matchRequested() {
        try {
            if (!request.isEmpty() && !confirm.isEmpty()) {
                return request.equals(confirm);
            } else {
                // The passwords dont match
                return false;
            }
        } catch (NullPointerException npe) {
        	LOG.log(Level.CONFIG, npe.getMessage(), npe);
            // The requested and/or confirm password is null
            return false;
        }
    }

    /**
     * Check to make sure that the submitted password matches the stored
     * existing one in the db. The submitted password should be in an
     * un-encrypted format. Whilst the encrypted password should be in its
     * encrypted format.
     *
     * @return true if the submitted password matches the else returns false
     */
    public boolean matchEncrypted() {

        boolean result = false;
        try {
            PasswordService psvc = new DefaultPasswordService();
            result = psvc.passwordsMatch(
                    current,
                    encrypted);
        } catch (Exception e) {
            LOG.log(Level.OFF, "Exception: SHIRO -> PASSWORD SERVICE", e);
        }
        return result;
    }

    /**
     * Generate a random String suitable for use as a temporary password. This
     * method will make sure it meets the minimum requirements for the system
     * password as controlled by meetsMinRequirement().
     *
     * @return String suitable for use as a temporary password
     */
    public String generate() {

        String letters = "abcdefghjkmnpqrstuvwxyz"
                + "ABCDEFGHJKMNPQRSTUVWXYZ"
                + "0123456789"
                + "~!@#$%^&*()-_=+[{]}|;:<,>/?]";
        String password = null;
        boolean retry = true;

        /*
         * Setup a loop so that if the password doesn't contain the correct min
         * character requirements the password is re-generated.
         */
        while (retry) {

            // Generate a random password
            password = "";
            Random RANDOM = new SecureRandom();
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                int index = (int) (RANDOM.nextDouble() * letters.length());
                password += letters.substring(index, index + 1);
            }

            // Check to make sure the password conforms
            String upperCaseChars = "(.*[A-Z].*)";
            String lowerCaseChars = "(.*[a-z].*)";
            String numbers = "(.*[0-9].*)";
            String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,"
                    + "[,{,],},|,;,:,<,>,/,?].*$)";
            retry = !(password.matches(upperCaseChars)
                    & password.matches(lowerCaseChars)
                    & password.matches(numbers)
                    & password.matches(specialChars));

        }
        return password;
    }

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.current);
        hash = 53 * hash + Objects.hashCode(this.request);
        hash = 53 * hash + Objects.hashCode(this.confirm);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Password other = (Password) obj;
        if (!Objects.equals(this.current, other.current)) {
            return false;
        }
        if (!Objects.equals(this.request, other.request)) {
            return false;
        }
        if (!Objects.equals(this.confirm, other.confirm)) {
            return false;
        }
        return true;
    }
//</editor-fold>

}
