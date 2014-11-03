/*
 * Created Oct 29, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.gmail;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.dap.QueryParameter;
import com.dastrax.per.entity.Attachment;
import com.dastrax.per.entity.Comment;
import com.dastrax.per.entity.Metier;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.entity.User;
import com.dastrax.per.entity.User_;
import com.dastrax.per.project.DTX;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
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
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

/**
 * Converts an email into a ticket.
 *
 * @version 3.1.0
 * @since Build 141029.115419
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
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
//            LOG.log(Level.SEVERE, null, ex);
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

        if (subject.matches("(.*)(\\({1})(DTX-)([0-9]*)(\\){1})(.*)")) {
            // Extract the email id
            String fullEmailId = StringUtils.substringBetween(msg.getSubject(), "(", ")");
            String emailId = fullEmailId.substring(4);

            // Set the title
            title = title.replace("fullEmailId", "");

            // get the ticket
            List<Ticket> tickets = dap.findWithNamedQuery(
                    "Ticket.findAllByEmail",
                    QueryParameter
                    .with("email", emailId)
                    .parameters());

            if (!tickets.isEmpty()) {
                // The ticket exists
                Comment comment = new Comment();
                comment.setCommenter(user);
                comment.setCreateEpoch(new Date().getTime());
                comment.setComment(getMessage(msg));

                Ticket ticket = tickets.get(0);
                ticket.setComment(comment);
                ticket.setSendEmailToRequester(true);
                if (ticket.getAssignee() != null) {
                    ticket.setSendEmailToAssignee(true);
                }
                ticket.edit(DTX.TicketStatus.OPEN, user);

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
                ticket.setStatus(DTX.TicketStatus.OPEN);
                ticket.setTitle(title);
                ticket.setSeverity(DTX.TicketSeverity.S3);
                ticket.setTopic(DTX.TicketTopic.GENERAL);
                ticket.setSendEmailToRequester(true);
                ticket.setComment(comment);
                ticket.create(user, DTX.TicketStatus.OPEN);

                // Take care of the attachements
                for (Attachment a : attachments) {
                    ticket.setAttachment(a);
                    ticket.addAttachment();
                }
                
            }

        } else {
            // Create a new ticket
            Comment comment = new Comment();
            comment.setCommenter(user);
            comment.setCreateEpoch(new Date().getTime());
            comment.setComment(getMessage(msg));

            Ticket ticket = new Ticket();
            ticket.setStatus(DTX.TicketStatus.OPEN);
            ticket.setTitle(title);
            ticket.setSeverity(DTX.TicketSeverity.S3);
            ticket.setTopic(DTX.TicketTopic.GENERAL);
            ticket.setSendEmailToRequester(true);
            ticket.setComment(comment);
            ticket.create(user, DTX.TicketStatus.OPEN);

            // Take care of the attachements
            for (Attachment a : attachments) {
                ticket.setAttachment(a);
                ticket.addAttachment();
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
        String from = "unknown";
        if (msg.getReplyTo().length >= 1) {
            from = msg.getReplyTo()[0].toString();
        } else if (msg.getFrom().length >= 1) {
            from = msg.getFrom()[0].toString();
        }
        String email = StringUtils.substringBetween(from, "<", ">");

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
    private User createUser(String email, String firstName, String lastName) {

        // New user
        User user = new User();

        // Use the email to see if they belong to a company
        CriteriaBuilder builder = dap.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(User.class);
        Root root = query.from(User.class);
        String[] split = email.split("@");

        // Use the second half of the email to match
        Expression literal = builder.literal((String) "%" + split[1]);
        Predicate predicate = builder.like(root.get(User_.email), literal);
        query.where(predicate);
        List<User> users = dap.findWithCriteriaQuery(query);

        if (users.isEmpty()) {
            // No match so create undefined user
            // Get the metiers
            List<Metier> metiers = dap.findWithNamedQuery(
                    "Metier.findByName",
                    QueryParameter
                    .with("name", DTX.Metier.UNDEFINED.toString())
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
        String emailAddress = ResourceBundle.getBundle("config").getString("SenderEmailAddress");
        if (result != null && result.contains(emailAddress)) {
            result = StringUtils.substringBefore(
                    result,
                    ResourceBundle.getBundle("config").getString("SenderEmailAddress"));
            result = result.substring(0, result.lastIndexOf("\n"));
        }

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

        String name = StringUtils.substringBefore(from, "<").trim();
        // Just for my name because of the appostrohe
        if (name.contains("Tarka")) {
            name = "Tarka";
        }
        if (result != null && result.contains(name)) {
            result = StringUtils.substringBefore(result, name);
            result = result.substring(0, result.lastIndexOf("\n"));
        }

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
