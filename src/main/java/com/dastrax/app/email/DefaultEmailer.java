/*
 * Created 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.email;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.dastrax.per.project.DTX;

/**
 * This is the public utility that is used to convert an <Email.class> into an
 * actual email and send it using AWS SES.
 *
 * @version 2.0.0
 * @since Build 2.0.0 (2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class DefaultEmailer implements Emailer {

	// <editor-fold defaultstate="collapsed" desc="Properties">
	private static final Logger LOG = Logger.getLogger(DefaultEmailer.class
			.getName());

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Constructors">
	public DefaultEmailer() {
	}

	// </editor-fold>

	/**
	 * This is the public method used to construct and send an email. The method
	 * requires an <Email.class> to be passed as the only argument. The method
	 * is @Asynchronous and completes immediately regardless of the outcome.
	 *
	 * @param email
	 */

	@Override
	@Asynchronous
	public void send(Email email) {

		/*
		 * Set the variables timestamps
		 */
		if (!email.getVariables().containsKey(DTX.EmailVariableKey.TIME)) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					DTX.TemporalFormat.TIME_FORMAT.getValue());
			String time = simpleDateFormat.format(Calendar.getInstance()
					.getTime());
			email.getVariables().put(DTX.EmailVariableKey.TIME, time);
		}
		if (!email.getVariables().containsKey(DTX.EmailVariableKey.DATE)) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					DTX.TemporalFormat.DATE_FORMAT.getValue());
			String date = simpleDateFormat.format(Calendar.getInstance()
					.getTime());
			email.getVariables().put(DTX.EmailVariableKey.DATE, date);
		}

		/*
		 * Generate the custom link required in the email
		 */
		if (email.getTemplate().getLinkPath() != null) {
			String link = protocol + baseUrl
					+ email.getTemplate().getLinkPath() + "?token="
					+ email.getParameters().getId();
			email.getVariables().put(DTX.EmailVariableKey.LINK, link);
		}

		/*
		 * Convert Enum to string value so Velocity can replace values
		 */
		Map<String, String> convertedVars = new HashMap<>();
		for (Entry<DTX.EmailVariableKey, String> entry : email.getVariables()
				.entrySet()) {
			convertedVars.put(entry.getKey().toString(), entry.getValue());
		}

		/*
		 * Generate a list<Array> of variables that can be used by the Velocity
		 * Template engine
		 */
		ArrayList<Map> variablesList = new ArrayList<>();
		variablesList.add(convertedVars);

		/*
		 * Merge the variables with the templates from the database using
		 * Velocity
		 */
		Velocity velocity = new Velocity();
		if (email.getTemplate().getHtml() != null) {
			String s = velocity.combine(variablesList, new String(email
					.getTemplate().getHtml()));
			email.getTemplate().setHtml(s.toCharArray());
		}
		if (email.getTemplate().getPlainText() != null) {
			String s = velocity.combine(variablesList, new String(email
					.getTemplate().getPlainText()));
			email.getTemplate().setPlainText(s.toCharArray());
		}

		/*
		 * Use AWS SES to send the email
		 */
		Sender sender = new Sender();
		sender.sendEmail(emailSender, email.getRecipientEmail(), email
				.getTemplate().getSubject(), new String(email.getTemplate()
				.getHtml()), new String(email.getTemplate().getPlainText()));

	}

	/**
	 * Apache Velocity is used to combine the variables with the template before
	 * rendering out a string to be sent as the final email.
	 *
	 * @version 2.0.0
	 * @since Build 2.0.0 (2013)
	 * @author Tarka L'Herpiniere
	 * @author <tarka@solid.com>
	 */
	private class Velocity {

		// <editor-fold defaultstate="collapsed" desc="Constructors">
		public Velocity() {
		}

		// </editor-fold>

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
			 * Variables are added through Map keypairs that are added to the
			 * ArrayList.
			 */
			context.put("variablesList", variablesList);

			StringWriter writer = new StringWriter();

			try {
				/*
				 * Evaluates template String
				 */
				org.apache.velocity.app.Velocity.evaluate(context, writer,
						"Velocity log Tag", emailTemplate);
			} catch (ResourceNotFoundException rnfe) {
				LOG.log(Level.SEVERE, "Velocity couldn't find the template",
						rnfe);
			} catch (ParseErrorException pee) {
				LOG.log(Level.SEVERE,
						"Velocity Syntax error: problem parsing the template",
						pee);
			} catch (MethodInvocationException mie) {
				LOG.log(Level.SEVERE,
						"Velocity found something invoked in the template threw an exception",
						mie);
			}

			return writer.toString();
		}
	}

	/**
	 * Generates a AWS SES request to send the email
	 *
	 * @version 2.0.0
	 * @since Build 2.0.0 (2013)
	 * @author Tarka L'Herpiniere
	 * @author <tarka@solid.com>
	 */
	private class Sender {

		// <editor-fold defaultstate="collapsed" desc="Constructors">
		public Sender() {
		}

		// </editor-fold>

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
		 * @throws AmazonClientException
		 */
		protected void sendEmail(String emailSender, String emailRecipient,
				String emailSubject, String emailHtmlBody, String emailTextBody)
				throws AmazonClientException {

			SendEmailRequest request = new SendEmailRequest()
					.withSource(emailSender);

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
			} catch (AmazonClientException ace) {
				LOG.log(Level.SEVERE, ace.getMessage(), ace);
			}
		}
	}

	/**
	 * In order to send an email using AWS SES we need to create and use a
	 * AmazonSimpleEmailServiceClient. This class is responsible for creating
	 * that object. It only has a single getter that returns the
	 * AmazonSimpleEmailServiceClient.
	 *
	 * @version 2.0.0
	 * @since Build 2.0.0 (2013)
	 * @author Tarka L'Herpiniere
	 * @author <tarka@solid.com>
	 */
	private class SES {

		// <editor-fold defaultstate="collapsed" desc="Properties">
		private AmazonSimpleEmailServiceClient client;

		// </editor-fold>

		// <editor-fold defaultstate="collapsed" desc="Constructors">
		public SES() {
			AWSCredentials credentials = new EnvironmentVariableCredentialsProvider()
					.getCredentials();
			this.client = new AmazonSimpleEmailServiceClient(credentials);
		}

		// </editor-fold>

		// <editor-fold defaultstate="collapsed" desc="Getters">
		/**
		 * Get the value of client
		 *
		 * @return the value of client
		 */
		protected AmazonSimpleEmailServiceClient getClient() {
			return client;
		}
		// </editor-fold>
	}
}
