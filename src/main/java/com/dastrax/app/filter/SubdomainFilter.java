/*
 * Created Aug 16, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */

package com.dastrax.app.filter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filters and redirects users to their respective authentication sub-domain 
 * based on their Company. This filter is used by SHIRO and is specified and 
 * mapped to URI's in the <shiro.ini> file.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Aug 16, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class SubdomainFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(SubdomainFilter.class.getName());
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final boolean debug = true;
    private FilterConfig filterConfig = null;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public SubdomainFilter() {
    }
//</editor-fold>
  
    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Return the filter configuration object for this filter.
     * @return
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }
//</editor-fold>
    
   /**
     * When invoked, this filter redirects a user to their respective
     * authentication sub-domain page based on their Company.
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
        
        
        /*
         * Obtain the original request URL made by the user to arrive at the
         * home page. I don't think any query strings can be present in this
         * but we will include them for completeness for futur implementations.
         */
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String url = httpRequest.getRequestURL().toString();
        
        /*
         * This is a fix! Without this the login page will fail to load with a 
         * javax.servlet.ServletException: Cannot create a session after the 
         * response has been committed.
         * This error should have been fixed in Majorra 2.1.8 but seems to have
         * been re-introduced in 2.2.1! The following are other alternative 
         * solutions:
         * http://stackoverflow.com/questions/8072311/illegalstateexception-cannot-create-a-session-after-the-response-has-been-commi
         */
        HttpSession session = httpRequest.getSession(true);
        
        /*
         * Evaluate the URL to determin if a subdomain needs to be extracted.
         */
        String subdomain = null;
        String baseUrl = ResourceBundle.getBundle("config").getString("BaseUrl");
        String accessProtocol = ResourceBundle.getBundle("config").getString("AccessProtocol");
        String applicationURL = accessProtocol + baseUrl + "/";

        if (!url.equals(applicationURL)) {
            StringTokenizer st = new StringTokenizer(url, "/.");

            for (int i = 0; i < 2; i++) {
                subdomain = st.nextToken();
            }

            /*
             * Check to make sure the subdomain string is not the uat subdomain
             */
            if (subdomain == null || subdomain.equals("uat")) {
                subdomain = null;
            }
        }
        
        /*
         * If the subdomain logic returns a string then a subdomain is present 
         * and we need redirect the user to the login page.
         */
        if (subdomain != null) {
            HttpServletResponse httpResponse=(HttpServletResponse)response;
            httpResponse.sendRedirect("login.jsf");
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
    private void sendProcessingError(Throwable t, ServletResponse response) {
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
            	LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        } else {
            try {
                try (PrintStream ps = new PrintStream(response.getOutputStream())) {
                    t.printStackTrace(ps);
                }
                response.getOutputStream().close();
            } catch (IOException ex) {
            	LOG.log(Level.SEVERE, ex.getMessage(), ex);
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
        	LOG.log(Level.SEVERE, ex.getMessage(), ex);
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
                log("SubdomainRedirect:Initializing filter");
            }
        }
    }
    
    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("SubdomainRedirect()");
        }
        StringBuilder sb = new StringBuilder("SubdomainRedirect(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
//</editor-fold>
    
}
