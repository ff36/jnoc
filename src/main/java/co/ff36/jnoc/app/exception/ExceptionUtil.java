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

package co.ff36.jnoc.app.exception;

import co.ff36.jnoc.app.email.Email;
import co.ff36.jnoc.app.email.Emailer;
import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.entity.Template;
import co.ff36.jnoc.per.project.JNOC;
import co.ff36.jnoc.per.project.JNOC.EmailVariableKey;
import co.ff36.jnoc.per.project.JNOC.ProjectStage;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;

/**
 * When a runtime exception is encountered in the PRO environment we can
 * accelerate production response by notifying the system developer by email.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (Jul 14, 2013)
 * @author Tarka L'Herpiniere

 * 
 * TODO: This could probably do with an interface and split out the development
 * stages
 */
public class ExceptionUtil {
	private static final Logger LOG = Logger.getLogger(ExceptionUtil.class.getName());
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private final String stage = ResourceBundle.getBundle("config").getString("ProjectStage");
    private final String recipent = ResourceBundle.getBundle("config").getString("WebMasterEmailAddress");
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CDI">
    @Inject
    private Emailer emailer;
//</editor-fold>

    /**
     * Generates an email with the captured exception and sends it to the system
     * developer
     *
     * @param exception
     */
    public void report(Exception exception) {
        
        if (stage.equals(ProjectStage.UAT.toString())) {
            

            // Build an new email
            Email email = new Email();

            // Set the recipient
            email.setRecipientEmail(recipent);

            // Get the current user if one exists
            String user = "Unavailable";
            try {
            	user = SecurityUtils.getSubject()
                        .getPrincipals()
                        .asList()
                        .get(0)
                        .toString();
			} catch (Exception e) {
				// Do nothing as this means we have no security manager
				LOG.log(Level.CONFIG, e.getMessage(), e);
			}

            // Set the email variables
            Map<EmailVariableKey, String> vars = new HashMap<>();
            vars.put(EmailVariableKey.USER_EMAIL, user);
            vars.put(EmailVariableKey.STAGE, stage);
            vars.put(EmailVariableKey.EXCEPTION_NAME,
                    exception.getClass().getName());
            vars.put(EmailVariableKey.EXCEPTION_MESSAGE,
                    exception.getMessage());
            vars.put(EmailVariableKey.EXCEPTION_STACK,
                    ExceptionUtils.getStackTrace(exception));
            email.setVariables(vars);

            // Retreive the email template from the database.
            Template et = 
                    (Template)dap.find(
                            Template.class, JNOC.EmailTemplate.EXCEPTION
                    );
            email.setTemplate(et);

            // Send the email
            emailer.send(email);
        }
    }

}
