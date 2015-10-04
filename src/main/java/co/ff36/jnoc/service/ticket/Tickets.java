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

package co.ff36.jnoc.service.ticket;

import co.ff36.jnoc.app.misc.CookieUtil;
import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.app.model.DataTable;
import co.ff36.jnoc.app.model.ModelQuery;
import co.ff36.jnoc.app.model.TicketModelQuery;
import co.ff36.jnoc.app.service.internal.DefaultAttributeFilter;
import co.ff36.jnoc.service.navigation.MenuItem;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Cookie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Ticket table CDI bean. Provides advanced data table capability for
 * displaying and querying.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere

 */
@Named
@ViewScoped
public class Tickets implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Tickets.class.getName());
    
    private static final long serialVersionUID = 1L;
    private DataTable dataTable;
    private final ModelQuery model;
    private final Map<String, List<String>> parameters;
    private int browserHeight;
    private int browserWidth;
    private int numberOfRows;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Tickets() {
        this.model = new TicketModelQuery();
        this.parameters = JsfUtil.getRequestParameters();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of dataTable.
     *
     * @return the value of dataTable
     */
    public DataTable getDataTable() {
        return dataTable;
    }

    public int getBrowserHeight() {
        return browserHeight;
    }

    public void setBrowserHeight(int browserHeight) {
        this.browserHeight = browserHeight;
    }

    public int getNumberOfRows() {
//    	Cookie[] cookies = CookieUtil.readCookie();
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals("tickets_number_of_rows")) {
//                this.numberOfRows = Integer.parseInt(cookie.getValue());
//                break;
//            }
//        }
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
		
    	Cookie cookie = new Cookie(
		        "tickets_number_of_rows",
		        numberOfRows+"");
		//cookie.setHttpOnly(true);
		cookie.setMaxAge(31536000);
		cookie.setPath("/");
		CookieUtil.writeCookie(cookie);
		
        this.numberOfRows = numberOfRows;
    }

    public int getBrowserWidth() {
        return browserWidth;
    }

    public void setBrowserWidth(int browserWidth) {
        this.browserWidth = browserWidth;
    }
    
    
 
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of dataTable.
     *
     * @param dataTable new value of dataTable
     */
    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }
//</editor-fold>

    /**
     * Called by the ajax remote command call to initialize the page properties
     * and data after the page has loaded.
     */
    public void init() {
        // Get the browser height
        Map<String, String> requestParameterMap = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
        browserHeight = Integer.valueOf(requestParameterMap.get("hidden_field_browser_height"));
        browserWidth = Integer.valueOf(requestParameterMap.get("hidden_field_browser_width"));
        
        boolean isExistForCookie = false;
        Cookie[] cookies = CookieUtil.readCookie();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("tickets_number_of_rows")) {
                this.numberOfRows = Integer.parseInt(cookie.getValue());
                isExistForCookie = true;
                break;
            }
        }
        
        if(!isExistForCookie){
        	if (browserWidth >= 2100) {
                numberOfRows = browserHeight / 34; 
             } 
             if (browserWidth > 1900 && browserWidth < 2100) {
                 numberOfRows = browserHeight / 40;
             }
             if (browserWidth < 1900) {
                 numberOfRows = browserHeight / 50;
             }
        }
        
        dataTable = new DataTable(model);
        dataTable.initTable(
                parameters, 
                new DefaultAttributeFilter().authorizedTickets());
    }

}
