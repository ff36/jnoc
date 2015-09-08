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

import co.ff36.jnoc.app.misc.JsfUtil;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.entity.Ticket;
import co.ff36.jnoc.per.entity.Token;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Ticket satisfaction CDI bean. Permits Users to give satisfaction feedback on
 * closed tickets.
 *
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 21, 2013)
 * @author Tarka L'Herpiniere

 */
@Named
@ViewScoped
public class Satisfaction implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(Satisfaction.class.getName());
    private static final long serialVersionUID = 1L;

    private final Map<String, List<String>> requestParameters;
    private String feedback;
    private boolean renderForm;
    private boolean renderThankyou;
    private boolean renderError;
    private Ticket ticket;
    private Token token;
    private Integer rating;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Satisfaction() {
        this.requestParameters = JsfUtil.getRequestParameters();
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters & Setters">
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isRenderForm() {
        return renderForm;
    }

    public void setRenderForm(boolean renderForm) {
        this.renderForm = renderForm;
    }

    public boolean isRenderThankyou() {
        return renderThankyou;
    }

    public void setRenderThankyou(boolean renderThankyou) {
        this.renderThankyou = renderThankyou;
    }

    public boolean isRenderError() {
        return renderError;
    }

    public void setRenderError(boolean renderError) {
        this.renderError = renderError;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Ticket getTicket() {
        return ticket;
    }

//</editor-fold>
    
    /**
     * Initialize the page by loading the specified token from the persistence
     * layer.
     */
    public void init() {
        try {
            // Get the token
            token = (Token) dap.find(Token.class,
                    Long.valueOf(requestParameters.get("token").get(0)));

            // Get the ticket
            this.ticket = (Ticket) dap.find(Ticket.class,
                    Long.valueOf(token.getParameters().get("ticket")));

            // If a rating is supplied use it and close the feedback
            if (requestParameters.get("rating") != null) {
                this.rating = Integer.valueOf(requestParameters.get("rating").get(0));
                submit();
            } else {
                this.rating = 5;
                renderForm = true;
            }
        } catch (NullPointerException npe) {
            // The token was null
            renderError = true;
        }
    }

    /**
     * Submit the feedback.
     *
     */
    public void submit() {
        if (this.rating > 0 && this.rating <= 5) {
            this.ticket.setSatisfied(this.rating);
            if (this.feedback != null && !this.feedback.isEmpty()) {
                this.ticket.setFeedback(feedback);
            }
            this.ticket.update();
            this.token.delete();
            renderThankyou = true;
            renderForm = false;
        } else {
            try {
                JsfUtil.addSuccessMessage("Please add a feedback rating between 1 and 5.");
                renderForm = true;
            } catch (Exception e) {
                // Request does not come from JSF
            }
        }
    }

}
