/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.csm.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import com.dastrax.app.util.ExceptionUtil;
import com.dastrax.csm.pojo.JsonMsg;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.csm.DmsTicketDAO;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.csm.DmsAlarm;
import com.dastrax.per.entity.csm.DmsAlarmLog;
import com.dastrax.per.entity.csm.DmsTicket;
import com.dastrax.per.project.DastraxCst;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ejb.TimerService;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @version Build 2.0.0
 * @since Aug 3, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@Startup
public class DmsTicketUtil {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(DmsTicketUtil.class.getName());

    // Variables----------------------------------------------------------------
    private final String queueUrl = ResourceBundle.getBundle("Config").getString("SQSTicketQueueURL");
    private final SQS sqs = new SQS();

    // EJB----------------------------------------------------------------------
    @EJB
    ExceptionUtil exu;
    @EJB
    DmsTicketDAO dmsTicketDAO;
    @EJB
    SiteDAO siteDAO;

    // Resources----------------------------------------------------------------
    @Resource
    TimerService timerService;

    // Methods------------------------------------------------------------------
    @Schedule(minute = "*/2", hour = "*")
    public void queryQueue() {
        List<JsonMsg> jsonMsgs = new ArrayList<>();
        try {
            // Find out approx how many tickets are in the queue
            GetQueueAttributesRequest request = new GetQueueAttributesRequest()
                    .withAttributeNames("ApproximateNumberOfMessages")
                    .withQueueUrl(queueUrl);
            Map<String, String> attrs = sqs.getClient().getQueueAttributes(request).getAttributes();
            int qty = Integer.parseInt(attrs.get("ApproximateNumberOfMessages"));
            // the value must be between 1-10 so add conditional message retreive
            ReceiveMessageRequest rmr;
            if (qty > 0 && qty <= 10) {
                rmr = new ReceiveMessageRequest()
                        .withMaxNumberOfMessages(qty)
                        .withWaitTimeSeconds(20)
                        .withQueueUrl(queueUrl);
            } else {
                rmr = new ReceiveMessageRequest()
                        .withWaitTimeSeconds(20)
                        .withQueueUrl(queueUrl);
            }

            // Get the messages
            while (qty > 0) {
                List<Message> messages = sqs.getClient().receiveMessage(rmr).getMessages();
                for (Message message : messages) {
                    // Convert the json messages
                    JsonMsg jsonMsg = convertAlarm(message);
                    jsonMsg.setReceiptHandle(message.getReceiptHandle());
                    jsonMsgs.add(jsonMsg);
                }
                qty = qty - messages.size();
            }
        } catch (AmazonServiceException ase) {
            exu.report(ase);
            LOG.log(Level.SEVERE, "Caught an SQS AmazonServiceException", ase);
        } catch (AmazonClientException ace) {
            exu.report(ace);
            LOG.log(Level.SEVERE, "Caught an SQS AmazonClientException", ace);
        }
        // Once the process of collecting the messages is complete and we have 
        // closed the connection we can process them
        processQueue(jsonMsgs);
    }

