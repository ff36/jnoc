/*
 * Created Oct 29, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.gmail;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Comment;
import com.dastrax.per.entity.Metier;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.entity.User;
import com.dastrax.per.project.DTX;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

/**
 * Converts an email into a ticket.
 *
 * @version 3.1.0
 * @since Build 141029.115419
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class EmailToTicket {

    private static final Logger LOG = Logger.getLogger(EmailToTicket.class.getName());

    private CrudService dap;

    public EmailToTicket() {
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
//            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public void processEmail(Message msg) throws MessagingException, IOException {
        // Is this email part of an existing ticket 
        isPartofExistingTicket(msg);

        // If yes add it to the end of the ticket
        // If no does the sender have a dastrax account
        // If yes create a new ticket for that user
        // If no create a new user account and create a ticket for them
    }

    private void isPartofExistingTicket(Message msg) throws MessagingException, IOException {
        String subject = msg.getSubject();
        if (subject.matches("(.*)((DTX-)(.{10})())(.*)")) {
            // Extract the email id
            String substringBetween = StringUtils.substringBetween(msg.getSubject(), "(", ")");
            String emailId = substringBetween.substring(4);

            // get the ticket
            List<Ticket> tickets = dap.findWithNamedQuery(
                    "Ticket.findAllByEmail",
                    QueryParameter
                    .with("email", emailId)
                    .parameters());

            // Make sure the user exists
            User user = aquireAccount(msg);

            if (!tickets.isEmpty()) {
                // The ticket exists
                Comment comment = new Comment();
                comment.setCommenter(user);
                comment.setCreateEpoch(new Date().getTime());
                comment.setComment(getMessage(msg));

                Ticket ticket = tickets.get(0);
                ticket.setComment(comment);
                ticket.edit(DTX.TicketStatus.OPEN, user);

            } else {
                // Create a new ticket
                System.out.println("Creating New Ticket");
            }

        } else {
            // New ticket
            System.out.println("New");
        }
    }

    /**
     *
     * @param msg
     * @return
     * @throws MessagingException
     */
    private User aquireAccount(Message msg) throws MessagingException {
        // The user to return
        User user = null;

        // Get the email
        String from = "unknown";
        if (msg.getReplyTo().length >= 1) {
            from = msg.getReplyTo()[0].toString();
        } else if (msg.getFrom().length >= 1) {
            from = msg.getFrom()[0].toString();
        }
        String email = StringUtils.substringBetween(from, "<", ">");
        String[] names = from.split(" ");
        String firstName, lastName;
        if (names.length >= 1) {
            firstName = names[0];
        } else {
            firstName = "Unknown";
            lastName = "Unknown";
        }
        
        if (names.length >= 2) {
            lastName = names[1];
        } else {
            lastName = "Unknown";
        }

        // Make sure its a real email
        if (email.matches(DTX.EMAIL_REGEX)) {
            // Get the user by email
            List<User> users = dap.findWithNamedQuery(
                    "User.findByEmail",
                    QueryParameter
                    .with("email", email)
                    .parameters());

            // Does the user exists
            if (!users.isEmpty()) {
                // return the user
                user = users.get(0);
            } else {
                // Create a new user
                user = createUndefinedUser(email, firstName, lastName);
            }

        }
        return user;
    }

    /**
     *
     * @param email
     * @return
     */
    private User createUndefinedUser(String email, String firstName, String lastName) {
        // Get the metiers
        List<Metier> metiers = dap.findWithNamedQuery(
                "Metier.findByName",
                QueryParameter
                .with("name", DTX.Metier.UNDEFINED.toString())
                .parameters());

        // Create the user
        User user = new User();
        user.setNewEmail(email);
        user.getContact().setFirstName(firstName);
        user.getContact().setLastName(lastName);
        user.setMetier(metiers.get(0));
        user.setCompany(null);
        user.getAccount().setLocked(true);
        user.create();

        return user;
    }

    /**
     *
     * @param message
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    private String getMessage(Message message) throws
            MessagingException, IOException {

        String result = null;

        if (message instanceof MimeMessage) {
            MimeMessage m = (MimeMessage) message;
            Object contentObject = m.getContent();
            if (contentObject instanceof Multipart) {
                BodyPart clearTextPart = null;
                BodyPart htmlTextPart = null;
                Multipart content = (Multipart) contentObject;
                int count = content.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart part = content.getBodyPart(i);
                    if (part.isMimeType("text/plain")) {
                        clearTextPart = part;
                        break;
                    } else if (part.isMimeType("text/html")) {
                        htmlTextPart = part;
                    }
                }

                if (clearTextPart != null) {
                    result = (String) clearTextPart.getContent();
                } else if (htmlTextPart != null) {
                    String html = (String) htmlTextPart.getContent();
                    result = Jsoup.parse(html).text();
                }

            } else if (contentObject instanceof String) // a simple text message
            {
                result = (String) contentObject;
            } else {
                result = null;
            }
        }
        return result;
    }
}
