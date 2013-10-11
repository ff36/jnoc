/*
 * Copyright 2013 Tarka L'Herpiniere <info@tarka.tv> ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.cnx.monitor;

import com.dastrax.app.util.ExceptionUtil;
import com.dastrax.cnx.pojo.Device;
import com.dastrax.per.entity.core.Site;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @version Build 2.0.0
 * @since Sep 19, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Stateless
@LocalBean
public class DeviceUtil {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(DeviceUtil.class.getName());

    // Variables----------------------------------------------------------------
    private final String username = ResourceBundle.getBundle("Config").getString("DMSUsernameDB");
    private final String password = ResourceBundle.getBundle("Config").getString("DMSPasswordDB");

    // EJB----------------------------------------------------------------------
    @EJB
    ExceptionUtil exu;

    // Methods------------------------------------------------------------------
    public List<Device> getDeviceTree(Site site) {
        List<Device> devices = new ArrayList<>();
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

            // Create the statement
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM status");

            // Convert the Results to objects
            devices = convertResults(resultSet);

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
        return devices;
    }

    private List<Device> convertResults(ResultSet resultSet) throws SQLException {
        List<Device> devices = new ArrayList<>();
        while (resultSet.next()) {
            if (!resultSet.getString("address").equals("Site") && !resultSet.getString("state").contains("OFF")) {
                Device device = new Device();
                device.setId(UUID.randomUUID().toString());
                device.setState(resultSet.getString("state"));
                device.setAddress(resultSet.getString("address"));
                device.setFirmware(resultSet.getString("firmware_version"));
                device.setNode(extractNode(device.getAddress()));
                device.setFrequency(convertFrequency(resultSet.getInt("type"), device.getNode()));
                devices.add(device);
            }
        }

        // Sort the list with the most senior device at the top
        Collections.sort(devices,
                new Comparator<Device>() {
                    @Override
                    public int compare(Device o1, Device o2) {
                        if (o1.getAddress().length() == o2.getAddress().length()) {
                            return 0;
                        } else if (o1.getAddress().length() < o2.getAddress().length()) {
                            return -1;
                        }
                        return 1;
                    }
                });

        // Set the relationships
        for (Device device : devices) {
            String[] names = device.getAddress().split("(?=\\p{Upper})");
            String parentAddress = device.getAddress().substring(0, device.getAddress().lastIndexOf(names[names.length - 1]));
            if (names.length > 1) {
                for (Device d : devices) {
                    if (d.getAddress().equals(parentAddress)) {
                        // Set the parent
                        device.setParent(d);
                        // Add the child
                        d.getChildren().add(device);
                    }
                }
            }
        }

        return devices;
    }

    // Returns the last device in the address
    private String extractNode(String address) {
        String[] name = address.split("(?=\\p{Upper})");
        return name[name.length - 1];
    }

    // Adds an empty root to the tree to allow building a visual tree
    public List<Device> getDeviceTreeWithRoot(Site site) {
        // Get the normal tree
        List<Device> devices = getDeviceTree(site);
        // Create the root device
        Device root = new Device();
        root.setId(UUID.randomUUID().toString());
        root.setNode("ROOT");
        // Set the root
        for (Device device : devices) {
            String[] names = device.getAddress().split("(?=\\p{Upper})");
            if (names.length == 1) {
                // Add the root children
                root.getChildren().add(device);
                // Add the root as parent
                device.setParent(root);
            }
        }
        // Add the root after the loop so the address doesn't generate NPE
        devices.add(root);
        return devices;
    }

    // Converts the frequency type to a value
    private String convertFrequency(int type, String module) {
        String frequency = null;
        if (module.matches("^.+?[1|2]")) {
            switch (type) {
                case 0:
                    frequency = "800PS";
                    break;
                case 1:
                    frequency = "850PS";
                    break;
                case 2:
                    frequency = "AWS-1";
                    break;
                case 3:
                    frequency = "1900PCS";
                    break;
                case 4:
                    frequency = "800C";
                    break;
                case 5:
                    frequency = "850C";
                    break;
                case 6:
                    frequency = "LTE 700";
                    break;
            }
        }
        if (module.matches("^.+?[3|4]")) {
            switch (type) {
                case 0:
                    frequency = "800PS";
                    break;
                case 1:
                    frequency = "850PS";
                    break;
                case 2:
                    frequency = "AWS-1";
                    break;
                case 3:
                    frequency = "1900PCS";
                    break;
                case 4:
                    frequency = "900P";
                    break;
                case 5:
                    frequency = "700PS";
                    break;
                case 6:
                    frequency = "LTE 700";
                    break;
            }
        }

        return frequency;
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
        for (Device d : devices) {
            if (d.getAddress().equals(address)) {
                device = d;
            }
        }
        return device;
    }

    /**
     * Retrieved the hysteresis value for a given alarm on a given device.
     *
     * @param site
     * @param deviceAddress
     * @param alarm
     * @return
     */
    public long obtainSingleHysteresis(Site site, String deviceAddress, String alarm) {
        long hysteresis = 0;
        if (alarm != null) {
            alarm = alarm.toLowerCase().replaceAll("\\s+", "");
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

                // Create the statement
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM alarm WHERE address = '" + deviceAddress + "' AND dbcolumn LIKE '%" + alarm + "%'");

                // Get the Hysteresis in millisecounds
                while (resultSet.next()) {
                    hysteresis = resultSet.getInt("alarmHysteresis") * 1000;
                }

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
        }
        return hysteresis;
    }

    public Map<String, Long> obtainBatchHysteresis(Site site) {
        Map<String, Long> results = new HashMap<>();
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

            // Create the statement
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM alarm");

            // Get the Hysteresis in millisecounds
            while (resultSet.next()) {
                if (resultSet.getString("alarmHysteresis") != null) {
                    results.put(resultSet.getString("address"), resultSet.getLong("alarmHysteresis") * 1000);
                }
            }

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
        return results;
    }

    /**
     * Returns a list of frequencies associated with a site
     *
     * @param site
     * @return
     */
    public List<String> obtainFrequencies(Site site) {
        List<Device> devices = getDeviceTree(site);
        Set<String> frequencies = new HashSet();
        for (Device device : devices) {
            frequencies.add(device.getFrequency());
        }
        return new ArrayList<>(frequencies);
    }

}
