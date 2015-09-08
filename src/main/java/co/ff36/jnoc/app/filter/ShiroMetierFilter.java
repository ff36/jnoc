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

package co.ff36.jnoc.app.filter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import co.ff36.jnoc.app.security.SessionUser;
import co.ff36.jnoc.per.entity.User;

/**
 * Filters and redirects users based on their Metier. This filter is used by
 * SHIRO and is specified and mapped to URI's in the <shiro.ini> file.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 15, 2013)
 * @author Tarka L'Herpiniere

 */
public class ShiroMetierFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(ShiroMetierFilter.class.getName());
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
        
        User user = SessionUser.getCurrentUser();
        HttpServletResponse httpResponse=(HttpServletResponse)response;

        if (user.isAdministrator()) {
            httpResponse.sendRedirect("a/dashboard.jsf");
            return;
        } 
        
        if (user.isVAR()) {            
            httpResponse.sendRedirect("a/dashboard.jsf");
            return;
        } 
        
        if (user.isClient()) {            
            httpResponse.sendRedirect("a/dashboard.jsf");
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
            	LOG.log(Level.CONFIG, ex.getMessage(), ex);
            }
        } else {
            try {
                try (PrintStream ps = new PrintStream(response.getOutputStream())) {
                    t.printStackTrace(ps);
                }
                response.getOutputStream().close();
            } catch (IOException ex) {
            	LOG.log(Level.CONFIG, ex.getMessage(), ex);
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
        	LOG.log(Level.CONFIG, ex.getMessage(), ex);
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
