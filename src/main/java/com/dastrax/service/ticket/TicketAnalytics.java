/*
 * Created Nov 4, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.dastrax.service.ticket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.dastrax.per.dap.CrudService;
import com.dastrax.per.entity.Comment;
import com.dastrax.per.entity.Ticket;
import com.dastrax.per.project.DTX;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {description}
 *
 * @version 3.1.0
 * @since Build 141104.092550
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
/**
 * @author Think
 *
 */
@Named
@ViewScoped
public class TicketAnalytics implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = Logger.getLogger(EditTicket.class.getName());
    private static final long serialVersionUID = 1L;

    private String data;
    private boolean render;
    private DefaultStreamedContent file;
//</editor-fold>

    private List<ReportTicketStatus> status;
    private List<ReportTicketStatus> severities;
    
    public List<ReportTicketStatus> getStatus() {
		return status;
	}

	public List<ReportTicketStatus> getSeverities() {
		return severities;
	}

	//<editor-fold defaultstate="collapsed" desc="Constructors">
    public TicketAnalytics() {
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EJB">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of render.
     *
     * @return the value of render
     */
    public boolean isRender() {
        return render;
    }

    public String getData() {
        return data;
    }
//</editor-fold>

	/**
     * Initialize the page by loading the specified ticket from the persistence
     * layer.
     */
    public void init() {
        // Get the ticket from the persistence layer
        List<Ticket> tickets = dap.findWithNamedQuery("Ticket.findAll");

        List<TicketDigest> digests = new ArrayList<>();
        status = new ArrayList<ReportTicketStatus>();
        severities = new ArrayList<ReportTicketStatus>();
        
        int statusOpen = 0;
        int statusClose = 0;
        int s1 = 0;
        int s2 = 0;
        int s3 = 0;
        for (Ticket ticket : tickets) {
            // Only process tickets that belong to legitimate users
            if (!"UNDEFINED".equals(ticket.getRequester().getMetier().getName())) {
                TicketDigest ticketDigest = new TicketDigest();
                ticketDigest.create(ticket);
                digests.add(ticketDigest);
                
                if(DTX.TicketStatus.CLOSED.equals(ticket.getStatus())){
                	statusClose++;
                }else
                	statusOpen++;
                
                if(DTX.TicketSeverity.S1.equals(ticket.getSeverity()))
                	s1++;
                else if(DTX.TicketSeverity.S2.equals(ticket.getSeverity())){
                	s2++;
                }else if(DTX.TicketSeverity.S3.equals(ticket.getSeverity()))
                	s3++;
                
            }
        }

        status.add(new ReportTicketStatus(DTX.TicketStatus.OPEN.toString(), statusOpen));
        status.add(new ReportTicketStatus(DTX.TicketStatus.CLOSED.toString(), statusClose));
        
        severities.add(new ReportTicketStatus("SERVICE DOWN (S1)", s1));
        severities.add(new ReportTicketStatus("SERVICE DISRUPTION (S2)", s2));
        severities.add(new ReportTicketStatus("GENERAL SUPPORT (S3)", s3));
        
        try {
            data = new ObjectMapper().writeValueAsString(digests);
            //System.out.println(data);
        } catch (JsonProcessingException ex) {
            LOG.log(Level.SEVERE, "JSON SERIALIZATION ERROR", ex);
        }
        render = true;
    }

    
    public void setData(String data) {
		this.data = data;
	}

	/**
     * down load pdf report
     * @return StreamedContent 
     */
    public StreamedContent getReportAsPdf() {
    	try{
    		File tmpfile = generateReport();
    		this.file  = new DefaultStreamedContent(new FileInputStream(tmpfile), "application/pdf", "ticket.report"+System.currentTimeMillis()+".pdf");
    		System.out.println("size:"+this.file.getStream().available());
    	}catch (IOException e){
    		LOG.log(Level.SEVERE, "JasperReprot error", e);
    	}
    	return this.file;
    } 
    
    public void getReportAsPdf1() {
    	try{
    		File tmpfile = generateReport();
    		
    		FileInputStream fis = new FileInputStream(tmpfile);
    		
    		FacesContext fc = FacesContext.getCurrentInstance();
    	    ExternalContext ec = fc.getExternalContext();

    	    ec.responseReset(); 
    	    ec.setResponseContentType("application/pdf"); 
    	    ec.setResponseContentLength(fis.available());
    	    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"ticket.report" + System.currentTimeMillis() + ".pdf\""); 

    	    OutputStream output = ec.getResponseOutputStream();
    	    byte[] buf = new byte[1024*10]; 
    	    int byteread = 0;
    	    
    	    while((byteread = fis.read(buf))!=-1){
    	    	output.write(buf, 0, byteread);
    	    }
    	    
    	    output.flush();
    	    output.close();
    	    fis.close();

    	    fc.responseComplete();
    		
    	}catch (IOException e){
    		LOG.log(Level.SEVERE, "JasperReprot error", e);
    	}
    } 
    
    /**
     * fill data to report
     * @return
     */
    private File generateReport(){
    	File tmpfile = new File("ticketreport.pdf");
    	
		try {
			
			if(tmpfile.exists())
				tmpfile.delete();
			tmpfile.createNewFile();
			
			//use jdbc 
			/*
			 
			//Context context = new InitialContext();
			//DataSource datasource = (DataSource) context.lookup(UriUtil.getDataSourceJNDI());
			
			//Connection connection = datasource.getConnection();
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(
				"jdbc:mysql://"+System.getenv("DTX_DB_SERVER_NAME")+":3306/"+System.getenv("DTX_DB_NAME"), 
				System.getenv("DTX_DB_USER_NAME"), 
				System.getenv("DTX_DB_SECRET")
			);
					
			
			String sql = "select count(*) as scount, t.status as tstatus from ticket as t left join subject as s on s.id = t.REQUESTER_ID left join metier as m on m.ID = s.METIER_ID where m.id <> 'UNDEFINED'";
			
			PreparedStatement pst = connection.prepareStatement(sql);
			ResultSet result = pst.executeQuery();
			
			while (result.next()) {
				String out ="";
				out+=result.getString("tstatus")+":"+result.getInt("scount")+"\n";
				System.out.println(out);
			}
			*/
			
			JasperReport jasperReport = JasperCompileManager.compileReport(TicketDigest.class.getResourceAsStream("/report/TicketAnalytics.jrxml"));
			Map customParameters = new HashMap();
			
			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(getSeverities(), false);
			customParameters.put("subReportBeanList", ds);
			customParameters.put("severitySource",JasperCompileManager.compileReport(TicketDigest.class.getResourceAsStream("/report/TicketAnalytics_Severity.jrxml")));
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, customParameters, 
					new JRBeanCollectionDataSource(getStatus()));
			
			//subReportBeanList
			
			FileOutputStream os = new FileOutputStream(tmpfile);
			JasperExportManager.exportReportToPdfStream(jasperPrint, os);
		} catch (JRException | IOException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
		return tmpfile;
    }
    
    /**
     * {description}
     *
     * @version 3.1.0
     * @since Build 141104.092550
     * @author Tarka L'Herpiniere
     * @author <tarka@solid.com>
     */
    protected class TicketDigest {

        //<editor-fold defaultstate="collapsed" desc="Properties">
        private Long id;
        private DTX.TicketStatus status;
        private DTX.TicketTopic topic;
        private DTX.TicketSeverity severity;
        private String creatorName;
        private String requesterName;
        private String requesterCompany;
        private String closerName;
        private String assigneeName;
        private String title;
        private Long openEpoch;
        private Long closeEpoch;
        private int satisfied;
        private String das;
        private int commentQty;
        private long resolutionDuration;
        private long averageResponseTime;
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public TicketDigest() {
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Getters & Setters">
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public DTX.TicketStatus getStatus() {
            return status;
        }

        public void setStatus(DTX.TicketStatus status) {
            this.status = status;
        }

        public DTX.TicketTopic getTopic() {
            return topic;
        }

        public void setTopic(DTX.TicketTopic topic) {
            this.topic = topic;
        }

        public DTX.TicketSeverity getSeverity() {
            return severity;
        }

        public void setSeverity(DTX.TicketSeverity severity) {
            this.severity = severity;
        }

        public String getCreatorName() {
            return creatorName;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public String getRequesterName() {
            return requesterName;
        }

        public void setRequesterName(String requesterName) {
            this.requesterName = requesterName;
        }

        public String getRequesterCompany() {
            return requesterCompany;
        }

        public void setRequesterCompany(String requesterCompany) {
            this.requesterCompany = requesterCompany;
        }

        public String getCloserName() {
            return closerName;
        }

        public void setCloserName(String closerName) {
            this.closerName = closerName;
        }

        public String getAssigneeName() {
            return assigneeName;
        }

        public void setAssigneeName(String assigneeName) {
            this.assigneeName = assigneeName;
        }

        public Long getOpenEpoch() {
            return openEpoch;
        }

        public void setOpenEpoch(Long openEpoch) {
            this.openEpoch = openEpoch;
        }

        public Long getCloseEpoch() {
            return closeEpoch;
        }

        public void setCloseEpoch(Long closeEpoch) {
            this.closeEpoch = closeEpoch;
        }

        public int getSatisfied() {
            return satisfied;
        }

        public void setSatisfied(int satisfied) {
            this.satisfied = satisfied;
        }

        public String getDas() {
            return das;
        }

        public void setDas(String das) {
            this.das = das;
        }

        public int getCommentQty() {
            return commentQty;
        }

        public void setCommentQty(int commentQty) {
            this.commentQty = commentQty;
        }

        public long getResolutionDuration() {
            return resolutionDuration;
        }

        public void setResolutionDuration(long resolutionDuration) {
            this.resolutionDuration = resolutionDuration;
        }

        public long getAverageResponseTime() {
            return averageResponseTime;
        }

        public void setAverageResponseTime(long averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
//</editor-fold>

        protected void create(Ticket ticket) {

            // Organise the comments
            ticket.sortCommentsOldestFirst();

            // Convert the data
            this.id = ticket.getId();
            this.status = ticket.getStatus();
            this.topic = ticket.getTopic();
            this.severity = ticket.getSeverity();
            this.openEpoch = ticket.getOpenEpoch();
            this.closeEpoch = ticket.getCloseEpoch();
            this.satisfied = ticket.getSatisfied();
            this.commentQty = ticket.getComments().size();
            this.title = ticket.getTitle();
            if (ticket.getCreator().getContact() != null) {
                this.creatorName = ticket.getCreator().getContact().buildFullName();
            }
            if (ticket.getRequester().getContact() != null) {
                this.requesterName = ticket.getRequester().getContact().buildFullName();
            }
            if (ticket.getRequester().getCompany() != null) {
                this.requesterCompany = ticket.getRequester().getCompany().getName();
            }
            // Only add assignee if one exists
            if (ticket.getAssignee() != null) {
                this.assigneeName = ticket.getAssignee().getContact().buildFullName();
            }
            // Only add DAS if one exists
            if (ticket.getDas() != null) {
                this.das = ticket.getDas().getName();
            }
            // Only calculate the close duration if the ticket is closed
            if (ticket.getCloseEpoch() != null) {
                this.resolutionDuration = resolutionDuration(ticket.getComments());
            }

            // Calculate the average response time for a ticket
            this.averageResponseTime = avgResponseTime(ticket.getComments());
        }

        /**
         * Calculate the average response time for the comments on this ticket.
         * @param comments
         * @return 
         */
        private long avgResponseTime(List<Comment> comments) {
            // The first epoch to subtract
            Comment previousComment = null;
            long totalDuration = 0;
            int numberOfComments = comments.size();

            for (Comment comment : comments) {
                if (previousComment != null) {
                    long duration = comment.getCreateEpoch() - previousComment.getCreateEpoch();
                    totalDuration = totalDuration + duration;
                }
                // Set this as the previous comment for the next iteration
                previousComment = comment;
            }
            // Calculate the average
            return totalDuration / numberOfComments;

        }

        private long resolutionDuration(List<Comment> comments) {
            long first = comments.get(0).getCreateEpoch();
            long last = comments.get(comments.size() - 1).getCreateEpoch();
            return last - first;
        }

    }

}
