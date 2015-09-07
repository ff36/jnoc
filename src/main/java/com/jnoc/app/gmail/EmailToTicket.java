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


/*
 * Created Oct 29, 2014.
 * Copyright 2014 555 Inc ALL RIGHTS RESERVED. 

 */
package com.jnoc.app.gmail;

import com.jnoc.per.dap.CrudService;
import com.jnoc.per.dap.QueryParameter;
import com.jnoc.per.entity.Attachment;
import com.jnoc.per.entity.Comment;
import com.jnoc.per.entity.Metier;
import com.jnoc.per.entity.Ticket;
import com.jnoc.per.entity.User;
import com.jnoc.per.entity.User_;
import com.jnoc.per.project.JNOC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

/**
 * Converts an email into a ticket.
 *
 * @version 3.1.0
 * @since Build 141029.115419
 * @author Tarka L'Herpiniere

 */
public class EmailToTicket {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final boolean showStructure = true;
    private static final boolean saveAttachments = true;
    private static String clearTextPart = null;
    private static String htmlTextPart = null;
    private static List<Attachment> attachments;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(EmailToTicket.class.getName());
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public EmailToTicket() {
        attachments = new ArrayList<>();
        try {
            dap = (CrudService) InitialContext.doLookup(
                    ResourceBundle.getBundle("config").getString("CRUD"));
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
//</editor-fold>

    /**
     *
     * @param msg
     * @throws MessagingException
     * @throws IOException
     */
    public void processEmail(Message msg) throws MessagingException, IOException {
        // Get the email subject
        String subject = msg.getSubject();
        String title = subject;
        
        // Aquire the user account or create it if not
        User user = aquireAccount(msg);

        if (subject.matches("(.*)(\\({1})(JNOC-)([0-9]*)(\\){1})(.*)")) {
            // Extract the email id
            String fullEmailId = StringUtils.substringBetween(msg.getSubject(), "(", ")");
            String emailId = fullEmailId.substring(4);

            // Set the title
            title = title.replace("fullEmailId", "");

            // get the ticket
            @SuppressWarnings("unchecked")
			List<Ticket> tickets = dap.findWithNamedQuery("Ticket.findAllByEmail",
                    QueryParameter.with("email", emailId)
                    .parameters());

            if (!tickets.isEmpty()) {
                // The ticket exists
                Comment comment = new Comment();
                comment.setCommenter(user);
                comment.setCreateEpoch(new Date().getTime());
                comment.setComment(getMessage(msg));

                Ticket ticket = tickets.get(0);
                ticket.setGmailJobTicketed(true);
                ticket.setComment(comment);
                ticket.setSendEmailToRequester(true);
                if (ticket.getAssignee() != null) {
                    ticket.setSendEmailToAssignee(true);
                }
                ticket.edit(JNOC.TicketStatus.OPEN, user);

                // Take care of the attachements
                for (Attachment a : attachments) {
                    ticket.setAttachment(a);
                    ticket.addAttachment();
                }

            } else {
                // Create a new ticket
                Comment comment = new Comment();
                comment.setCommenter(user);
                comment.setCreateEpoch(new Date().getTime());
                comment.setComment(getMessage(msg));

                Ticket ticket = new Ticket();
                ticket.setGmailJobTicketed(true);
                ticket.setStatus(JNOC.TicketStatus.OPEN);
                ticket.setTitle(title);
                ticket.setMailTitle(msg.getSubject());
                ticket.setSeverity(JNOC.TicketSeverity.S3);
                ticket.setTopic(JNOC.TicketTopic.GENERAL);
                ticket.setSendEmailToRequester(true);
                ticket.setComment(comment);
                ticket.createByUser(user, JNOC.TicketStatus.OPEN);

                // Take care of the attachements
                for (Attachment a : attachments) {
                    ticket.setAttachment(a);
                    ticket.addAttachment();
                }

            }

        } else {
        	
        	@SuppressWarnings("unchecked")
			List<Ticket> tickets = dap.findWithNamedQuery("Ticket.findAllByTitle",
                    QueryParameter.with("mailTitle", msg.getSubject())
                    .parameters());
        	if(!tickets.isEmpty()){
        		// The ticket exists
                Comment comment = new Comment();
                comment.setCommenter(user);
                comment.setCreateEpoch(new Date().getTime());
                comment.setComment(getMessage(msg));

                Ticket ticket = tickets.get(0);
                ticket.setGmailJobTicketed(true);
                ticket.setComment(comment);
                ticket.setSendEmailToRequester(true);
                if (ticket.getAssignee() != null) {
                    ticket.setSendEmailToAssignee(true);
                }
                ticket.edit(JNOC.TicketStatus.OPEN, user);

                // Take care of the attachements
                for (Attachment a : attachments) {
                    ticket.setAttachment(a);
                    ticket.addAttachment();
                }
        	}else{
        		// Create a new ticket
                Comment comment = new Comment();
                comment.setCommenter(user);
                comment.setCreateEpoch(new Date().getTime());
                comment.setComment(getMessage(msg));

                Ticket ticket = new Ticket();
                ticket.setGmailJobTicketed(true);
                ticket.setStatus(JNOC.TicketStatus.OPEN);
                ticket.setTitle(title);
                ticket.setMailTitle(msg.getSubject());
                ticket.setSeverity(JNOC.TicketSeverity.S3);
                ticket.setTopic(JNOC.TicketTopic.GENERAL);
                ticket.setSendEmailToRequester(true);
                ticket.setComment(comment);
                ticket.createByUser(user, JNOC.TicketStatus.OPEN);

                // Take care of the attachements
                for (Attachment a : attachments) {
                    ticket.setAttachment(a);
                    ticket.addAttachment();
                }
        	}

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
        String from = "";
        if (msg.getReplyTo().length >= 1) {
            from = msg.getReplyTo()[0].toString();
        }
        
        String email = null;
        if(from.indexOf(">") !=-1 && from.indexOf("<")!=-1)
        	email= StringUtils.substringBetween(from, "<", ">").toLowerCase();
        else 
        	email = from.toLowerCase();
        
        //String email = StringUtils.substringBetween(from, "<", ">").toLowerCase();

        // Check the email
        if (!email.matches(JNOC.EMAIL_REGEX)) {
            from = msg.getFrom()[0].toString();
        }

        // Try to set the users name
        String[] names = from.split(" ");
        String firstName, lastName;
        if (names.length >= 1) {
            firstName = names[0];
        } else {
            firstName = "Unknown";
        }

        if (names.length >= 2) {
            lastName = names[1];
        } else {
            lastName = "Unknown";
        }

        // Make sure its a real email
        if (email.matches(JNOC.EMAIL_REGEX)) {
            // Get the user by email
            @SuppressWarnings("unchecked")
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
                user = createUser(email, firstName, lastName);
            }

        }
        return user;
    }

    /**
     *
     * @param email
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private User createUser(String email, String firstName, String lastName) {

        // New user
        User user = new User();

        // Use the email to see if they belong to a company
        CriteriaBuilder builder = dap.getCriteriaBuilder();
		CriteriaQuery query = builder.createQuery(User.class);
        Root root = query.from(User.class);
        String[] split = email.split("@");

        // Use the second half of the email to match
        Expression literal = builder.literal("%" + split[1]);
        Predicate predicate = builder.like(root.get(User_.email), literal);
        query.where(predicate);
        List<User> users = dap.findWithCriteriaQuery(query);

        if (users.isEmpty()) {
            // No match so create undefined user
            // Get the metiers
            List<Metier> metiers = dap.findWithNamedQuery(
                    "Metier.findByName",
                    QueryParameter
                    .with("name", JNOC.Metier.UNDEFINED.toString())
                    .parameters());

            // Create the user
            user.setNewEmail(email);
            user.getContact().setFirstName(firstName);
            user.getContact().setLastName(lastName);
            user.setMetier(metiers.get(0));
            user.setCompany(null);
            user.getAccount().setLocked(true);
            user.create();
        } else {
            // A match was found so associate to the existing company
            User existing = users.get(0);

            // Create the user
            user.setNewEmail(email);
            user.getContact().setFirstName(firstName);
            user.getContact().setLastName(lastName);
            user.setMetier(existing.getMetier());
            user.setCompany(existing.getCompany());
            user.getAccount().setLocked(false);
            user.create();
        }

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

                Multipart content = (Multipart) contentObject;
                int count = content.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart part = content.getBodyPart(i);

                    try {
                        dumpPart(part);
                    } catch (Exception ex) {
                        Logger.getLogger(EmailToTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (contentObject instanceof String) {
                result = (String) contentObject;
            } else {
                result = null;
            }
        }

        if (clearTextPart != null) {
            result = clearTextPart;
        } else if (htmlTextPart != null) {
            String html = htmlTextPart;
            result = Jsoup.parse(html).text();
        }

        /*
         If the email is part of a discussion it will feature our email in the
         original message header so we can cut the email here so as not to include
         previouse parts of the email.
         */
//        String emailAddress = System.getenv("JNOC_SENDER_EMAIL");
//        if (result != null && result.contains(emailAddress)) {
//            result = StringUtils.substringBefore(
//                    result,
//                    emailAddress);
//            result = result.substring(0, result.lastIndexOf("\n"));
//        }

        /* 
         If people have a signature it will have their name in it usually. It is
         also unlikely that their name will feature in the last 10% section of 
         the email unless its part of their signature. So we can look for that and 
         cut off the email at that point.
         */
        // Get the name
        String from = "unknown";
        if (message.getReplyTo().length >= 1) {
            from = message.getReplyTo()[0].toString();
        } else if (message.getFrom().length >= 1) {
            from = message.getFrom()[0].toString();
        }

//        String name = StringUtils.substringBefore(from, "<").trim();
//        // Just for my name because of the appostrohe
//        if (name.contains("Tarka")) {
//            name = "Tarka";
//        }
//        if (result != null && result.contains(name)) {
//            result = StringUtils.substringBefore(result, name);
//            result = result.substring(0, result.lastIndexOf("\n"));
//        }

        // Remove any embedded image strings.
        if (result != null) {
            result = result.replaceAll("\\[image:.*", "");
        }

        return result;
    }

    /**
     * Iterate over the email parts and extract the required sections.
     *
     * @param p
     * @throws Exception
     */
    public void dumpPart(Part p) throws Exception {

        /**
         * Dump input stream .. * InputStream is = p.getInputStream(); // If
         * "is" is not already buffered, wrap a BufferedInputStream // around
         * it. if (!(is instanceof BufferedInputStream)) is = new
         * BufferedInputStream(is); int c; while ((c = is.read()) != -1)
         * System.out.write(c);
         *
         *
         */
//        String ct = p.getContentType();
//        try {
//            pr("CONTENT-TYPE: " + (new ContentType(ct)).toString());
//        } catch (ParseException pex) {
//            pr("BAD CONTENT-TYPE: " + ct);
//        }
        String filename = p.getFileName();
//        if (filename != null) {
//            pr("FILENAME: " + filename);
//        }

        /*
         * Using isMimeType to determine the content type avoids
         * fetching the actual content data until we need it.
         */
        if (p.isMimeType("text/plain")) {
            clearTextPart = (String) p.getContent();
//            System.out.println(clearTextPart);
        } else if (p.isMimeType("text/html")) {
            htmlTextPart = (String) p.getContent();
//            System.out.println(clearTextPart);
        } else if (p.isMimeType("multipart/*")) {
//            pr("This is a Multipart");
//            pr("---------------------------");
            Multipart mp = (Multipart) p.getContent();
            level++;
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                dumpPart(mp.getBodyPart(i));
            }
            level--;
        } else if (p.isMimeType("message/rfc822")) {
//            pr("This is a Nested Message");
//            pr("---------------------------");
            level++;
            dumpPart((Part) p.getContent());
            level--;
        } else {
            /*
             * If we actually want to see the data, and it's not a
             * MIME type we know, fetch it and check its Java type.
             */
//            Object o = p.getContent();
//            if (o instanceof String) {
//                pr("This is a string");
//                pr("---------------------------");
//                System.out.println((String) o);
//            } else if (o instanceof InputStream) {
//                pr("This is just an input stream");
//                pr("---------------------------");
//                InputStream is = (InputStream) o;
//                int c;
//                while ((c = is.read()) != -1) {
//                    System.out.write(c);
//                }
//            } else {
//                pr("This is an unknown type");
//                pr("---------------------------");
//                pr(o.toString());
//            }
        }

        /*
         * If we're saving attachments, write out anything that
         * looks like an attachment into an appropriately named
         * file.  Don't overwrite existing files to prevent
         * mistakes.
         */
        if (saveAttachments && p instanceof MimeBodyPart
                && !p.isMimeType("multipart/*")) {
            String disp = p.getDisposition();
            // many mailers don't include a Content-Disposition
            if (disp == null
                    || disp.equalsIgnoreCase(Part.ATTACHMENT)
                    || disp.equalsIgnoreCase(Part.INLINE)) {
                // Only save files with named attachements
                if (filename != null) {

                    //pr("Saving attachment " + filename);
                    if (p.getSize() > 7000) {
                        Attachment attachment = new Attachment();
                        attachment.uploadEmailAttachment((MimeBodyPart) p);
                        attachments.add(attachment);
                    }
                }

            }
        }
    }

    static int level = 0;

    /**
     * Print a, possibly indented, string.
     *
     * @param s
     */
    public static void pr(String s) {
        System.out.println(level + ": " + s);
    }
}
