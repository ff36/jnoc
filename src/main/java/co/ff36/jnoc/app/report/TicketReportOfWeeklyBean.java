package co.ff36.jnoc.app.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.ff36.jnoc.per.entity.Ticket;

public class TicketReportOfWeeklyBean {
	
	private String status;
	private String title;
	private String requester;
	private String assignee;
	private String openEpoch;
	private String topic;
	private String severity;
	
	public TicketReportOfWeeklyBean(){}
	
	public TicketReportOfWeeklyBean(String status, String title,
			String requester, String assignee, String openEpoch, String topic,
			String severity) {
		super();
		this.status = status;
		this.title = title;
		this.requester = requester;
		this.assignee = assignee;
		this.openEpoch = openEpoch;
		this.topic = topic;
		this.severity = severity;
	}

	public static List<TicketReportOfWeeklyBean> buildTickets(List<Ticket> tickets) {
		List<TicketReportOfWeeklyBean> beans = new ArrayList<TicketReportOfWeeklyBean>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Ticket ticket : tickets) {
			String requester = "";
			if(ticket.getRequester()!=null && ticket.getRequester().getContact()!=null)
				requester = ticket.getRequester().getContact().getFullname();
			
			String assignee = "";
			if(ticket.getAssignee()!=null && ticket.getAssignee().getContact()!=null)
				requester = ticket.getAssignee().getContact().getFullname();
			
			beans.add(new TicketReportOfWeeklyBean(
					ticket.getStatus().toString(), 
					ticket.getTitle(), 
					requester,
					assignee, 
					sdf.format(new Date(ticket.getOpenEpoch())),
					ticket.getTopic().toString(), 
					ticket.getSeverity().toString()));
		}
		
		
		return beans;
	}
	
	public static List<TicketReportOfWeeklyBean> buildTickets() {
		List<TicketReportOfWeeklyBean> beans = new ArrayList<TicketReportOfWeeklyBean>();
		
		return beans;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getOpenEpoch() {
		return openEpoch;
	}

	public void setOpenEpoch(String openEpoch) {
		this.openEpoch = openEpoch;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}
}
