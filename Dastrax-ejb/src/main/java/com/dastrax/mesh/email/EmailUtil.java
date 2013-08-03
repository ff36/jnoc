/* 
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>. 
 */
package com.dastrax.mesh.email;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.dastrax.app.util.ExceptionUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * This is the email constructor class. It constructs e-mails with the velocity
 * template engine and send the e-mails using AWS SES. The construction order
 * must be as follows: 1. Build links using EmailLink 2. Build Variables
 * ListArray 3. Retrieve the email Template data from the database 4. Combine
 * the template with the variables using velocity 5. Pass the complete dataset
 * to AWS to send
 *
 * @version Build 2.0.0
 * @since Jul 10, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
public class EmailUtil {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(EmailUtil.class.getName());
    // Variables----------------------------------------------------------------
    private final String protocol = ResourceBundle.getBundle("Config").getString("AccessProtocol");
    private final String baseUrl = ResourceBundle.getBundle("Config").getString("BaseUrl");
    private final String emailSender = ResourceBundle.getBundle("Config").getString("SenderEmailAddress");

    // EJB----------------------------------------------------------------------
    @EJB
    ExceptionUtil exu;

    // Methods------------------------------------------------------------------
    /**
     *
     * @param email
     */
    @Asynchronous
    public void build(Email email) {

        /*
         * Set the variables timestamps
         */
        if (!email.getVariables().containsKey("time")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String time = simpleDateFormat.format(Calendar.getInstance().getTime());
            email.getVariables().put("time", time);
        }
        if (!email.getVariables().containsKey("date")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(Calendar.getInstance().getTime());
            email.getVariables().put("date", date);
        }

        /*
         * Generate the custom link required in the email
         */
        if (email.getTemplate().getLinkPath() != null) {
            String link
                    = protocol
                    + baseUrl
                    + email.getTemplate().getLinkPath()
                    + "?token="
                    + email.getParam().getToken();
            email.getVariables().put("link", link);
        }

        /*
         * Generate a list<Array> of variables that can be used by the Velocity
         * Template engine
         */
        ArrayList<Map> variablesList = new ArrayList<>();
        variablesList.add(email.getVariables());

        /*
         * Merge the variables with the templates from the database using Velocity
         */
        Velocity velocity = new Velocity();
        if (email.getTemplate().getHtml() != null) {
            String s = velocity.combine(
                    variablesList, new String(email.getTemplate().getHtml()));
            email.getTemplate().setHtml(s.toCharArray());
        }
        if (email.getTemplate().getPlainText() != null) {
            String s = velocity.combine(
                    variablesList, new String(email.getTemplate().getPlainText()));
            email.getTemplate().setPlainText(s.toCharArray());
        }

        /*
         * Use AWS SES to send the email
         */
        Sender sender = new Sender();
        sender.sendEmail(
                emailSender,
                email.getRecipientEmail(),
                email.getTemplate().getSubject(),
                new String(email.getTemplate().getHtml()),
                new String(email.getTemplate().getPlainText()));

    }

    /**
     * Apache Velocity is used to create templates and combine the variables
     * with the template before rendering out a string to be sent as the final
     * email.
     *
     * @version Build 2.0.0
     * @since Jul 10, 2013
     * @author Tarka L'Herpiniere <info@tarka.tv>
     */
    public class Velocity {

        // Constructors-------------------------------------------------------------
        public Velocity() {
        }

        // Methods------------------------------------------------------------------
        /**
         * Apache Velocity is used to create templates and combine the variables
         * with the template before rendering out a string to be sent as the
         * final email. Email templates are obtained from the database and are
         * passed as strings rather than using the traditional template.vm file
         * reference.
         *
         * @param variablesList
         * @param emailTemplate
         * @return String
         */
        protected String combine(ArrayList variablesList, String emailTemplate) {

            /*
             * Initialise Velocity Context
             */
            VelocityContext context = new VelocityContext();

            /*
             * Variables are added through Map keypairs that are added to the ArrayList.    
             */
            context.put("variablesList", variablesList);

            StringWriter writer = new StringWriter();

            try {
                /*
                 * Evaluates template String
                 */
                org.apache.velocity.app.Velocity.evaluate(
                        context, writer, "Velocity log Tag", emailTemplate);
            } catch (ResourceNotFoundException rnfe) {
                LOG.log(Level.SEVERE, "Velocity couldn't find the template", rnfe);
            } catch (ParseErrorException pee) {
                LOG.log(Level.SEVERE, "Velocity Syntax error: problem parsing the template", pee);
            } catch (MethodInvocationException mie) {
                LOG.log(Level.SEVERE, "Velocity found something invoked in the template threw an exception", mie);
            }

            return writer.toString();
        }
    }

    /**
     * Sends the email via AWS SES
     *
     * @version Build 2.0.0
     * @since Jul 10, 2013
     * @author Tarka L'Herpiniere <info@tarka.tv>
     */
    public class Sender {

        // Constructor--------------------------------------------------------------
        public Sender() {
        }

        // Methods------------------------------------------------------------------
        /**
         * SendEmail uses Amazon SES to send the email. Credentials are obtained
         * as an application wide set of credentials as an input stream from the
         * AwsCredentials.properties file
         *
         * @param emailSender
         * @param emailRecipient
         * @param emailSubject
         * @param emailHtmlBody
         * @param emailTextBody
         */
        public void sendEmail(
                String emailSender,
                String emailRecipient,
                String emailSubject,
                String emailHtmlBody,
                String emailTextBody) throws AmazonClientException {

            SendEmailRequest request = new SendEmailRequest().withSource(emailSender);

            List<String> toAddresses = new ArrayList<>();
            toAddresses.add(emailRecipient);
            Destination dest = new Destination().withToAddresses(toAddresses);
            request.setDestination(dest);

            Content subjContent = new Content().withData(emailSubject);
            Message msg = new Message().withSubject(subjContent);

            /*
             * Include a body in both text and HTML formats
             */
            Content textContent = new Content().withData(emailTextBody);
            Content htmlContent = new Content().withData(emailHtmlBody);
            Body body = new Body().withHtml(htmlContent).withText(textContent);
            msg.setBody(body);

            request.setMessage(msg);

            /*
             * Call Amazon SES to send the message 
             */
            try {
                new SES().getClient().sendEmail(request);
                // Create an audit log of the event
                //AuditDAO audit = (AuditDAO) InitialContext.doLookup(JNDI_AUD);
                //audit.create("Email sent to " + emailRecipient);
            } catch (AmazonClientException ace) {
                exu.report(ace);
                LOG.log(Level.SEVERE, ace.getMessage(), ace);
            }
        }
    }

    /**
     * Creates an SES client to authenticate communication with AWS SES to send
     * email.
     *
     * @version Build 2.0.0
     * @since Jul 10, 2013
     * @author Tarka L'Herpiniere <info@tarka.tv>
     */
    public class SES {

        // Variables----------------------------------------------------------------
        private AmazonSimpleEmailServiceClient client = null;

        // Constructor--------------------------------------------------------------
        public SES() {
            try {
                try (InputStream credentialsAsStream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("AwsApi.properties")) {
                    
                    AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
                    client = new AmazonSimpleEmailServiceClient(credentials);
                }

            } catch (IOException t) {
                System.err.println("Error creating AmazonDynamoDBClient: " + t);
            }
        }

        // Getters------------------------------------------------------------------
        public AmazonSimpleEmailServiceClient getClient() {
            return client;
        }
    }
}
