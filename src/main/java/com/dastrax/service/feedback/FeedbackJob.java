/*
 * Created Nov 20, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.feedback;

import com.dastrax.app.email.DefaultEmailer;
import com.dastrax.app.email.Email;
import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Template;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.entity.Token;
import com.dastrax.per.project.DTX;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Sends out feedback email to tickets closed in the past hour.
 *
 * @version 3.1.0
 * @since Build 141120.161719
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class FeedbackJob implements Job {

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
                    Template.class, DTX.EmailTemplate.TICKET_FEEDBACK.getValue());

            for (Ticket ticket : tickets) {
                sendEmail(ticket, template);
            }

        } catch (NamingException ex) {
//            LOG.log(Level.SEVERE, null, ex);
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
        Map<String, String> params = new HashMap(1);
        params.put("ticket", ticket.getId().toString());
        token.setParameters(params);
        token.create();
        e.setParam(token);
        
        String baseUrl = ResourceBundle.getBundle("config").getString("BaseUrl");
        String protocol = ResourceBundle.getBundle("config").getString("AccessProtocol");

        // Set the variables
        Map<DTX.EmailVariableKey, String> vars = new HashMap<>();
        vars.put(DTX.EmailVariableKey.TICKET_TITLE, ticket.getTitle());
        vars.put(DTX.EmailVariableKey.TICKET_ASSIGNEE, ticket.getAssignee().getContact().buildFullName());
        vars.put(DTX.EmailVariableKey.TICKET_5_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=5");
        vars.put(DTX.EmailVariableKey.TICKET_4_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=4");
        vars.put(DTX.EmailVariableKey.TICKET_3_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=3");
        vars.put(DTX.EmailVariableKey.TICKET_2_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=2");
        vars.put(DTX.EmailVariableKey.TICKET_1_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId() + "&rating=1");
        vars.put(DTX.EmailVariableKey.TICKET_0_STAR, protocol + baseUrl + "/p/feedback.jsf?token=" + token.getId());
        e.setVariables(vars);

        // Set the template
        e.setTemplate(template);

        // Send the email
        new DefaultEmailer().send(e);
    }

}
