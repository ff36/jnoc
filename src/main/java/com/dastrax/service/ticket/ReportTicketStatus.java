package com.dastrax.service.ticket;

public class ReportTicketStatus {
	private String name;
	private Integer count;
	
	public ReportTicketStatus(){}
	public ReportTicketStatus(String name, int count){
		this.name = name;
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
}
