/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.app.security;

import com.dastrax.app.pojo.Response;
import com.dastrax.app.util.ExceptionUtil;
import java.security.SecureRandom;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;

/**
 * This class performs password checks and validations. Before passwords are
 * accepted an encrypted they need to be sufficiently strong. Once they are
 * encrypted by SHIRO they need to be able to be checked for comparisons.
 *
 * @version Build 2.0.0
 * @since Jul 14, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class PasswordSvcs {
    
    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(PasswordSvcs.class.getName());
    
    // Constants----------------------------------------------------------------
    private static final Random RANDOM = new SecureRandom();
    public static final int PASSWORD_LENGTH = 20;

    // EJB----------------------------------------------------------------------
    @EJB
    ExceptionUtil exu;
    
    // Methods------------------------------------------------------------------
    /**
     * This is a summary method that combines most methods in this class to
     * perform a complete password validation and returns an encrypted version
     * of the password if all is well.
     *
     * @param passwords if the String[] length != 2 a random password will be
     * generated
     * @param email
     * @return Shiro encrypted version of the password if the operation is
     * successful or null is not. Additionally if null is returned the
     * respective condition that caused the validation to fail is returned as 
     * a String message with the JSF message type to the Response map.
     */
    public Response validate(String[] passwords, String email) {
        
        Response response = new Response();
        if (passwords.length != 2) {
            passwords[0] = generateRandom();
            passwords[1] = passwords[0];
        }
        Response r1 = meetsMinRequirement(passwords[0], email);
        if ((Boolean)r1.getObject()) {
            Response r2 = passwordsMatch(passwords[0], passwords[1]);
            if ((Boolean)r2.getObject()) {
                // Add the encrypted password to the response
                response.setObject(encryptPassword(passwords[0]));
            } else {
                // Set the object to null
                r2.setObject(null);
                response = r2;
            }
        } else {
            // Set the object to null
            r1.setObject(null);
            response = r1;
        }
        return response;
    }

    /**
     * Extends the validate method to include a current password check
     *
     * @param passwords if the String[] length != 2 a random password will be
     * generated
     * @param email
     * @param currentPsw
     * @param encryptedPsw
     * @return Shiro encrypted version of the password if the operation is
     * successful or null is not. Additionally if null is returned the
     * respective condition that caused the validation to fail is returned as 
     * a String message with the JSF message type to the Response map.
     */
    public Response validateExt(
            String[] passwords,
            String email,
            String currentPsw,
            String encryptedPsw) {
        
        Response response = new Response();
        if (passwordCorrect(currentPsw, encryptedPsw)) {
            // Password is correct
            response = validate(passwords, email);
        } else {
            // Password is wrong
            response.addJsfWrnMsg(
                    ResourceBundle
                    .getBundle("EjbBundle")
                    .getString("valid.password.wrong"));
        }
        return response;
    }

    /**
     * System passwords should be of a specified strength. For security this
     * method checks to make sure that the supplied password meets the min
     * requirements.
     *
     * Password should be less than 100 and more than 5 characters in length.
     * Password should contain at least one upper case and one lower case
     * alphabet. Password should contain at least one number.
     *
     * @param password desired password
     * @param email email of the subject for whom the password will be
     * associated
     * @return Response object containing the status and any response messages.
     */
    public Response meetsMinRequirement(String password, String email) {
        Response response = new Response();
        response.setObject((Boolean)true);
        if (password.length() > 100 || password.length() < 5) {
            response.addJsfWrnMsg(
                    ResourceBundle
                    .getBundle("EjbBundle")
                    .getString("valid.password.length"));
            response.setObject((Boolean)false);

        }
        if (password.indexOf(email) > -1) {
            response.addJsfWrnMsg(
                    ResourceBundle
                    .getBundle("EjbBundle")
                    .getString("valid.password.email"));
            response.setObject((Boolean)false);
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars)) {
            response.addJsfWrnMsg(
                    ResourceBundle
                    .getBundle("EjbBundle")
                    .getString("valid.password.uppercase"));
            response.setObject((Boolean)false);
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars)) {
            response.addJsfWrnMsg(
                    ResourceBundle
                    .getBundle("EjbBundle")
                    .getString("valid.password.lowercase"));
            response.setObject((Boolean)false);
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers)) {
            response.addJsfWrnMsg(
                    ResourceBundle
                    .getBundle("EjbBundle")
                    .getString("valid.password.number"));
            response.setObject((Boolean)false);
        }
        return response;
    }

    /**
     * Check to make sure that the password submitted matches in both instances.
     *
     * @param requestedPassword1
     * @param requestedPassword2 confirmation of password
     * @return true if the passwords are identical in both instances
     */
    public Response passwordsMatch(String requestedPassword1, String requestedPassword2) {
        Response response = new Response();
        if (requestedPassword1.equals(requestedPassword2)) {
            response.setObject((Boolean)true);
        } else {
            response.addJsfWrnMsg(
                    ResourceBundle
                    .getBundle("EjbBundle")
                    .getString("valid.password.match"));
            response.setObject((Boolean)false);
        }
        return response;
    }

    /**
     * Check to make sure that the submitted password matches the stored
     * existing one in the db. The submitted password should be in an
     * un-encrypted format. Whilst the encrypted password should be in its
     * encrypted format.
     *
     * @param submittedPassword
     * @param encryptedPassword
     * @return true if the submitted password match else returns false
     */
    public boolean passwordCorrect(String submittedPassword, String encryptedPassword) {

        boolean result = false;
        try {
            PasswordService psvc = new DefaultPasswordService();
            result = psvc.passwordsMatch(submittedPassword, encryptedPassword);
        } catch (Exception e) {
            exu.report(e);
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
    public String generateRandom() {

        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ0123456789~!@#$%^&*()-_=+[{]}|;:<,>/?]";
        String password = null;
        boolean retry = true;

        /*
         * Setup a loop so that if the password doesn't contain the correct min
         * character requirements the password is regenerated.
         */
        while (retry) {
            /*
             * Generate the random password
             */
            password = "";
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                int index = (int) (RANDOM.nextDouble() * letters.length());
                password += letters.substring(index, index + 1);
            }

            /*
             * Check to make sure the password conforms
             */
            String upperCaseChars = "(.*[A-Z].*)";
            String lowerCaseChars = "(.*[a-z].*)";
            String numbers = "(.*[0-9].*)";
            String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)";
            retry = !(password.matches(upperCaseChars)
                    & password.matches(lowerCaseChars)
                    & password.matches(numbers)
                    & password.matches(specialChars));

        }
        return password;
    }

    /**
     * Encrypts a password using SHIRO.
     *
     * @param plainTextPassword
     * @return An encrypted version of the password
     */
    public String encryptPassword(String plainTextPassword) {
        PasswordService psvc = new DefaultPasswordService();
        String encryptedPassword = psvc.encryptPassword(plainTextPassword);
        return encryptedPassword;
    }
}
