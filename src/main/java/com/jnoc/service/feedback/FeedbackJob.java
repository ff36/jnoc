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

package com.jnoc.service.feedback;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jnoc.app.email.DefaultEmailer;
import com.jnoc.app.email.Email;
import com.jnoc.per.dap.CrudService;
import com.jnoc.per.dap.QueryParameter;
import com.jnoc.per.entity.Template;
import com.jnoc.per.entity.Ticket;
import com.jnoc.per.entity.Token;
import com.jnoc.per.project.JNOC;

/**
 * Sends out feedback email to tickets closed in the past hour.
 *
 * @version 3.1.0
 * @since Build 141120.161719
 * @author Tarka L'Herpiniere

 */
public class FeedbackJob implements Job {
	private static final Logger LOG = Logger.getLogger(FeedbackJob.class.getName());
    /**
     * @param context
     * @throws org.quartz.JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        try {
            CrudService dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));

            // Date object with epoch now and 1 hour ago.
            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.MINUTE) >= 30) {
                now.add(Calendar.HOUR, 1);
            }
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);

            Calendar oneHourAgo = Calendar.getInstance();
            oneHourAgo.setTime(now.getTime());
            oneHourAgo.add(Calendar.HOUR, -1);
            
            long nowT = now.getTimeInMillis();
            long oneHourAgoT = oneHourAgo.getTimeInMillis();

            // Get tickets closed in the past hour
            List<Ticket> tickets = dap.findWithNamedQuery(
                    "Ticket.findAllByCloseRange",
                    QueryParameter
                    .with("start", oneHourAgoT)
                    .and("end", nowT)
                    .parameters());

            // Retreive the email template from the database.
            Template template = (Template) dap.find(
                    Template.class, JNOC.EmailTemplate.TICKET_FEEDBACK.getValue());

            for (Ticket ticket : tickets) {
                sendEmail(ticket, template);
            }

        } catch (NamingException ex) {
            LOG.log(Level.CONFIG, null, ex);
        }

    }

    /**
     * Sends asynchronous email to the specified recipient
     *
     * @param recipient
     */
    private void sendEmail(Ticket ticket, Template template) {
        // Build an new email
        Email e = new Email();

        // Set the recipient
        e.setRecipientEmail(ticket.getRequester().getEmail());

        // Override the template subject
        template.setSubject("Review: " + ticket.getTitle());

        // Create a Token
        Token token = new Token();
        token.setId(new Random().nextLong());
        token.setEmail(ticket.getRequester().getEmail());
        token.setCreateEpoch(Calendar.getInstance().getTimeInMillis());
        Map<String, String> params = new HashMap<>(1);
        params.put("ticket", ticket.getId().toString());
        token.setParameters(params);
        token.create();
        e.setParam(token);
        
        String baseUrl = System.getenv("JNOC_BASE_URL");
        String protocol = System.getenv("JNOC_ACCESS_PROTOCOL");

        // Set the variables
        Map<JNOC.EmailVariableKey, String> vars = new HashMap<>();
        vars.put(JNOC.EmailVariableKey.TICKET_TITLE, ticket.getTitle());
        vars.put(JNOC.EmailVariableKey.TICKET_ASSIGNEE, ticket.getAssignee().getContact().buildFullName());
        vars.put(JNOC.EmailVariableKey.TICKET_5_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=5");
        vars.put(JNOC.EmailVariableKey.TICKET_4_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=4");
        vars.put(JNOC.EmailVariableKey.TICKET_3_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=3");
        vars.put(JNOC.EmailVariableKey.TICKET_2_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=2");
        vars.put(JNOC.EmailVariableKey.TICKET_1_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=1");
        vars.put(JNOC.EmailVariableKey.TICKET_0_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId());
        e.setVariables(vars);

        // Set the template
        e.setTemplate(template);

        // Send the email
        new DefaultEmailer().send(e);
    }

}
