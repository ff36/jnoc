/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.cnx.report;

import com.dastrax.app.util.ExceptionUtil;
import com.dastrax.cnx.monitor.DeviceUtil;
import com.dastrax.cnx.pojo.Device;
import com.dastrax.per.dao.cnx.EventLogDAO;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.entity.cnx.EventLog;
import com.dastrax.per.entity.core.Site;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ejb.Timeout;

/**
 *
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@Startup
public class EventLogs {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(EventLogs.class.getName());

    // Variables----------------------------------------------------------------
    private final String username = ResourceBundle.getBundle("Config").getString("DMSUsernameDB");
    private final String password = ResourceBundle.getBundle("Config").getString("DMSPasswordDB");

    // EJB----------------------------------------------------------------------
    @EJB
    SiteDAO siteDAO;
    @EJB
    ExceptionUtil exu;
    @EJB
    EventLogDAO eventLogDAO;
    @EJB
    DeviceUtil deviceUtil;

    // Methods------------------------------------------------------------------
    /**
     * This method is executed every 12 hours to at set intervals to trigger an
     * SNMP get to retrieve the event log from the site DMS. No guarantee is
     * provided that the schedule will be maintained with absolute accuracy. If
     * a cycle is still running when the next scheduled cycle is due to start,
     * the preceeding cycle will be allowed to complete before beginning the new
     * one. Cycles in arrears will be queued for execution when possible.
     */
    @Schedule(minute = "*/2", hour = "*")
    protected void getLogs() {
        processLogs();
    }

    @Asynchronous
    private void processLogs() {
        List<EventLog> logs = new ArrayList<>();

        // Obtain a list of sites to retreive logs from
        for (Site site : siteDAO.findAllSites()) {
            Connection connect = null;
            try {
                // Set the target url for the connection
                String url = null;
                switch (site.getDmsType()) {
                    case "1200":
                        url = "jdbc:mysql://" + site.getDmsIP() + ":3306/dms1200";
                        break;
                    case "600":
                        url = "jdbc:mysql://" + site.getDmsIP() + ":3306/solid";
                        break;
                }
                // Create the connection
                Class.forName("com.mysql.jdbc.Driver");
                connect = DriverManager.getConnection(url, username, password);

                // Get the last synched log entry
                int last = eventLogDAO.findLastEntryBySite(site.getId());

                // Create the query statement
                Statement statement = connect.createStatement();
                statement.setMaxRows(5000);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM eventlog WHERE clearedTime IS NOT NULL AND id > " + last);

                // Convert the Results to objects
                logs.addAll(convertResult(resultSet, site, deviceUtil.getDeviceTree(site)));

            } catch (ClassNotFoundException e) {
                exu.report(e);
            } catch (SQLException e) {
                if (ResourceBundle.getBundle("Config").getString("ProjectStage").equals("PRO")) {
                    exu.report(e);
                }
            } finally {
                if (connect != null) {
                    try {
                        connect.close();
                    } catch (SQLException e) {
                        exu.report(e);
                    }
                }
            }
            // Set the Hysterisis after the connection is closed
            if (logs.size() < 10) {
                // If there are less than 10 events we can get the Hysteresis individually
                for (EventLog eventLog : logs) {
                    eventLog.setHysteresis(deviceUtil.obtainSingleHysteresis(site, eventLog.getAddress(), eventLog.getDbcolumn()));
                }
            } else {
                HashMap<String, Long> hysteresis = (HashMap<String, Long>) deviceUtil.obtainBatchHysteresis(site);
                for (EventLog eventLog : logs) {
                    if (hysteresis.get(eventLog.getAddress()) != null) {
                        eventLog.setHysteresis(hysteresis.get(eventLog.getAddress()));
                    } else {
                        eventLog.setHysteresis(0);
                    }
                }
            }

        }
        // Save the logs to the db
        persistLogs(logs);
    }

    @Timeout
    public void onTimeout() {
        // Do nothing
    }

    /**
     * Once all the processing has completed we can store the logs
     *
     * @param logs
     */
    private void persistLogs(List<EventLog> logs) {
        // Persist the logs
        if (!logs.isEmpty()) {
            eventLogDAO.create(logs);
        }
    }

    /**
     * Converts the SQL ResultSet into a List<EventLog> to be stored in the db.
     *
     * @param resultSet
     * @param site
     * @return
     * @throws SQLException
     */
    private List<EventLog> convertResult(ResultSet resultSet, Site site, List<Device> devices) throws SQLException {
        List<EventLog> logs = new ArrayList<>();
        while (resultSet.next()) {
            // Get the respective device from the tree
            Device device = obtainDevice(devices, resultSet.getString("address"));
            // Build the eventlog
            EventLog eventLog = new EventLog();
            eventLog.setSite(site.getId());
            eventLog.setExternalId(resultSet.getInt("id"));
            eventLog.setAddress(resultSet.getString("address"));
            eventLog.setDbcolumn(resultSet.getString("dbcolumn"));
            eventLog.setReceivedTime(resultSet.getDate("receivedTime").getTime());
            eventLog.setClearedTime(resultSet.getDate("receivedTime").getTime());
            eventLog.setClearedTime(resultSet.getDate("clearedTime").getTime());
            eventLog.setDownTime(eventLog.getClearedTime() - eventLog.getReceivedTime());
            if (device != null) {
                eventLog.setFrequency(device.getFrequency());
            }
            //eventLog.setCarrier();

            // Add the log to the list
            logs.add(eventLog);
        }
        return logs;
    }

    /**
     * Returns the device from the site tree or null if it does not exist
     *
     * @param devices
     * @param address
     * @return
     */
    public Device obtainDevice(List<Device> devices, String address) {
        Device device = null;
        if (devices != null && address != null) {
            for (Device d : devices) {
                if (d.getAddress().equals(address)) {
                    device = d;
                }
            }
        }
        return device;
    }
}
