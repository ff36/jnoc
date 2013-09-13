/*
 * Copyright 2013 SOLiD Inc ALL RIGHTS RESERVED.
 * Developed by: Tarka L'Herpiniere <info@tarka.tv>.
 */
package com.dastrax.service.report;

import com.dastrax.per.dao.core.SiteDAO;
import com.dastrax.per.dao.core.SubjectDAO;
import com.dastrax.per.entity.core.Site;
import com.dastrax.per.entity.core.Subject;
import com.dastrax.per.project.DastraxCst;
import java.io.Serializable;
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

        // Setup a list of frequencies and carriers
        List<String> freq = new ArrayList<>();
        freq.add("700 LTE");
        freq.add("700 PS");
        freq.add("800 PS/iDEN");
        freq.add("850 Cellular");
        freq.add("900 SMR");
        freq.add("900 Paging");
        freq.add("1900 PSC");
        freq.add("2100 AWS");

        List<String> carr = new ArrayList<>();
        carr.add("AT&T");
        carr.add("Verizon");

        // Setup the reporting objects
        for (Site site : sites) {
            // Overall System
            reports.add(new Report(UUID.randomUUID().toString(), site, "Overall System"));
            
            // DAS Overall
            reports.add(new Report(UUID.randomUUID().toString(), site, "DAS Uptime"));

            // Frequency Overall
            for (String f : freq) {
                reports.add(new Report(UUID.randomUUID().toString(), site, f + " Overall"));
            }

            // Frequency by Carrier
            for (String c : carr) {
                for (String f : freq) {
                    reports.add(new Report(UUID.randomUUID().toString(), site, f + " / " + c));
                }
            }
            
            // Carrier Overall
            for (String c : carr) {
                reports.add(new Report(UUID.randomUUID().toString(), site, c + " Overall"));
            }

            // Carrier by Frequency
            for (String f : freq) {
                for (String c : carr) {
                    reports.add(new Report(UUID.randomUUID().toString(), site, c + " / " + f));
                }
            }

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

    public void buildChart() {
        reportChart = new CartesianChartModel();
        for (Report report : selectedReports) {
            LineChartSeries series1 = new LineChartSeries();
            series1.setLabel(report.getMetrics() + " for " + report.getSite().getName());
            for (long i = from.getTime(); i < to.getTime(); i = i + 86400000) {
                series1.set(i, 99 + Math.random());
            }
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
