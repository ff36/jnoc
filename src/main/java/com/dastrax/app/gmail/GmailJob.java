/*
 * Created Sep 29, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.gmail;

import java.io.IOException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.mail.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

/**
 * Implements the Quartz Job interface to schedule Gmail IMAP check.
 *
 * @version 2.0.0
 * @since Build 140929.115744
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class GmailJob implements Job {

    /**
     * @param context
     * @throws org.quartz.JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Folder folder = null;
        Store store = null;
        try {

            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);
            // session.setDebug(true);
            store = session.getStore("imaps");
            store.connect(
                    "imap.gmail.com",
                    ResourceBundle.getBundle("config").getString("SenderEmailAddress"),
                    ResourceBundle.getBundle("config").getString("SenderEmailPassword"));
            folder = store.getFolder("Inbox");
            /* Others GMail folders :
             * [Gmail]/All Mail   This folder contains all of your Gmail messages.
             * [Gmail]/Drafts     Your drafts.
             * [Gmail]/Sent Mail  Messages you sent to other people.
             * [Gmail]/Spam       Messages marked as spam.
             * [Gmail]/Starred    Starred messages.
             * [Gmail]/Trash      Messages deleted from Gmail.
             */
            folder.open(Folder.READ_WRITE);

            // Attributes & Flags for all messages ..
            Message[] messages = folder.getMessages();
            
            for (int i = 0; i < messages.length; ++i) {
                final Message msg = messages[i];

                /* To prevent the same message being reread if a long running
                 process is actioned we will only pull in messages that have 
                 been unread. As soon as we take the message into the queue we 
                 will mark it as read.
                 */
                if (!msg.getFlags().contains(Flags.Flag.SEEN)) {
                    // Set the message as seen
                    msg.setFlag(Flags.Flag.SEEN, true);
                    // Process it
                    new EmailToTicket().processEmail(msg);
                    // Delete the message
                    msg.setFlag(Flags.Flag.DELETED, true);
                }

            }
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(GmailJob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(GmailJob.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (folder != null) {
                try {
                    folder.close(true);
                } catch (MessagingException ex) {
                    Logger.getLogger(GmailJob.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException ex) {
                    Logger.getLogger(GmailJob.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
