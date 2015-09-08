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

import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.dap.QueryParameter;
import co.ff36.jnoc.per.entity.Token;
import co.ff36.jnoc.per.entity.User;
import co.ff36.jnoc.service.navigation.Navigator;
import co.ff36.jnoc.service.ticket.EditTicket;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author tarka
 */
@Named
@ViewScoped
public class Approve implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(EditTicket.class.getName());
    private static final long serialVersionUID = 1L;

    private Token token;
    private User user;
    private final String viewParamTokenID;
    private String code;
    private boolean render;
    private String email;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Approve() {
        this.token = new Token();
        this.user = new User();
        this.viewParamTokenID = JsfUtil.getRequestParameter("token");
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CDI">
    @Inject
    private Navigator navigator;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of token.
     *
     * @return the value of token
     */
    public Token getToken() {
        return token;
    }

    /**
     * Get the value of render.
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * Get the value of code.
     *
     * @return the value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the value of user.
     *
     * @return the value of user
     */
    public User getUser() {
        return user;
    }

    /**
     * Get the value of email.
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of token.
     *
     * @param token new value of token
     */
    public void setToken(Token token) {
        this.token = token;
    }

    /**
     * Set the value of code.
     *
     * @param code new value of code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Set the value of user.
     *
     * @param user new value of user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Set the value of email.
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

//</editor-fold>
    /**
     * Initialize the page by loading the specified token from the persistence
     * layer.
     */
    public void init() {

        try {
            // Get the ticket from the persistence layer
            token = (Token) dap.find(Token.class, Long.valueOf(viewParamTokenID));

            // Get the user
            user = (User) dap.find(User.class, Long.valueOf(token.getUser()));

        } catch (NullPointerException | NumberFormatException e) {
            // The ticket was not found in the persistence
            navigator.navigate("LOGIN");
        }
        render = true;
    }

    /**
     * Confirms a new users account
     */
    public void confirmAccount() {
        Map<String, String> parameters = token.getParameters();
        String pin = parameters.get("pin");
        if (code.equals(pin)) {
            user.updatePassword();
            user.getAccount().setConfirmed(true);
            user.getAccount().update();
            token.delete();
            navigator.navigate("LOGIN");
        } else {
            JsfUtil.addWarningMessage("Invalid PIN Code");
        }

    }

    public void setNavigator(Navigator navigator) {
		this.navigator = navigator;
	}

	/**
     * Confirms a new users account
     */
    public void confirmNewEmail() {
        Map<String, String> parameters = token.getParameters();
        String em = parameters.get("email");
        user.setEmail(em);
        user.update();
        token.delete();
    }

    /**
     * Send an email to reset a password
     */
    public void sendLostPasswordEmail() {

        try {
            // Get the user
            user = (User) dap.findWithNamedQuery("User.findByEmail",
                    QueryParameter.with("email", email).parameters()).get(0);

            user.setNewEmail(email);
            user.newAnonymousPassword();
            // Add message
            JsfUtil.addSuccessMessage("An email has been sent with instructions.");

        } catch (Exception e) {
            // Add error message
            JsfUtil.addErrorMessage("No account was found with that email.");
        }
        render = true;

    }

    /**
     * Confirms a new users password
     */
    public void confirmNewPassword() {

        user.updatePassword();
        token.delete();
        navigator.navigate("LOGIN");

    }

}
