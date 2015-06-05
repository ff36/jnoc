/*
 * Created Sep 29, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.app.gmail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dastrax.app.service.internal.DefaultStorageManager;
import com.dastrax.app.services.StorageManager;
import com.dastrax.per.dap.DefaultCrudService;
import com.dastrax.per.project.DTX;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        Folder folder = null, failFolder = null;
        
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
            
            /*
             * handle email is fail, this email will move to Fail folder.
             * Fail folder is not exist, create Fail folder 
             */
            try {
    			failFolder = store.getFolder("Fail");
    		} catch (MessagingException e) {
    			//Fail folder is not exist, create Fail Folder
    			try {
    				Folder defaultFolder = store.getDefaultFolder();
    				failFolder = defaultFolder.getFolder("Fail");   
    		        failFolder.create(Folder.HOLDS_MESSAGES);
    			} catch (MessagingException e1) {
    				e1.printStackTrace();
    			}
    		}
            
            // Attributes & Flags for all messages ..
            Message[] messages = folder.getMessages();

            // Get the blacklist
            StorageManager storage = new DefaultStorageManager();
            InputStream input = storage.get(storage.keyGenerator(DTX.KeyType.EMAIL_BLACKLIST, null)).getObjectContent();
            Map<String, Object> blacklist = new ObjectMapper().readValue(input, Map.class);

            
            for (int i = 0; i < messages.length; ++i) {
                final Message msg = messages[i];

                // Only process emails not on the blacklist
                if (!blacklist.containsKey(msg.getFrom()[0].toString())) {

                    /* To prevent the same message being reread if a long running
                     process is actioned we will only pull in messages that have 
                     been unread. As soon as we take the message into the queue we 
                     will mark it as read.
                     */
                    if (!msg.getFlags().contains(Flags.Flag.SEEN)) {
                        // Set the message as seen
                        msg.setFlag(Flags.Flag.SEEN, true);
                        // Process it
                        try{
                        	//System.out.println(Thread.currentThread().getName()+" : "+msg.getSubject());
                        	new EmailToTicket().processEmail(msg);
                        } catch (Exception e){
                        	// error, remove to other folder
           					folder.copyMessages(new Message[]{msg}, failFolder);
           					msg.setFlag(Flags.Flag.DELETED, true);
                        }
                        // Delete the message
                        msg.setFlag(Flags.Flag.DELETED, true);
                    }
                } else {
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

    private DefaultCrudService lookupDefaultCrudServiceBean() {
        try {
            Context c = new InitialContext();
            return (DefaultCrudService) c.lookup("java:global/com.dastrax_Dastrax_war_3.1.2/DefaultCrudService!com.dastrax.per.dap.DefaultCrudService");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