    @Asynchronous
    private void processQueue(List<JsonMsg> jsonMsgs) {

        for (JsonMsg jsonMsg : jsonMsgs) {
            
            DmsTicket dmsT = dmsTicketDAO.findDmsTicketByCause(jsonMsg.getRootId());
            if (dmsT == null) {
                // Create a new ticket
                createNewTicket(jsonMsg);
            } else {
                boolean reopened = false;
                boolean active = false;
                // ticket exists so determin if it is open
                if (dmsT.getStatus().equals(DastraxCst.TicketStatus.SOLVED.toString())
                        || dmsT.getStatus().equals(DastraxCst.TicketStatus.ARCHIVED.toString())) {
                    // ticket closed, check if less than 24 hrs
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.HOUR, -24);
                    if (dmsT.getCloseEpoch() > cal.getTimeInMillis()) {
                        // ticket should be re-opened
                        dmsT.setStatus(DastraxCst.TicketStatus.OPEN.toString());
                        reopened = true;
                    } else {
                        // ticket is older thah 24hrs create a new one
                        createNewTicket(jsonMsg);
                    }
                } else {
                    // ticket is already open so just add the 
                    active = true;
                }

                if (reopened || active) {
                    // verify if the ticket alarm exists
                    boolean exists = false;
                    for (DmsAlarm dmsA : dmsT.getAlarms()) {
                        // If alarm exists so we want to update it
                        if (dmsA.getAlarmId().equals(jsonMsg.getAlarmId())) {
                            // create a alarm log of current
                            DmsAlarmLog dal = new DmsAlarmLog();
                            dal.setUpdateEpoch(Calendar.getInstance().getTimeInMillis());
                            dal.setStartEpoch(dmsA.getStartEpoch());
                            dal.setStopEpoch(dmsA.getStopEpoch());
                            // set the new values
                            dmsA.setStartEpoch(jsonMsg.getStartEpoch());
                            dmsA.setStopEpoch(jsonMsg.getStopEpoch());

                            // add the log
                            if (dmsA.getLogs() == null) {
                                List<DmsAlarmLog> ldmal = new ArrayList<>();
                                ldmal.add(dal);
                                dmsA.setLogs(ldmal);
                            } else {
                                dmsA.getLogs().add(dal);
                            }
                            exists = true;
                        }
                    }
                    // check to see if the alarm exists
                    if (exists) {
                        // alarm does exist so update
                        dmsTicketDAO.updateAlarms(dmsT.getId(), dmsT.getAlarms());
                    } else {
                        // alarm doesn't exist so create a new one
                        DmsAlarm dmsA = createNewAlarm(jsonMsg);
                        // update ticket
                        dmsT.getAlarms().add(dmsA);
                        // persist
                        dmsTicketDAO.updateAlarms(dmsT.getId(), dmsT.getAlarms());
                    }
                }
            }
            removeSQSMsg(jsonMsg.getReceiptHandle());
        }
    }

    /**
     * Convert a JSON Message from AWS SQS to JSON object
     *
     * @param message
     * @return
     */
    private JsonMsg convertAlarm(Message message) {
        //    Mock JSON alarm
        //    {"root_id":"1","alarm_id":"a","set_epoch":1355514772,"ip_addr":"50.76.52.205","clear_epoch":0,"alarm_name":"douPd1Alarm","squealer_name":"Biu12Odu1Dou1OeuDou2"}
        JsonMsg jm = new JsonMsg();
        try {
            // Get a new JSON converter from Jackson
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
            jm
                    = mapper.readValue(message.getBody(), JsonMsg.class
                    );
        } catch (IOException ioe) {
            exu.report(ioe);
            LOG.log(Level.SEVERE, "SQS IOException", ioe);
        }
        return jm;
    }

    /**
     * Creates a new ticket
     *
     * @param jm
     * @param ids
     * @return
     */
    private DmsTicket createNewTicket(JsonMsg jm) {
        // Format the IP and get the site
        jm.setIp(formatIP(jm.getIp()));
        Site site = siteDAO.findSiteByIP(jm.getIp());
        if (site != null) {

            // Create a new alarm
            DmsAlarm dmsA = new DmsAlarm();
            dmsA.setAlarmId(jm.getAlarmId());
            dmsA.setStartEpoch(jm.getStartEpoch() * 1000);
            dmsA.setStopEpoch(jm.getStopEpoch() * 1000);
            dmsA.setAlarmName(jm.getName());
            dmsA.setSquealer(jm.getSquealer());

            // Create a new ticket
            DmsTicket dmsT = new DmsTicket();
            dmsT.setCause(jm.getRootId());
            dmsT.setOpenEpoch(Calendar.getInstance().getTimeInMillis());
            dmsT.setPriority("Sev 1");
            dmsT.setStatus(DastraxCst.TicketStatus.OPEN.toString());
            dmsT.setSite(site.getId());
            dmsT.setTitle(site.getName() + " (" + jm.getIp() + ") detected a new \'" + jm.getName() + "\' alarm.");

            // Persist the ticket and alarm in seperate trx
            dmsT = persistTicket(dmsT);
            addFirstAlarm(dmsT.getId(), dmsA);

            return dmsT;
        }
        return null;
    }

    /**
     * Persist new ticket
     *
     * @param dmsT
     * @return
     */
    private DmsTicket persistTicket(DmsTicket dmsT) {
        return dmsTicketDAO.create(dmsT);
    }

    /* 
     * TODO FIX: 
     * Eclipselink has a bug and you cannot persist an object and persist
     * an anotated @OneToMany object in the same transaction. The object will be 
     * persisted but the cache is not updated and so the object cannot be 
     * retrieved. The only way around it is to perform the persist in two
     * seperate txs.
     */
    private void addFirstAlarm(String id, DmsAlarm dmsAlarm) {
        List<DmsAlarm> alarms = new ArrayList<>();
        alarms.add(dmsAlarm);
        dmsTicketDAO.updateAlarms(id, alarms);
    }

    /**
     * Create new alarm
     *
     * @param jsonMsg
     * @param ids
     * @return
     */
    private DmsAlarm createNewAlarm(JsonMsg jsonMsg) {
        // Create a new alarm
        DmsAlarm dmsA = new DmsAlarm();
        dmsA.setAlarmId(jsonMsg.getAlarmId());
        dmsA.setStartEpoch(jsonMsg.getStartEpoch() * 1000);
        dmsA.setStopEpoch(jsonMsg.getStopEpoch() * 1000);
        dmsA.setAlarmName(jsonMsg.getName());
        dmsA.setSquealer(jsonMsg.getSquealer());

        return dmsA;
    }

    /**
     * Remove a message from the SQS queue
     *
     * @param msg
     */
    private void removeSQSMsg(String receiptHandle) {
        sqs.getClient().deleteMessage(new DeleteMessageRequest(queueUrl, receiptHandle));
    }

    /**
     * Convert the incoming IP into a standard 12 digit format
     *
     * @param ip
     * @return
     */
    private String formatIP(String ip) {
        try {
            // Seperate the id into root and alarm
            List<String> octets = new ArrayList<>(Arrays.asList(ip.split("\\.")));
            int oct1 = Integer.parseInt(octets.get(0));
            String o1 = String.format("%03d", oct1);

            int oct2 = Integer.parseInt(octets.get(1));
            String o2 = String.format("%03d", oct2);

            int oct3 = Integer.parseInt(octets.get(2));
            String o3 = String.format("%03d", oct3);

            int oct4 = Integer.parseInt(octets.get(3));
            String o4 = String.format("%03d", oct4);

            return o1 + "." + o2 + "." + o3 + "." + o4;
        } catch (NumberFormatException nfe) {
            exu.report(nfe);
            LOG.log(Level.SEVERE, "SQS Exception whilst formating IP", nfe);
        }
        return "000.000.000.000";

    }

    /**
     * This class is used to create a Client for SQS connections.
     *
     * @author Tarka L'Herpiniere <info@tarka.tv>
     * @since Mar 10, 2013
     */
    public class SQS {

        // Variables----------------------------------------------------------------
        private AmazonSQSClient client = null;

        // Constructor--------------------------------------------------------------
        public SQS() {
            try {
                try (InputStream credentialsAsStream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("AwsApi.properties")) {

                    AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
                    client = new AmazonSQSClient(credentials);
                }

            } catch (IOException t) {
                System.err.println("Error creating AmazonSQSClient: " + t);
            }
        }

        // Methods------------------------------------------------------------------
        public AmazonSQSClient getClient() {
            return client;
        }

    }

}
