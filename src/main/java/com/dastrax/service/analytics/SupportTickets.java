/*
 * Created Aug 8, 2013.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.analytics;

import au.com.bytecode.opencsv.CSVWriter;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.dastrax.app.service.internal.DefaultStorageManager;
import com.dastrax.app.service.internal.DefaultURI;
import com.dastrax.app.services.StorageManager;
import com.dastrax.per.project.DTX;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Support ticket Analytics CDI bean. Performs analytics services on support
 * tickets.
 * 
 *
 * @version 2.0.0
 * @since Build 2.0-SNAPSHOT (Aug 8, 2013)
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
@Named
@ViewScoped
public class SupportTickets implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(SupportTickets.class.getName());
    
    private boolean render;
    private String requestURI;

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public SupportTickets() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of render
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }
    
    /**
     * Get the value of requestURI. Each report request generates a unique data
     * set stored as a temporary file. This is the path to that file.
     *
     * @return the value of requestURI
     */
    public String getRequestURI() {
        return requestURI;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of render.
     *
     * @param render new value of render
     */
    public void setRender(boolean render) {
        this.render = render;
    }
//</editor-fold>

    /**
     * Initialize the page properties and data after the page has loaded.
     *
     */
    public void init() {
        try {
            // Generate an ID for the request
            String id = UUID.randomUUID().toString();

            File temp = File.createTempFile(id, ".csv");

            CSVWriter writer = new CSVWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(temp), "UTF-8")));
            String[] columns = "date#open#high#low#close#volume#oi".split("#");
            writer.writeNext(columns);

            // Generate a random data sample
            String date = "01/01/1986";
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(date));

            for (int i = 0; i < 10000; i++) {
                // Increment the date
                calendar.add(Calendar.DATE, 1);
                date = sdf.format(calendar.getTime());

                // Generate the values
                Random r = new Random();
                String openValue = String.format(
                        "%.2f", 110.00 + (2000.00 - 110.00) * r.nextDouble());
                String closeValue = String.format(
                        "%.2f", 110.00 + (2000.00 - 110.00) * r.nextDouble());
                String lowValue = String.format(
                        "%.2f", 110.00 + (2000.00 - 110.00) * r.nextDouble());
                String highValue = String.format(
                        "%.2f", 110.00 + (2000.00 - 110.00) * r.nextDouble());

                int volume = r.nextInt(20000 - 1000) + 1000;

                String nextLine = date 
                        + "#" 
                        + openValue 
                        + "#" 
                        + closeValue 
                        + "#" 
                        + lowValue 
                        + "#" 
                        + highValue 
                        + "#" 
                        + volume 
                        + "#" 
                        + "0";
                String[] record = nextLine.split("#");
                writer.writeNext(record);

            }

            writer.close();

            // Write the data to S3
            StorageManager storage = new DefaultStorageManager();
            String key = storage.keyGenerator(DTX.KeyType.TEMPORARY_FILE, id + ".csv");
            storage.put(key, temp, CannedAccessControlList.PublicRead);

            // Delete the temporary file
            temp.delete();

            // Set the temporary path
            requestURI = new DefaultURI.Builder(
                    DTX.URIType.TEMPORARY_FILE)
                    .withFile(id + ".csv")
                    .create()
                    .generate();

            // Render the page
            render = true;
        } catch (IOException | ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

    }

}
