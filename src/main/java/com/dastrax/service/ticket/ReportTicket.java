package com.dastrax.service.ticket;

public class ReportTicket {
	private String name;
	private Integer count;
	private String category;
	
	public ReportTicket(){}
	public ReportTicket(String name, int count){
		this.name = name;
		this.count = count;
	}
	
	public ReportTicket(String name, Integer count, String category) {
		super();
		this.name = name;
		this.count = count;
		this.category = category;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
}
