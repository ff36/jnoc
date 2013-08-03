/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */

package com.dastrax.app.exception;

/**
 *
 * @version Build 2.0.0
 * @since Jul 13, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class DastraxJTAException extends Exception {

    public DastraxJTAException() {
        super();
    }

    public DastraxJTAException(String message) {
        super(message);
    }

    public DastraxJTAException(String message, Throwable cause) {
        super(message, cause);
    }

    public DastraxJTAException(Throwable cause) {
        super(cause);
    }

}
