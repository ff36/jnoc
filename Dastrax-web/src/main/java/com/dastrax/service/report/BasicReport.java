/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.report;

import com.dastrax.cnx.monitor.DeviceUtil;
import com.dastrax.cnx.report.Generator;
import com.dastrax.per.dao.cnx.EventLogDAO;
import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.cnx.EventLog;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.project.DastraxCst;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @version Build 2.0.0
 * @since Sep 11, 2013
 * @author Tarka L'Herpiniere <info@tarka.tv>
 */
@Named
@ViewScoped
public class BasicReport implements Serializable {

    // Logger-------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(BasicReport.class.getName());

    // Variables----------------------------------------------------------------
    private List<Report> reports = new ArrayList<>();
    private List<Report> filteredReports = new ArrayList<>();
    private Report[] selectedReports;
    private ReportModel reportModel;
    private CartesianChartModel reportChart;
    private Date from;
    private Date to;

    // EJB----------------------------------------------------------------------
    @EJB
    SubjectDAO subjectDAO;
    @EJB
    SiteDAO siteDAO;
    @EJB
    DeviceUtil deviceUtil;
    @EJB
    EventLogDAO eventLogDAO;
    @EJB
    Generator generator;

    // Getters------------------------------------------------------------------
    public List<Report> getReports() {
        return reports;
    }

    public CartesianChartModel getReportChart() {
        return reportChart;
    }

    public Report[] getSelectedReports() {
        return selectedReports;
    }

    public ReportModel getReportModel() {
        return reportModel;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public List<Report> getFilteredReports() {
        return filteredReports;
    }

    // Setters------------------------------------------------------------------
    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void setReportChart(CartesianChartModel reportChart) {
        this.reportChart = reportChart;
    }

    public void setSelectedReports(Report[] selectedReports) {
        this.selectedReports = selectedReports;
    }

    public int getSelectedLength() {
        if (selectedReports != null) {
            return selectedReports.length;
        } else {
            return 0;
        }
    }

    public void setReportModel(ReportModel reportModel) {
        this.reportModel = reportModel;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public void setFilteredReports(List<Report> filteredReports) {
        this.filteredReports = filteredReports;
    }

    // Methods------------------------------------------------------------------
    @PostConstruct
    private void postConstruct() {

        List<Site> sites = new ArrayList<>();

        // ADMIN access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.ADMIN.toString())) {
            sites = siteDAO.findAllSites();
        }
        // VAR access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.VAR.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            sites = s.getCompany().getVarSites();
        }
        // CLIENT access
        if (SecurityUtils.getSubject().hasRole(DastraxCst.Metier.CLIENT.toString())) {
            Subject s = subjectDAO.findSubjectByUid(
                    SecurityUtils.getSubject().getPrincipals()
                    .asList().get(1).toString());
            sites = s.getCompany().getClientSites();
        }

        // Setup the reporting objects
        for (Site site : sites) {

            // Get a list of available frequencies for a given site
            List<String> frequencies = deviceUtil.obtainFrequencies(site);

            // Overall
            if (!frequencies.isEmpty()) {
                reports.add(new Report(UUID.randomUUID().toString(), site, "Overall Uptime", null));
            }
            
            // Frequency Overall
            for (String f : frequencies) {
                if (f != null) {
                    reports.add(new Report(UUID.randomUUID().toString(), site, f + " Overall", f));
                }
            }

//            // Frequency by Carrier
//            for (String c : carr) {
//                for (String f : freq) {
//                    reports.add(new Report(UUID.randomUUID().toString(), site, f + " / " + c));
//                }
//            }
//            
//            // Carrier Overall
//            for (String c : carr) {
//                reports.add(new Report(UUID.randomUUID().toString(), site, c + " Overall"));
//            }
//
//            // Carrier by Frequency
//            for (String f : freq) {
//                for (String c : carr) {
//                    reports.add(new Report(UUID.randomUUID().toString(), site, c + " / " + f));
//                }
//            }
        }
        reportModel = new ReportModel(reports);
        filteredReports = reports;

        // Set the dates
        if (from == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            from = cal.getTime();
        }
        if (to == null) {
            Calendar cal = Calendar.getInstance();
            to = cal.getTime();
        }
    }

    public void selectEvent(SelectEvent event) {
        buildChart();
    }

    public void unselectEvent(UnselectEvent event) {
        buildChart();
    }

    public void presetSelection(String range) {
        Calendar cal = Calendar.getInstance();
        to = cal.getTime();
        switch (range) {
            case "DAY":
                cal.add(Calendar.HOUR, -24);
                break;
            case "WEEK":
                cal.add(Calendar.HOUR, -168);
                break;
            case "MONTH":
                cal.add(Calendar.MONTH, -1);
                break;
            default:
                break;
        }
        from = cal.getTime();
        buildChart();
    }
    
    public void buildChart() {
        reportChart = new CartesianChartModel();
        for (Report report : selectedReports) {
            LineChartSeries series1 = new LineChartSeries();
            series1.setLabel(report.getMetrics() + " for " + report.getSite().getName());

            // Get the results
            List<EventLog> logs = generator.generateReport(report.getSite(), from.getTime(), to.getTime(), report.getFrequency());

            // Normalize the results
            Double uptime = generator.normalizeReport(from.getTime(), to.getTime(), logs);
            
            // Format the dates
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            
            // Create the new series
            series1.set("Performance Report(s) from " + sdf.format(from) + " to " + sdf.format(to), uptime);

            reportChart.addSeries(series1);
        }
    }

    public long fromAsLong() {
        return from.getTime();
    }

    public long toAsLong() {
        return to.getTime();
    }

    public Date today() {
        return new Date();
    }
}
