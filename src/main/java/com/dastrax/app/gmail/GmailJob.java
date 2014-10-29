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
import javax.mail.*;

/**
 * Implements the Quartz Job interface to schedule UDP datagrams. The datagram
 * is constructed sequentially for each of the 16 BIUs. The datagram is then
 * sent to the BIU and receives a response with all the system attributes. These
 * are subsequently stored in the persistence layer as snapshots.
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
            store.connect("imap.gmail.com", "support-development-823764@solid.com", "&ehuPMg9x%DQ");
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
            Message messages[] = folder.getMessages();
            System.out.println("No of Messages : " + folder.getMessageCount());
            for (int i = 0; i < messages.length; ++i) {
                final Message msg = messages[i];
                /*
                 We don't want to fetch messages already processed
                 */
                //if (!msg.isSet(Flags.Flag.SEEN)) {

                String from = "unknown";
                if (msg.getReplyTo().length >= 1) {
                    from = msg.getReplyTo()[0].toString();
                } else if (msg.getFrom().length >= 1) {
                    from = msg.getFrom()[0].toString();
                }

//                Multipart mp = (Multipart) msg.getContent();
//                BodyPart bp = mp.getBodyPart(0);
//                System.out.println("MSG NUMBER: " + msg.getMessageNumber());
//                System.out.println("SENT DATE: " + msg.getSentDate());
//                System.out.println("SUBJECT: " + msg.getSubject());
//                System.out.println("CONTENT: " + bp.getContent());
//                System.out.println("FROM: " + from);
//                new Thread(new Runnable() {
//                    public void run() {
//                        try {
//                            new EmailToTicket().processEmail(msg);
//                        } catch (MessagingException | IOException ex) {
//                            Logger.getLogger(GmailJob.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                }).start();
                
                new EmailToTicket().processEmail(msg);

                // Delete the message
                msg.setFlag(Flags.Flag.DELETED, true);
 
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
