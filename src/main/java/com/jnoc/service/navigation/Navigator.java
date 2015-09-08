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

package com.jnoc.service.navigation;

import com.jnoc.per.project.JNOC;
import com.jnoc.app.misc.CookieUtil;
import com.jnoc.app.misc.JsfUtil;
import com.jnoc.app.security.SessionUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import org.apache.shiro.SecurityUtils;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 * Navigator is a CDI bean that facilitates application navigation functions.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (May 26, 2014)
 * @author Tarka L'Herpiniere

 */
@Named
@SessionScoped
public class Navigator implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Navigator.class.getName());
    private static final long serialVersionUID = 1L;

    private MenuModel menu;
    private List<MenuItem> menuItems;
    private MenuItem[] selectedMenuItems;
    private MenuItemDataModel menuItemDataModel;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Navigator() {
        this.menuItems = new ArrayList<>();
        init();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of menu
     *
     * @return the value of menu
     */
    public MenuModel getModel() {
        return menu;
    }

    /**
     * Get the value of selectedMenuItems
     *
     * @return the value of selectedMenuItems
     */
    public MenuItem[] getSelectedMenuItems() {
        return selectedMenuItems;
    }

    /**
     * Get the value of menuItemDataModel
     *
     * @return the value of menuItemDataModel
     */
    public MenuItemDataModel getMenuItemDataModel() {
        return menuItemDataModel;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of selectedMenuItems.
     *
     * @param selectedMenuItems new value of selectedMenuItems
     */
    public void setSelectedMenuItems(MenuItem[] selectedMenuItems) {
        this.selectedMenuItems = selectedMenuItems;
    }
//</editor-fold>

    /**
     * Called when the bean is constructed and after every modification to the
     * navigation preferences. This method constructs the navigation menu bar
     * based on the cookies set by the navigation preferences.
     */
    private void init() {

        menuItems = new ArrayList<>();

        // List all the existing menu items
        menuItems.add(new MenuItem("1", "Dashboard", "/a/dashboard.xhtml?faces-redirect=true"));
        menuItems.add(new MenuItem("2", "Support Tickets", "/a/tickets/list.xhtml?faces-redirect=true"));
        menuItems.add(new MenuItem("3", "Create Support Ticket", "/a/tickets/create.xhtml?faces-redirect=true"));
        //menuItems.add(new MenuItem("4", "Incidents", "/a/incidents/list.xhtml?faces-redirect=true"));
        if (SessionUser.getCurrentUser().hasPermission("account:access")
                && SessionUser.getCurrentUser().isAdministrator()) {
            menuItems.add(new MenuItem("5", "Accounts", "/a/accounts/list.xhtml?faces-redirect=true"));
        }
        if (SessionUser.getCurrentUser().hasPermission("account:create")
                && SessionUser.getCurrentUser().isAdministrator()) {
            menuItems.add(new MenuItem("6", "Create Account", "/a/accounts/create.xhtml?faces-redirect=true"));
        }
        if (SessionUser.getCurrentUser().hasPermission("company:access")
                && SessionUser.getCurrentUser().isAdministrator()) {
            menuItems.add(new MenuItem("7", "Companies", "/a/companies/list.xhtml?faces-redirect=true"));
        }
        if (SessionUser.getCurrentUser().hasPermission("company:create")
                && SessionUser.getCurrentUser().isAdministrator()) {
            menuItems.add(new MenuItem("8", "Create Company", "/a/companies/create.xhtml?faces-redirect=true"));
        }
        if (SessionUser.getCurrentUser().hasPermission("das:access")
                && SessionUser.getCurrentUser().isAdministrator()) {
            menuItems.add(new MenuItem("9", "DAS", "/a/das/list.xhtml?faces-redirect=true"));
        }
        if (SessionUser.getCurrentUser().hasPermission("das:create")
                && SessionUser.getCurrentUser().isAdministrator()) {
            menuItems.add(new MenuItem("10", "Create DAS", "/a/das/create.xhtml?faces-redirect=true"));
        }
        menuItems.add(new MenuItem("11", "Create RMA", "/a/rma/create.xhtml?faces-redirect=true"));
        //menuItems.add(new MenuItem("12", "Analytics", "/a/analytics/dashboard.xhtml?faces-redirect=true"));
        menuItems.add(new MenuItem("13", "Profile Settings", "/a/settings/personal/profile.xhtml?faces-redirect=true"));
        menuItems.add(new MenuItem("14", "Account Settings", "/a/settings/personal/admin.xhtml?faces-redirect=true"));
        menuItems.add(new MenuItem("15", "Security Settings", "/a/settings/personal/security.xhtml?faces-redirect=true"));
        menuItems.add(new MenuItem("16", "Navigation Settings", "/a/settings/personal/navigation.xhtml?faces-redirect=true"));
        menuItems.add(new MenuItem("17", "Company Profile Settings", "/a/settings/company/profile.xhtml?faces-redirect=true"));
        //menuItems.add(new MenuItem("18", "Support Tickets", "/a/tickets/analytics.xhtml?faces-redirect=true"));

        menuItemDataModel = new MenuItemDataModel(menuItems);

        // TODO: remove the unauthorized ones
        // Read the cookies
        try {
            Cookie[] cookies = CookieUtil.readCookie();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jnoc_navigation")) {
                    final ObjectMapper mapper = new ObjectMapper();
                    selectedMenuItems = mapper.readValue(cookie.getValue(), MenuItem[].class);
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        // Create a menu based on the selectedMenuItems
        menu = new DefaultMenuModel();
        if (selectedMenuItems != null) {
            for (MenuItem menuItem : selectedMenuItems) {
                DefaultMenuItem item = new DefaultMenuItem(menuItem.getLabel());
                item.setOutcome(menuItem.getOutcome());
                menu.addElement(item);
            }
        }

    }

    /**
     * Creates a cookie called "jnoc_navigation" with a JSON mapped string
     * containing the chosen navigation menu options.
     */
    public void writeCookie() {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            Cookie cookie = new Cookie(
                    "jnoc_navigation",
                    mapper.writeValueAsString(selectedMenuItems));
            //cookie.setHttpOnly(true);
            cookie.setComment("Stores personal navigation preferences");
            cookie.setMaxAge(31536000);
            cookie.setPath("/");
            CookieUtil.writeCookie(cookie);
        } catch (JsonProcessingException ex) {
            JsfUtil.addErrorMessage(
                    "We where unable to update your navigation preferences");
        }

        // Create a menu based on the selectedMenuItems
        menu = new DefaultMenuModel();
        if (selectedMenuItems != null) {
            for (MenuItem menuItem : selectedMenuItems) {
                DefaultMenuItem item = new DefaultMenuItem(menuItem.getLabel());
                item.setOutcome(menuItem.getOutcome());
                menu.addElement(item);
            }
        }

        JsfUtil.addSuccessMessage(
                "Your navigation preferences have been updated");
    }

    /**
     * Accepts a string navigation path that redirects the user to that path.
     * This is to extend the JSF navigation strings that are found in the
     * faces-config.xml because they do not offer sufficient flexibility.
     *
     * @param path Should be relative to the root context (ie.
     * 'example.com/a/b/index.jsf' should be supplied as '/a/b/index.jsf'
     */
    public static void redirect(String path) {
        try {
            ExternalContext ectx
                    = FacesContext.getCurrentInstance().getExternalContext();
            String url = ectx.getRequestContextPath() + path;
            ectx.redirect(url);
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "IOException: JSFREDIRECT -> REDIRECT", ioe);
        }
    }

    /**
     * Accepts a navigation case and redirects the user to the requested page.
     * This is to extend the JSF navigation strings that are found in the
     * faces-config.xml because they do not offer sufficient flexibility.
     *
     * @param navigationCase
     */
    public void navigate(String navigationCase) {
        navigate(navigationCase, null);
    }

    /**
     * Accepts a navigation case and a query parameter string that redirects the
     * user to the requested page. This is to extend the JSF navigation strings
     * that are found in the faces-config.xml because they do not offer
     * sufficient flexibility.
     *
     * @param navigationCase
     * @param parameter
     */
    public void navigate(String navigationCase, String parameter) {

        try {
            // Determin the authorised path based on the user metier
            // TODO: This can be removed once the pages are all unified.
            String role = null;
            if (SecurityUtils.getSubject().hasRole(JNOC.Metier.ADMIN.toString())) {
                role = "a";
            }
            if (SecurityUtils.getSubject().hasRole(JNOC.Metier.VAR.toString())) {
                role = "a";
            }
            if (SecurityUtils.getSubject().hasRole(JNOC.Metier.CLIENT.toString())) {
                role = "a";
            }

            /*
             JSF RequestParameterMap() doesn't seem to recognise URL encoded
             %26 => & but the JSF layer won't allow the & character so we need
             to intercept the %26 and convert it to & here.
             */
            if (parameter != null) {
                parameter = parameter.replaceAll("%26", "&");
            }

            ExternalContext ectx = FacesContext
                    .getCurrentInstance()
                    .getExternalContext();
            String url = ectx.getRequestContextPath();

            switch (navigationCase) {
                case "DASHBOARD":
                    url = url.concat("/" + role + "/dashboard.jsf");
                    break;
                case "SETTINGS_PERSONAL_NAVIGATION":
                    url = url.concat("/" + role + "/settings/personal/navigation.jsf");
                    break;
                case "SETTINGS_PERSONAL_PROFILE":
                    url = url.concat("/" + role + "/settings/personal/profile.jsf");
                    break;
                case "CREATE_COMPANY":
                    url = url.concat("/" + role + "/companies/create.jsf");
                    break;
                case "LIST_COMPANIES":
                    url = url.concat("/" + role + "/companies/list.jsf");
                    break;
                case "CREATE_SITE":
                    url = url.concat("/" + role + "/sites/create.jsf");
                    break;
                case "CREATE_TICKET":
                    url = url.concat("/" + role + "/tickets/create.jsf");
                    break;
                case "CREATE_TICKET_WITH_PARAM":
                    url = url.concat("/" + role + "/tickets/create.jsf?" + parameter);
                    break;
                case "LIST_ACCOUNTS":
                    url = url.concat("/" + role + "/accounts/list.jsf");
                    break;
                case "LIST_ACCOUNTS_WITH_PARAM":
                    url = url.concat("/" + role + "/accounts/list.jsf?" + parameter);
                    break;
                case "LIST_COMPANIES_WITH_PARAM":
                    url = url.concat("/" + role + "/companies/list.jsf?" + parameter);
                    break;
                case "LIST_TICKETS_WITH_PARAM":
                    url = url.concat("/" + role + "/tickets/list.jsf?" + parameter);
                    break;
                case "LIST_TICKETS":
                    url = url.concat("/" + role + "/tickets/list.jsf");
                    break;
                case "EDIT_TICKET_WITH_PARAM":
                    url = url.concat("/" + role + "/tickets/edit.jsf?" + parameter);
                    break;
                case "DOCUMENT_WITH_PARAM":
                    url = url.concat("/" + role + "/documents/editor.jsf?" + parameter);
                    break;
                case "RMA_REQUEST":
                    url = url.concat("/" + role + "/rma/create.jsf");
                    break;
                case "LIST_DAS":
                    url = url.concat("/" + role + "/das/list.jsf");
                    break;
                case "FORGOT_PASSWORD":
                    url = url.concat("/p/password.jsf");
                    break;
                case "REQUEST_ACCOUNT":
                    url = url.concat("/p/request.jsf");
                    break;
                case "LOGIN":
                    url = url.concat("/login.jsf");
                    break;
                case "LEGAL":
                    url = url.concat("/p/legal.jsf");
                    break;
            }

            ectx.redirect(url);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Navigator Primefaces data model allowing table and list implementation.
     *
     * @version 3.0.0
     * @since Build 3.0.0 (May 26, 2014)
     * @author Tarka L'Herpiniere
    
     */
    public class MenuItemDataModel extends ListDataModel<MenuItem>
            implements SelectableDataModel<MenuItem> {

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public MenuItemDataModel() {
        }

        public MenuItemDataModel(List<MenuItem> data) {
            super(data);
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Overrides">
        @Override
        public MenuItem getRowData(String rowKey) {
            List<MenuItem> menuItems = (List<MenuItem>) getWrappedData();

            for (MenuItem menuItem : menuItems) {
                if (menuItem.getId().equals(rowKey)) {
                    return menuItem;
                }
            }

            return null;
        }

        @Override
        public Object getRowKey(MenuItem menuItem) {
            return menuItem.getId();
        }
//</editor-fold>

    }

}
