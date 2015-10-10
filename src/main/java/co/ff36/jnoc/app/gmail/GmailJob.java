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

package co.ff36.jnoc.app.gmail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import co.ff36.jnoc.app.service.internal.DefaultStorageManager;
import co.ff36.jnoc.app.services.StorageManager;

/**
 * Implements the Quartz Job interface to schedule Gmail IMAP check.
 *
 * @version 2.0.0
 * @since Build 140929.115744
 * @author Tarka L'Herpiniere

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
                    System.getenv("JNOC_SENDER_EMAIL"),
                    System.getenv("JNOC_SENDER_EMAIL_SECRET"));
            
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

            /*
             * sort message by received date. when in the email conversion. the last mail is full message. but the conversion email has same title.
             * so. we will create new ticket by last email, the conversion other email is skip.  
             */
//            List<Message> messageList = Arrays.asList(messages);
//            Collections.sort(messageList,new Comparator<Message>(){
//                public int compare(Message arg0, Message arg1) {
//                    try {
//						return arg1.getReceivedDate().compareTo(arg0.getReceivedDate());
//					} catch (MessagingException e) {
//						e.printStackTrace();
//					}
//                    return 0;
//                }
//            });
            
            // Get the blacklist
            StorageManager storage = new DefaultStorageManager();
            //InputStream input = storage.get(storage.keyGenerator(JNOC.KeyType.EMAIL_BLACKLIST, null)).getObjectContent();
            //Map<String, Object> blacklist = new ObjectMapper().readValue(input, Map.class);

            
            for (int i = 0; i < messages.length; ++i) {
                final Message msg = messages[i];
                // Only process emails not on the blacklist
                //if (!blacklist.containsKey(msg.getFrom()[0].toString())) {

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
           					Logger.getLogger(GmailJob.class.getName()).log(Level.ALL, "handler email error: "+e.getMessage(), e);
                        }
                        // Delete the message
                        msg.setFlag(Flags.Flag.DELETED, true);
                    }
//                } else {
//                     // Delete the message
//                     msg.setFlag(Flags.Flag.DELETED, true);
//                }

            }
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(GmailJob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException /*| IOException*/ ex) {
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
