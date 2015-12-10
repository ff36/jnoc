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

package co.ff36.jnoc.service.user;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.app.model.DataTable;
import co.ff36.jnoc.app.model.ModelQuery;
import co.ff36.jnoc.app.model.UserModelQuery;
import co.ff36.jnoc.app.service.internal.DefaultAttributeFilter;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.entity.Ticket;
import co.ff36.jnoc.per.entity.User;

/**
 * User table CDI bean. Provides advanced data table capability for
 * displaying and querying.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere

 */
@Named
@ViewScoped
public class Users implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Users.class.getName());
    private static final long serialVersionUID = 1L;
    
    private DataTable dataTable;
    private final ModelQuery model;
    private final Map<String, List<String>> parameters;
    
    private User selectUser;
    @EJB
    private CrudService dap;
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Users() {
        this.model = new UserModelQuery();
        parameters = JsfUtil.getRequestParameters();
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
    
//</editor-fold>

    public User getSelectUser() {
		return selectUser;
	}

	public void setSelectUser(User u) {
		this.selectUser = (User) dap.find(User.class, Long.valueOf(u.getId()));
	}

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
        dataTable = new DataTable(model);
        dataTable.initTable(
                parameters, 
                new DefaultAttributeFilter().authorizedUsers());
    }
 
}
