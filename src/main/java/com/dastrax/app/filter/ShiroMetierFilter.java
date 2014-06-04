/*
 * Created Jul 15, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.filter;

import com.dastrax.per.project.DTX;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Filters and redirects users based on their Metier. This filter is used by
 * SHIRO and is specified and mapped to URI's in the <shiro.ini> file.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 15, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class ShiroMetierFilter implements Filter {
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final boolean debug = true;
    private FilterConfig filterConfig;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public ShiroMetierFilter() {
        this.filterConfig = null;
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of filterConfig
     *
     * @return the value of filterConfig
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of filterConfig
     *
     * @param filterConfig new value of filterConfig
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }
//</editor-fold>
    
    /**
     * When invoked, this filter redirects a user to their respective
     * dashboard / welcome page based on their metier.
     * 
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException 
     */
    @Override
    public void doFilter(
            ServletRequest request, 
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        Subject currentUser = SecurityUtils.getSubject();
        HttpServletResponse httpResponse=(HttpServletResponse)response;

        if (currentUser.hasRole(DTX.Metier.ADMIN.toString())) {
            httpResponse.sendRedirect("a/dashboard.jsf");
            return;
        }

        if (currentUser.hasRole(DTX.Metier.VAR.toString())) {            
            httpResponse.sendRedirect("b/welcome.jsf");
            return;
        }
        
        if (currentUser.hasRole(DTX.Metier.CLIENT.toString())) {            
            httpResponse.sendRedirect("c/welcome.jsf");
            return;
        }

        if (currentUser.hasRole(null)) {
            httpResponse.sendRedirect("login.jsf");
            return;
        }

        
        Throwable problem = null;
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
        }
        

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    /**
     * 
     * @param t
     * @param response 
     */
    private void sendProcessingError(
            Throwable t, 
            ServletResponse response) {
        String stackTrace = getStackTrace(t);        
        
        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                try (PrintStream ps = new PrintStream(response.getOutputStream()); PrintWriter pw = new PrintWriter(ps)) {                
                    pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                    // PENDING! Localize this for next official release
                    pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");                
                    pw.print(stackTrace);                
                    pw.print("</pre></body>\n</html>"); //NOI18N
                }
                response.getOutputStream().close();
            } catch (IOException ex) {
            }
        } else {
            try {
                try (PrintStream ps = new PrintStream(response.getOutputStream())) {
                    t.printStackTrace(ps);
                }
                response.getOutputStream().close();
            } catch (IOException ex) {
            }
        }
    }
    
    /**
     * 
     * @param t
     * @return 
     */
    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (IOException ex) {
        }
        return stackTrace;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
    
    //<editor-fold defaultstate="collapsed" desc="Overrides">
    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
    }
    
    /**
     * Init method for this filter
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("RoleRedirect:Initializing filter");
            }
        }
    }
    
    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("RoleRedirect()");
        }
        StringBuilder sb = new StringBuilder("RoleRedirect(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
//</editor-fold>
    
}
