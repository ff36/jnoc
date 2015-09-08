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

package co.ff36.jnoc.service.util;

import co.ff36.jnoc.app.service.internal.DefaultDNSManager;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.entity.User;
import co.ff36.jnoc.per.project.JNOC;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;


/**
 * CDI bean to perform real time ajax view value validations.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Sep 5, 2013)
 * @author Tarka L'Herpiniere

 */
@Named
@ViewScoped
public class AjaxValidUtil implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final long serialVersionUID = 1L;
    private final List<String> emails;
    private List<String> subdomains;
    private final String emailRegex;
    private final String subdomainRegex;
    private final String context;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public AjaxValidUtil() {
        this.emails = new ArrayList<>();
        this.subdomains = new ArrayList<>();
        this.context = System.getenv("JNOC_BASE_URL");
        this.subdomainRegex = "[a-z0-9]{2,20}";
        this.emailRegex = JNOC.EMAIL_REGEX;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;

//</editor-fold>
    
    @PostConstruct
    public void init() {
        List<User> users = dap.findWithNamedQuery("User.findAll");
        for (User user : users) {
            emails.add(user.getEmail());
        }
        List<String> tmpSubDomain = new DefaultDNSManager().listRecordSets(0);
        if(tmpSubDomain!=null) subdomains = tmpSubDomain;
    }

    /**
     * Query of type string can be submitted to determine if an email is both
     * available as a username (or already registered) and in the correct
     * format.
     *
     * @param query
     * @return True if the email is both available and in the correct format.
     * Otherwise false.
     */
    public boolean emailAvailable(String query) {
        try {
            List<String> collection = new ArrayList<>();
            // Make sure its a valid email
            if (query.matches(emailRegex)) {
                // Check query against existing list
                for (String email : emails) {
                    if (email.toLowerCase().startsWith(query.toLowerCase())) {
                        collection.add(email);
                    }
                    // Exit the loop if we have a match
                    if (!collection.isEmpty()) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException npe) {
            // Query is null
            return false;
        }
    }

    /**
     * Query of type string can be submitted to determine if a CNAME is both
     * available (or already registered) and in the correct format.
     *
     * @param query
     * @return True if the CNAME is both available and in the correct format.
     * Otherwise false.
     */
    public boolean subdomainAvailable(String query) {
        try {
            List<String> collection = new ArrayList<>();
            // Make sure its a valid email
            if (query.matches(subdomainRegex)) {
                // Check query against existing list
                for (String subdomain : subdomains) {
                    String q = query + "." + context + ".";
                    if (subdomain.toLowerCase().equals(q.toLowerCase())) {
                        collection.add(subdomain);
                    }
                    // Exit the loop if we have a match
                    if (!collection.isEmpty()) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException npe) {
            // Query is null
            return false;
        }
    }
}
