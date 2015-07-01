package com.dastrax.service.ticket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.dastrax.per.project.DTX;
import com.dastrax.service.ticket.TicketAnalytics.TicketDigest;

public class TicketAnalyticsPDFHandler {
	private static final Logger LOG = Logger.getLogger(TicketAnalyticsPDFHandler.class.getName());
	
	private List<TicketDigest> tickets = null;

	private List<ReportTicket> severities = new ArrayList<ReportTicket>();

	private List<ReportTicket> quarters = new ArrayList<ReportTicket>();

	private List<ReportTicket> topics = new ArrayList<ReportTicket>();

	private List<ReportTicket> status = new ArrayList<ReportTicket>();
	
	private String averageResponseTime="0";

	private String averageResolutionTime="0";

	private String averageCommentQty="0";

	private Long fromDate;
	private Long toDate;
	
	public TicketAnalyticsPDFHandler(List<TicketDigest> tickets){
		this.tickets = tickets;
	}
	
	public TicketAnalyticsPDFHandler(List<TicketDigest> tickets, Long fromDate, Long toDate){
		this.tickets = tickets;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}
	
	@SuppressWarnings("unchecked")
	public File generatePDF(){
		
		//prepare data
		this.prepareData();
		
		File tmpfile = new File("ticketreport.pdf");
		
		try {
			
			if(tmpfile.exists())
				tmpfile.delete();
			tmpfile.createNewFile();
			
			JasperReport jasperReport = JasperCompileManager.compileReport(TicketDigest.class.getResourceAsStream("/report/TicketAnalytics.jrxml"));

			Map customParameters = new HashMap();
			
			//JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(getSeverities(), false);
			customParameters.put("severityBeanList", 
					new JRBeanCollectionDataSource(this.severities, false));
			customParameters.put("severitySource",
					JasperCompileManager.compileReport(
							TicketDigest.class.getResourceAsStream("/report/TicketAnalytics_Severity.jrxml")));
			
			customParameters.put("quarterBeanList", 
					new JRBeanCollectionDataSource(this.quarters, false));
			customParameters.put("quarterSource",
					JasperCompileManager.compileReport(
							TicketDigest.class.getResourceAsStream("/report/TicketAnalytics_Quarter.jrxml")));
			
			customParameters.put("topicBeanList", 
					new JRBeanCollectionDataSource(this.topics, false));
			customParameters.put("topicSource",
					JasperCompileManager.compileReport(
							TicketDigest.class.getResourceAsStream("/report/TicketAnalytics_Topic.jrxml")));
			
			customParameters.put("averageResponseTime", this.averageResponseTime);
			customParameters.put("averageResolutionTime", this.averageResolutionTime);
			customParameters.put("averageCommentQty", this.averageCommentQty);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, customParameters, 
					new JRBeanCollectionDataSource(this.status));
			
			FileOutputStream os = new FileOutputStream(tmpfile);
			JasperExportManager.exportReportToPdfStream(jasperPrint, os);
		} catch (JRException | IOException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return tmpfile;
	}

