/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */

package com.dastrax.app.filter;

import com.dastrax.per.project.DastraxCst;
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
 *
 * @version Build 2.0.0
 * @since Jul 15, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class ShiroRoleFilter implements Filter {
    
    // Variables----------------------------------------------------------------
    private static final boolean debug = true;
    FilterConfig filterConfig = null;
    
    // Constructor--------------------------------------------------------------
    public ShiroRoleFilter() {
    
    }    
    
    // Methods------------------------------------------------------------------
    /**
     * The filters primary do method that performs the filter action. This filter
     * is called by Shiro, post authentication ONLY if no original URL request 
     * exists. That is to say; If a subject requested a protected resource but
     * was not authorized to access it, Shiro will have intercepted the request 
     * and prompted the subject to authenticate. If the authentication was 
     * successful the original request will be fulfilled. If the subject accessed
     * the authentication page directly then no pre-existing request exists and
     * this filter determines the page that the subject should be directed to 
     * based on their role.
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException 
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        Subject currentUser = SecurityUtils.getSubject();
        HttpServletResponse httpResponse=(HttpServletResponse)response;

        if (currentUser.hasRole(DastraxCst.Metier.ADMIN.toString())) {
            httpResponse.sendRedirect("a/dashboard.jsf");
            return;
        }

        if (currentUser.hasRole(DastraxCst.Metier.VAR.toString())) {            
            httpResponse.sendRedirect("b/welcome.jsf");
            return;
        }
        
        if (currentUser.hasRole(DastraxCst.Metier.CLIENT.toString())) {            
            httpResponse.sendRedirect("c/welcome.jsf");
            return;
        }

        if (currentUser.hasRole("ANONYMOUS")) {
            httpResponse.sendRedirect("index.jsf");
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
     * Return the filter configuration object for this filter.
     * @return 
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

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
}
