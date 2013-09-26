/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.report;

import com.dastrax.per.entity.core.Site;

/**
 *
 * @version Build 2.0.0
 * @since Sep 11, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
public class Report {
    // Variables----------------------------------------------------------------
    private String id;
    private Site site;
    private String metrics;
    private String frequency;

    // Constructors-------------------------------------------------------------
    public Report(String id, Site site, String metrics, String frequency) {
        this.id = id;
        this.site = site;
        this.metrics = metrics;
        this.frequency = frequency;
    }

    // Getters------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getMetrics() {
        return metrics;
    }
    
    public Site getSite() {
        return site;
    }

    public String getFrequency() {
        return frequency;
    }

    // Setters------------------------------------------------------------------
    public void setSite(Site site) {
        this.site = site;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

}