	private void prepareData() {
		if(tickets!=null){
			
			int statusOpen = 0;
	        int statusClose = 0;
	        int s1 = 0, s2=0, s3=0;
	        int connectionFailure = 0, equipmentFailure=0, general=0, 
	        		informational=0,signalSourceFailure=0, totalSysFailure=0; 
	        int q1=0, q2=0, q3=0, q4=0;
	        
	        Long totalResponseTime = 0L, totalResolutionDuration=0L, totalCommentQty = 0L;
	        
	        for(int i=0; i<tickets.size(); i++){
				TicketDigest ticket = tickets.get(i);
				
				if(DTX.TicketStatus.CLOSED.equals(ticket.getStatus())){
                	statusClose++;
                	totalResponseTime += ticket.getAverageResponseTime();
                	totalResolutionDuration += ticket.getResolutionDuration();
                	totalCommentQty += ticket.getCommentQty();
                }else
                	statusOpen++;
                
                if(DTX.TicketSeverity.S1.equals(ticket.getSeverity()))
                	s1++;
                else if(DTX.TicketSeverity.S2.equals(ticket.getSeverity())){
                	s2++;
                }else if(DTX.TicketSeverity.S3.equals(ticket.getSeverity()))
                	s3++;
				
                if(DTX.TicketTopic.CONNECTION_FAILURE.equals(ticket.getTopic())){
                	connectionFailure++;
                }else if(DTX.TicketTopic.EQUIPMENT_FAILURE.equals(ticket.getTopic()))
                	equipmentFailure++;
                else if(DTX.TicketTopic.GENERAL.equals(ticket.getTopic()))
                	general++;
                else if(DTX.TicketTopic.INFORMATIONAL.equals(ticket.getTopic()))
                	informational++;
                else if(DTX.TicketTopic.SIGNAL_SOURCE_FAILURE.equals(ticket.getTopic()))
                	signalSourceFailure ++;
                else if(DTX.TicketTopic.TOTAL_SYSTEM_FAILURE.equals(ticket.getTopic()))
                	totalSysFailure ++;
                
                Calendar openDate = Calendar.getInstance();
                openDate.setTimeInMillis(ticket.getOpenEpoch());
                int month = openDate.get(Calendar.MONTH);
                if (month <= 2)
                    q1++;
                else if (month > 3 && month <= 5)
                    q2++;
                else if (month > 5 && month <= 8)
                    q3++;
                else
                	q4++;
                
                
			}
			
			status.add(new ReportTicket(DTX.TicketStatus.OPEN.toString(), statusOpen));
	        status.add(new ReportTicket(DTX.TicketStatus.CLOSED.toString(), statusClose));
	        
	        severities.add(new ReportTicket("SERVICE DOWN (S1)", s1));
	        severities.add(new ReportTicket("SERVICE DISRUPTION (S2)", s2));
	        severities.add(new ReportTicket("GENERAL SUPPORT (S3)", s3));
			
	        topics.add(new ReportTicket(DTX.TicketTopic.CONNECTION_FAILURE.getLabel(), connectionFailure, "0"));
	        topics.add(new ReportTicket(DTX.TicketTopic.EQUIPMENT_FAILURE.getLabel(), equipmentFailure, "0"));
	        topics.add(new ReportTicket(DTX.TicketTopic.GENERAL.getLabel(), general, "0"));
	        topics.add(new ReportTicket(DTX.TicketTopic.INFORMATIONAL.getLabel(), informational, "0"));
	        topics.add(new ReportTicket(DTX.TicketTopic.SIGNAL_SOURCE_FAILURE.getLabel(), signalSourceFailure, "0"));
	        topics.add(new ReportTicket(DTX.TicketTopic.TOTAL_SYSTEM_FAILURE.getLabel(), totalSysFailure, "0"));
	        
	        quarters.add(new ReportTicket("Q1", q1));
	        quarters.add(new ReportTicket("Q2", q2));
	        quarters.add(new ReportTicket("Q3", q3));
	        quarters.add(new ReportTicket("Q4", q4));
	        
	        
	        Double seconds = totalResponseTime.doubleValue()/statusClose;

	        int days = (int) Math.floor(seconds / 86400000);
	        seconds -= days * 86400000;
	        int  hours = (int) (Math.floor(seconds / (3600*1000)));
	        seconds -= hours * 3600;
	        double minutes = Math.floor(seconds / 60) % 60;
	        seconds -= minutes * 60;
	        
	        this.averageResponseTime = days+" days "+ hours+" hours " + minutes +" mins";
	        
	        
	        double duraSconds = (totalResolutionDuration/statusClose);
	        int day =  (int) Math.floor(duraSconds / 86400000);
	        duraSconds -= day*86400000;
	        if(day>10)
	        	this.averageResolutionTime = " > 10 days";
	        else
	        	this.averageResolutionTime = day+" days "+(int) (Math.floor(duraSconds / (3600*1000)))+" hours";
	        
	        
	        BigDecimal avg = new BigDecimal(totalCommentQty.doubleValue()/statusClose);
	        this.averageCommentQty = avg.setScale(1, BigDecimal.ROUND_HALF_UP)+" per solved ticket";
	        
		}
		
	}
	
	public void setFromDate(Long fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(Long toDate) {
		this.toDate = toDate;
	}

}
